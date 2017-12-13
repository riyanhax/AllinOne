package com.parasme.swopinfo.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.daimajia.numberprogressbar.NumberProgressBar;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.FileSelectionActivity;
import com.parasme.swopinfo.adapter.FeedAdapter;
import com.parasme.swopinfo.adapter.UploadAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.model.Feed;
import com.parasme.swopinfo.webservice.Progress;
import com.parasme.swopinfo.webservice.ProgressListener;
import com.parasme.swopinfo.webservice.WebServiceListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okio.BufferedSink;
import okio.Okio;


/**
 * Created by :- Mukesh Kumawat on 24-Oct-16.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */
public class Utils {

    private String TAG = getClass().getName();
    private Activity activity;
    private String date;
    public static MoveFileHelper moveFileHelper;
    public static FolderCreateHelper folderCreateHelper;
    public static URLPreview urlPreview;
    private String title, description, pageurl, thumburl;
    public static boolean isPreviewLoading = false;
    private static final String URL_REGEX = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
    private static final Pattern PATTERN_URL = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE);
    private static final int DETAILS_MAX_LENGTH = 100;

    public Utils(Activity activity){
        this.activity=activity;
    }

    public ArrayList<String> getCountryNames() {
        ArrayList<String> countryNameList=new ArrayList<>();
        String[] countries =  activity.getResources().getStringArray(R.array.countries);
        for (int i = 0; i < countries.length; i++) {
            String[] country = countries[i].split(",");
            countryNameList.add(country[2]);
        }
        return countryNameList;
    }

    public Object[] loadCountryDialog(ArrayList<String> countryNameList) {
        Dialog dialogCountry= new Dialog(activity);

        dialogCountry.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialogCountry.setContentView(R.layout.dialog_country);
        ListView listViewCountry=(ListView) dialogCountry.findViewById(R.id.list_contry);

        listViewCountry.setAdapter(setAdapter(countryNameList));

        return new Object[] {dialogCountry,listViewCountry};
    }


    private ArrayAdapter<String> setAdapter(final ArrayList<String> arrayList){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, arrayList){

            @Override
            public View getView(final int position, View convertView, ViewGroup paren) {
                View view = super.getView(position, convertView, paren);
                TextView textView=(TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));
                textView.setGravity(Gravity.CENTER);
                String text=arrayList.get(position);
                if(text.contains(":")){
                    text=text.split(":")[1];
                    textView.setText(text);
                }
                return view;
            }
        };

        return adapter;
    }

    public void selectDate(final EditText editText) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        editText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }


    public void sharePostWithImage(String packageName, String name, Bitmap bitmap){
        try{
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setPackage(packageName);
            intent.putExtra(Intent.EXTRA_TEXT, "Hey, View/Download This");
            String path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, "", null);
            Uri screenshotUri = Uri.parse(path);
            intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            intent.setType("image/*");
            activity.startActivity(intent);
        }
        catch (ActivityNotFoundException e){
            MyApplication.alertDialog(activity,"No compatible Application found for "+name,"App Not Found");
        }
    }

    public void sharePost(String packageName, String name, String url){
        try{
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setPackage(packageName);
            intent.setType("text/plain");
            Log.e("URLLL",url);
            intent.putExtra(Intent.EXTRA_TEXT, url);
            activity.startActivity(intent);
        }
        catch (ActivityNotFoundException e){
            MyApplication.alertDialog(activity,"No compatible Application found for "+name,"App Not Found");
        }
    }

    public void shareOnMails(String url){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, "Share");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
    }

    public String[] getFileNameAndType(String path){
        String type="";
        String filename = path.substring(path.lastIndexOf("/")+1);
        Log.e("FILENAME",filename);
        String extension = MimeTypeMap.getFileExtensionFromUrl((Uri.fromFile(new File(path))).toString());

        if (extension != null)
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        else {
            String fileType = filename.substring(filename.lastIndexOf(".")+1);
            type = "file/"+fileType;
        }
        return new String[]{filename,type};
    }

    public static String createThumbURL(String userid, String folderName, String thumbName, String fileType, int companyId) {
        String thumbURL;

        if(!thumbName.equals("")){
            if(fileType.equals("videourl"))     // If filetype is videourl then thumname will be thumb url
                thumbURL = thumbName;

            else {
                thumbURL = AppConstants.URL_DOMAIN+"upload/user" + userid + (folderName.equals("/") ? "" : "/" + folderName.replace(" ", "%20")) + "/" + thumbName;
                if(companyId!=0)
                    thumbURL = AppConstants.URL_DOMAIN+"upload/company" + companyId + (folderName.equals("/") ? "" : "/" + folderName.replace(" ", "%20")) + "/" + thumbName;
            }
        }
        else
            thumbURL = AppConstants.URL_DOMAIN+"abc.jpg";

        return thumbURL;
    }


    public static String createFileURL(JSONObject fileObject) {
        String folderName="";
        int companyId = fileObject.optInt("companyid");

        // Extracting folder name
        if(!fileObject.optString("foldername").equals("/"))
            folderName = fileObject.optString("foldername").replace(" ","%20")+"/";

        String fileURL;
        if(companyId==0)
            fileURL = AppConstants.URL_DOMAIN+"upload/user"+fileObject.optInt("userid")+"/"+folderName+fileObject.optString("filename");
        else
            fileURL = AppConstants.URL_DOMAIN+"upload/company"+companyId+"/"+folderName+fileObject.optString("filename");

        return fileURL;
    }

    public static String createAudioFileURL(JSONObject fileObject) {
        String folderName="";
        int companyId = fileObject.optInt("companyid");

        // Extracting folder name
        if(!fileObject.optString("foldername").equals("/"))
            folderName = fileObject.optString("foldername").replace(" ","%20")+"/";

        String fileURL;
        if(companyId==0)
            fileURL = AppConstants.URL_DOMAIN+"upload/user"+fileObject.optInt("userid")+"/"+folderName+fileObject.optString("realfilename");
        else
            fileURL = AppConstants.URL_DOMAIN+"upload/company"+companyId+"/"+folderName+fileObject.optString("realfilename");

        return fileURL;
    }


    public static ArrayList<Object> loadDownloadDialog(Activity activity, String title){
        ArrayList<Object> objectArrayList = new ArrayList<>();
        Dialog dialog= new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_number_progress);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setCancelable(false);

        NumberProgressBar numberProgressBar = (NumberProgressBar) dialog.findViewById(R.id.numberProgress);
        TextView textTitle = (TextView) dialog.findViewById(R.id.textTitle);
        textTitle.setText(title);
        objectArrayList.add(dialog);
        objectArrayList.add(numberProgressBar);
        return objectArrayList;
    }

    public static Dialog loadCommentDialog(Activity activity){
        Dialog dialog= new Dialog(activity,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_comments);
        dialog.setCancelable(false);
        return dialog;
    }

    public static Dialog loadRegisterDialog(Activity activity){
        Dialog dialog= new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_register);
        dialog.setCancelable(true);
        return dialog;
    }

    public static Dialog loadShareDialog(Activity activity){
        Dialog dialog= new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_share);
        dialog.setCancelable(false);
        return dialog;
    }

    public static Dialog loadBookmarkDialog(Activity activity){
        Dialog dialog= new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_bookmark);
        dialog.setCancelable(false);
        return dialog;
    }

    public static Dialog loadFolderDialog(Activity activity){
        Dialog dialog= new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_folder);
        dialog.setCancelable(false);
        return dialog;
    }

    public static Object[] loadMoveDialog(Activity activity, ArrayList<String> fileIds){
        Dialog dialog= new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_file_move);
        dialog.setCancelable(false);

        TextView textMove = (TextView) dialog.findViewById(R.id.textMove);
        GridView gridFolders = (GridView) dialog.findViewById(R.id.gridFolders);
        Button btnMove = (Button) dialog.findViewById(R.id.btnMove);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        return new Object[]{dialog,textMove,gridFolders,btnCancel,btnMove,fileIds};
    }

    public static Object[] loadFolderCreateDialog(Activity activity){
        Dialog dialog= new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_folder_create);
        dialog.setCancelable(false);

        EditText editFolderName = (EditText) dialog.findViewById(R.id.editFolderName);
        Button btnCreate = (Button) dialog.findViewById(R.id.btnCreate);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        return new Object[]{dialog,editFolderName,btnCancel,btnCreate};
    }


    public static Dialog loadGroupShareDialog(Activity activity){
        Dialog dialog= new Dialog(activity,android.R.style.Theme_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_groups);
        dialog.setCancelable(true);
        return dialog;
    }



    public static String getRealPathFromURI(Context context, Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        }
        catch (IllegalArgumentException e){return null;}
        finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }





    public static View getViewByPosition(int pos, GridView gridView) {
        final int firstListItemPosition = gridView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + gridView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return gridView.getAdapter().getView(pos, null, gridView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return gridView.getChildAt(childIndex);
        }
    }

    public static void setGridMultiSelection(final AppCompatActivity mActivity, final GridView gridUploads, final UploadAdapter adapter) {
        gridUploads.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridUploads.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                adapter.removeSelection();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.move_action_mode, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.move_mode:
                        ArrayList<String> fileIds = adapter.getSelectedFileIds(gridUploads);
                        mode.finish();
                        Object object[] = loadMoveDialog(mActivity,fileIds);
                        moveFileHelper.onFileMoveButtonClicked(object);
                        return true;
                    case R.id.create_mode:
                        Object object1[] = loadFolderCreateDialog(mActivity);
                        folderCreateHelper.onFolderCreate(object1);
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                                                  boolean checked) {

                LinearLayout linearLayout = (LinearLayout) getViewByPosition(position,gridUploads);
                int checkedCount = gridUploads.getCheckedItemCount();
                mode.setTitle(checkedCount + " selected");
                adapter.toggleSelection(position,linearLayout);
            }
        });

    }


    public interface MoveFileHelper{
        public void onFileMoveButtonClicked(Object[] object);
    }

    public interface FolderCreateHelper{
        public void onFolderCreate(Object[] object);
    }

    public static void openDialerActivity(AppCompatActivity activity, String telephone){
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+telephone));
        try{
            activity.startActivity(callIntent);
        }

        catch (android.content.ActivityNotFoundException ex){
            Toast.makeText(activity,"Something is not Right",Toast.LENGTH_SHORT).show();
        }

    }

    public static void openEmailClients(AppCompatActivity activity, String emaiId){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emaiId});
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
        else
            MyApplication.alertDialog(activity,"No application found to handle mail client", "App Not Found");
    }


    public void getMetaDataFromURL(AsyncResponse asyncResponse, String url){
        if(!url.contains("http"))
            url = "http://"+url;
        Log.e(TAG, "getMetaDataFromURL: "+url );

        new AsyncJSOUPClass(asyncResponse).execute((url));
    }

    public class AsyncJSOUPClass extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        public AsyncResponse asyncResponse = null;

        public AsyncJSOUPClass(AsyncResponse asyncResponse){
            this.asyncResponse = asyncResponse;
        }

        @Override
        protected String doInBackground(String... params) {
            String data="null";
            try {
/*
                Document doc = Jsoup.connect("http://www.brazzers.com").get();

                String keywords = doc.select("meta[name=keywords]").first().attr("content");
                System.out.println("Meta keyword : " + keywords);
                String description = doc.select("meta[name=description]").get(0).attr("content");
                System.out.println("Meta description : " + description);
*/

                Document document = Jsoup.connect(params[0]).get();
                Element e=document.head().select("link[href~=.*\\.ico]").first();
                String url=e.attr("href");
                data = document.title()+","+url;

            }catch (Exception e){}
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
            Log.e("RESULT", "onPostExecute: "+result );
            progressDialog.dismiss();
            asyncResponse.processFinish(result);
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Fetching Title");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }


    public void selectDialog(String [] items, final FileSelectionActivity.FilePicker filePicker, final ImagePicker.Picker imagePicker){
        Dialog dialog;
        Log.e(TAG,"get Image");
        ArrayAdapter<String> adapter	= new ArrayAdapter<String>(activity, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder		= new AlertDialog.Builder(activity);

        // Defining Alert Dialog box to pop up options Either Take from Camera or Select from Gallery

        builder.setTitle("Choose image from");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener()

        {
            public void onClick(DialogInterface dialog, int item)

            { //pick from camera
                if (item == 0)

                {
                    ImagePicker.picker=imagePicker;
                    Intent intent = new Intent(activity, ImagePicker.class);
                        intent.putExtra("selectType",0);
                        activity.startActivity(intent);

                } else if(item ==1){
//                    Intent intent = new Intent(activity, ImagePicker.class);
//                    intent.putExtra("selectType",1);
//                    activity.startActivity(intent);
                     pickImage();
                }
                else{
                    FileSelectionActivity.setOnFilePickListener(filePicker);
                    activity.startActivity(new Intent(activity, FileSelectionActivity.class));
                }
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    private void pickImage() {
        GalleryConfig config = new GalleryConfig.Build()
                .singlePhoto(false)
                .hintOfPick("select images")
                .limitPickPhoto(15)
                .filterMimeTypes(new String[]{"image/*"})
                .build();
        GalleryActivity.openActivity(activity , 15, config);
    }

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public static String fixExif(String imagePath){
        try {
            File f = new File(imagePath);
            ExifInterface exif = new ExifInterface(f.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;

            // If image is normal then return path as it is
            if (orientation == 0)
                return imagePath;

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }

            Matrix mat = new Matrix();
            mat.postRotate(angle);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;

            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f),
                    null, options);
            Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                    bmp.getHeight(), mat, true);
            ByteArrayOutputStream outstudentstreamOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    outstudentstreamOutputStream);

            return saveFileFromBitmap(bitmap,f,false);

        } catch (IOException e) {
            Log.e("TAG", "-- Error in setting image");
        } catch (OutOfMemoryError oom) {
            Log.e("TAG", "-- OOM Error in setting image");
        }

        return "null";
    }

    //    Saving file to the mobile internal memory
     public static String saveFileFromBitmap(Bitmap sourceUri, File destination, boolean isFromShare) {
//        if (destination.exists()) destination.delete();
        try {
            FileOutputStream out = new FileOutputStream(destination);
            sourceUri.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            if(isFromShare) {
                Log.e("Fixing exif","ok");
                fixExif(destination.getPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
         return destination.toString();
    }

    public static String saveFileFromURI(URI sourceuri, String extension)
    {
        String sourceFilename= sourceuri.getPath();
        String destinationFilename = android.os.Environment.getExternalStorageDirectory().getPath()+File.separatorChar+"abc."+extension;

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFilename));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {
            Log.e("e1",e.toString());

        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                Log.e("e2",e.toString());
            }
        }
        return destinationFilename;
    }



    public static void downloadFile(final String fileURL, final Activity activity) throws Exception {
        Log.e("1111", "downloadFile: " );
        ArrayList<Object> objects = Utils.loadDownloadDialog(activity,"Downloading");
        final Dialog dialog = (Dialog) objects.get(0);
        final NumberProgressBar numberProgressBar = (NumberProgressBar) objects.get(1);
        dialog.show();

        try {
            final Progress pr = new Progress();
            pr.progressListener = new ProgressListener() {

                @Override
                public void update(final long bytesRead, final long contentLength, boolean done) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final long progress = ((100 * bytesRead) / contentLength);
                            Log.e("aaaa", "update: "+progress+"%" );
                            numberProgressBar.setProgress((int) progress);
                        }
                    });
                }
            };

            pr.webServiceListener = new WebServiceListener() {
                @Override
                public void onResponse(String response) {
                    Log.e("check", "onResponse: "+response );
                    String root = Environment.getExternalStorageDirectory().toString();
                    File myDir = new File(root + "/SwopInfo/");
                    myDir.mkdirs();
                    String filename = fileURL.substring(fileURL.lastIndexOf("/")+1);


/*
                    File file = new File(myDir, fname);
                    if (file.exists()) file.delete();

                    InputStream is = Progress.finalResponse.body().byteStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    FileOutputStream fos = null;
*/
                    try {
                        File downloadedFile = new File(myDir, filename);
                        BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
                        sink.writeAll(Progress.finalResponse.body().source());
                        sink.close();
                    } catch (Exception e) {

                        e.printStackTrace();
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            MyApplication.alertDialog(activity,"File Successfully Saved in 'SwopInfo' folder under Internal Storage","File Download");
                        }
                    });

                }
            };

            pr.main(fileURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface URLPreview{
        void onDataReceived(String title, String pageURL, String description, String thumbURL);
    }


    public static String copyInputStreamToFile(InputStream in, File file) {
        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            // Ensure that the InputStreams are closed even if there's an exception.
            try {
                if ( out != null ) {
                    out.close();
                }

                // If you want to close the "in" InputStream yourself then remove this
                // from here but ensure that you close it yourself eventually.
                in.close();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        return file.getPath();
    }

    public static Bitmap fixExifRotationFlip(Bitmap bitmap, String path) throws IOException {
        ExifInterface ei = new ExifInterface(path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static int generateRandomNumber(){
        Random r = new Random();
        int Low = 10;
        int High = 10000;
        int Result = r.nextInt(High-Low) + Low;
        return Result;
    }
}