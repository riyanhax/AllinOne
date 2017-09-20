package com.parasme.swopinfo.webservice;

/**
 * Created by :- Mukesh Kumawat on 18-Jan-17.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */

import android.util.Log;


import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
public final class Progress {
    private  OkHttpClient client;
    public static ProgressListener progressListener;
    public static WebServiceListener webServiceListener;
    public static Response finalResponse;

    public void run(String url) throws Exception {
        Request request = new Request.Builder()
                .url(url)
//                .addHeader("authenticationtoken", "44d0fd7b-41de-4823-9fa2-dcaa155fa539")
//                .addHeader("userid", "290789")
                .build();
/*
        final ProgressListener progressListener = new ProgressListener() {
            @Override public void update(long bytesRead, long contentLength, boolean done) {
*/
/*
                System.out.println(bytesRead);
                System.out.println(contentLength);
                System.out.println(done);
*//*

                Log.e("PROGRESS", ((100 * bytesRead) / contentLength)+"%");
                System.out.format("%d%% done\n", (100 * bytesRead) / contentLength);
            }
        };
*/
        client = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Interceptor.Chain chain) throws IOException {
                                Response originalResponse = chain.proceed(chain.request());
                                return originalResponse.newBuilder()
                                        .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                                        .build();
                            }
                        }).build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                finalResponse = response;
                webServiceListener.onResponse("downloaded");
            }

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ERROR",e.toString());
            }


        });

/*
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        System.out.println(response.body().string());
*/
    }

    public static void main(String url) throws Exception {
        new Progress().run(url);
    }

    private static class ProgressResponseBody extends ResponseBody {
        private final ResponseBody responseBody;
        private BufferedSource bufferedSource;
        public ProgressResponseBody(ResponseBody responseBody, ProgressListener progressListener) {
            this.responseBody = responseBody;
        }
        @Override public MediaType contentType() {
            return responseBody.contentType();
        }
        @Override public long contentLength() {
            return responseBody.contentLength();
        }
        @Override public BufferedSource source()  {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }
        private synchronized Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;
                @Override public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    // read() returns the number of bytes read, or -1 if this source is exhausted.
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    progressListener.update(totalBytesRead, responseBody.contentLength(), bytesRead == -1);
                    return bytesRead;
                }
            };
        }
    }
/*
    interface ProgressListener {
        void update(long bytesRead, long contentLength, boolean done);
    }
*/
}
