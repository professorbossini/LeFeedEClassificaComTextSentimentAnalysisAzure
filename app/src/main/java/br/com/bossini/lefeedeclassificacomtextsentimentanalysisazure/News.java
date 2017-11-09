package br.com.bossini.lefeedeclassificacomtextsentimentanalysisazure;

import java.util.List;

/**
 * Created by rodrigo on 11/8/17.
 */

public class News {

    private List <NewsItem> items;

    public List<NewsItem> getItems() {
        return items;
    }

    public void setItems(List<NewsItem> items) {
        this.items = items;
    }
}
