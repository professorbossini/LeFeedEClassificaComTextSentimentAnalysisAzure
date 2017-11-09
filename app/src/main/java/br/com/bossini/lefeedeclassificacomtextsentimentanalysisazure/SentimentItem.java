package br.com.bossini.lefeedeclassificacomtextsentimentanalysisazure;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by rodrigo on 11/4/17.
 */

class SentimentItem {
    private Utils.FACES face;
    private String titulo;
    private String descricao;
    private String link;
    private Calendar data;
    public Utils.FACES getFace() {
        return face;
    }
    public void setFace(Utils.FACES face) {
        this.face = face;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public Calendar getData() {
        return data;
    }
    public void setData(Calendar data) {
        this.data = data;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public SentimentItem(String titulo, String descricao, String data){
        this.titulo = titulo;
        this.descricao = descricao;
        this.data = Calendar.getInstance();
        try{
            this.data.setTimeInMillis(new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z").parse(data).getTime());
        }
        catch (ParseException e){
            e.printStackTrace();
        }
    }
}
