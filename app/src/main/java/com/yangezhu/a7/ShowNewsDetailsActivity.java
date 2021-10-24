package com.yangezhu.a7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ShowNewsDetailsActivity extends AppCompatActivity {

    private News news;
    ImageView image;

    private LinearLayout news_details_screen_container;
    private TextView title;
    private TextView link;
    private TextView description;
    private TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_news_details);

        Toolbar toolbar = findViewById(R.id.my_toolbar_news_details);
        setSupportActionBar(toolbar);
        toolbar.setTitle("News Details");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        news_details_screen_container = (LinearLayout) findViewById(R.id.news_details_screen_container);

        Gson gson = new Gson();
        News selected_news = gson.fromJson(getIntent().getStringExtra(ListNewsActivity.SELECTED_NEWS), News.class);

        title = (TextView) findViewById(R.id.news_details_title);
        link = (TextView) findViewById(R.id.news_details_link);
        description = (TextView) findViewById(R.id.news_details_description);
        date = (TextView) findViewById(R.id.news_details_date);
        news = selected_news;

        title.setText(selected_news.getTitle());
        link.setText(selected_news.getLink());
        description.setText(Html.fromHtml(selected_news.getDescription()));

        String dateValue = selected_news.getPublish_date();
        SimpleDateFormat date_format = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        DateFormat display_date_format = new SimpleDateFormat("MMMM dd, yyyy");

        Date dateStringToDateObj = null;
        String display_date = dateValue;
        try {
            dateStringToDateObj = date_format.parse(dateValue);
            display_date = display_date_format.format(dateStringToDateObj);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        date.setText(display_date);


        image = (ImageView)findViewById(R.id.news_details_image);
        new DownloadAndShowImage().execute();

        load_settings();
    }

    @Override
    protected void onResume() {
        load_settings();
        super.onResume();
    }

    private void load_settings(){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        boolean chk_night = sp.getBoolean("NIGHT", false);
        if (chk_night){
            news_details_screen_container.setBackgroundColor(Color.parseColor("#222222"));
        }else{
            news_details_screen_container.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        String orien = sp.getString("ORIENTATION", "false");
        if ("Auto".equals(orien)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);
        }else if ("Portrait".equals(orien)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else if ("Landscape".equals(orien)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        String font_size = sp.getString("FONT_SIZE", "false");
        float settings_font_size = 10;
        if ("Small".equals(font_size)){
            settings_font_size=12;
        }else if ("Medium".equals(font_size)){
            settings_font_size=16;
        }else if ("Large".equals(font_size)){
            settings_font_size=20;
        }

        title.setTextSize(settings_font_size);
        link.setTextSize(settings_font_size);
        description.setTextSize(settings_font_size);
        date.setTextSize(settings_font_size);

    }

    private class DownloadAndShowImage extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... voids) {

            Bitmap bitmap = null;
            try {
                String imageURL = news.getImage_url();
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(imageURL).getContent());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            image.setImageBitmap(bitmap);
            super.onPostExecute(bitmap);

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}