package com.parasme.swopinfo.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;
import com.parasme.swopinfo.model.Feed;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    public static final String DATABASE_NAME = "SwopInfo";

    // Table URL Preview
    public static final String TABLE_URL_PREVIEW = "tbl_url_preview";
    public static final String ID= "id";
    public static final String PREVIEW_FEED_ID = "feed_id";
    public static final String PREVIEW_TITLE = "title";
    public static final String PREVIEW_DESCRIPTION = "description";
    public static final String PREVIEW_URL = "url";
    public static final String PREVIEW_IMAGE = "image";


    // Table File Thumbs
    public static final String TABLE_FILE_THUMBS = "tbl_file_thumbs";
    public static final String THUMB_ID = "thumb_id";
    public static final String THUMB_URL = "thumb_url";
    public static final String THUMB_PATH = "thumb_path";
    public static final String THUMB_CREATE_TIME = "thumb_time";

    private Context context;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 2);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Creating table url preview
        String createTableUrlPreview = "CREATE TABLE "+ TABLE_URL_PREVIEW +"("
                + ID +" INTEGER PRIMARY KEY   AUTOINCREMENT,"
                + PREVIEW_FEED_ID +" INTEGER UNIQUE,"
                + PREVIEW_TITLE +" TEXT,"
                + PREVIEW_DESCRIPTION +" TEXT,"
                + PREVIEW_URL +" TEXT,"
                + PREVIEW_IMAGE +" TEXT);";

        // Creating table url preview
        String createTableFileThumbs = "CREATE TABLE "+ TABLE_FILE_THUMBS +"("
                + THUMB_ID +" INTEGER PRIMARY KEY   AUTOINCREMENT,"
                + THUMB_URL +" TEXT,"
                + THUMB_PATH +" TEXT,"
                + THUMB_CREATE_TIME +" TEXT);";


        db.execSQL(createTableUrlPreview);
        db.execSQL(createTableFileThumbs);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.e("MyAppTag","Updating database from version " + oldVersion + " to "
                + newVersion + " .Existing data will be lost.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_URL_PREVIEW);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILE_THUMBS);

        onCreate(db);
    }


    public long addURLPreview(int fileId, String title, String description, String url, String image) {
        long row=0;
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PREVIEW_FEED_ID, fileId);
        values.put(PREVIEW_TITLE, title);
        values.put(PREVIEW_DESCRIPTION, description);
        values.put(PREVIEW_URL, url);
        values.put(PREVIEW_IMAGE, image);
        row = db.insert(TABLE_URL_PREVIEW, null, values);

        return  row;  }


    public long addFileThumb(String url, String filePath) {
        long row=0;
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(THUMB_URL, url);
        values.put(THUMB_PATH, filePath);
        values.put(THUMB_CREATE_TIME, String.valueOf(Calendar.getInstance().getTimeInMillis()));
        row = db.insert(TABLE_FILE_THUMBS, null, values);

        return  row;  }

    public File getThumbFileFromURL(String url){
        db = this.getReadableDatabase();
        String query = "select * from "+TABLE_FILE_THUMBS+" where "+THUMB_URL + "='"+url + "'";
        Log.e("query",query);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            String path = cursor.getString(2);
            return new File(path);
        }
        else
            return null;
    }

    public Feed getURLPreviewData(int fileId){
        db = this.getReadableDatabase();
        String query = "select * from "+TABLE_URL_PREVIEW +" where "+PREVIEW_FEED_ID + "="+fileId;
        Log.e("query",query);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            Feed feed = new Feed();
            feed.setUserId(cursor.getInt(0));
            feed.setFileId(cursor.getInt(1));
            feed.setPreviewTitle(cursor.getString(2));
            feed.setPreviewDescription(cursor.getString(3));
            feed.setPreviewPageURL(cursor.getString(4));
            feed.setPreviewThumbURL(cursor.getString(5));
            db.close();
            return  feed;
        }
        else
            return null;
    }

/*
    public void updateProductQty(int productCartId,int newQty){
        db=this.getWritableDatabase();
        String query = "UPDATE " + TABLE_URL_PREVIEW + " SET " + URL_IMAGE +" = "+newQty+" where "+ID+" = "+productCartId;
        db.execSQL(query);
    }
*/

    public Map<Integer, String> getAllThumbs() {
        db = getReadableDatabase();
        Map<Integer, String> thumbMap = new HashMap<Integer, String>();
        String query = "select * from "+ TABLE_FILE_THUMBS;
        Cursor c= db.rawQuery(query, null);

        if(c.moveToFirst())
        {
            do {
                thumbMap.put(c.getInt(0), c.getString(3));
            }while (c.moveToNext());
        }

        db.close();
        return thumbMap;
    }

    public File getThumbFileFromId(int id){
        db = this.getReadableDatabase();
        String query = "select * from "+TABLE_FILE_THUMBS+" where "+THUMB_ID + "="+id;
        Log.e("query",query);
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.moveToFirst()){
            return new File(cursor.getString(2));
        }
        else
            return null;
    }

    public void deleteThumbRecord(int id){
        db = this.getWritableDatabase();
        String query="delete from "+TABLE_FILE_THUMBS+" where "+THUMB_ID+"="+id;
        db.execSQL(query);
        db.close();
    }
}