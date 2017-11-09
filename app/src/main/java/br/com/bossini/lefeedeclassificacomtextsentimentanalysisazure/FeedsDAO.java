package br.com.bossini.lefeedeclassificacomtextsentimentanalysisazure;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by rodrigo on 11/4/17.
 */

public class FeedsDAO {
    private String sharedPrefsFileName = "urls";
    private String urlsKey = "urlsKey";
    private Context context;
    public FeedsDAO (Context context){
        this.context = context;
    }

    public void salvarFeed (String feed){
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(sharedPrefsFileName, Context.MODE_PRIVATE);
        Set <String> urls = sharedPreferences.getStringSet(urlsKey, new HashSet<String>());
        urls.add(feed);
        sharedPreferences.edit().putStringSet(urlsKey, urls).apply();
    }

    public List<String> obterFeeds (){
        SharedPreferences sharedPreferences = this.context.getSharedPreferences(sharedPrefsFileName, Context.MODE_PRIVATE);
        return new ArrayList <String>(sharedPreferences.getStringSet(urlsKey, new HashSet<String>()));
    }
}
