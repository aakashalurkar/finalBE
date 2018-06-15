package com.example.tensorflowtrial;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "emails.db";
        public Context contextnew;
        // Books table name
    private static final String TABLE_BOOKS = "emails";
    private static final String DBPATH="/data/user/0/com.example.tensorflowtrial/databases/emails.db";


    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_SUBJECT = "subject";
    private static final String KEY_BODY = "body";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_DATETIME="dateTime";
    private static final String KEY_BODYHTML="bodyHTML";
    private static final String KEY_URGENCY = "urgency";

    private static final String[] COLUMNS = {KEY_ID,KEY_SUBJECT,KEY_BODY,KEY_AUTHOR,KEY_DATETIME,KEY_BODYHTML,KEY_URGENCY};

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        String DBPath=context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_BOOK_TABLE = "CREATE TABLE emails ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "subject TEXT, "+
                "body TEXT, " +
                "author TEXT, " +
                "dateTime TEXT, " +
                "bodyHTML TEXT, " +
                "urgency INT)";

        // create books table
        db.execSQL(CREATE_BOOK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS books");


        // create fresh books table
        this.onCreate(db);
    }


    public void addBook(Email email){
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_SUBJECT, email.getSubject());
        values.put(KEY_BODY, email.getBody());
        values.put(KEY_AUTHOR, email.getAuthor());
        values.put(KEY_DATETIME,email.getDateTime());
        values.put(KEY_BODYHTML,email.getBodyHTML());
        values.put(KEY_URGENCY, email.getUrgency());


        // 3. insert
        db.insert(TABLE_BOOKS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }




    public Email getBook(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_BOOKS, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        Email email = new Email();
        email.setId(Integer.parseInt(cursor.getString(0)));
        email.setSubject(cursor.getString(1));
        email.setBody(cursor.getString(2));
        email.setAuthor(cursor.getString(3));
        email.setDateTime(cursor.getString(4));
        email.setBodyHTML(cursor.getString(5));
        email.setUrgency(cursor.getInt(6));
        // 5. return book
        return email;
    }

    public List<Email> getAllBooks() {
        List<Email> emails = new LinkedList<Email>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_BOOKS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Email email = null;
        if (cursor.moveToFirst()) {
            do {
                email = new Email();
                email.setId(Integer.parseInt(cursor.getString(0)));
                email.setSubject(cursor.getString(1));
                email.setBody(cursor.getString(2));
                email.setAuthor(cursor.getString(3));
                email.setDateTime(cursor.getString(4));
                email.setBodyHTML(cursor.getString(5));
                email.setUrgency(cursor.getInt(6));

                // Add book to books
                emails.add(email);


            } while (cursor.moveToNext());
        }


        return emails;
    }

    public void deleteEverything(){
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            db.execSQL("DELETE FROM " + TABLE_BOOKS);
        }
    }




}