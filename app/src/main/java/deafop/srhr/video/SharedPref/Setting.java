package deafop.srhr.video.SharedPref;

import android.content.Context;
import java.io.Serializable;
import java.util.ArrayList;

import deafop.srhr.video.Constant.Constant;
import deafop.srhr.video.item.ItemHomeBanner;
import deafop.srhr.video.item.itemVideo;
import deafop.srhr.video.Login.ItemUser;
import deafop.srhr.video.Receiver.ItemNemosofts;


public class Setting implements Serializable {


    public static String api="speed_api.php";
    public static String SERVER_URL = Constant.Saver_Url + api;

    public static String purchase_code = "";
    public static String nemosofts_key = "";
    public static ItemNemosofts itemAbout;
    public static boolean Dark_Mode = false;

    public static Boolean isAdmobBannerAd = true, isAdmobInterAd = true, isAdmobNativeAd = true, isFBBannerAd = true, isFBInterAd = true, isFBNativeAd = false;
    public static String ad_publisher_id = "";
    public static String ad_banner_id = "", ad_inter_id = "", ad_native_id = "", fb_ad_banner_id = "", fb_ad_inter_id = "", fb_ad_native_id = "";

    public static int adShow = 5;
    public static int adShowFB = 5;
    public static int adCount = 0;
    public static int admobNativeShow = 5, fbNativeShow = 5;

    public static Context context;

    public static String company = "";
    public static String email = "";
    public static String website = "";
    public static String contact = "";

    public static final String METHOD_APP_DETAILS = "app_details";
    public static final String METHOD_CAT = "cat_list";

    public static final String TAG_CAT_NAME = "category_name";
    public static final String TAG_CAT_IMAGE = "category_image";
    public static final String TAG_CID = "cid";
    public static final String METHOD_MOST_VIEWED = "most_viewed";
    public static final String METHOD_HOME = "home";
    public static final String METHOD_LATEST = "latest";
    public static final String METHOD_VIDEO_BY_CAT = "video_by_cat";
    public static final String METHOD_VIDEO_BY_CAT_NEW = "video_by_cat_new";
    public static final String METHOD_All_VIDEO = "All_Video";
    public static final String METHOD_VIDEO = "video";


    public static final String YOUTUBE_IMAGE_FRONT="http://img.youtube.com/vi/";
    public static final String YOUTUBE_SMALL_IMAGE_BACK="/hqdefault.jpg";

    public static final String DAILYMOTION_IMAGE_PATH="http://www.dailymotion.com/thumbnail/video/";

    public static final String TAG_ROOT = "nemosofts";

    public static final String TAG_SUCCESS = "success";
    public static final String TAG_MSG = "msg";

    public static ItemUser itemUser;
    public static Boolean isLogged = false;
    public static Boolean isLoginOn = true;

    public static ArrayList<itemVideo> arrayList = new ArrayList<>();
    public static ArrayList<ItemHomeBanner> arrayList2 = new ArrayList<>();

    public static Boolean getPurchases = false;
    public static Boolean in_app = true;
    public static  String SUBSCRIPTION_ID = "SUBSCRIPTION_ID";
    public static  String MERCHANT_KEY = "MERCHANT_KEY";
    public static int  SUBSCRIPTION_DURATION = 30;

    public static final String METHOD_LOGIN = "user_login";
    public static final String METHOD_REGISTER = "user_register";
    public static final String METHOD_COMMENT = "comment";
    public static final String METHOD_COMMENT_ID = "comment_video";
}