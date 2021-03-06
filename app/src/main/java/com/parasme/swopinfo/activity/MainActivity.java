package com.parasme.swopinfo.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.applozic.mobicomkit.api.account.register.RegisterUserClientService;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserClientService;
import com.applozic.mobicomkit.api.account.user.UserDetail;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.api.account.user.UserLogoutTask;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity;
import com.applozic.mobicomkit.uiwidgets.people.activity.MobiComKitPeopleActivity;
import com.applozic.mobicommons.json.GsonUtils;
import com.google.gson.Gson;
import com.onesignal.OneSignal;
import com.parasme.swopinfo.R;
import com.parasme.swopinfo.adapter.SearchAdapter;
import com.parasme.swopinfo.application.AppConstants;
import com.parasme.swopinfo.application.MyApplication;
import com.parasme.swopinfo.fragment.FragmentCard;
import com.parasme.swopinfo.fragment.FragmentCompany;
import com.parasme.swopinfo.fragment.FragmentFile;
import com.parasme.swopinfo.fragment.FragmentGroupDetail;
import com.parasme.swopinfo.fragment.FragmentHome;
import com.parasme.swopinfo.fragment.FragmentProfile;
import com.parasme.swopinfo.fragment.FragmentPromotionPager;
import com.parasme.swopinfo.fragment.FragmentRadio;
import com.parasme.swopinfo.fragment.FragmentRateUs;
import com.parasme.swopinfo.fragment.FragmentSearch;
import com.parasme.swopinfo.fragment.FragmentSettings;
import com.parasme.swopinfo.fragment.FragmentSharedPromotion;
import com.parasme.swopinfo.fragment.FragmentSubscription;
import com.parasme.swopinfo.fragment.FragmentSubscriptionPayment;
import com.parasme.swopinfo.fragment.FragmentUploads;
import com.parasme.swopinfo.fragment.FragmentUploadsWrapper;
import com.parasme.swopinfo.fragment.FragmentUser;
import com.parasme.swopinfo.helper.DatabaseHelper;
import com.parasme.swopinfo.helper.RippleBackground;
import com.parasme.swopinfo.helper.SharedPreferenceUtility;
import com.parasme.swopinfo.helper.Utils;
import com.parasme.swopinfo.webservice.WebServiceHandler;
import com.parasme.swopinfo.webservice.WebServiceListener;
import com.tangxiaolv.telegramgallery.GalleryActivity;

import net.alhazmy13.catcho.library.Catcho;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


import static com.parasme.swopinfo.application.AppConstants.PREF_BUSINESS_CELL;
import static com.parasme.swopinfo.application.AppConstants.PREF_BUSINESS_EMAIL;
import static com.parasme.swopinfo.application.AppConstants.PREF_BUSINESS_FAX;
import static com.parasme.swopinfo.application.AppConstants.PREF_BUSINESS_TEL;
import static com.parasme.swopinfo.application.AppConstants.PREF_USER_FIRST_NAME;
import static com.parasme.swopinfo.application.AppConstants.PREF_USER_ID;
import static com.parasme.swopinfo.application.AppConstants.PREF_USER_NAME;
import static com.parasme.swopinfo.application.AppConstants.PREF_USER_SUR_NAME;
import static com.parasme.swopinfo.fragment.FragmentUploadsWrapper.textFilesCount;

/**
 * Created by Mukesh Kumawat on 22-Sep-16.
 * Designation Android Senior Developer
 * Organization Parasme Software And Technology
 * Email mukeshkmtskr@gmail.com
 * Mobile +917737556190
 */

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

    private ActionBarDrawerToggle mDrawerToggle;
    public CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] navMenuTitles;
    private Dialog dialogSearch;
    public static Activity activityContext;
    public String gcmToken;

    DrawerLayout drawerLayout;
    ListView listLeftDrawer;
    Toolbar toolbar;
    TextView text_title;

    boolean isActive = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Thread.setDefaultUncaughtExceptionHandler(new Catcho.Builder(this).recipients("parasme.mukesh@gmail.com").build());
        activityContext = MainActivity.this;


        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        listLeftDrawer = (ListView) findViewById(R.id.listLeftDrawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        text_title = (TextView) findViewById(R.id.text_title);

        listLeftDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                drawerClick(i);
            }
        });

        MyApplication.initOneSignal(MyApplication.getInstance());

        init();

        if(!SharedPreferenceUtility.getInstance().get(AppConstants.PREF_APPLOZIC_LOGIN,false))
            appLozicLogin(SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_ID)+"", SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_FIRST_NAME)+" "+SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_SUR_NAME), SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_EMAIL)+"");

        new VersionChecker().execute();
    }

    private void delete7DaysOlderCache() {
        long currentTimeInMillis = Calendar.getInstance().getTimeInMillis();
        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
        Map<Integer, String> thumbMap = databaseHelper.getAllThumbs();

        for(Iterator<Map.Entry<Integer, String>> it = thumbMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Integer, String> entry = it.next();
            long thumbTimeInMillis = Long.parseLong(entry.getValue());
            long daysInterval = TimeUnit.MILLISECONDS.toDays(currentTimeInMillis-thumbTimeInMillis);
            if(daysInterval >= 7) {
                File file = databaseHelper.getThumbFileFromId(entry.getKey());
                boolean deleted = file.delete();
                if (deleted)
                    databaseHelper.deleteThumbRecord(entry.getKey());
            }
        }
    }


    protected void init(){
        AppConstants.AUTH_TOKEN = SharedPreferenceUtility.getInstance().get(AppConstants.PREF_AUTH_TOKEN)+"";
        String userId=SharedPreferenceUtility.getInstance().get(AppConstants.PREF_USER_ID)+"";
        AppConstants.USER_ID=userId;

        String imageURL=AppConstants.URL_DOMAIN+"upload/user"+ userId+"/profilepic.jpg";
        AppConstants.USER_IMAGE_URL=imageURL;

        mTitle = mDrawerTitle = getTitle();
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        listLeftDrawer.setAdapter(new MenuAdapter());

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon

        setSupportActionBar(toolbar);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer image to replace 'Up' caret */
                R.string.app_name,  /* "open drawer" description for accessibility */
                R.string.app_name  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                //getSupportActionBar().setTitle(mTitle);
                text_title.setText(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                //getSupportActionBar().setTitle(mDrawerTitle);
                text_title.setText(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }




    private void appLozicLogin(String userId, String userName, String userEmail) {
        UserLoginTask.TaskListener listener = new UserLoginTask.TaskListener() {

            @Override
            public void onSuccess(RegistrationResponse registrationResponse, Context context) {
                //After successful registration with Applozic server the callback will come here
                Log.e(TAG,"App Lozic Login success");
                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_APPLOZIC_LOGIN,true);

                // Disabling map sharing
//                ApplozicSetting.getInstance(context).disableLocationSharingViaMap();

                // FCM TOKEN for push notification when message arrived
                gcmToken = SharedPreferenceUtility.getInstance().get(AppConstants.PREF_GCM_TOKEN)+"";
                Log.e("GCMTOKEN",gcmToken);
                initPushForMessage(gcmToken);
            }

            @Override
            public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                //If any failure in registration the callback  will come here
                Log.e("REGISTRATION", new Gson().toJson(registrationResponse));
                Log.e("FAILURE",exception.toString());
            }};

        Log.e("AAA",User.AuthenticationType.APPLOZIC.getValue()+"");
        User user = new User();
        user.setUserId(userId); //userId it can be any unique user identifier
        user.setDisplayName(userName); //displayName is the name of the user which will be shown in chat messages
        user.setEmail(userEmail); //optional
        user.setAuthenticationTypeId(User.AuthenticationType.APPLOZIC.getValue());  //User.AuthenticationType.APPLOZIC.getValue() for password verification from Applozic server and User.AuthenticationType.CLIENT.getValue() for access Token verification from your server set access token as password
        user.setPassword(""); //optional, leave it blank for testing purpose, read this if you want to add additional security by verifying password from your server https://www.applozic.com/docs/configuration.html#access-token-url
        user.setImageLink(AppConstants.URL_DOMAIN+"upload/user"+ userId+"/profilepic.jpg"); //optional,pass your image link
        new UserLoginTask(user, listener, this).execute((Void) null);
    }

    private void initPushForMessage(final String fcmToken) {
        if(MobiComUserPreference.getInstance(MainActivity.this).isRegistered()) {

            PushNotificationTask pushNotificationTask = null;
            PushNotificationTask.TaskListener listener = new PushNotificationTask.TaskListener() {
                @Override
                public void onSuccess(RegistrationResponse registrationResponse) {
                    Log.e(TAG, "Success init push for chat");
                    if (MobiComUserPreference.getInstance(MainActivity.this).isRegistered()) {
                        try {
                            new AsyncUpdateGCM().execute(fcmToken);
                        } catch (Exception e) {
                            Log.e("gcmupdateerr",e.toString());
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void onFailure(RegistrationResponse registrationResponse, Exception exception) {
                    Log.e(TAG, "failure init push for chat");
                }

            };

            pushNotificationTask = new PushNotificationTask(fcmToken, listener, MainActivity.this);
            pushNotificationTask.execute((Void) null);
        }
    }


    public static void replaceFragment(Fragment fragment, android.app.FragmentManager manager, Activity activity, int id) {
/*
        if(!MyApplication.isConnectingToInternet()) {
            Snackbar.make(activity.findViewById(android.R.id.content),"No Internet Connection Found!",Snackbar.LENGTH_LONG).show();
            return;
        }
*/

        try {
            String backStateName = fragment.getClass().getName();
            String fragmentTag = backStateName;

            boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

            if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) {
                //fragment not in back stack, create it.
                FragmentTransaction ft = manager.beginTransaction();
                ft.replace(id, fragment, fragmentTag);
                ft.addToBackStack(backStateName);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

                ft.commit();
            }
        }catch (IllegalStateException e){
            Log.e("FragemtnREpl","Failed");
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.content_frame);
        if(currentFragment instanceof FragmentHome) {
            if (!FragmentHome.swipeRefreshLayout.isRefreshing()) {
                android.app.AlertDialog.Builder adb = new android.app.AlertDialog.Builder(MainActivity.this);
                adb.setMessage("Do you want to Exit ?");
                adb.setTitle("Exit");
                adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);
                    }
                });
                adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                adb.show();
            }
        }
        else if(currentFragment instanceof FragmentUser && getIntent().getBooleanExtra("startUserWrapper",false)){
            MainActivity.replaceFragment(new FragmentHome(),getFragmentManager(),MainActivity.this,R.id.content_frame);
        }
        else
            super.onBackPressed();
    }

    private void showLogoutDialog() {
        new android.app.AlertDialog.Builder(MainActivity.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Log Out")
                .setMessage("Are you sure you want to Log Out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        logout();


                    }
                    //dialog.dismiss();

                }).setNegativeButton("No", null).show();
    }

    private void logout() {
        UserLogoutTask.TaskListener userLogoutTaskListener = new UserLogoutTask.TaskListener() {
            @Override
            public void onSuccess(Context context) {
                //Logout success
                Log.e("lOGOUT Success", "Ok");
                // Clearing all sharedPreferences
                SharedPreferenceUtility.getInstance().clearSharedPreferences();

                // Clearing Notifications and handlers
                OneSignal.clearOneSignalNotifications();
                OneSignal.removeNotificationOpenedHandler();
                OneSignal.removeNotificationReceivedHandler();

                SharedPreferenceUtility.getInstance().save(AppConstants.PREF_INTRO,true);
                startActivity(new Intent(MainActivity.this, LoginActivity.class));

                finish();

            }
            @Override
            public void onFailure(Exception exception) {
                //Logout failure
                Log.e("lOGOUT Failed", exception.toString());
                Toast.makeText(MainActivity.this, "Can not logout the user", Toast.LENGTH_SHORT).show();
            }
        };

        UserLogoutTask userLogoutTask = new UserLogoutTask(userLogoutTaskListener, MainActivity.this);
        userLogoutTask.execute((Void) null);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        //searchView.setMaxWidth(200);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Search","click");
                text_title.setVisibility(View.GONE);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.e("Close","CLOSE");
                text_title.setVisibility(View.VISIBLE);
                return false;
            }
        });

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        ((EditText)  searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text))
                .setTextColor(getResources().getColor(android.R.color.white));

//        ((EditText)  searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text))
//                .setHintTextColor(getResources().getColor(android.R.color.white));

        ((ImageView)  searchView.findViewById(android.support.v7.appcompat.R.id.search_button))
                .setImageResource(R.drawable.ic_search);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("acti","acitivy");
                text_title.setVisibility(View.VISIBLE);
                if(query.length()>0) {
                    searchView.setQuery("", false);
                    searchView.clearFocus();
                    searchView.setIconified(true);
                    drawerLayout.closeDrawer(listLeftDrawer,true);
                    Fragment fragment = new FragmentSearch();
                    Bundle bundle = new Bundle();
                    bundle.putString("query",query);
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.content_frame,fragment).addToBackStack(null).commit();
//                    replaceFragment(fragment,getFragmentManager(),MainActivity.this,R.id.content_frame);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    private void drawerClick(int position) {
        //mTitle = navMenuTitles[position];
        Fragment fragment=null;
        switch (position){
            case 0:
                fragment=new FragmentHome();
                break;
            case 1:
                if(!SharedPreferenceUtility.getInstance().get(AppConstants.PREF_IS_BUSINESS,false)) {
                    Dialog dialogPayment = loadDialog();
                    dialogPayment.show();
                    //fragment = new FragmentSubscription();
                }
                else {
                    fragment = new FragmentCompany();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isOwnCompany",true);
                    FragmentCompany.isOwnCompany = true;
                    FragmentCompany.companyId = SharedPreferenceUtility.getInstance().get(AppConstants.PREF_COMPANY_ID,0);
                    fragment.setArguments(bundle);
                }
                break;
            case 2:
                fragment=new FragmentCard();
                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.KEY_USER_ID, (SharedPreferenceUtility.getInstance().get(PREF_USER_ID,0))+"");
                fragment.setArguments(bundle);
                break;
            case 3:
                Intent intent = new Intent(MainActivity.this, ConversationActivity.class);
                startActivity(intent);
                break;
            case 4:
                fragment=new FragmentSettings();
                break;
            case 5:
                fragment = new FragmentRateUs();
                break;
            case 6:
                showLogoutDialog();
                break;
        }
        if(fragment!=null)
            replaceFragment(fragment,getFragmentManager(),MainActivity.this,R.id.content_frame);
        drawerLayout.closeDrawer(listLeftDrawer,true);
    }



    class MenuAdapter extends BaseAdapter {

        private LayoutInflater vi;
        private ViewHolder viewHolder;

        public MenuAdapter() {
            // TODO Auto-generated constructor stub
            vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return navMenuTitles.length;
        }


        class ViewHolder
        {
            TextView textTitle;
            ImageView imageIcon;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view=convertView;
            if(view==null)
            {
                viewHolder = new ViewHolder();
                view = vi.inflate(R.layout.row_menu_item, parent, false);
                viewHolder.textTitle = (TextView) view.findViewById(R.id.textTitle);
                viewHolder.imageIcon = (ImageView) view.findViewById(R.id.imageIcon);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.textTitle.setText(navMenuTitles[position]);
            switch (position){
                case 0:
                    viewHolder.imageIcon.setImageResource(R.drawable.ic_menu_home);
                    break;
                case 1:
                    viewHolder.imageIcon.setImageResource(R.drawable.ic_menu_user);
                    break;
                case 2:
                    viewHolder.imageIcon.setImageResource(R.drawable.ic_menu_user);
                    break;
                case 3:
                    viewHolder.imageIcon.setImageResource(R.drawable.ic_chat);
                    break;
                case 4:
                    viewHolder.imageIcon.setImageResource(R.drawable.ic_setting);
                    break;
                case 5:
                    viewHolder.imageIcon.setImageResource(R.drawable.ic_rate);
                    break;
                case 6:
                    viewHolder.imageIcon.setImageResource(R.drawable.ic_menu_logout);
                    break;
            }

            return view;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //list of photos of seleced
        if (requestCode == LocationActivity.REQUEST_CHECK_SETTINGS)
            super.onActivityResult(requestCode, resultCode, data);

        else if (requestCode == ConversationUIService.REQUEST_CODE_CONTACT_GROUP_SELECTION && resultCode == RESULT_OK) {
            String userId = data.getStringExtra(ConversationUIService.USER_ID);
            Integer groupId = data.getIntExtra(ConversationUIService.GROUP_ID, -1);

            String oldMessage = data.getStringExtra(MobiComKitPeopleActivity.FORWARD_MESSAGE);
            Message message = (Message)GsonUtils.getObjectFromJson(oldMessage, Message.class);

            Intent conversationIntent = new Intent(this, ConversationActivity.class);
            if (!TextUtils.isEmpty(userId)) {
                conversationIntent.putExtra(ConversationUIService.USER_ID, userId);
                message.setTo(userId);
                message.setContactIds(userId);
            }

            if (groupId != null  && groupId != -1) {
                conversationIntent.putExtra(ConversationUIService.GROUP_ID, groupId);
                message.setGroupId(groupId);
            }

            conversationIntent.putExtra(MobiComKitPeopleActivity.FORWARD_MESSAGE, GsonUtils.getJsonFromObject(message, message.getClass()));
            conversationIntent.putExtra(ConversationUIService.TAKE_ORDER, true);
            startActivity(conversationIntent);
            finish();
        }

        else{
            if (data != null) {
                Log.e("TAG", "onActivityResult: ");
                int size;
                List<String> photos = new ArrayList<>();
                Bundle bundle = data.getExtras();

                if (bundle != null) {
                    for (String key : bundle.keySet()) {
                        Object value = bundle.get(key);
                        Log.e("HELLO", String.format("%s %s (%s)", key,
                                value.toString(), value.getClass().getName()));
                    }
                }

                photos = (List<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);
                if (photos==null) {
                    photos = new ArrayList<>();
                    photos.add(data.getStringExtra(GalleryActivity.VIDEO));
                }

                size = photos.size();

                if (getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentGroupDetail) {
                    FragmentGroupDetail.fileArrayList.clear();
                    FragmentGroupDetail.textFilesCount.setVisibility(View.VISIBLE);
                    FragmentGroupDetail.textFilesCount.setText((size == 1) ? "1 File Selected" : (size + " Files Selected"));
                    for (int i = 0; i < photos.size(); i++) {
                        FragmentGroupDetail.fileArrayList.add(new File(photos.get(i)));
                    }
                } else if (getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentCompany) {
                    FragmentCompany.fileArrayList.clear();
                    FragmentCompany.textFilesCount.setVisibility(View.VISIBLE);
                    FragmentCompany.textFilesCount.setText((size == 1) ? "1 File Selected" : (size + " Files Selected"));
                    for (int i = 0; i < photos.size(); i++) {
                        FragmentCompany.fileArrayList.add(new File(photos.get(i)));
                    }
                } else if (getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentHome) {
/*
                    FragmentHome.fileArrayList.clear();
                    FragmentHome.textFilesCount.setVisibility(View.VISIBLE);
                    FragmentHome.textFilesCount.setText((size == 1) ? "1 File Selected" : (size + " Files Selected"));
                    for (int i = 0; i < photos.size(); i++) {
                        FragmentHome.fileArrayList.add(new File(photos.get(i)));
                    }
*/
                } else {
                    FragmentUploadsWrapper.fileArrayList.clear();
                    textFilesCount.setVisibility(View.VISIBLE);
                    textFilesCount.setText((size == 1) ? "1 File Selected" : (size + " Files Selected"));
                    for (int i = 0; i < photos.size(); i++) {
                        FragmentUploadsWrapper.fileArrayList.add(new File(photos.get(i)));
                    }
                }
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }



    public boolean isUserInApplozic(String userID){

        if(TextUtils.isEmpty(userID)){
            Log.e("UserId","is null");
        }
        UserClientService userClientService = new UserClientService(MainActivity.this);
        Set<String> userIds = new HashSet<>();
        userIds.add(userID);
        String response = userClientService.getUserDetails(userIds);
        if (!TextUtils.isEmpty(response)) {
            UserDetail[] userDetails = (UserDetail[]) GsonUtils.getObjectFromJson(response, UserDetail[].class);
            if(userDetails != null && userDetails.length>0 ){
                UserDetail userDetail = userDetails[0];
                if(userDetail != null){
                    Log.e("user is "," Registered ");
                    return true;
                }
            }else {
                Log.e("user is ","not Registered ");
                return false;
            }
        }
        Log.e("user is ","not Registered ");
        return false;
    }

    public class AsyncUpdateGCM extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;


        @Override
        protected String doInBackground(String... params) {
            String data="null";
            try {
                new RegisterUserClientService(MainActivity.this).updatePushNotificationId(gcmToken);
            }catch (Exception e){}
            return gcmToken;
        }
        @Override
        protected void onPostExecute(String result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
            Log.e("GCMIDapplozic","success___"+result);

        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private Dialog loadDialog() {
        final Dialog dialogPayment = new Dialog(MainActivity.this);
        dialogPayment.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPayment.setContentView(R.layout.dialog_payment_type);
        dialogPayment.setCancelable(true);

        ImageView imgPayFast = (ImageView) dialogPayment.findViewById(R.id.img_payfast);
        ImageView imgPayPal = (ImageView) dialogPayment.findViewById(R.id.img_paypal);

        imgPayFast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FragmentSubscription();
                replaceFragment(fragment,getFragmentManager(),MainActivity.this, R.id.content_frame);
                dialogPayment.dismiss();
            }
        });
        imgPayPal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FragmentSubscriptionPayment();
                replaceFragment(fragment,getFragmentManager(),MainActivity.this, R.id.content_frame);
                dialogPayment.dismiss();
            }
        });

        dialogPayment.show();
        return dialogPayment;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemLive= menu.findItem(R.id.menu_live);
        itemLive.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                replaceFragment(new FragmentRadio(), getFragmentManager(), MainActivity.this, R.id.content_frame);
                return false;
            }
        });
        return super.onPrepareOptionsMenu(menu);

    }



    private void replaceFragmentAccordingly() {
        Log.e("Replaceing","PRELACING");
        if(getIntent().getBooleanExtra("startUserWrapper",false)){
            Fragment fragment=new FragmentUser();
            Bundle bundle = new Bundle();
            bundle.putString(AppConstants.KEY_USER_ID,getIntent().getStringExtra(AppConstants.KEY_USER_ID));
            bundle.putString(AppConstants.KEY_USER_NAME,getIntent().getStringExtra(AppConstants.KEY_USER_NAME));
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.content_frame,fragment).commit();
        }

        else if(getIntent().hasExtra("actionView")){
            String action = getIntent().getStringExtra("actionView");
            if(action.contains("carduser")){
                String userId = action.split("=")[1];
                Fragment fragment = new FragmentCard();
                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.KEY_USER_ID, userId);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.content_frame,fragment).commit();
            }
            else if(action.contains("fileid")){
                String fileId = action.split("=")[1];
                Fragment fragment = new FragmentFile();
                Bundle bundle = new Bundle();
                bundle.putInt("fildeId",Integer.parseInt(fileId));
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.content_frame,fragment).commit();

            }
            else if(action.contains("groupprofile")){
                String groupId = action.split("=")[1];
                Fragment fragment = new FragmentGroupDetail();
                Bundle bundle = new Bundle();
                bundle.putString(AppConstants.KEY_GROUP_ID,groupId);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.content_frame,fragment).commit();

            }
            else if (action.contains("Prmotions")){
                String url = action;
                Fragment fragment = new FragmentSharedPromotion();
                Bundle bundle = new Bundle();
                bundle.putString("promotion",url);
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.content_frame,fragment).commit();

            }
            else
                replaceFragment(new FragmentHome(),getFragmentManager(),MainActivity.this,R.id.content_frame);

        }

        else if(getIntent().hasExtra("fromOneSignal")){
            try {
                JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("fromOneSignal"));
                Log.e("MainActivity", "init: "+jsonObject.toString() );
                if(jsonObject.optString("type").equals("file")){
                    String fileId = jsonObject.optString("fileid");
                    Fragment fragment = new FragmentFile();
                    Bundle bundle = new Bundle();
                    bundle.putInt("fildeId",Integer.parseInt(fileId));
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.content_frame,fragment).commit();
                }
                else
                    replaceFragment(new FragmentHome(),getFragmentManager(),MainActivity.this,R.id.content_frame);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        else {
            replaceFragment(new FragmentHome(), getFragmentManager(), MainActivity.this, R.id.content_frame);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        isActive = true;
    }


    public Message buildMessages(ArrayList<File> fileArrayList, String text, Activity activity)
    {
        Message message = new Message();
        MobiComUserPreference userPreferences = MobiComUserPreference.getInstance(activity);
        message.setRead(Boolean.TRUE);
        message.setStoreOnDevice(Boolean.TRUE);
        message.setCreatedAtTime(System.currentTimeMillis() + userPreferences.getDeviceTimeOffset());
        message.setSendToDevice(Boolean.FALSE);
        message.setType(Message.MessageType.MT_OUTBOX.getValue());
        message.setSource(Message.Source.MT_MOBILE_APP.getValue());
        message.setMessage(text);

        if (fileArrayList!=null) {
            List<String> fileList = new ArrayList<String>();
            for (int i = 0; i < fileArrayList.size(); i++) {
                fileList.add(fileArrayList.get(i).getPath());
            }

            message.setFilePaths(fileList);
        }

        return message;
    }


    public void startMessageForward(Message message, Activity activity)
    {
        Intent intent = new Intent(activity, MobiComKitPeopleActivity.class);
        if (message != null) {
            intent.putExtra(MobiComKitPeopleActivity.FORWARD_MESSAGE, GsonUtils.getJsonFromObject(message, message.getClass()));
        }
        activity.startActivityForResult(intent, ConversationUIService.REQUEST_CODE_CONTACT_GROUP_SELECTION);
    }


    public class VersionChecker extends AsyncTask<String, String, String> {

        String newVersion;
        ArrayList<String> arrayList = new ArrayList<>();
        String versionName;

        @Override
        protected String doInBackground(String... params) {

            try {

                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + "com.parasme.swopinfo" + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();

                Elements elements =  Jsoup.connect("https://play.google.com/store/apps/details?id=" + "com.parasme.swopinfo" + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[class=recent-change]");

                for (Element element : elements){
                    String a = element.text();
                    arrayList.add(a);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return newVersion;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                PackageInfo pInfo = MainActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                String codeVersion = pInfo.versionName;
                Log.e("codeversion", codeVersion);

                if (newVersion != null && codeVersion != null) {
                    if (Float.parseFloat(newVersion) > Float.parseFloat(codeVersion)) {
                        Intent intent = new Intent(MainActivity.this, NewVersionActivity.class);
                        intent.putExtra("newVersion", newVersion);
                        intent.putExtra("whatsNew", arrayList);
                        startActivity(intent);
                        finish();
                    } else {
                        delete7DaysOlderCache();
                        replaceFragmentAccordingly();
                    }
                }
                else{
                    delete7DaysOlderCache();
                    replaceFragmentAccordingly();
                }
            }

            catch (PackageManager.NameNotFoundException e){e.printStackTrace();}
        }
    }


}