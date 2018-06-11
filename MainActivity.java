package com.example.tensorflowtrial;

import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Bundle;
import android.os.Trace;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.tensorflowtrial.MySQLiteHelper;
import com.example.tensorflowtrial.R;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class MainActivity extends AppCompatActivity {

    //   public static String urlString="http://192.168.1.6:5000/?emailList=sid";
    public static String urlString;
    public static String priorityResult;
    public static String spamResultstring;

    private static final String TAG = "PlayHelloActivity";
    private final static String GMAIL_SCOPE = "https://www.googleapis.com/auth/gmail.readonly";
    private final static String SCOPE = "oauth2:" + GMAIL_SCOPE;
    private TextView mOut;
    private TextView spamResult;
    private ListView lView,listViewPrio,listViewSpam;
    private ProgressBar spinner;
    private ArrayList<String> l;
    private ArrayList<String> b;
    private ArrayList<String> dateList;
    private ArrayList<String> fList;
    public ArrayList<String> fullEmailList;

    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;
    public static final String PREFS_NAME = "PrimeFile";

    Button sendPrio;
    Button sendSpam;
    Button prioritiseButton;
    Button spamButton;

    Button callapi;
    private String mEmail;
    Gson gson = new Gson();

    public String allEmails = " ";
//    public String path = Environment.getExternalStorageDirectory().getAbsolutePath()
//            ;


    static final int READ_BLOCK_SIZE = 100;
//
//    public MainActivity() {
//    }
//

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callapi = (Button) findViewById(R.id.greet_me_button);
        sendPrio = (Button) findViewById(R.id.sendPrioButton);
        sendSpam = (Button) findViewById(R.id.sendSpamButton);
        prioritiseButton = (Button) findViewById(R.id.prioritiseButton);
        spamButton = (Button) findViewById(R.id.spamButton);

        listViewPrio = (ListView) findViewById(R.id.listViewPrio);
        listViewSpam = (ListView) findViewById(R.id.listViewSpam);

        sendPrio.setVisibility(View.GONE);
        sendSpam.setVisibility(View.GONE);

        listViewPrio.setVisibility(View.GONE);

        listViewSpam.setVisibility(View.GONE);

        prioritiseButton.setBackgroundColor(Color.parseColor("#FFFFFF"));
        callapi.setBackgroundColor(Color.parseColor("#FFFFFF"));
        spamButton.setBackgroundColor(Color.parseColor("#FFFFFF"));

        //////////////////////

        ////////////////////////

        final String email = loadSavedPreferences();
        if (!email.equals("EmailStuff")) {
            mEmail = loadSavedPreferences();
//            Log.d("Email2", mEmail);
        }

        MySQLiteHelper db = new MySQLiteHelper(this);
        l = new ArrayList<String>();
        b = new ArrayList<String>();
        final ArrayList<String> dateList = new ArrayList<String>();
        fList = new ArrayList<String>();
        fullEmailList = new ArrayList<String>();
        final List<Email> list = db.getAllBooks();

        for (Email e : list) {
            l.add(e.getSubject());
            b.add(e.getBody());
            fList.add(e.getAuthor());
            dateList.add(e.getDateTime());

            //fullemaillist has subject and body..concatenate other details if needed.
            fullEmailList.add(
                    "Author: " + e.getAuthor()
                            + " Date: " + e.getDateTime()
                            + " Subject: " + e.getSubject()
                     + "Body: " + e.getBody()
            );
        }

        sendPrio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                System.out.println("Send  Prio button clicked");
                int size = fullEmailList.size();
                System.out.println("Size of email list " + size);
                for (int i = 0; i < size; i++) {

                    allEmails = allEmails + fullEmailList.get(i).toString();
                    //                    final JSONObject email1 = myJSON.put("Email", fullEmailList.get(i));
                }

//                    System.out.println("All emails"+allEmails);

                String paramValue = allEmails;
                try {
                    urlString = "http://192.168.1.2:5000/priority?emailList=" + java.net.URLEncoder.encode(paramValue, "UTF-8");
//                        urlString = "http://127.0.0.1:5000/?emailList=" + java.net.URLEncoder.encode(paramValue, "UTF-8");
                    Log.d("URL Final",urlString);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                new SendingPrioTask().execute();


            }

        });


        sendSpam.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Send spam button clicked");
                int size = fullEmailList.size();
                System.out.println("Size of email list " + size);
                for (int i = 0; i < size; i++) {

                    allEmails = allEmails + fullEmailList.get(i).toString();
                    //                    final JSONObject email1 = myJSON.put("Email", fullEmailList.get(i));
                }

//                    System.out.println("All emails"+allEmails);

                String paramValue = allEmails;
                try {
                    urlString = "http://192.168.1.2:5000/spamham?emailList2=" + java.net.URLEncoder.encode(paramValue, "UTF-8");
                    Log.d("Spam url",urlString);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                new SendingSpamTask().execute();
            }
        });

        callapi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                lView.setVisibility(View.VISIBLE);
                listViewPrio.setVisibility(View.GONE);
                listViewSpam.setVisibility(View.GONE);

                callapi.setTextColor(Color.parseColor("#133468"));
                callapi.setBackgroundColor(Color.parseColor("#FFFFFF"));
                prioritiseButton.setBackgroundColor(Color.parseColor("#133468"));
                spamButton.setBackgroundColor(Color.parseColor("#133468"));
                prioritiseButton.setTextColor(Color.parseColor("#FFFFFF"));
                spamButton.setTextColor(Color.parseColor("#FFFFFF"));
                greetTheUser(v);
            }
        });



        prioritiseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (priorityResult == null){
                    Toast.makeText(MainActivity.this, "moklay he...", Toast.LENGTH_SHORT).show();
                }else {
                    String[] prioparts = priorityResult.split("\\]\\[");
                    final List<String> pList = new ArrayList<String>(Arrays.asList(prioparts));

                    final ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, pList);

                    listViewPrio.setAdapter(arrayAdapter1);
                }

                listViewPrio.setVisibility(View.VISIBLE);
                lView.setVisibility(View.GONE);
                listViewSpam.setVisibility(View.GONE);
                mOut.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);


                prioritiseButton.setTextColor(Color.parseColor("#133468"));
                prioritiseButton.setBackgroundColor(Color.parseColor("#FFFFFF"));
                callapi.setBackgroundColor(Color.parseColor("#133468"));
                spamButton.setBackgroundColor(Color.parseColor("#133468"));
                callapi.setTextColor(Color.parseColor("#FFFFFF"));
                spamButton.setTextColor(Color.parseColor("#FFFFFF"));

            }
        });

        spamButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                if (spamResultstring == null){
//                    Toast.makeText(MainActivity.this, "moklay he...", Toast.LENGTH_SHORT).show();
//                }else {
//                    String[] spamparts = spamResultstring.split("\\]\\[");
//                    for(int k=0;k<spamparts.length;k++)
//                    {
//                        System.out.println("Spam parts"+spamparts[k]);
//                    }
//                    final List<String> sList = new ArrayList<String>(Arrays.asList(spamparts));
//
//                    final ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, sList);
//
//                    listViewSpam.setAdapter(arrayAdapter2);
//                }

                if (spamResultstring == null){
                    Toast.makeText(MainActivity.this, "moklay he...", Toast.LENGTH_SHORT).show();
                }else {
                    String[] spamparts = spamResultstring.split("\\], \\[");

                    final  List<String> sList = new ArrayList<String>(Arrays.asList(spamparts));
                    final ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, sList);
                    listViewSpam.setAdapter(arrayAdapter2);
                }

                listViewSpam.setVisibility(View.VISIBLE);
                listViewPrio.setVisibility(View.GONE);
                lView.setVisibility(View.GONE);
                mOut.setVisibility(View.GONE);
                spinner.setVisibility(View.GONE);

                spamButton.setTextColor(Color.parseColor("#133468"));
                spamButton.setBackgroundColor(Color.parseColor("#FFFFFF"));
                prioritiseButton.setBackgroundColor(Color.parseColor("#133468"));
                callapi.setBackgroundColor(Color.parseColor("#133468"));
                callapi.setTextColor(Color.parseColor("#FFFFFF"));
                prioritiseButton.setTextColor(Color.parseColor("#FFFFFF"));

            }
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, l);

        mOut = (TextView) findViewById(R.id.message);
        lView = (ListView) findViewById(R.id.listView);

        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);
        lView.setAdapter(arrayAdapter);
        lView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i = new Intent(getApplicationContext(), com.example.tensorflowtrial.InfoActivity.class);
                i.putExtra("body", b.get(position));
                i.putExtra("subject", l.get(position));
                i.putExtra("from", fList.get(position));
                i.putExtra("date", dateList.get(position));
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == RESULT_OK) {
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                savePreferences("email", mEmail);
//                Log.d("Email1", "Putting " + mEmail + " into prefs");
                getUsername();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You must pick an account", Toast.LENGTH_SHORT).show();
            }
        } else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR ||
                requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
                && resultCode == RESULT_OK) {
            handleAuthorizeResult(resultCode, data);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleAuthorizeResult(int resultCode, Intent data) {
        if (data == null) {
            show("Unknown error, click the button again");
            return;
        }
        if (resultCode == RESULT_OK) {
            Log.i(TAG, "Retrying");
            getTask(this, mEmail, SCOPE).execute();
            return;
        }
        if (resultCode == RESULT_CANCELED) {
            show("User rejected authorization.");
            return;
        }
        show("Unknown error, click the button again");
    }

    /**
     * Called by button in the layout
     */
    public void greetTheUser(View view) {
        getUsername();

    }


    private void savePreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private String loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = sharedPreferences.getString("email", "EmailStuff");
        return name;
    }

    /**
     * Attempt to get the user name. If the email address isn't known yet,
     * then call pickUserAccount() method so the user can pick an account.
     */

    private void getUsername() {
        if (mEmail == null) {
            pickUserAccount();
        } else {
            if (isDeviceOnline()) {
                getTask(MainActivity.this, mEmail, SCOPE).execute();
            } else {
                Toast.makeText(this, "No network connection available", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * Starts an activity in Google Play Services so the user can pick an account
     */
    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    /**
     * Checks whether the device currently has a network connection
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }


    /**
     * This method is a hook for background threads and async tasks that need to update the UI.
     * It does this by launching a runnable under the UI thread.
     */
    public void show(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mOut.setText(message);
            }
        });
    }


    public void list(final ArrayList<String> l) {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, l);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lView.setAdapter(arrayAdapter);

            }
        });
    }

    public void showSpinner() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner.setVisibility(View.VISIBLE);
            }
        });
    }

    public void hideSpinner() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner.setVisibility(View.GONE);
            }
        });
    }

    public void setItemListener(final ArrayList<String> b, final ArrayList<String> s, /*, final ArrayList<String> fromList*/ArrayList<String> author, ArrayList<String> dateTimeList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Intent i = new Intent(getApplicationContext(), com.example.tensorflowtrial.InfoActivity.class);
                        i.putExtra("body", b.get(position));
                        i.putExtra("subject", s.get(position));
                        i.putExtra("from", fList.get(position));
                        i.putExtra("date", dateList.get(position));
                        // shreeya - PUT DATE, FROM TO , ETC HERE!!!!!!!!!
                        startActivity(i);
                    }
                });
            }
        });
    }

    /**
     * This method is a hook for background threads and async tasks that need to provide the
     * user a response UI when an exception occurs.
     */

    public void handleException(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    // The Google Play services APK is old, disabled, or not present.
                    // Show a dialog created by Google Play services that allows
                    // the user to update the APK
                    int statusCode = ((GooglePlayServicesAvailabilityException) e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            MainActivity.this,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    Intent intent = ((UserRecoverableAuthException) e).getIntent();
                    startActivityForResult(intent,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }

    /**
     * Note: This approach is for demo purposes only. Clients would normally not get tokens in the
     * background from a Foreground activity.
     */
    private com.example.tensorflowtrial.GetNameTask getTask(
            MainActivity activity, String email, String scope) {

        return new com.example.tensorflowtrial.GetNameTask(activity, email, scope);

    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

    public class SendingPrioTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                Log.d("I am Started"," ");
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                BufferedReader bfReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String value = bfReader.readLine();
                priorityResult = value;
                System.out.println("VALUE PRIO RECEIVED\n");
//                System.out.println(value);
                System.out.println(priorityResult);
                bfReader.close();



            } catch (Exception e) {
                System.out.println(e);
            }

            return null;
        }

    }

    public class SendingSpamTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                BufferedReader bfReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String valueSpam = bfReader.readLine();
                spamResultstring = valueSpam;
                System.out.println("VALUE SPAM RECEIVED\n");
                System.out.println(valueSpam);
                bfReader.close();


            } catch (Exception e) {
                System.out.println(e);
            }

            return null;
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {

            case R.id.sendToPriority:
                sendPrio.performClick();
                Toast.makeText(MainActivity.this, "sending prio data to flask...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sendToSpam:
                sendSpam.performClick();
                Toast.makeText(MainActivity.this, "sending spam data to flask...", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}
