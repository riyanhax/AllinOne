package com.parasme.swopinfo.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.activity.FileSelectionActivity;
import com.parasme.swopinfo.activity.LocationActivity;
import com.parasme.swopinfo.activity.MainActivity;
import com.parasme.swopinfo.adapter.FeedAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.helper.DatabaseHelper;
import com.parasme.swopinfo.helper.EmojiHandler;
import com.parasme.swopinfo.helper.ImagePicker;
import com.parasme.swopinfo.helper.RippleBackground;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.helper.routemap.DrawMarker;
import com.parasme.swopinfo.model.Comment;
import com.parasme.swopinfo.model.Feed;
import com.parasme.swopinfo.model.Retailer;
import com.parasme.swopinfo.model.Store;
import com.parasme.swopinfo.model.Upload;
import com.parasme.swopinfo.socialedittext.Link;
import com.parasme.swopinfo.socialedittext.LinkableEditText;
import com.parasme.swopinfo.socialedittext.SpaceTokenizer;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.android.gms.actions.NoteIntents.EXTRA_TEXT;
import static com.parasme.swopinfo.helper.Utils.createThumbURL;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;

/**
 * Created by Mukesh Kumawat on 22-Sep-16.
 * Designation Android Senior Developer
 * Organization Parasme Software And Technology
 * Email mukeshkmtskr@gmail.com
 * Mobile +917737556190
 */

public class FragmentHome extends BaseFragment implements FileSelectionActivity.FilePicker,SwipeRefreshLayout.OnRefreshListener, LinkableEditText.OnTextCountListener, LocationActivity.LocationUpdater, ImagePicker.Picker {

    private View childView=null;
    public static ListView listFeeds;
    private LinearLayout layoutOwnProfile;
    private ImageView imageUser;
    public static ArrayList<Feed> feedArrayList = new ArrayList<>();
    private LinearLayout layout=null;
    public static Dialog dialogSwop;
    public ArrayList<File> fileArrayList = new ArrayList<>();
    public static SwipeRefreshLayout swipeRefreshLayout;
    private String TAG = getClass().getName();
    public static TextView textFilesCount;
    public static FeedAdapter adapter;
    private ImageView imgChecking;
    public static int storeId = 0;
    public static ArrayList<Retailer> retailerList;
    public TextView textCount;

    TextView textSelection;
    private final int PICK_PHOTO = 22, PICK_VIDEO = 33, PICK_DOC = 44, PICK_VOICE = 55, PICK_LOCATION=66;
    private RelativeLayout layoutSelection;
    private LinearLayout layoutPick;

    public static RippleBackground rippleBackground;
    public String fullAddress = "";
    //public boolean isLoading = false;

    // TO record audio
    private boolean isRecording = false;
    MediaRecorder mediaRecorder;
    String recordedPath;
    private final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    private GoogleMap googleMap;
    private MapView mapView;
    private RelativeLayout layoutMap,layoutShareThoughts;
    private Bundle bundle;
    private Dialog dialogSwop1;
    private LinkableEditText editSwop;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = savedInstanceState;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        loadFeedView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

//        if (childView == null)
/*
        else{
            layout = (LinearLayout) baseView.findViewById(R.id.layout);
            //layout.removeAllViews();
            layout.addView(childView);
//            ViewGroup header = (ViewGroup) mActivity.getLayoutInflater().inflate(R.layout.home_header_layout, listFeeds, false);
//            listFeeds.addHeaderView(header);

            adapter = new FeedAdapter(getActivity(), R.layout.row_feed, feedArrayList, listFeeds);
            listFeeds.setAdapter(adapter);
            swipeRefreshLayout.setRefreshing(false);
            adapter.notifyDataSetChanged();

        }
*/
    }



    private void checkIn(String latitude, String longitude, String userId) {
        String catIds = SharedPreferenceUtility.getInstance().get(AppConstants.PREF_FAV_IDS);
        Log.e("catids", catIds);
        String url = "https://swopinfo.com/retailerswithpromo.aspx?cat_id=" + catIds + "&retailer_lat=" + latitude +
                "&retailer_long=" + longitude;
//        String url = "https://swopinfo.com/retailerswithpromo.aspx?cat_id=1,2,3,4,5,6,7,8,9,1,0,11,12,1,3,14,15,16,17,18" +
//                "&retailer_long=27.991750799999977&retailer_lat=-32.93408169999999";
//        String url = "https://swopinfo.com/retailerswithpromo.aspx?cat_id=1,2,3,4,5,6,7,8,9,1,0,11,12,1,3,14,15,16,17,18&retailer_lat=26.962659&retailer_long=75.726692";
        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Checkin", response);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.opt("Result") instanceof JSONArray) {
                                JSONArray jsonArray = jsonObject.optJSONArray("Result");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject retailerObject = jsonArray.getJSONObject(i);
                                    Retailer retailer = new Retailer();
                                    retailer.setRetailerLogo(retailerObject.optString("storelogo"));
                                    retailer.setStoreId(retailerObject.optString("storeID"));
                                    retailer.setRetailerMessage(retailerObject.optString("msg"));
                                    retailer.setRetailerId(retailerObject.optString("retailerid"));
                                    retailer.setRetailerName(retailerObject.optString("storename"));

                                    ArrayList<Store.Promotion> promotionList = new ArrayList<>();

                                    JSONArray promotionArray = retailerObject.optJSONArray("pro");
                                    for (int j = 0; j < promotionArray.length(); j++) {
                                        JSONObject promotionObject = promotionArray.optJSONObject(j);
                                        Store.Promotion promotion = new Store.Promotion();
                                        String url = promotionObject.optString("promotionimg");
                                        url = url.replace("http://swopinfo", "https://swopinfo");
                                        url = url.replaceAll(" ", "%20");
                                        promotion.setImageURL(url);
                                        promotion.setUseId(promotionObject.optString("userid"));
                                        promotion.setCompanyId(promotionObject.optString("companyid"));
                                        promotionList.add(promotion);
                                    }

                                    retailer.setPromotions(promotionList);
                                    if (promotionArray.length() != 0)
                                        retailerList.add(retailer);
                                }

                                Retailer retailer = new Retailer();
                                retailer.setStoreId("0");
                                retailer.setRetailerLogo("https://www.ecigssa.co.za/data/attachments/99/99598-ff08bbcb3dbbe9846619354ee13b48d2.jpg");
                                retailerList.add(retailer);

                                ((MainActivity) mActivity).replaceFragment(new FragmentRetailerLogos(), mActivity.getFragmentManager(), mActivity, R.id.content_frame);

                            } else
                                alertDialog(mActivity, "Thanks for Checking in. We unfortunately do not have stores listed in this Geolocated area but will have soon. Please ask your Local Retailers to join so that you can benefit.", "Check In");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        try {
            webServiceHandler.get(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showIntroDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_promotions);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        dialog.getWindow().setAttributes(lp);

        TextView textChooseFav = (TextView) dialog.findViewById(R.id.text_choose_favs);
        textChooseFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                MainActivity.replaceFragment(new FragmentSelectCategories(), mActivity.getFragmentManager(), mActivity, R.id.content_frame);
            }
        });

        dialog.show();
    }


    public void getFeeds(final Activity activity, final ListView listView, final SwipeRefreshLayout swipeRefreshLayout) {
//        if (activity.getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentHome)
//            isLoading = true;

        feedArrayList.clear();

        WebServiceHandler webServiceHandler = new WebServiceHandler(activity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(String response) {
//                if (activity.getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentHome)
//                    isLoading = false;

                Log.e("FEEDS RESP" + feedArrayList.size(), response);
                try {

                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject feedJsonObject = jsonArray.getJSONObject(i);
                        Feed feed = new Feed();
                        feed.setUserId(feedJsonObject.optInt("UserID"));
                        feed.setType(feedJsonObject.optString("Type"));
                        feed.setItemDate(feedJsonObject.optString("ItemDate"));
                        feed.setFileId(feedJsonObject.optInt("FileID"));
                        Log.e("FILEID", "" + feedJsonObject.optInt("FileID"));
                        feed.setFileUserId(feedJsonObject.optInt("FileUserID"));

                        // Decoding emojis
                        feed.setComment(EmojiHandler.decodeJava(feedJsonObject.optString("Comment")));
                        feed.setUserFullName(EmojiHandler.decodeJava(feedJsonObject.optString("UserFullName")));
                        feed.setCompanyName(EmojiHandler.decodeJava(feedJsonObject.optString("CompanyName")));

                        feed.setFileName(feedJsonObject.optString("Filename"));
                        feed.setThumbFileName(feedJsonObject.optString("Thumbnailfilename"));
                        feed.setThumbURL(createThumbURL(feedJsonObject.optInt("FileUserID") + "", feedJsonObject.optString("FolderName"), feedJsonObject.optString("Thumbnailfilename"), feedJsonObject.optString("FileType"), feedJsonObject.optInt("companyid")));
                        feed.setMenuExpanded(false);
                        feed.setFileTitle(EmojiHandler.decodeJava(feedJsonObject.optString("FileTitle")));
                        feed.setFolderName(feedJsonObject.optString("FolderName"));
                        feed.setFileType(feedJsonObject.optString("FileType"));
                        feed.setVideoUrl(feedJsonObject.optString("videourl"));
                        feed.setCompanyId(feedJsonObject.optInt("companyid"));
                        feed.setCompanyThumbFileName(feedJsonObject.optString("CompanyThumbnailfilename"));
                        feed.setViewsCount(feedJsonObject.optInt("viewscount"));
                        feed.setDownloadsCount(feedJsonObject.optInt("downloadscount"));
                        feed.setStatusInfoId(feedJsonObject.optInt("StatusInformationID"));
                        feed.setUserThumb(feedJsonObject.optString("UserThumbnail"));
                        feed.setVoteStatus(feedJsonObject.optInt("CurrentUserVote"));
                        feed.setErrorSet(false);
                        feed.setCommentEditText(null);
                        feed.setPreviewLoaded(false);
                        feed.setUserPicLoaded(false);
                        feed.setWidth(feedJsonObject.optString("Width"));
                        feed.setHeight(feedJsonObject.optString("Height"));

                        // Calculate time ago
                        feed.setItemTimeAgo(feedJsonObject.optString("DisplayDate"));
                        feed.setFileTime(calculateTimeAgo(feedJsonObject.optString("FileDate")));

                        // Getting Comments
                        ArrayList<Comment> commentArrayList = new ArrayList<>();
                        JSONArray commentArray = feedJsonObject.optJSONArray("comments");
                        for (int j = 0; j < commentArray.length(); j++) {
                            Comment comment = new Comment();
                            JSONObject commentObject = commentArray.optJSONObject(j);
                            comment.setCommentId(commentObject.optInt("commentid"));
                            comment.setFileId(commentObject.optInt("fileid"));
                            comment.setUserId(commentObject.optInt("userid"));

                            // Decoding EMojis
                            comment.setCommentText(EmojiHandler.decodeJava(commentObject.optString("commenttext")));
                            comment.setTimeDate(commentObject.optString("timedate"));
                            comment.setDate(commentObject.optString("DisplayDate"));

                            JSONObject userObject = commentObject.optJSONObject("User");
                            comment.setUserId(userObject.optInt("userid"));
                            comment.setUserName(userObject.optString("username"));
                            comment.setUserFullName(EmojiHandler.decodeJava(userObject.optString("UserFullName")));
                            comment.setCompanyId(userObject.optInt("companyid"));
                            String imageURL = AppConstants.URL_DOMAIN + "upload/user" + userObject.optInt("userid") + "/profilepic.jpg";
                            comment.setUserImageURL(imageURL);
                            commentArrayList.add(comment);

                            /***********************************/
//                            if(j==0 && !commentObject.optString("commenttext").contains("Sexy.."))
//                                feed.setUserId(commentObject.optInt("userid"));
                            /***********************************/
                        }

                        feed.setCommentArrayList(commentArrayList);


                        if (!feedJsonObject.isNull("filelist")) {
                            ArrayList<Upload> uploadArrayList = new ArrayList<>();
                            JSONArray jsonArray1 = feedJsonObject.getJSONArray("filelist");
                            for (int j = 0; j < jsonArray1.length(); j++) {
                                JSONObject jsonObject = jsonArray1.getJSONObject(j);
                                Upload upload = new Upload();
                                upload.setBroadcast(jsonObject.getBoolean("broadcast"));
                                upload.setComments(jsonObject.getBoolean("comments"));
                                upload.setDownloads(jsonObject.getBoolean("downloads"));
                                upload.setCompanyId(jsonObject.getInt("companyid"));
                                upload.setDownloadsCount(jsonObject.getInt("downloadscount"));
                                upload.setFileId(jsonObject.getInt("fileid"));
                                upload.setFileName(jsonObject.getString("filename"));
                                upload.setFileType(jsonObject.getString("filetype"));
                                upload.setFolderName(jsonObject.getString("foldername"));
                                upload.setScore(jsonObject.getInt("Score"));
                                upload.setViewsCount(jsonObject.getInt("viewscount"));
                                upload.setThumbURL("https://swopinfo.com" + jsonObject.getString("Thumbnailfilename").replace("~", ""));
                                upload.setFileURL(Utils.createFileURL(jsonObject));
                                upload.setFileType(jsonObject.optString("filetype"));
                                upload.setUserVote(jsonObject.optInt("Vote"));
                                upload.setVideoURL(jsonObject.optString("videourl"));
                                if (j == 0) {
                                    upload.setFileId(jsonObject.getInt("fileid"));

                                    //feed.setCompanyId(jsonObject.getInt("companyid")); // When multiple image shared from company then main array returns company id zero so replace it here

                                    // Decoding eMojis
                                    upload.setCommentText(EmojiHandler.decodeJava(feedJsonObject.optString("Comment")));
                                    upload.setDescription(EmojiHandler.decodeJava(jsonObject.getString("description")));
                                    upload.setUserFullName(EmojiHandler.decodeJava(feedJsonObject.optString("UserFullName")));
                                    upload.setUserThumbURL(feedJsonObject.optString("UserThumbnail"));
                                }
                                uploadArrayList.add(upload);
                            }

                            feed.setUploadArrayList(uploadArrayList);
                        }

                        feedArrayList.add(feed);

                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // When feeds api is called from its own screen
                            adapter = new FeedAdapter(activity, R.layout.row_feed, feedArrayList, listView);
                            listView.setAdapter(adapter);
                            swipeRefreshLayout.setRefreshing(false);
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (JSONException e) {
                } catch (NullPointerException e) {
                }
            }
        };

        try {
            webServiceHandler.get(AppConstants.URL_FEED);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String calculateTimeAgo(String itemDate) {
        long feedTimeMillis = getDateInMillis(itemDate);
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(feedTimeMillis, System.currentTimeMillis(), 0);

//        Log.e("CHECK22", "calculateTimeAgo: "+feedTimeMillis +  System.currentTimeMillis());

/*
        Moment moment = null;
        try {
            if (!itemDate.contains("Z"))
                itemDate = itemDate+"Z";
            moment = Iso8601Format.EXTENDED_DATE_TIME_OFFSET.parse("2017-03-01T12:18:36.213Z");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String ago = PrettyTime.of(Locale.getDefault()).printRelativeInStdTimezone(moment);
*/
        return timeAgo + "";
    }

    public static long getDateInMillis(String srcDate) {
        // To remove last three digit after .
//        if(srcDate.contains("."))
//            srcDate = srcDate.substring(0,srcDate.lastIndexOf("."));
        SimpleDateFormat desiredFormat = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS");
        //desiredFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        long dateInMillis = 0;
        try {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            return dateInMillis;
        } catch (ParseException e) {
            Log.e("ERROR", "getDateInMillis: " + e.toString());
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        layout.removeAllViews();
    }

    private void loadDialog() {
        dialogSwop1 = new Dialog(mActivity);
        dialogSwop1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSwop1.setContentView(R.layout.dialog_swop);
        dialogSwop1.setCancelable(true);
        dialogSwop1.setCanceledOnTouchOutside(true);


        editSwop = (LinkableEditText) dialogSwop1.findViewById(R.id.editSwop);
        editSwop.setOnTextCountListener(FragmentHome.this);
        textCount = (TextView) dialogSwop1.findViewById(R.id.textCount);
        setTagMentionPattern(editSwop);


        final Button btnPost = (Button) dialogSwop1.findViewById(R.id.btnPost);
        textSelection = (TextView) dialogSwop1.findViewById(R.id.text_selection);
        layoutSelection = (RelativeLayout) dialogSwop1.findViewById(R.id.layout_selection);
        layoutPick = (LinearLayout) dialogSwop1.findViewById(R.id.layout_pick);
        final LinearLayout layoutPhoto = (LinearLayout) dialogSwop1.findViewById(R.id.layout_photo);
        final LinearLayout layoutVideo = (LinearLayout) dialogSwop1.findViewById(R.id.layout_video);
        final LinearLayout layoutCamera = (LinearLayout) dialogSwop1.findViewById(R.id.layout_camera);
        final LinearLayout layoutDoc = (LinearLayout) dialogSwop1.findViewById(R.id.layout_doc);
        final LinearLayout layoutAudio = (LinearLayout) dialogSwop1.findViewById(R.id.layout_audio);
        final LinearLayout layoutVoice = (LinearLayout) dialogSwop1.findViewById(R.id.layout_voice_note);
        final LinearLayout layoutLocation = (LinearLayout) dialogSwop1.findViewById(R.id.layout_location);
        layoutMap = (RelativeLayout) dialogSwop1.findViewById(R.id.layout_map);
        layoutShareThoughts = (RelativeLayout) dialogSwop1.findViewById(R.id.layout_share_thoughts);

        final RelativeLayout layoutRecord = (RelativeLayout) dialogSwop1.findViewById(R.id.layoutRecord);
        final ImageView imgDeselect = (ImageView) dialogSwop1.findViewById(R.id.img_deselect);
        final ImageView imgBack = (ImageView) dialogSwop1.findViewById(R.id.img_back);
        final ImageView imgRecord = (ImageView) dialogSwop1.findViewById(R.id.img_record);
        final Chronometer chronometer = (Chronometer) dialogSwop1.findViewById(R.id.chronometer);
        mapView = (MapView) dialogSwop1.findViewById(R.id.mapView);


        layoutPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Photo"), PICK_PHOTO);

            }
        });


        layoutVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO);

            }
        });

        layoutCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.picker = FragmentHome.this;
                Intent intent = new Intent(mActivity, ImagePicker.class);
                intent.putExtra("selectType", 0);
                mActivity.startActivity(intent);
            }
        });

        layoutAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Audio"), PICK_VOICE);

            }
        });

        layoutVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutPick.setVisibility(View.GONE);
                layoutRecord.setVisibility(View.VISIBLE);
            }
        });

        layoutDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                String[] mimetypes = {"application/pdf",
                        "text/*",
                        "application/vnd.ms-powerpoint",
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                        "application/vnd.ms-excel",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(intent, PICK_DOC);


//                Intent intent = new Intent();
//                intent.setType("audio/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent,"Select Audio"),PICK_VOICE);

            }
        });


        imgDeselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutPick.setVisibility(View.VISIBLE);
                layoutSelection.setVisibility(View.GONE);
                layoutMap.setVisibility(View.GONE);
                layoutShareThoughts.setVisibility(View.VISIBLE);
                editSwop.setText("");
            }
        });


        imgRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRecording) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ActivityCompat.checkSelfPermission(mActivity,
                                Manifest.permission.RECORD_AUDIO)
                                != PackageManager.PERMISSION_GRANTED) {
                            // No explanation needed, we can request the permission.
                            requestPermissions(
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                            // MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        } else {
                            imgRecord.setImageResource(R.drawable.ic_stop);
                            startRecording(chronometer);
                        }

                    } else {
                        imgRecord.setImageResource(R.drawable.ic_stop);
                        startRecording(chronometer);
                    }

                } else {
                    imgRecord.setImageResource(R.drawable.ic_record);
                    stopRecording(layoutRecord, chronometer);
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecorder(chronometer);
                layoutRecord.setVisibility(View.GONE);
                layoutPick.setVisibility(View.VISIBLE);
                imgRecord.setImageResource(R.drawable.ic_record);
            }
        });


        layoutLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    LatLngBounds latLngBounds = toBounds(new LatLng(LocationActivity.mCurrentLocation.getLatitude(), LocationActivity.mCurrentLocation.getLongitude()), 10000);
                    Intent intent = (new PlaceAutocomplete.IntentBuilder(2)).setBoundsBias(latLngBounds).build(getActivity());
                    startActivityForResult(intent,PICK_LOCATION);
                }
                catch (Exception e){e.printStackTrace();}
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String swopText = editSwop.getText().toString().trim();
                if (swopText.equals("") && fileArrayList.size() == 0) {
                    editSwop.setError("Please enter some text share or select a file");
                    editSwop.requestFocus();
                    return;
                }
                dialogSwop1.dismiss();
                new FragmentAdd().uploadFile(AppConstants.USER_ID, "0", "0", "", " ", " ", " ", "true", "true", "true",
                        "true", "true", "", fileArrayList, mActivity, null, swopText, false);

            }
        });

        dialogSwop1.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                stopRecorder(chronometer);
            }
        });

        dialogSwop1.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                    final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                            getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.remove(autocompleteFragment);
                    ft.commit();
            }
        });
        dialogSwop1.show();
    }


    private void startRecording(Chronometer chronometer) {
        recordedPath = setFilePath();
        Log.e("Filepath", recordedPath);
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(recordedPath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioChannels(1);

        try {
            isRecording = true;
            mediaRecorder.prepare();
            mediaRecorder.start();
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRecording(RelativeLayout layoutRecord, Chronometer chronometer) {
        if (mediaRecorder != null) {
            stopRecorder(chronometer);
            layoutSelection.setVisibility(View.VISIBLE);
            layoutRecord.setVisibility(View.GONE);
            layoutPick.setVisibility(View.GONE);
            textSelection.setText("Voice Recorded");
            Log.e("Selected Path", recordedPath);
            fileArrayList.add(new File(recordedPath));

        }
    }


    private void stopRecorder(Chronometer chronometer) {
        if (isRecording) {
            mediaRecorder.stop();
            mediaRecorder.release();
            isRecording = false;
            fileArrayList.clear();
            if (chronometer != null) {
                chronometer.stop();
                chronometer.setBase(SystemClock.elapsedRealtime());
            }
        }
    }

    private String setFilePath() {
        String root = Environment.getExternalStorageDirectory().toString();
        File dirSwopInfo = new File(root + "/SwopInfo/");
        if (!dirSwopInfo.exists())
            dirSwopInfo.mkdirs();
        File dirRecording = new File(dirSwopInfo.getPath() + "/Recordings/");
        if (!dirRecording.exists())
            dirRecording.mkdir();

        File file = new File(dirRecording, System.currentTimeMillis() + ".m4a");

        return file.getPath();
    }

    private void setTagMentionPattern(final LinkableEditText editSwop) {


        Link linkHashtag = new Link(Pattern.compile("(#\\w+)"))
                .setUnderlined(true)
                .setTextStyle(Link.TextStyle.BOLD)
                .setClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String text) {
                    }
                });

        Link linkUsername = new Link(Pattern.compile("(@\\w+)"))
                .setUnderlined(false)
                .setTextColor(Color.parseColor("#147956"))
                .setTextStyle(Link.TextStyle.BOLD)
                .setClickListener(new Link.OnClickListener() {
                    @Override
                    public void onClick(String text) {
                    }
                });

        List<Link> links = new ArrayList<>();
        links.add(linkHashtag);
        links.add(linkUsername);


        editSwop.setTokenizer(new SpaceTokenizer());
        //editSwop.setAdapter(adapter);

        editSwop.addLinks(links);

    }


    @Override
    public void onFilesSelected(ArrayList<File> resultFileList) {
        fileArrayList = resultFileList;
        int size = fileArrayList.size();
        textFilesCount.setVisibility(View.VISIBLE);
        textFilesCount.setText((size == 1) ? "1 File Selected" : (size + " Files Selected"));

        Log.e("asfdsaf", resultFileList.get(0).getPath());

    }

    @Override
    public void onRefresh() {
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);
        feedArrayList.clear();
        if (adapter != null)
            adapter.notifyDataSetChanged();
        getFeeds(mActivity, listFeeds, swipeRefreshLayout);
    }


    public void alertDialog(final Activity activity, final String message, final String title) {
        AlertDialog.Builder adb = new AlertDialog.Builder(activity);
        adb.setMessage(message);
        adb.setTitle(title);
        adb.setCancelable(false);
        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        adb.setNegativeButton("Select Categories", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MainActivity.replaceFragment(new FragmentSelectCategories(), mActivity.getFragmentManager(), mActivity, R.id.content_frame);
            }
        });

        adb.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("aaaaa", "aaaaa");
        String path = "";
        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case PICK_PHOTO:
                    fileArrayList.clear();
                    layoutSelection.setVisibility(View.VISIBLE);
                    layoutPick.setVisibility(View.GONE);
                    textSelection.setText("Photo Selected");
                    path = Utils.getRealPathFromURI(mActivity, data.getData());
                    Log.e("Selected Path", path);
                    fileArrayList.add(new File(path));
                    break;
                case PICK_VIDEO:
                    fileArrayList.clear();
                    layoutSelection.setVisibility(View.VISIBLE);
                    layoutPick.setVisibility(View.GONE);
                    textSelection.setText("Video Selected");
                    path = Utils.getRealPathFromURI(mActivity, data.getData());
                    Log.e("Selected Path", path);
                    fileArrayList.add(new File(path));
                    break;
                case PICK_DOC:
                    fileArrayList.clear();
                    layoutSelection.setVisibility(View.VISIBLE);
                    layoutPick.setVisibility(View.GONE);
                    textSelection.setText("Document Selected");
                    path = Utils.getRealPathFromURI(mActivity, data.getData());
                    Log.e("Selected Path", path);
                    fileArrayList.add(new File(path));
                    break;
                case PICK_VOICE:
                    fileArrayList.clear();
                    layoutSelection.setVisibility(View.VISIBLE);
                    layoutPick.setVisibility(View.GONE);
                    textSelection.setText("Voice Selected");
                    path = Utils.getRealPathFromURI(mActivity, data.getData());
                    Log.e("Selected Path", path);
                    fileArrayList.add(new File(path));
                    break;
                case PICK_LOCATION:
                    Place place = PlaceAutocomplete.getPlace(this.getActivity(), data);
                    if(place != null) {
                        double lat = place.getLatLng().latitude;
                        double lng = place.getLatLng().longitude;

                        String url = place.getName()+"\nhttps://maps.googleapis.com/maps/api/staticmap?center="+lat+","+lng+"&zoom=18&size=640x400&markers=color:red|"+lat+","+lng+"&path=weight:3%7Ccolor:blue%7Cenc:{coaHnetiVjM??_SkM??~R&key=AIzaSyAW50fc18EAuLVT_qpXNwdQe_h6EvcFwTc";

                        layoutSelection.setVisibility(View.VISIBLE);
                        layoutPick.setVisibility(View.GONE);
                        textSelection.setText(place.getName());
                        layoutShareThoughts.setVisibility(View.GONE);
                        editSwop.setText(url);
                        initAndLoadMap(bundle, mapView, place);
                    }

                    break;
            }

        }
    }


    @Override
    public void onTextCount(String count) {
        textCount.setText(count);
    }


    @Override
    public void onReceiveLocation(Location location) {

        if (location != null && fullAddress.equals(""))
            getAddress(location.getLatitude(), location.getLongitude());
        else if (!fullAddress.equals("") && getFragmentManager().getBackStackEntryCount() == 0 && FragmentHome.swipeRefreshLayout != null
                && !FragmentHome.swipeRefreshLayout.isRefreshing()) {
            Log.e("ELSE OK", "part");
            //replaceFragment(new FragmentHome(), getFragmentManager(), MainActivity.this, R.id.content_frame);
        }
    }

    @Override
    public void onRejectLocationRequest() {
        Log.e("REJECT", "LOCATION");
    }

    @Override
    public void onStart() {
        super.onStart();
        LocationActivity.locationUpdater = FragmentHome.this;
        Log.e("REQULOC", "REQUESTING LOCATIONACTIVITY");
        startActivity(new Intent(mActivity, LocationActivity.class));
    }

/*
    private void getAddress(String latitude, String longitude) {


        Log.e("Getting", "Addrwess");
        fullAddress = " ";  // TO avoid multiple hit of api on received location as it triggered repeatedly.
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=AIzaSyABGigUX5fvihfR8Bc8WnCScS3XXWK2B78&sensor=true";

        WebServiceHandler webServiceHandler = new WebServiceHandler(mActivity);
        webServiceHandler.serviceListener = new WebServiceListener() {
            @Override
            public void onResponse(final String response) {
                Log.e("Address Response", response);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("status").equalsIgnoreCase("ok")) {
                                JSONObject jsonObject1 = jsonObject.getJSONArray("results").getJSONObject(0);
                                fullAddress = jsonObject1.optString("formatted_address");
                                Log.e("Address", fullAddress);


                                if (fullAddress.contains("South Africa") && FragmentHome.rippleBackground != null) {
//                                if (fullAddress.contains("India") && rippleBackground!=null){
                                    rippleBackground.setVisibility(View.VISIBLE);
                                    Log.e("CHECKINVISIBLE", "condition here");
                                }

                            } else
                                Log.e("Address Err", "Could not get address");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        try {
            webServiceHandler.get(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/


    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            fullAddress = add;
            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();

            Log.e("IGA", "Address" + add);
            if (fullAddress.contains("South Africa"))
                rippleBackground.setVisibility(View.VISIBLE);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("ERROR", e.toString());
            //Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (permissions[0].equals(Manifest.permission.RECORD_AUDIO)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    @Override
    public void onImagePicked(Bitmap bitmap, String imagePath) {
        fileArrayList.clear();
        layoutSelection.setVisibility(View.VISIBLE);
        layoutPick.setVisibility(View.GONE);
        textSelection.setText("Photo Selected");
        Log.e("Selected Path", imagePath);
        fileArrayList.add(new File(imagePath));

    }

    @Override
    public void onVideoPicked(String videoPath) {
        fileArrayList.clear();
        layoutSelection.setVisibility(View.VISIBLE);
        layoutPick.setVisibility(View.GONE);
        textSelection.setText("Video Selected");
        Log.e("Selected Path", videoPath);
        fileArrayList.add(new File(videoPath));
    }

    private void initAndLoadMap(Bundle savedInstanceState, final MapView mapView, final Place place) {
        //dialogSwop1.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        MapsInitializer.initialize(getActivity());

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                layoutMap.setVisibility(View.VISIBLE);

                googleMap = map;
                if(googleMap!=null){

                    googleMap.getUiSettings().setZoomControlsEnabled(false);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    googleMap.getUiSettings().setMapToolbarEnabled(false);

                    CameraUpdate center = CameraUpdateFactory.newLatLngZoom(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude), 17);
                    googleMap.animateCamera(center);
                    DrawMarker.getInstance(getActivity()).draw(map, place.getLatLng(), R.drawable.map, "You");
                }

            }


        });
    }

    public LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }

    private void loadFeedView() {
        LocationActivity.locationUpdater = FragmentHome.this;

        retailerList = new ArrayList<>();

        imageActionFeed.setImageResource(R.drawable.ic_newsfeed_active);

        ((TextView) mActivity.findViewById(R.id.text_title)).setText("Home");

        childView = getActivity().getLayoutInflater().inflate(R.layout.fragment_home, null);

        listFeeds = (ListView) childView.findViewById(R.id.listFeeds);

        ViewGroup header = (ViewGroup) mActivity.getLayoutInflater().inflate(R.layout.home_header_layout, listFeeds, false);
        listFeeds.addHeaderView(header);

        layoutOwnProfile = (LinearLayout) childView.findViewById(R.id.layoutOwnProfile);
        imageUser = (ImageView) listFeeds.findViewById(R.id.imageUser);

        Picasso.with(mActivity).load(AppConstants.USER_IMAGE_URL)
                .error(R.drawable.avtar)
                .placeholder(R.drawable.avtar)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(imageUser);


        imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.replaceFragment(new FragmentProfile(), mActivity.getFragmentManager(), mActivity, R.id.content_frame);
            }
        });

        ((LinearLayout) childView.findViewById(R.id.layoutShareSomething)).setVisibility(View.VISIBLE);
        ((EditText) childView.findViewById(R.id.editSwopText)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(dialogSwop1==null)
                    loadDialog();
//                else
//                    dialogSwop1.show();
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) childView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(getActivity().getResources().getColor(R.color.colorPrimaryDark));


        swipeRefreshLayout.setOnRefreshListener(this);


        Log.e("ONRESUMEON", "onresume");

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
//                    feedArrayList.clear();
                if (feedArrayList.size() == 0) {
                    swipeRefreshLayout.setRefreshing(true);
                    getFeeds(mActivity, listFeeds, swipeRefreshLayout);
                } else
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listFeeds.setAdapter(new FeedAdapter(mActivity, R.layout.row_feed, feedArrayList, listFeeds));
                        }
                    });
            }
        });


        imgChecking = (ImageView) childView.findViewById(R.id.img_checkin);


        rippleBackground = (RippleBackground) childView.findViewById(R.id.rippleBackground);
        rippleBackground.startRippleAnimation();

        TelephonyManager tm = (TelephonyManager) mActivity.getSystemService(Context.TELEPHONY_SERVICE);
        String country = tm.getSimCountryIso();
        Log.e("COUNTRY", country);
        if (country.equalsIgnoreCase("za"))
            rippleBackground.setVisibility(View.VISIBLE);


        imgChecking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("LOCATION", LocationActivity.mCurrentLocation.getLatitude() + "");
                if (SharedPreferenceUtility.getInstance().get(AppConstants.PREF_CHECK_IN_INTRO, false)) {
                    if (LocationActivity.mCurrentLocation != null)
                        checkIn(LocationActivity.mCurrentLocation.getLatitude() + "", LocationActivity.mCurrentLocation.getLongitude() + "", AppConstants.USER_ID);
                    else {
                        Toast.makeText(mActivity, "Getting Location", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(mActivity, LocationActivity.class));
                    }
                } else
                    showIntroDialog();

            }
        });


        layout = (LinearLayout) baseView.findViewById(R.id.layout);
        layout.removeAllViews();
        layout.addView(childView);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("fsdafdasa","DESTROYED");
        if (WebServiceHandler.call != null)
            WebServiceHandler.call.cancel();

        stopRecorder(null);

    }



}