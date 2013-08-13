package org.jamel.kladr;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jamel.kladr.AddressIndexWriter.FIELD_ADDRESS;
import static org.jamel.kladr.AddressIndexWriter.FIELD_INDEX;

/**
 * @author Sergey Polovko
 */
public class KladrSearcher implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(KladrSearcher.class);

    private static final Pattern BLACK_LIST =
            Pattern.compile("(\\s*(AND|[,.\\+\\-&\\|!\\(\\){}\\[\\]\\^~\\*\\?:]|\\s{2})\\s*)+");
    private static final Pattern INDEX_PATTERN = Pattern.compile("^\\d{6}\\s*");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    public static final int TERM_MIN_LENGTH = 3;
    public static final int TERMS_MAX_COUNT = 15;

    private IndexSearcher searcher;
    private IndexReader reader;


    public KladrSearcher(File indexDir) throws IOException {
        reader = DirectoryReader.open(FSDirectory.open(indexDir));
        searcher = new IndexSearcher(reader);
    }

    public List<AddressObject> search(String line, int limit) {
        String searchQuery = BLACK_LIST.matcher(line).replaceAll(" ");
        searchQuery = INDEX_PATTERN.matcher(searchQuery).replaceFirst("");
        searchQuery = searchQuery.trim().toLowerCase();

        if (searchQuery.length() >= TERM_MIN_LENGTH) {
            try {
                long start = System.currentTimeMillis();
                Query query = buildQuery(searchQuery, !line.endsWith(" "));
                TopDocs results = searcher.search(query, limit);
                logger.info("Query: '{}', Found: {}, Time: {}", query, results.totalHits,
                        System.currentTimeMillis() - start);

                ScoreDoc[] hits = results.scoreDocs;

                List<AddressObject> result = new ArrayList<>(hits.length);
                for (ScoreDoc hit : hits) {
                    Document doc = reader.document(hit.doc);
                    String index = doc.get(FIELD_INDEX);
                    String address = doc.get(FIELD_ADDRESS);

                    if (index.length() > 1) {
                        result.add(new AddressObject(index, address));
                    } else {
                        result.add(new AddressObject("", address));
                    }
                }

                return result;
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return Collections.emptyList();
    }

    private Query buildQuery(String searchQuery, boolean usePrefixQuery) {
        String[] terms = WHITESPACE_PATTERN.split(searchQuery);
        if (terms.length > 1) {
            BooleanQuery booleanQuery = new BooleanQuery();
            int termsCount = Math.min(TERMS_MAX_COUNT, terms.length);
            for (int i = 0; i < termsCount; i++) {
                String termStr = terms[i];
                boolean lastTerm = (i == termsCount - 1);

                // for last term do not use length restriction
                if (lastTerm || termStr.length() >= TERM_MIN_LENGTH) {
                    Term term = new Term(FIELD_ADDRESS, termStr);
                    final Query subQuery = (lastTerm && usePrefixQuery)
                            ? new PrefixQuery(term)  // use prefix query for last term
                            : new TermQuery(term);
                    booleanQuery.add(subQuery, BooleanClause.Occur.MUST);
                }
            }
            return booleanQuery;
        }

        Term term = new Term(FIELD_ADDRESS, searchQuery);
        return usePrefixQuery ? new PrefixQuery(term) : new TermQuery(term);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
