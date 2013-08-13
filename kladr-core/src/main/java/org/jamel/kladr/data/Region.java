package org.jamel.kladr.data;

/**
 * @author Sergey Polovko
 */
public class Region implements KladrObject {

    private final byte[] name;
    private final byte[] socr;
    private final int index;


    public Region(byte[] name, byte[] socr, int index) {
        this.name = name;
        this.socr = socr;
        this.index = index;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Region{");
        sb.append("name='").append(getName()).append('\'');
        sb.append(", socr='").append(getSocr()).append('\'');
        sb.append(", index=").append(index);
        sb.append('}');
        return sb.toString();
    }
}
