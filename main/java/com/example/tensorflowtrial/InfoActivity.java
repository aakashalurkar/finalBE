package com.example.tensorflowtrial;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by User on 4/29/2015.
 */
public class InfoActivity extends AppCompatActivity {
    public static String urlString = "http://192.168.1.4:5000/?num1=%22siddhesh1%22";

    TextView header;
    TextView body;
    TextView Date;
    TextView From;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        header = (TextView) findViewById(R.id.header);
        body = (TextView) findViewById(R.id.body);
        Date = (TextView) findViewById(R.id.dateid);
        From = (TextView) findViewById(R.id.authorid);

        body.setMovementMethod(new ScrollingMovementMethod());
        String s = "";
        String b = "";
        String date = "";
        String from = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            b = extras.getString("body");
            s = extras.getString("subject");
            date = extras.getString("date");
            from = extras.getString("from");
        }

        body.setText(b);
        header.setText(s);
        Date.setText(date);
        From.setText(from);

        final String email = "Author: " + from
                + " Date: " + date
                + " Subject: " + s
                + " Body: " + b;

    }
}

