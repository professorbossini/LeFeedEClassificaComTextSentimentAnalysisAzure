package br.com.bossini.lefeedeclassificacomtextsentimentanalysisazure;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by rodrigo on 11/4/17.
 */

public class SentimentItemAdapter extends ArrayAdapter <SentimentItem> {
    public SentimentItemAdapter (Context context, List<SentimentItem> sentiments){
        super (context, -1, sentiments);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        SentimentItem item = getItem(position);
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.list_item, null);
        ImageView imageView = linearLayout.findViewById(R.id.sentimentImageView);
        imageView.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(), Utils.getId(item.getFace())));
        TextView textView = linearLayout.findViewById(R.id.newsTextView);
        textView.setText(item.getTitulo());
        return linearLayout;
    }
}
