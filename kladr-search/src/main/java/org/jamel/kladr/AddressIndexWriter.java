package org.jamel.kladr;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * Writes inverted index of addresses.
 * This class is not thread-safe.
 *
 * @author Sergey Polovko
 */
public class AddressIndexWriter implements Closeable {

    public static final Version LUCENE_VERSION = Version.LUCENE_44;

    public static final String FIELD_INDEX = "i";
    public static final String FIELD_ADDRESS = "a";

    private final StoredField indexField = new StoredField(FIELD_INDEX, 0);
    private final TextField addressField = new TextField(FIELD_ADDRESS, "", Field.Store.YES);
    private final List<Field> doc = Arrays.asList(indexField, addressField);

    private final IndexWriter indexWriter;

    private int writtenAddressesCount;


    public AddressIndexWriter(File indexDir) throws IOException {
        this.indexWriter = createIndexWriter(indexDir);
    }

    public void write(int index, String address) {
        try {
            indexField.setIntValue(index);
            addressField.setStringValue(address);

            indexWriter.addDocument(doc);
            writtenAddressesCount++;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getWrittenAddressesCount() {
        return writtenAddressesCount;
    }

    private static IndexWriter createIndexWriter(File indexDir) throws IOException {
        IndexWriterConfig iwc = new IndexWriterConfig(LUCENE_VERSION, new StandardAnalyzer(LUCENE_VERSION));
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        iwc.setRAMBufferSizeMB(256);

        FileUtils.deleteDirectory(indexDir);
        FileUtils.forceMkdir(indexDir);

        return new IndexWriter(FSDirectory.open(indexDir), iwc);
    }

    @Override
    public void close() throws IOException {
        indexWriter.forceMerge(1);
        indexWriter.close();
    }
}
