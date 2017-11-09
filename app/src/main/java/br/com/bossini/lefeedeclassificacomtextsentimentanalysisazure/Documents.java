package br.com.bossini.lefeedeclassificacomtextsentimentanalysisazure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rodrigo on 11/7/17.
 */

public class Documents {
    private List<Document> documents;
    public Documents (){
        this.documents = new ArrayList<>();
    }
    public Documents (News news){
        this();
        int cont = 0;
        for (NewsItem item : news.getItems()){
            this.documents.add(new Document (Integer.toString(cont++), "pt", item.getDescription()));
        }
    }
    public void add (String id, String language, String text){
        documents.add(new Document(id, language, text));
    }
    public List <Document> getDocuments (){
        return this.documents;
    }
}
