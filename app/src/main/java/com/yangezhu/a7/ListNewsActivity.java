package com.yangezhu.a7;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ListNewsActivity extends AppCompatActivity {

    public static String SELECTED_NEWS = "SELECTED_NEWS";
    private LinearLayout list_news_container;

    String url;
    List<News> newsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_news);

        Intent intent = getIntent();
        url = intent.getStringExtra(MainActivity.NEWS_URL);

        //        Toast.makeText(this,url, Toast.LENGTH_SHORT).show();
        list_news_container = (LinearLayout) findViewById(R.id.list_news_container);

        new DownloadAndParseRSS().execute();

        Toolbar toolbar = findViewById(R.id.my_toolbar_refresh);
        setSupportActionBar(toolbar);
        toolbar.setTitle("List of News");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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
            list_news_container.setBackgroundColor(Color.parseColor("#222222"));
        }else{
            list_news_container.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        String orien = sp.getString("ORIENTATION", "false");
        if ("Auto".equals(orien)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);
        }else if ("Portrait".equals(orien)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else if ("Landscape".equals(orien)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_refresh, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.refresh){
            // Toast.makeText(this, "refresh Pressed", Toast.LENGTH_LONG).show();
            new DownloadAndParseRSS().execute();
        }else if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DownloadAndParseRSS extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            URL rssURL = null;
            HttpsURLConnection connection = null;
            InputStream inputStream = null;

            try {
                rssURL = new URL(url);
                connection = (HttpsURLConnection) rssURL.openConnection();
                inputStream = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            SAXParserFactory spf = SAXParserFactory.newInstance();

            try {
                SAXParser saxParser = spf.newSAXParser();
                // First argument is the data to parse
                // Second Argument is the directions on how to parse
                RSSParseHandler rssParseHandler = new RSSParseHandler();
                saxParser.parse(inputStream, rssParseHandler);
                newsList = rssParseHandler.getNewsList();

                Gson gson = new Gson();
                String newsListJson = gson.toJson(newsList);
                Log.d("ZHU_JSON_MSG", newsListJson);

            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            ListView mListView = findViewById(R.id.lv_main_list_view);

            NewsListAdapter newsListAdapter = new NewsListAdapter(ListNewsActivity.this, R.layout.news_layout, newsList);

            mListView.setAdapter(newsListAdapter);
            mListView.setOnItemClickListener(newsClick);
        }
    }

    AdapterView.OnItemClickListener newsClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            News news = (News) adapterView.getItemAtPosition(position);
            Log.d("ZHU_JSON_MSG", news.toString());

            Intent intent = new Intent(ListNewsActivity.this, ShowNewsDetailsActivity.class);
            Gson gson = new Gson();
            String stringifiedNews = gson.toJson(news);
            intent.putExtra(SELECTED_NEWS, stringifiedNews);

            ListNewsActivity.this.startActivity(intent);
        }
    };


}