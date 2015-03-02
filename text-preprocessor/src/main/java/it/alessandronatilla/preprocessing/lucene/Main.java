package it.alessandronatilla.preprocessing.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Author: alexander
 * Project: crowd-pulse
 */
public class Main {
    public static void main(String[] args) throws IOException {
        AnalyzerMeyer meyer = new AnalyzerMeyer();
        Directory directory = FSDirectory.open(Paths.get("/home/alexander/index_file"));
        IndexWriterConfig config = new IndexWriterConfig(meyer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

//        IndexWriter iwriter = new IndexWriter(directory, config);
//
//        Document doc = new Document();
//
//        doc.add(new TextField("super_name", "io sono leggenda", Field.Store.YES));
//
//        doc.add(new TextField("name", "andando a mangiando", Field.Store.YES));
//
//        doc.add(new TextField("category", "super vissuto programmando", Field.Store.YES));
//
//        iwriter.addDocument(doc);
//
//        iwriter.close();

        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(ireader);

        BooleanQuery query = new BooleanQuery();
        Query term = new TermQuery(new Term("name", "*"));
        query.add(new BooleanClause(term, BooleanClause.Occur.SHOULD));
        ScoreDoc[] hits = indexSearcher.search(query, null, 100).scoreDocs;

        System.out.println(hits.length);
//
//        for (String field : fields) {
//
//            Terms terms = fields.terms(field);
//
//            TermsEnum termsEnum = terms.iterator(null);
//
//            BytesRef term = null;
//
//            while ((term = termsEnum.next()) != null) {
//
//                System.out.println(term.utf8ToString() + ":" + termsEnum.docFreq());
//
//            }
//
//        }
    }
}
