package br.com.bossini.lefeedeclassificacomtextsentimentanalysisazure;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    //referência para a listView
    private ListView newsListView;
    //para registrar um observer posteriormente
    private SwipeRefreshLayout swipeRefreshLayout;
    //acesso aos feeds
    private FeedsDAO feedsDAO;
    //alimenta a listView
    private ArrayAdapter <SentimentItem> adapter;
    //conjunto de itens a serem exibidos
    private List <SentimentItem> sentimentItems;
    //pool de threads para fazer a conversão de XML para JSON usando um serviço específico
    private ExecutorService convertToJSONThreadsPool = Executors.newFixedThreadPool(2);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        feedsDAO = new FeedsDAO(this);
        newsListView = (ListView) findViewById(R.id.newsListView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.newsSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(fabOnClickListener);
        sentimentItems = new ArrayList<>();
        adapter = new SentimentItemAdapter(this, sentimentItems);
        newsListView.setAdapter(adapter);
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            sentimentItems.clear();
            adapter.notifyDataSetChanged();
            List<String> feeds = feedsDAO.obterFeeds();
            for (String feed : feeds){
                convertToJSONThreadsPool.submit(new ConvertFeedToJSONTask(feed));
            }
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    private View.OnClickListener fabOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            View view = inflater.inflate(R.layout.view_for_adding_feed_dialog, null);
            final TextInputEditText addFeedEditText = (TextInputEditText) view.findViewById(R.id.addFeedEditText);
            AlertDialog d = new AlertDialog.Builder(MainActivity.this)
                    .setMessage(R.string.titulo_adicionar_novo_feed)
                    .setView(view)
                    .setPositiveButton(getString(R.string.adicionar), new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            feedsDAO.salvarFeed(addFeedEditText.getEditableText().toString());
                            Toast.makeText(MainActivity.this, getString(R.string.feed_adicionado), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, getString(R.string.feed_nao_adicionado), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create();
           d.show();
        }
    };

    private class ConvertFeedToJSONTask implements Runnable{
        private String feed;
        public ConvertFeedToJSONTask (String feed){
            this.feed = feed;
        }
        private String executa(){
            try{
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(getString(R.string.url_conversao_feed_json) + feed)
                        .build();
                Response response = client.newCall(request).execute();
                return response.body().string();
            }
            catch (IOException e){
                e.printStackTrace();
                return null;
            }
        }
        @Override
        public void run() {
            String json = executa();
            Gson gson = new Gson();
            new ConsultaAzureSentimentService().execute(gson.fromJson(json, News.class));
        }
    }

    private class ConsultaAzureSentimentService extends AsyncTask <News, Void, Documents>{
        private News news;
        @Override
        protected Documents doInBackground(News... news) {
            try{
                this.news = news[0];
                Documents documents = new Documents (news[0]);
                final String accessKey = getString(R.string.chave_acesso_azure);
                final String text = new Gson().toJson(documents);
                final String host = getString(R.string.url_azure);
                MediaType JSON
                        = MediaType.parse("application/json; charset=utf-8");
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, text);
                Request request = new Request.Builder()
                        .addHeader("Ocp-Apim-Subscription-Key", accessKey)
                        .url(host)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                Documents documentsResult = new Gson().fromJson(response.body().string(), Documents.class);
                for (Document d : documentsResult.getDocuments()){
                    for (Document d2 : documents.getDocuments()){
                        if (d.getId().equalsIgnoreCase(d2.getId()))
                            d2.setScore(d.getScore());
                    }
                }
                return documents;
            }
            catch (IOException e){
                throw new RuntimeException(e);
            }
        }
        @Override
        protected void onPostExecute(Documents documents) {
            for (Document d : documents.getDocuments()){
                NewsItem newsItem = getFrom (d);
                if (newsItem != null){
                    SentimentItem item = new SentimentItem(newsItem.getTitle(), newsItem.getDescription(), newsItem.getPubDate());
                    if (d.getScore() < 0.3)
                        item.setFace(Utils.FACES.TRISTE);
                    else if (d.getScore() < 0.6)
                        item.setFace(Utils.FACES.NEUTRO);
                    else
                        item.setFace(Utils.FACES.FELIZ);
                    sentimentItems.add(item);
                }
            }
            adapter.notifyDataSetChanged();
        }
        private NewsItem getFrom (Document d){
            for (NewsItem newsItem : news.getItems()){
                if (newsItem.getDescription().equalsIgnoreCase(d.getText()))
                    return newsItem;
            }
            return null;
        }
    }
}
