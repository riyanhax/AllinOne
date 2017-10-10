package com.parasme.swopinfo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.fragment.FragmentAdd;
import com.parasme.swopinfo.fragment.FragmentUser;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.urlpreview.LinkPreviewCallback;
import com.parasme.swopinfo.urlpreview.SourceContent;
import com.parasme.swopinfo.urlpreview.TextCrawler;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.jsoup.helper.StringUtil;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.kaelaela.opengraphview.OnLoadListener;
import me.kaelaela.opengraphview.OpenGraphView;
import okhttp3.Credentials;
import okhttp3.FormBody;

import static com.parasme.swopinfo.adapter.FeedAdapter.context;
import static com.parasme.swopinfo.helper.Utils.fixExif;

/**
 * Created by Mukesh Kumawat on 22-Sep-16.
 * Designation Android Senior Developer
 * Organization Parasme Software And Technology
 * Email mukeshkmtskr@gmail.com
 * Mobile +917737556190
 */
@EActivity(R.layout.activity_splash)
public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 4000;
    private ArrayList<File> fileArrayList=new ArrayList<>();
    private String swopText = "";
    String arr[];
    private boolean isFilesFetched;
    private OpenGraphView openGraphView;
    private ProgressBar progressBar;
    private TextView textPreviewTitle;
    private TextView textPreviewDescription;
    private TextView textPreviewURL;
    private ImageView imagePreviewThumb;
    DisplayImageOptions optionsFile = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.document_gray)
            .showImageOnFail(R.drawable.document_gray)
            .cacheInMemory(false)
            .cacheOnDisk(false)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("ppppP",getCurrentDate()+"__"+getCurrentTime());
        /*
        *         if (!TextUtils.isEmpty(clientGroupId)) {
            channel = ChannelService.getInstance(fragmentActivity).getChannelByClientGroupId(clientGroupId);
            if (channel == null) {
                return;
            }
*/
        String credential = Credentials.basic("gavin@swopinfo.com", "gavinsimoen01");
        Log.e("cRED",credential);

        String str = "https://commonsbow_Rose_(3366550029)jpg";
        str = str.substring(str.lastIndexOf(".")+1, str.length());
        Log.e("str",str);

        Log.e("playyer",SharedPreferenceUtility.getInstance().get(AppConstants.PREF_PLAYER_ID,""));
        String appPath = getApplicationContext().getFilesDir().getAbsolutePath();
        Log.e("HCECK", "onCreate: "+appPath );
        //Thread.setDefaultUncaughtExceptionHandler(new Catcho.Builder(this).recipients("parasme.mukesh@gmail.com").build());

/*
        Intent intent = new AppInviteInvitation
                .IntentBuilder("Send Invitations for SwopInfo app")
                // Ensure valid length for any message used before calling otherwise this will throw
                // an IllegalArgumentException if greater than MAX_MESSAGE_LENGTH.
                .setMessage("Try out SwopInfo app now")
                //.setDeepLink(Uri.parse("https://swopinfo.com"))
                .setCallToActionText("Find data")
                //.setEmailSubject("Hello")
                //.setEmailHtmlContent("<p>Content</p>")
                .build();
        startActivityForResult(intent, 110);
*/
    }

    @AfterViews
    protected void init(){
        Log.e("UUUUUU","aaa");

//        File imgFile = new  File(getApplicationContext().getFilesDir().getAbsolutePath()+"/avatars/avatar1.png");
//        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//
//        ((ImageView) findViewById(R.id.image)).setImageBitmap(myBitmap);




        if(getIntent().getAction().equals(Intent.ACTION_SEND) || getIntent().getAction().equals(Intent.ACTION_SEND_MULTIPLE)){
            initAndLoadDialog();
        }
        else if(getIntent().getAction().equals(Intent.ACTION_VIEW)){
            Intent intent = new Intent(SplashActivity.this,MainActivity_.class);
            intent.putExtra("actionView",getIntent().getData().toString());
            startActivity(intent);
            finish();
        }
        else{
            if (!((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                builder.setTitle("Location Setting")
                        .setMessage("GPS or Location is not enabled. Please enable from setting")
                        .setCancelable(false)
                        .setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent,1001);
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }

            else
            {
                startNextActivity();
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("ok","ok");
        if (requestCode == 1001) {
            Log.e("ok","ok11");
            if (((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.e("ok","ok222");
                startNextActivity();
            }
            else{
                Log.e("ok","ok4444");
                Toast.makeText(SplashActivity.this, "Could not fetch location",Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(SplashActivity.this, SplashActivity_.class));
                finish();
            }
        }
    }
    private void startNextActivity() {
        if(getSharedPreferences("swopinfo", Context.MODE_PRIVATE).getBoolean(AppConstants.PREF_LOGIN, false)){
            //if(SharedPreferenceUtility.getInstance().get(AppConstants.PREF_LOGIN,false)){
            AppConstants.AUTH_TOKEN = SharedPreferenceUtility.getInstance().get(AppConstants.PREF_AUTH_TOKEN)+"";
            String userId=SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_ID)+"";
            AppConstants.USER_ID = userId;
            Log.e("splash", "getting details: "+true );

            //Check if intro screen has seen or not
            if(SharedPreferenceUtility.getInstance().get(AppConstants.PREF_INTRO,false))
                new FragmentUser().getUserDetails(AppConstants.URL_USER + SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_ID),true,SplashActivity.this);
            else
                loadDefaultSplash();
        }
        else
            loadDefaultSplash();

    }

    private void loadDefaultSplash() {
        Log.e("splash", "loadDefaultSplash: "+true );
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i=null;
                if(SharedPreferenceUtility.getInstance().get(AppConstants.PREF_LOGIN,false)){
                    if(SharedPreferenceUtility.getInstance().get(AppConstants.PREF_INTRO,false)){
                        i = new Intent(SplashActivity.this, MainActivity_.class);
                        i.putExtra("startUserWrapper",false);
                    }
                    else
                        i = new Intent(SplashActivity.this, FlowActivity.class);
                }
                else
                    i = new Intent(SplashActivity.this, LoginActivity_.class);

                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }


    private void initAndLoadDialog(){
        final Dialog dialog = Utils.loadShareDialog(SplashActivity.this);

        Button btnNewsfeed = (Button) dialog.findViewById(R.id.btnNewsfeed);
        Button btnFolder = (Button) dialog.findViewById(R.id.btnFolder);
        Button btnCompany = (Button) dialog.findViewById(R.id.btnCompany);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        TextView textShare = (TextView) dialog.findViewById(R.id.textShare);
        openGraphView = (OpenGraphView) dialog.findViewById(R.id.og_view);
        progressBar = (ProgressBar) dialog.findViewById(R.id.progress_bar);

        if(SharedPreferenceUtility.getInstance().get(AppConstants.PREF_IS_BUSINESS))
            btnCompany.setVisibility(View.VISIBLE);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.e("HELLO", String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        }
        //Log.e("bbb",bundle.get(Intent.EXTRA_STREAM)+"");
        String str = bundle.get(Intent.EXTRA_STREAM)+"";


        // When sharing files
        if(!TextUtils.equals(str,"null"))
        {
            Log.e("DATAAA", "init: " + str);
            str = str.replace("[", "");
            str = str.replace("]", "");
            str = str.replaceAll(", ", ",");
            arr = str.split(",");
            Log.e("CHECK", "inita: " + arr[0]);
            Log.e("mydata", arr.length+"");


            if(arr.length<=15) {

                for (int i = 0; i < arr.length; i++) {
                    String path = "";
                    try {
                        File file = new File(Environment.getExternalStorageDirectory(),
                                "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                        File filef = new File(Environment.getExternalStorageDirectory(),
                                "tmpf_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                        Uri imageUri = Uri.parse(arr[i]);
                        String fileMimeType = getMimeTypeFromURI(Uri.parse(arr[i]));
                        //if(fileMimeType == null)
                        //    String extension = uri.substring(uri.lastIndexOf(".")+1, uri.length());

                        Log.e("type", fileMimeType);

                        if (fileMimeType.contains("image")) {
/*
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        String tempPath = Utils.saveFileFromBitmap(bitmap, file, false);
                        Bitmap exifBitmap = Utils.fixExifRotationFlip(bitmap,file.getAbsolutePath());
                        path = Utils.saveFileFromBitmap(exifBitmap, filef, false);
*/
                            Log.e("HELLO", "itsok");
                            String temp = Utils.getRealPathFromURI(SplashActivity.this, Uri.parse(arr[i]));
                            if (!StringUtil.isBlank(temp))
                                path = Utils.fixExif(Utils.getRealPathFromURI(SplashActivity.this, Uri.parse(arr[i])));
                            else {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                                path = Utils.saveFileFromBitmap(bitmap, filef, false);

                            }
                        } else {
                            String uri = arr[i];
                            String extension = uri.substring(uri.lastIndexOf(".") + 1, uri.length());
                            if (extension.equals(fileMimeType.split("/")[1])) {
                                Log.e("CHECK", "Ending with extension");
                                path = Utils.getRealPathFromURI(SplashActivity.this, Uri.parse(arr[i]));
                            } else {
                                Log.e("CHECK", "Ending without extension");
                                File file1 = new File(Environment.getExternalStorageDirectory(),
                                        "tmp_file" + String.valueOf(System.currentTimeMillis()) + "." + fileMimeType.split("/")[1]);
                                path = Utils.copyInputStreamToFile(getContentResolver().openInputStream(Uri.parse(arr[i])), file1);
                                //path = Utils.saveFileFromURI(temp, fileMimeType.split("/")[1]);
                            }
                        }
                    } catch (IOException e) {
                        Log.e("ERR", e.toString());
                    }

//                fileArrayList.add(new File(fixExif(path)));
                    fileArrayList.add(new File(path));
                }

                isFilesFetched = true;
                Log.e("CCC", "count:" + fileArrayList.size() + " " + fileArrayList.get(0).getPath());

            }
            else
                MyApplication.alertDialog(SplashActivity.this,"Can not share more than 15 files", "Share");
        }

        else{       // When sharing text urls from other apps to swopinfo
            if (bundle != null) {
                btnCompany.setVisibility(View.GONE);
                btnFolder.setVisibility(View.GONE);
                //swopText = bundle.getString(Intent.EXTRA_TEXT);
                Log.e("datadata",bundle.getString(Intent.EXTRA_TEXT));
                String url = extractUrls(bundle.getString(Intent.EXTRA_TEXT)).get(0);
                swopText = url;
                Log.e("URL",url);
                initPreview(url);
                textShare.setVisibility(View.GONE);
            }
        }




        final EditText editDescription = (EditText) dialog.findViewById(R.id.editDescription);

        btnNewsfeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFilesFetched) {
                    dialog.dismiss();
                    new FragmentAdd().uploadFile(SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_ID) + "", "0", "0", "", "FILE", "", editDescription.getText().toString(), "true", "true", "true", "true", "true", "", fileArrayList, SplashActivity.this, null, swopText, true);
                }
                else
                    Snackbar.make(findViewById(android.R.id.content),"Pleasw Wait, Getting Files",Snackbar.LENGTH_LONG).show();
            }
        });

        btnFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFilesFetched){
                    dialog.dismiss();
                    new FragmentAdd().uploadFile(SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_ID)+"","0","0","","FILE","",editDescription.getText().toString(),"false","true","true","true","true","",fileArrayList,SplashActivity.this,null,swopText,true);
                }
                else
                    Snackbar.make(findViewById(android.R.id.content),"Pleasw Wait, Getting Files",Snackbar.LENGTH_LONG).show();
            }
        });

        btnCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFilesFetched){
                    dialog.dismiss();
                    Log.e("COMPANY",SharedPreferenceUtility.getInstance().get(AppConstants.PREF_COMPANY_ID)+"");
                    new FragmentAdd().uploadFile(SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_ID)+"",SharedPreferenceUtility.getInstance().get(AppConstants.PREF_COMPANY_ID)+"","0","","FILE","",editDescription.getText().toString(),"true","true","true","true","true","",fileArrayList,SplashActivity.this,null,swopText,true);
                }
                else
                    Snackbar.make(findViewById(android.R.id.content),"Please Wait, Getting Files",Snackbar.LENGTH_LONG).show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SplashActivity.this,MainActivity_.class));
                finish();
            }
        });

        if(arr != null) {
            if (arr.length <= 15)
                dialog.show();
        }
        else
            dialog.show();
    }


    private String getMimeTypeFromURI(Uri uri) {
//        ContentResolver cr = SplashActivity.this.getContentResolver();
//        return cr.getType(Uri.parse(uri));


        String extension;
        String mimeType;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(SplashActivity.this.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }

        mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        return mimeType;
    }


    public static List<String> extractUrls(String text)
    {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }
    public void initPreview(String url) {
        if(!url.contains("24.com")) {
            openGraphView.setVisibility(View.VISIBLE);
            openGraphView.clear();
            if (url.startsWith("http://"))
                url = url.replace("http://", "https://");
            openGraphView.loadFrom(url);
            isFilesFetched = true;
        }
        else{
            TextCrawler textCrawler = new TextCrawler();
            textPreviewTitle = (TextView) openGraphView.findViewById(R.id.og_title);
            textPreviewDescription = (TextView) openGraphView.findViewById(R.id.og_description);
            textPreviewURL = (TextView) openGraphView.findViewById(R.id.og_url);
            imagePreviewThumb = (ImageView) openGraphView.findViewById(R.id.og_image);

            textCrawler.makePreview(callback, url);
            isFilesFetched = true;
        }
    }


    private LinkPreviewCallback callback = new LinkPreviewCallback() {
        @Override
        public void onPre() {
            openGraphView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPos(SourceContent sourceContent, boolean isNull) {
            openGraphView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            textPreviewTitle.setText(sourceContent.getTitle());
            textPreviewDescription.setText(sourceContent.getDescription());
            textPreviewURL.setText(sourceContent.getUrl());
            ImageLoader.getInstance()
                    .displayImage(sourceContent.getImages().get(0), imagePreviewThumb, optionsFile, null);

        }
    };

    public String getCurrentDate() {
        String formattedDate="";
        Calendar c = Calendar.getInstance();
        System.out.println("Current date => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public String getCurrentTime() {
        String formattedDate="";
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        formattedDate = df.format(c.getTime());
        return formattedDate;
    }
}