package org.jamel.kladr;

import gnu.trove.map.TByteObjectMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TByteObjectHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.procedure.TByteObjectProcedure;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TLongObjectProcedure;
import org.jamel.kladr.data.City;
import org.jamel.kladr.data.District;
import org.jamel.kladr.data.Region;
import org.jamel.kladr.data.Street;


/**
 * Holds kladr objects.
 * This class is not thread-safe.
 *
 * @author Sergey Polovko
 */
public class KladrCache {

    private final TByteObjectMap<Region> regions = new TByteObjectHashMap<>();
    private final TIntObjectMap<District> districts = new TIntObjectHashMap<>();
    private final TLongObjectMap<City> cities = new TLongObjectHashMap<>();
    private final TLongObjectMap<Street> streets = new TLongObjectHashMap<>();


    public Region putRegion(byte regionCode, Region region) {
        return regions.put(regionCode, region);
    }

    public Region getRegion(byte regionCode) {
        return regions.get(regionCode);
    }

    public boolean forEachRegion(TByteObjectProcedure<Region> procedure) {
        return regions.forEachEntry(procedure);
    }

    public District putDistrict(int districtCode, District district) {
        return districts.put(districtCode, district);
    }

    public District getDistrict(int districtCode) {
        return districts.get(districtCode);
    }

    public boolean forEachDistrict(TIntObjectProcedure<District> procedure) {
        return districts.forEachEntry(procedure);
    }

    public City putCity(long cityCode, City city) {
        return cities.put(cityCode, city);
    }

    public City getCity(long cityCode) {
        return cities.get(cityCode);
    }

    public boolean forEachCity(TLongObjectProcedure<City> procedure) {
        return cities.forEachEntry(procedure);
    }

    public Street putStreet(long streetCode, Street street) {
        return streets.put(streetCode, street);
    }

    public Street getStreet(long streetCode) {
        return streets.get(streetCode);
    }

    public boolean forEachStreet(TLongObjectProcedure<Street> procedure) {
        return streets.forEachEntry(procedure);
    }

}
