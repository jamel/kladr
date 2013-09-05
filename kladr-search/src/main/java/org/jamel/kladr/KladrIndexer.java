package org.jamel.kladr;

import java.io.File;
import java.io.IOException;

import gnu.trove.procedure.TByteObjectProcedure;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TLongObjectProcedure;
import org.jamel.dbf.processor.DbfRowProcessor;
import org.jamel.dbf.utils.DbfUtils;
import org.jamel.kladr.cache.KladrCache;
import org.jamel.kladr.data.City;
import org.jamel.kladr.data.Country;
import org.jamel.kladr.data.District;
import org.jamel.kladr.data.KladrObject;
import org.jamel.kladr.data.Region;
import org.jamel.kladr.data.Street;
import org.jamel.kladr.utils.KladrCodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sergey Polovko
 */
public class KladrIndexer {

    private static final Logger logger = LoggerFactory.getLogger(KladrIndexer.class);

    private final KladrCache kladrCache;
    private final KladrReader kladrReader;
    private final AddressBuilder addressBuilder;
    private final File indexDir;


    public KladrIndexer(File kladrDir, File indexDir) throws IOException {
        this.kladrReader = new KladrReader(kladrDir);
        this.kladrCache = new KladrCache();
        this.addressBuilder = new AddressBuilder(kladrCache);
        this.indexDir = indexDir;
    }

    public void doIndexing() {
        try (AddressIndexWriter addressIndexWriter = new AddressIndexWriter(indexDir)) {
            long start = System.currentTimeMillis();
            kladrReader.readKladrTableTo(kladrCache);
            kladrReader.readStreetTableTo(kladrCache);
            logger.debug("Load kladr cache tooks " + (System.currentTimeMillis() - start) + " ms");

            start = System.currentTimeMillis();
            indexRegions(addressIndexWriter);
            indexCities(addressIndexWriter);
            indexDistricts(addressIndexWriter);
            indexCountries(addressIndexWriter);
            indexStreets(addressIndexWriter);
            indexBuildings(addressIndexWriter);
            logger.debug("Indexing kladr tooks " + (System.currentTimeMillis() - start) + " ms");

            logger.info("Indexed {} addresses", addressIndexWriter.getWrittenAddressesCount());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void indexRegions(final AddressIndexWriter addressIndexWriter) {
        kladrCache.forEachRegion(new TByteObjectProcedure<Region>() {
            public boolean execute(byte kladrCode, Region region) {
                String address = region.getSocr() + ". " + region.getName();
                addressIndexWriter.write(region.getIndex(), address);
                return true;
            }
        });
    }

    private void indexCities(final AddressIndexWriter addressIndexWriter) {
        // index major cities
        kladrCache.forEachCity(new TLongObjectProcedure<City>() {
            public boolean execute(long kladrCode, City city) {
                if (city.getRegionCode() == 0) return true; // skip Moscow, Piter and Baykonur
                if (city.getDistrictCode() == 0) {
                    addressIndexWriter.write(city.getIndex(), addressBuilder.buildFor(city));
                }
                return true;
            }
        });

        // index not major cities
        kladrCache.forEachCity(new TLongObjectProcedure<City>() {
            public boolean execute(long kladrCode, City city) {
                if (city.getRegionCode() == 0) return true; // skip Moscow, Piter and Baykonur
                if (city.getDistrictCode() != 0) {
                    addressIndexWriter.write(city.getIndex(), addressBuilder.buildFor(city));
                }
                return true;
            }
        });
    }

    private void indexDistricts(final AddressIndexWriter addressIndexWriter) {
        kladrCache.forEachDistrict(new TIntObjectProcedure<District>() {
            public boolean execute(int kladrCode, District district) {
                addressIndexWriter.write(district.getIndex(), addressBuilder.buildFor(district));
                return true;
            }
        });
    }

    private void indexCountries(final AddressIndexWriter addressIndexWriter) {
        kladrCache.forEachCountry(new TLongObjectProcedure<Country>() {
            public boolean execute(long kladrCode, Country country) {
                addressIndexWriter.write(country.getIndex(), addressBuilder.buildFor(country));
                return true;
            }
        });
    }

    private void indexStreets(final AddressIndexWriter addressIndexWriter) {
        kladrCache.forEachStreet(new TLongObjectProcedure<Street>() {
            public boolean execute(long kladrCode, Street street) {
                addressIndexWriter.write(street.getIndex(), addressBuilder.buildFor(street));
                return true;
            }
        });
    }

    private void indexBuildings(final AddressIndexWriter addressIndexWriter) {
        kladrReader.readDomaTable(new DbfRowProcessor() {
            @Override
            public void processRow(Object[] row) {
                byte[] name = DbfUtils.trimLeftSpaces((byte[]) row[0]);
                byte[] code = (byte[]) row[3];
                int index = DbfUtils.parseInt((byte[]) row[4]);

                int streetId = KladrCodeUtils.getStreetId(code);
                int countryId = KladrCodeUtils.getCountryId(code);

                final KladrObject parentObject;
                if (streetId == 0 && countryId == 0) {
                    // (1) building in city
                    long cityCode = KladrCodeUtils.getCityCode(code);
                    parentObject = kladrCache.getCity(cityCode);
                } else if (streetId == 0) {
                    // (2) building in country
                    long cityCode = KladrCodeUtils.getCountryCode(code);
                    parentObject = kladrCache.getCity(cityCode);
                } else {
                    // (3) building on street
                    long streetCode = KladrCodeUtils.getStreetCode(code);
                    parentObject = kladrCache.getStreet(streetCode);
                }

                if (parentObject != null) {
                    String address = (parentObject instanceof Street)
                            ? addressBuilder.buildFor((Street) parentObject)
                            : addressBuilder.buildFor((City) parentObject);
                    if (!address.isEmpty()) {
                        String buildings = new String(name, KladrObject.CHARSET);
                        if (index == 0) index = parentObject.getIndex();

                        for (String building : buildings.split(",")) {
                            addressIndexWriter.write(index, address + ", д. " + building);
                        }
                    }
                } else {
                    logger.debug("Can't get parent city or street by code=" + new String(code));
                }
            }
        });
    }
}
