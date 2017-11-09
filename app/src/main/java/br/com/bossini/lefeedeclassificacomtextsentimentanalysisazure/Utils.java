package br.com.bossini.lefeedeclassificacomtextsentimentanalysisazure;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rodrigo on 11/4/17.
 */

public class Utils {
    private static  Map<FACES, Integer> faces = new HashMap<>();
    static{
        faces.put(FACES.FELIZ, R.drawable.ic_sentiment_satisfied_black_24dp);
        faces.put(FACES.NEUTRO, R.drawable.ic_sentiment_neutral_black_24dp);
        faces.put(FACES.TRISTE, R.drawable.ic_sentiment_dissatisfied_black_24dp);
    }
    public static int getId (FACES face){
        return faces.get(face);
    }
    public enum FACES {FELIZ, NEUTRO, TRISTE};
}
