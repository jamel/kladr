package org.jamel.kladr;

import java.io.File;

import org.jamel.dbf.processor.DbfProcessor;
import org.jamel.dbf.processor.DbfRowProcessor;
import org.jamel.dbf.utils.DbfUtils;
import org.jamel.kladr.data.City;
import org.jamel.kladr.data.District;
import org.jamel.kladr.data.Region;
import org.jamel.kladr.data.Street;
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
        doRead(KladrTable.KLADR, new KladrRowProcessor() {

            @Override
            public void readRow(byte[] code, byte regionId, int districtId, int cityId, int countryId,
                    byte[] name, byte[] socr, int index)
            {
                if (districtId == 0 && cityId == 0 && countryId == 0) {
                    // (1) region
                    if (KladrCodeUtils.isCityRegion(regionId)) {
                        long cityCode = regionId * 1_000_000;
                        City city = new City(name, socr, index, (byte) 0, 0, 0);
                        Check.isNull(kladrCache.putCity(cityCode, city), "cities collision");
                    }

                    Region region = new Region(name, socr, index);
                    Check.isNull(kladrCache.putRegion(regionId, region), "regions collision");

                } else if (cityId == 0 && countryId == 0) {
                    // (2) district
                    int districtCode = KladrCodeUtils.getDistrictCode(code);
                    District district = new District(name, socr, index, regionId);
                    Check.isNull(kladrCache.putDistrict(districtCode, district), "districts collision");

                } else if (countryId == 0) {
                    // (3) city
                    int districtCode = districtId != 0 ? KladrCodeUtils.getDistrictCode(code) : 0;
                    long cityCode = KladrCodeUtils.getCityCode(code);
                    City city = new City(name, socr, index, regionId, districtCode, 0);
                    Check.isNull(kladrCache.putCity(cityCode, city), "countries collision");

                } else {
                    // (4) country
                    int districtCode = districtId != 0 ? KladrCodeUtils.getDistrictCode(code) : 0;
                    long parentCityCode = cityId != 0 ? KladrCodeUtils.getCityCode(code) : 0;
                    long countryCode = KladrCodeUtils.getCountryCode(code);
                    City city = new City(name, socr, index, regionId, districtCode, parentCityCode);
                    Check.isNull(kladrCache.putCity(countryCode, city), "countries collision");
                }
            }
        });
    }

    /**
     * Reads data from street.dbf to kladr cache in memory
     *
     * @param kladrCache   stored in memory cache
     */
    public void readStreetTableTo(final KladrCache kladrCache) {
        doRead(KladrTable.STREET, new KladrRowProcessor() {

            @Override
            public void readRow(byte[] code, byte regionId, int districtId, int cityId, int countryId,
                    byte[] name, byte[] socr, int index)
            {
                long streetCode = KladrCodeUtils.getStreetCode(code);

                final long cityCode;
                if (districtId == 0 && cityId == 0 && countryId == 0) {
                    // (1) in region city
                    if (regionId == 77 || regionId == 78 || regionId == 99) {
                        cityCode = regionId * 1_000_000;
                    } else {
                        // street in region
                        // HACK: promote street to city with streetCode
                        City city = new City(name, socr, index, regionId, regionId * 1000, 0);
                        Check.isNull(kladrCache.putCity(streetCode, city), "promoted street collision");
                        return;
                    }

                } else if (cityId == 0 && countryId == 0) {
                    // street in district
                    // HACK: promote street to city with streetCode
                    int districtCode = KladrCodeUtils.getDistrictCode(code);
                    City city = new City(name, socr, index, regionId, districtCode, 0);
                    Check.isNull(kladrCache.putCity(streetCode, city), "promoted street collision");
                    return;

                } else if (countryId == 0) {
                    // (2) in city
                    cityCode = KladrCodeUtils.getCityCode(code);

                } else {
                    // (3) in country
                    cityCode = KladrCodeUtils.getCountryCode(code);
                }

                Street street = new Street(name, socr, index, cityCode);
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


    /**
     * Process each data row from kladr tables with splitting
     * CODE by meaningful parts and string data trimming.
     */
    private static abstract class KladrRowProcessor implements DbfRowProcessor {

        @Override
        public final void processRow(Object[] row) {
            byte[] code = (byte[]) row[2];

            // skip invalid values
            if (!KladrCodeUtils.isValid(code)) return;

            byte regionId = KladrCodeUtils.getRegionId(code);
            int districtId = KladrCodeUtils.getDistrictId(code);
            int cityId = KladrCodeUtils.getCityId(code);
            int countryId = KladrCodeUtils.getCountryId(code);

            byte[] name = DbfUtils.trimLeftSpaces((byte[]) row[0]);
            byte[] socr = DbfUtils.trimLeftSpaces((byte[]) row[1]);
            int index = DbfUtils.parseInt((byte[]) row[3]);

            readRow(code, regionId, districtId, cityId, countryId, name, socr, index);
        }

        public abstract void readRow(byte[] code, byte regionId, int districtId, int cityId, int countryId,
                byte[] name, byte[] socr, int index);
    }

}
