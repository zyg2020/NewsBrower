package com.yangezhu.a7;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.List;

public class NewsListAdapter extends ArrayAdapter<News> {

    private Context context;
    private int resourceID;

    public NewsListAdapter(@NonNull Context context, int resource, @NonNull List<News> newsList) {
        super(context, resource, newsList);

        this.context = context;
        this.resourceID = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resourceID, parent, false);
        }

        float font_size = load_font_size_settings();

        News news = getItem(position);

        TextView title = convertView.findViewById(R.id.news_title);
        TextView date = convertView.findViewById(R.id.news_date);
        TextView link = convertView.findViewById(R.id.news_link);

        title.setText(news.getTitle());
        date.setText(news.getPublish_date());
        link.setText(news.getLink());

        date.setTextSize(font_size);
        link.setTextSize(font_size);
//
//        Log.d("ZHU_JSON_MSG", news.toString());

        return convertView;


    }

    private float load_font_size_settings(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String font_size = sp.getString("FONT_SIZE", "false");
        float settings_font_size = 10;
        if ("Small".equals(font_size)){
            settings_font_size=12;
        }else if ("Medium".equals(font_size)){
            settings_font_size=16;
        }else if ("Large".equals(font_size)){
            settings_font_size=20;
        }

        return settings_font_size;
    }
}
