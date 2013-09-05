package org.jamel.kladr.processors;

import org.jamel.kladr.data.City;
import org.jamel.kladr.data.Street;
import org.jamel.kladr.utils.KladrCodeUtils;

/**
 * @author Sergey Polovko
 */
public abstract class StreetProcessor extends KladrRowProcessor {

    @Override
    public final void processKladrRow(
            byte[] code,
            byte regionId, int districtId, int cityId, int countryId,
            byte[] name, byte[] socr, int index)
    {
        long streetCode = KladrCodeUtils.getStreetCode(code);

        final long cityCode;
        if (districtId == 0 && cityId == 0 && countryId == 0) {
            // (1) in region city
            if (KladrCodeUtils.isCityRegion(regionId)) {
                cityCode = regionId * 1_000_000;
            } else {
                // street in region
                // HACK: promote street to city with streetCode
                City city = new City(name, socr, index, regionId, regionId * 1000);
                processCity(streetCode, city);
                return;
            }

        } else if (cityId == 0 && countryId == 0) {
            // street in district
            // HACK: promote street to city with streetCode
            int districtCode = KladrCodeUtils.getDistrictCode(code);
            City city = new City(name, socr, index, regionId, districtCode);
            processCity(streetCode, city);
            return;

        } else if (countryId == 0) {
            // (2) in city
            cityCode = KladrCodeUtils.getCityCode(code);

        } else {
            // (3) in country
            cityCode = KladrCodeUtils.getCountryCode(code);
        }

        Street street = new Street(name, socr, index, cityCode);
        processStreet(streetCode, street);
    }

    public void processCity(long cityCode, City city) {
    }

    public void processStreet(long streetCode, Street street) {
    }
}
