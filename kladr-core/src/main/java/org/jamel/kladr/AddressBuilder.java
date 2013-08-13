package org.jamel.kladr;

import org.jamel.kladr.data.City;
import org.jamel.kladr.data.District;
import org.jamel.kladr.data.KladrObject;
import org.jamel.kladr.data.Region;
import org.jamel.kladr.data.Street;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Compose address string from kladr objects.
 * This class is not thread-safe.
 *
 * @author Sergey Polovko
 */
public class AddressBuilder {

    private static final Logger logger = LoggerFactory.getLogger(AddressBuilder.class);

    private static final int MAX_KLADR_LEVEL = 5;

    private final KladrCache kladrCache;

    private StringBuilder buf = new StringBuilder(256);
    private KladrObject[] address = new KladrObject[MAX_KLADR_LEVEL];


    public AddressBuilder(KladrCache kladrCache) {
        this.kladrCache = kladrCache;
    }

    public String buildFor(District district) {
        int i = 0;
        address[i++] = district;
        address[i++] = kladrCache.getRegion(district.getRegionCode());
        return toAddressString(address, i);
    }

    public String buildFor(City city) {
        return doBuildForCity(city, 0);
    }

    public String buildFor(Street street) {
        int i = 0;
        address[i++] = street;

        City city = kladrCache.getCity(street.getCityCode());
        if (city == null) {
            logger.debug("Can't get city by code=" + street.getCityCode());
            return "";
        }

        return doBuildForCity(city, i);
    }

    private String doBuildForCity(City city, int i) {
        address[i++] = city;

        if (city.getParentCityCode() != 0) {
            city = kladrCache.getCity(city.getParentCityCode());
            address[i++] = city;
        }

        if (city.getDistrictCode() != 0) {
            District district = kladrCache.getDistrict(city.getDistrictCode());
            if (district != null) address[i++] = district;
        }

        Region region = kladrCache.getRegion(city.getRegionCode());
        if (region != null) address[i++] = region;

        return toAddressString(address, i);
    }

    private String toAddressString(KladrObject[] address, int count) {
        buf.setLength(0);
        for (int i = count-1; i >= 0; i--) {
            KladrObject addressObject = address[i];
            if (addressObject != null) {
                buf.append(addressObject.getSocr())
                        .append(". ")
                        .append(addressObject.getName())
                        .append(", ");
            }
        }
        buf.setLength(buf.length() - 2);
        return buf.toString();
    }
}
