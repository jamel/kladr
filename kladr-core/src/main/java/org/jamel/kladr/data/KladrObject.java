package org.jamel.kladr.data;

import java.nio.charset.Charset;

/**
 * @author Sergey Polovko
 */
public interface KladrObject {

    Charset CHARSET = Charset.forName("cp866");

    String getName();
    String getSocr();
    int getIndex();
}
