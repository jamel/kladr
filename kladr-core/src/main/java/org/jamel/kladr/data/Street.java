package org.jamel.kladr.data;

/**
 * @author Sergey Polovko
 */
public class Street implements KladrObject {

    private final byte[] name;
    private final byte[] socr;
    private final int index;
    private final long cityCode;


    public Street(byte[] name, byte[] socr, int index, long cityCode) {
        this.name = name;
        this.socr = socr;
        this.index = index;
        this.cityCode = cityCode;
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

    public long getCityCode() {
        return cityCode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Street{");
        sb.append("name='").append(getName()).append('\'');
        sb.append(", socr='").append(getSocr()).append('\'');
        sb.append(", index=").append(index);
        sb.append(", cityCode=").append(cityCode);
        sb.append('}');
        return sb.toString();
    }
}
