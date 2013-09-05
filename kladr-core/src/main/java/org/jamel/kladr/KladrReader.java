package org.jamel.kladr;

import java.io.File;

import org.jamel.dbf.processor.DbfProcessor;
import org.jamel.dbf.processor.DbfRowProcessor;
import org.jamel.kladr.cache.KladrCache;
import org.jamel.kladr.data.City;
import org.jamel.kladr.data.Country;
import org.jamel.kladr.data.District;
import org.jamel.kladr.data.KladrObject;
import org.jamel.kladr.data.Region;
import org.jamel.kladr.data.Street;
import org.jamel.kladr.processors.KladrObjectsProcessor;
import org.jamel.kladr.processors.StreetProcessor;
import org.jamel.kladr.utils.Check;
import org.jamel.kladr.utils.KladrCodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sergey Polovko
 */
public class KladrReader {

    private static final Logger logger = LoggerFactory.getLogger(KladrReader.class);

    private final File kladrDir;


    public KladrReader(File kladrDir) {
        this.kladrDir = kladrDir;
    }

    /**
     * Reads data from kladr.dbf to kladr cache in memory
     *
     * @param kladrCache   stored in memory cache
     */
    public void readKladrTableTo(final KladrCache kladrCache) {
        doRead(KladrTable.KLADR, new KladrObjectsProcessor() {

            @Override
            public void processRegion(byte regionCode, Region region) {
                if (KladrCodeUtils.isCityRegion(regionCode)) {
                    long cityCode = regionCode * 1_000_000;
                    byte[] name = region.getName().getBytes(KladrObject.CHARSET);
                    byte[] socr = region.getSocr().getBytes(KladrObject.CHARSET);
                    City city = new City(name, socr, region.getIndex(), (byte) 0, 0);
                    Check.isNull(kladrCache.putCity(cityCode, city), "cities collision");
                }
                Check.isNull(kladrCache.putRegion(regionCode, region), "regions collision");
            }

            @Override
            public void processDistrict(int districtCode, District district) {
                Check.isNull(kladrCache.putDistrict(districtCode, district), "districts collision");
            }

            @Override
            public void processCity(long cityCode, City city) {
                Check.isNull(kladrCache.putCity(cityCode, city), "countries collision");
            }

            @Override
            public void processCountry(long countryCode, Country country) {
                Check.isNull(kladrCache.putCountry(countryCode, country), "countries collision");
            }
        });
    }

    /**
     * Reads data from street.dbf to kladr cache in memory
     *
     * @param kladrCache   stored in memory cache
     */
    public void readStreetTableTo(final KladrCache kladrCache) {
        doRead(KladrTable.STREET, new StreetProcessor() {
            @Override
            public void processCity(long cityCode, City city) {
                Check.isNull(kladrCache.putCity(cityCode, city), "promoted street collision");
            }

            @Override
            public void processStreet(long streetCode, Street street) {
                Check.isNull(kladrCache.putStreet(streetCode, street), "streets collision");
            }
        });
    }

    /**
     * Iterates through data in doma.dbf table.
     *
     * @param rowProcessor each row processor
     */
    public void readDomaTable(DbfRowProcessor rowProcessor) {
        doRead(KladrTable.DOMA, rowProcessor);
    }

    /**
     * Performs reading
     *
     * @param table         kladr table
     * @param rowProcessor  each row processor
     */
    private void doRead(KladrTable table, DbfRowProcessor rowProcessor) {
        File file = new File(kladrDir, table.getFileName());
        logger.info("Start reading {} ...", file);

        long start = System.currentTimeMillis();
        DbfProcessor.processDbf(file, rowProcessor);

        logger.debug("Reading {} took {} ms", file, System.currentTimeMillis() - start);
    }

    public void readKladrTable(KladrObjectsProcessor kladrObjectsProcessor) {
        doRead(KladrTable.KLADR, kladrObjectsProcessor);
    }

    public void readStreetTable(StreetProcessor streetProcessor) {
        doRead(KladrTable.STREET, streetProcessor);
    }
}
