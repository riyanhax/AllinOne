package com.parasme.swopinfo.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.parasme.swopinfo.model.Feed;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    public static final String DATABASE_NAME = "SwopInfo";

    // Table Cart
    public static final String TABLE_URL_PREVIEW = "tbl_url_preview";
    public static final String ID= "id";
    public static final String PREVIEW_FEED_ID = "feed_id";
    public static final String PREVIEW_TITLE = "title";
    public static final String PREVIEW_DESCRIPTION = "description";
    public static final String PREVIEW_URL = "url";
    public static final String PREVIEW_IMAGE = "image";

    private Context context;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Creating table Group Counter
        String createTableFavProducts = "CREATE TABLE "+ TABLE_URL_PREVIEW +"("
                + ID +" INTEGER PRIMARY KEY   AUTOINCREMENT,"
                + PREVIEW_FEED_ID +" INTEGER UNIQUE,"
                + PREVIEW_TITLE +" TEXT,"
                + PREVIEW_DESCRIPTION +" TEXT,"
                + PREVIEW_URL +" TEXT,"
                + PREVIEW_IMAGE +" TEXT);";


        db.execSQL(createTableFavProducts);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.w("MyAppTag","Updating database from version " + oldVersion + " to "
                + newVersion + " .Existing data will be lost.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_URL_PREVIEW);
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


/*
    public ArrayList<Feed> getAllCartItems() {
        db = this.getReadableDatabase();
        ArrayList<Item> productArrayList= new ArrayList<>();
        String query = "select * from "+ TABLE_URL_PREVIEW;
        Cursor c= db.rawQuery(query, null);

        if(c.moveToFirst())
        {
            do {
                Item item =new Item();
                item.setCartItemId(c.getInt(0));
                item.setSubCategoryId(c.getInt(1));
                item.setSubCategoryName(c.getString(2));
                item.setPrice(c.getString(3));
                item.setTotalPrice(c.getString(4));
                item.setQuantity(c.getInt(5));
                productArrayList.add(item);


            }while (c.moveToNext());
        }

        db.close();
        return productArrayList;
    }
*/

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

}