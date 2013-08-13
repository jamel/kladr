package org.jamel.kladr;

/**
 * @author Sergey Polovko
 */
public class AddressObject {

    private final String index;
    private final String address;


    public AddressObject(String index, String address) {
        this.index = index;
        this.address = address;
    }

    public String getIndex() {
        return index;
    }

    public String getAddress() {
        return address;
    }
}
