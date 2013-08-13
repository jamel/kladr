package org.jamel.kladr.data;

/**
 * @author Sergey Polovko
 */
public class City implements KladrObject {

    private final byte[] name;
    private final byte[] socr;
    private final int index;
    private final byte regionCode;
    private final int districtCode;
    private final long parentCityCode;


    public City(byte[] name, byte[] socr, int index, byte regionCode, int districtCode, long parentCityCode) {
        this.name = name;
        this.socr = socr;
        this.index = index;
        this.regionCode = regionCode;
        this.districtCode = districtCode;
        this.parentCityCode = parentCityCode;
    }

    public String getName() {
        return new String(name, CHARSET);
    }

    public String getSocr() {
        return new String(socr, CHARSET);
    }

    public int getIndex() {
        return index;
    }

    public byte getRegionCode() {
        return regionCode;
    }

    public int getDistrictCode() {
        return districtCode;
    }

    public long getParentCityCode() {
        return parentCityCode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("City{");
        sb.append("name='").append(getName()).append('\'');
        sb.append(", socr='").append(getSocr()).append('\'');
        sb.append(", index=").append(index);
        sb.append(", regionCode=").append(regionCode);
        sb.append(", districtCode=").append(districtCode);
        sb.append(", parentCityCode=").append(parentCityCode);
        sb.append('}');
        return sb.toString();
    }
}
