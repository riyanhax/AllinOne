package com.parasme.swopinfo.webservice;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.helper.NumberProgressBar;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.helper.Utils;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by Mukesh Kumawat on 15-Aug-16.
 * Designation Android Team Leader
 * Organization Parasme Software And Technology
 * Email mukeshkmtskr@gmail.com
 * Mobile +917737556190
 */

public class WebServiceHandler {
    private OkHttpClient okHttpClient;
    private RequestBody requestBody;
    private Request request;
    private static Context context;
    private ProgressDialog progressDialog=null;
    public WebServiceListener serviceListener;
    public static Call call;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public WebServiceHandler(Context context) {
        this.context= context;
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(25, TimeUnit.MINUTES)
                .writeTimeout(25, TimeUnit.MINUTES)
                .readTimeout(25, TimeUnit.MINUTES)
                .retryOnConnectionFailure(true)
                .build();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
    }


    public void post(String url, FormBody.Builder builder) throws IOException {
        if(!url.contains("voteup") && !url.contains("votedown"))
            progressDialog.show();
        Log.e("POSTURL",url);

        if(url.equalsIgnoreCase(AppConstants.URL_DOMAIN + "api/Authentication") || url.contains("facebook")){
            request = new Request.Builder()
                    .url(url)
                    .post(builder.build())
                    .build();
        }
        else {
            request = new Request.Builder()
                    .url(url)
                    .post(builder.build())
                    .addHeader("authenticationtoken", AppConstants.AUTH_TOKEN)
                    .addHeader("userid", AppConstants.USER_ID)
                    .build();
        }

        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                serviceListener.onResponse(response.body().string());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ERROR",e.toString());
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

            }


        });
    }


    public void patch(String url, FormBody.Builder builder) throws IOException {
        progressDialog.show();
        Log.e("PATCHURL",url);
        request =  new Request.Builder()
                .url(url)
                .patch(builder.build())
                .addHeader("authenticationtoken", AppConstants.AUTH_TOKEN)
                .addHeader("userid", AppConstants.USER_ID)
                .build();
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                serviceListener.onResponse(response.body().string());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

            }
        });
    }


    public void get(String url) throws IOException {
        if(!url.contains("NewsFeed") && ((Activity)context) instanceof MainActivity)
            progressDialog.show();

        Log.e("GETURL",url);
        Log.e("AUTHTOKEN",AppConstants.AUTH_TOKEN);
        request =  new Request.Builder()
                .url(url)
                .get()
                .addHeader("authenticationtoken", AppConstants.AUTH_TOKEN)
                .addHeader("userid", AppConstants.USER_ID)
                .build();
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                if (progressDialog!=null && progressDialog.isShowing())
                    progressDialog.dismiss();
                serviceListener.onResponse(data);
                //EventBus.getDefault().postSticky(data);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (progressDialog!=null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }


    public void postMultiPart(String url, MultipartBody.Builder builder, String userId) throws IOException {
        ArrayList<Object> objects = Utils.loadDownloadDialog((Activity) context,"Uploading");
        final Dialog dialog = (Dialog) objects.get(0);
        final NumberProgressBar numberProgressBar = (NumberProgressBar) objects.get(1);
        dialog.show();

        Log.e("POSTMPARTURL",url);
        Log.e("authenticationtoken", SharedPreferenceUtility.getInstance().get(AppConstants.PREF_AUTH_TOKEN)+"");
        Log.e("userid", userId);

        request =  new Request.Builder()
                .url(url)
                .addHeader("authenticationtoken", SharedPreferenceUtility.getInstance().get(AppConstants.PREF_AUTH_TOKEN,"")+"")
                .addHeader("userid", userId)
                .post(new ProgressRequestBody(builder.build(), new ProgressRequestBody.Listener() {
                    @Override
                    public void onProgress(final int progress) {
                        Log.e("CHECK", "onProgress: "+progress );
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                numberProgressBar.setProgress(progress);
                            }
                        });
                    }
                }))
                .build();
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (dialog.isShowing())
                    dialog.dismiss();
                serviceListener.onResponse(response.body().string());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ERROR",e.toString());
                if (dialog.isShowing())
                    dialog.dismiss();

            }
        });
    }


    public static FormBody.Builder createBuilder(String [] paramsName, String [] paramsValue){
        FormBody.Builder builder=new FormBody.Builder();
        for(int i=0;i<paramsName.length;i++){
            Log.e("CHECK", paramsName[i]+"___"+paramsValue[i]);
            builder.add(paramsName[i],paramsValue[i]);
        }
        return  builder;
    }

    public static MultipartBody.Builder createMultiPartBuilder(String [] paramsName, String [] paramsValue){
/*
        String filename = "image.jpg";
        String filenameArray[] = filename.split("\\.");
        String extension = filenameArray[filenameArray.length-1];
        System.out.println(extension);
*/

        MultipartBody.Builder builder=new MultipartBody.Builder();
/*
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date now1 = new Date();
        String fileName = formatter1.format(now1) + ".jpg";
*/

        for(int i=0;i<paramsName.length;i++){

            Log.e(paramsName[i],paramsValue[i]);

            if(paramsValue[i].contains("/storage") || paramsValue[i].contains("data/")) {
                String fileData[] = new Utils(((Activity)context)).getFileNameAndType(paramsValue[i]);
                Log.e("type",""+fileData[1]);
                String mimeType = fileData[1];
//                if (mimeType.equals("audio/mpeg"))
//                    mimeType = "audio/mp3";
                builder.setType(MultipartBody.FORM).addFormDataPart(paramsName[i], fileData[0]+"", RequestBody.create(MediaType.parse(mimeType), new File(paramsValue[i])));
            }

            else
                builder.setType(MultipartBody.FORM).addFormDataPart(paramsName[i],paramsValue[i]);

            // builder.add(paramsName[i],paramsValue[i]);
        }
        return  builder;
    }


    public void delete(String url, FormBody.Builder builder) throws IOException {
        progressDialog.show();
        Log.e("DELETEURL",url);
        request =  new Request.Builder()
                .url(url)
                .delete(builder.build())
                .addHeader("authenticationtoken", AppConstants.AUTH_TOKEN)
                .addHeader("userid", AppConstants.USER_ID)
                .build();
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                serviceListener.onResponse(response.body().string());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

            }
        });
    }

    public void getProgress(){
        final ProgressListener progressListener = new ProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                int progress = (int) ((100 * bytesRead) / contentLength);

                // Enable if you want to see the progress with logcat
                Log.e("PROGRESS", "DownloadFile: " + progress + "%");
            }
        };

        okHttpClient.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder()
                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();
            }
        });

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                serviceListener.onResponse(response.body().string());
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

            }
        });

    }

    private static class ProgressResponseBody extends ResponseBody {

        private final ResponseBody responseBody;
        private final ProgressListener progressListener;
        private BufferedSource bufferedSource;

        public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
            this.responseBody = responseBody;
            this.progressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength(){
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source()   {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                    return bytesRead;
                }
            };
        }
    }



    public void getAppLozic(String url) throws IOException {
            progressDialog.show();

        Log.e("GETURL",url);
        request =  new Request.Builder()
                .url(url)
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("Apz-Token", "Basic Z2F2aW5Ac3dvcGluZm8uY29tOmdhdmluc2ltb2VuMDE=")
                .addHeader("Apz-AppId", "swopinfo24db7fc38817b4fa125f3db11acc8d67")
                .build();
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                serviceListener.onResponse(data);
                //EventBus.getDefault().postSticky(data);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }
}