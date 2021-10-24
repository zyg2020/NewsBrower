package com.yangezhu.a7;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static String NEWS_URL = "NEWS_URL";

    private LinearLayout main_activity_container;

    private LinearLayout local_container;
    private LinearLayout sports_container;
    private LinearLayout travel_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        local_container = (LinearLayout)findViewById(R.id.local_container);
        sports_container = (LinearLayout)findViewById(R.id.sports_container);
        travel_container = (LinearLayout)findViewById(R.id.travel_container);

        local_container.setOnClickListener(categoryClickListener);
        sports_container.setOnClickListener(categoryClickListener);
        travel_container.setOnClickListener(categoryClickListener);

        main_activity_container = findViewById(R.id.main_activity);
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
            main_activity_container.setBackgroundColor(Color.parseColor("#222222"));
        }else{
            main_activity_container.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        String orien = sp.getString("ORIENTATION", "false");
        if ("Auto".equals(orien)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);
        }else if ("Portrait".equals(orien)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else if ("Landscape".equals(orien)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        TextView localDescription = (TextView)findViewById(R.id.local_description);
        TextView sportsDescription = (TextView)findViewById(R.id.sports_description);
        TextView travelDescription = (TextView)findViewById(R.id.travel_description);

        String font_size = sp.getString("FONT_SIZE", "false");
        float settings_font_size = 10;
        if ("Small".equals(font_size)){
            //this.setTheme(R.style.CustomizedLight_small);
            //Toast.makeText(this, "CustomizedLight_small", Toast.LENGTH_SHORT).show();
            settings_font_size=12;
        }else if ("Medium".equals(font_size)){
            //this.setTheme(R.style.CustomizedLight_middle);
            //Toast.makeText(this, "CustomizedLight_middle", Toast.LENGTH_SHORT).show();
            settings_font_size=16;
        }else if ("Large".equals(font_size)){
            //this.setTheme(R.style.CustomizedLight_large);
            //Toast.makeText(this, "CustomizedLight_large", Toast.LENGTH_SHORT).show();
            settings_font_size=20;
        }
        localDescription.setTextSize(settings_font_size);
        sportsDescription.setTextSize(settings_font_size);
        travelDescription.setTextSize(settings_font_size);

        boolean chk_show_local_news = sp.getBoolean("SHOW_LOCAL_NEWS", true);
        if (chk_show_local_news){
            local_container.setVisibility(View.VISIBLE);
        }else{
            local_container.setVisibility(View.GONE);
        }

        boolean chk_show_sports_news = sp.getBoolean("SHOW_SPORTS_NEWS", true);
        if (chk_show_sports_news){
            sports_container.setVisibility(View.VISIBLE);
        }else{
            sports_container.setVisibility(View.GONE);
        }

        boolean chk_show_travel_news = sp.getBoolean("SHOW_TRAVEL_NEWS", true);
        if (chk_show_travel_news){
            travel_container.setVisibility(View.VISIBLE);
        }else{
            travel_container.setVisibility(View.GONE);
        }
    }

    View.OnClickListener categoryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MainActivity.this, ListNewsActivity.class);
            String url = "";
            if (view.getId() == R.id.local_container){
                Toast.makeText(MainActivity.this,"Local news", Toast.LENGTH_SHORT).show();
                url = "https://www.winnipegfreepress.com/rss/?path=%2Flocal";
            }else if (view.getId() == R.id.sports_container){
                Toast.makeText(MainActivity.this,"sports container", Toast.LENGTH_SHORT).show();
                url="https://www.winnipegfreepress.com/rss/?path=%2Fsports";
            }else if(view.getId() == R.id.travel_container){
                Toast.makeText(MainActivity.this,"travel_container container", Toast.LENGTH_SHORT).show();
                url="https://www.winnipegfreepress.com/rss/?path=%2Ftravel";
            }
            intent.putExtra(NEWS_URL, url);
            startActivity(intent);

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_setting, menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == R.id.settings){
//            Toast.makeText(this, "settings Pressed", Toast.LENGTH_LONG).show();
//
//        }
        switch (item.getItemId()){
            case R.id.settings:
                Intent i = new Intent(MainActivity.this, Settings.class);
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}