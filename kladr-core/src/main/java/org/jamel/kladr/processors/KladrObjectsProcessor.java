package org.jamel.kladr.processors;

import org.jamel.kladr.data.City;
import org.jamel.kladr.data.Country;
import org.jamel.kladr.data.District;
import org.jamel.kladr.data.Region;
import org.jamel.kladr.utils.KladrCodeUtils;

/**
 * @author Sergey Polovko
 */
public abstract class KladrObjectsProcessor extends KladrRowProcessor {

    @Override
    public final void processKladrRow(
            byte[] code,
            byte regionId, int districtId, int cityId, int countryId,
            byte[] name, byte[] socr, int index)
    {
        // (1) region
        if (districtId == 0 && cityId == 0 && countryId == 0) {
            Region region = new Region(name, socr, index);
            processRegion(regionId, region);

        // (2) district
        } else if (cityId == 0 && countryId == 0) {
            int districtCode = KladrCodeUtils.getDistrictCode(code);
            District district = new District(name, socr, index, regionId);
            processDistrict(districtCode, district);

        // (3) city
        } else if (countryId == 0) {
            int districtCode = districtId != 0 ? KladrCodeUtils.getDistrictCode(code) : 0;
            long cityCode = KladrCodeUtils.getCityCode(code);
            City city = new City(name, socr, index, regionId, districtCode);
            processCity(cityCode, city);

        // (4) country
        } else {
            int districtCode = districtId != 0 ? KladrCodeUtils.getDistrictCode(code) : 0;
            long parentCityCode = cityId != 0 ? KladrCodeUtils.getCityCode(code) : 0;
            long countryCode = KladrCodeUtils.getCountryCode(code);
            Country country = new Country(name, socr, index, regionId, districtCode, parentCityCode);
            processCountry(countryCode, country);
        }
    }

    public void processRegion(byte regionCode, Region region) {
        // override for regions processing
    }

    public void processDistrict(int districtCode, District district) {
        // override for districts processing
    }

    public void processCity(long cityCode, City city) {
        // override for cities processing
    }

    public void processCountry(long countryCode, Country country) {
        // override for countries processing
    }
}
