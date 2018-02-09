package com.parasme.swopinfo.application;

/**
 * Created by Mukesh Kumawat on 15-Aug-16.
 * Designation Android Team Leader
 * Organization Parasme Software And Technology
 * Email mukeshkmtskr@gmail.com
 * Mobile +917737556190
 */
public class AppConstants {

    // SharedPreerences Keys
    public static final String PREF_LOGIN="isLoggedIn";
    public static final String PREF_USER_ID ="userId";
    public static final String PREF_USER_NAME ="userName";
    public static final String PREF_USER_FIRST_NAME ="userFirstName";
    public static final String PREF_USER_SUR_NAME ="userSurname";
    public static final String PREF_USER_EMAIL ="userEmail";
    public static final String PREF_COMPANY_ID ="companyId";
    public static final String PREF_NOTIFICATION ="receiveEmailNotifications";
    public static final String PREF_COUNTRY ="country";
    public static final String PREF_BDAY ="bday";
    public static final String PREF_BUSINESS_TITLE ="businessTitle";
    public static final String PREF_BUSINESS_EMAIL ="businessEmail";
    public static final String PREF_BUSINESS_CELL ="businessCell";
    public static final String PREF_BUSINESS_TEL ="businessDirecttel";
    public static final String PREF_BUSINESS_FAX ="businesscustomfax";
    public static final String PREF_BUSINESS_CUSTOMF1 ="businesscustomfield1";
    public static final String PREF_BUSINESS_CUSTOMF2 ="businesscustomfield2";
    public static final String PREF_BUSINESS_STATUS ="businessstatus";
    public static final String PREF_BUSINESS_EMP_DATE ="businessempdate";
    public static final String PREF_BUSINESS_PROFESSION ="businessprofession";
    public static final String PREF_BUSINESS_COMPANY ="businessCompanyName";
    public static final String PREF_AUTH_TOKEN ="authToken";
    public static final String PREF_IS_BUSINESS ="isBusiness";
    public static final String PREF_OWN_COMPANY_ID ="ownCompanyId";
    public static final String PREF_PLAYER_ID ="playerId";
    public static final String PREF_INTRO = "app_intro";
    public static final String PREF_APPLOZIC_LOGIN = "applozic_login";
    public static final String PREF_GCM_TOKEN = "gcm_token";
    public static final String PREF_DISABLE_APP_NOTIFICATION = "app_notification";
    public static final String PREF_ENABLE_CHAT_NOTIFICATION = "chat_notification";
    public static final String PREF_CHECK_IN_INTRO = "check_in_intro";
    public static final String PREF_FAV_IDS = "fav_ids";
    public static final String PREF_RETAILER_MSGS = "retailer_msgs";
    public static final String PREF_TIME = "pref_time";

    public static final String KEY_IS_GROUP_UPLOADS ="keyIsGroupUploads";
    public static final String KEY_USER_ID ="keyUserId";
    public static final String KEY_GROUP_ID ="keyGroupId";
    public static final String KEY_GROUP_NAME ="keyGroupName";
    public static final String KEY_USER_NAME ="keyUserName";
    public static final String KEY_COMPANY_ID ="keyCompanyId";

    public static String USER_IMAGE_URL ="url";
    public static String USER_ID ="userid";
    public static String AUTH_TOKEN ="authotoken";
    public static String UPLOAD_VIEWS ="N/A";
    public static String UPLOAD_DOWNLOADS ="N/A";
    public static String PROFILE_VIEWS ="N/A";

    public static final String GROUP_UPLOADS="groupUploads";
    public static final String USER_UPLOADS="userUploads";
    public static final String OWN_UPLOADS="ownUploads";
    public static final String FOLDER_UPLOADS="folderUploads";

    /************************** WEB SERVICE URLs*****************************/

    public static final String URL_DOMAIN = "https://swopinfo.com/";

//    public static final String URL_DOMAIN = "http://dev.swopinfo.com/";
    public static final String URL_FILE = URL_DOMAIN + "api/file/";
    public static final String URL_GROUP = URL_DOMAIN + "api/Group/";
    public static final String URL_USER_GROUP = URL_DOMAIN + "api/UserGroup/";
    public static final String URL_GET_COMMENTS = URL_FILE + "/comments/";
    public static final String URL_ADD_COMMENT = URL_FILE + "addcomment/";
    public static final String URL_DELETE_COMMENT = URL_FILE + "deletecomment/";
    public static final String URL_JOIN_GROUP = URL_USER_GROUP + "insert";
    public static final String URL_LEAVE_GROUP = URL_USER_GROUP + "delete";
    public static final String URL_CREATE_GROUP = URL_GROUP + "insert";
    public static final String URL_FOLLOWING = URL_DOMAIN + "api/Following/";
    public static final String URL_LOGIN = URL_DOMAIN + "api/Authentication";
    public static final String URL_FORGOT = URL_DOMAIN + "api/password/recover";
    public static final String URL_USER = URL_DOMAIN + "api/User/";
    public static final String URL_REGISTER = URL_DOMAIN + "api/user/register";
    public static final String URL_REGISTER_WITHOUT_OTP = URL_DOMAIN + "api/user/registerwithoutOTP";
    public static final String URL_MATCH_OTP = URL_DOMAIN + "api/user/confirmreg";
    public static final String URL_UPDATE = URL_DOMAIN + "api/user/update";
    public static final String URL_FOLLOWERS = URL_DOMAIN + "api/Followers/";
    public static final String URL_FEED = URL_DOMAIN + "api/NewsFeed";
    public static final String URL_PROFESSIONS = URL_DOMAIN + "api/professions";
    public static final String URL_UPLOAD_FILE = URL_USER + "uploadfiles";
    public static final String URL_UPDATE_PROFILE_PIC = URL_DOMAIN + "api/UserProfilePic/";
    public static final String URL_CARD = URL_DOMAIN + "api/card/";
    public static final String URL_DELETE_PROFILE = URL_DOMAIN + "api/user/";
    public static final String URL_COMAPNY = URL_DOMAIN + "api/company/";
    public static final String URL_COMAPNY_REGISTER = URL_DOMAIN + "api/company/register";
    public static final String URL_COMAPNY_ACTIVATE = URL_DOMAIN + "api/company/registercomplete";
    public static final String URL_CHECKIN_COUNT = URL_DOMAIN + "checkincounts.aspx?";
    public static final String URL_FB_LOGIN = URL_DOMAIN + "api/facebook";
}