package com.karacraft.utils;

/**
 * Created by       duke / Kara Craft
 * For Project      MasterSahab
 * Dated            Dec 08 2015
 * File Name        Constants.java
 * Comments         Application wide Constants
 */
public final class Constants
{
    /** Android Internal Usage */
    public static final String BASE_NAME    =   "com.karacraft";
    public static final String APP_TAG      =   "PTI";

    /** Oracle Database Preferences Keys */
    public static final String ORACLE_SERVER_IP_KEY     ="oracle_server_ip_key";
    public static final String ORAClE_SERVER_PORT_KEY   ="oracle_server_port_key";
    public static final String ORAClE_SERVER_USER_KEY   ="oracle_server_user_key";
    public static final String ORAClE_SERVER_PASS_KEY   ="oracle_server_pass_key";

    /** String Support */
    public static final String EMPTY_STRING         = "";
    public static final String SPACE                = " ";
    public static final String TAB                  = "\t";
    public static final String SINGLE_QUOTE         = "'";
    public static final String PERIOD               = ".";
    public static final String DOUBLE_QUOTE         = "\"";
    public static final String AT_THE_RATE          = "@";
    public static final String FORWARD_SLASH        = "/";
    public static final String EQUALS               = "=";
    public static final String AMPERSAND            = "&";

    /** Http Responses */
    public static final String RESPONSE_401 ="Error 401 : Invalid credentials";
    public static final String RESPONSE_404 ="Error 404 : Page not found.";
    public static final String RESPONSE_408 ="Error 408 : Request timeout";
    public static final String RESPONSE_500 ="Error 500 : Unable to create token";
    public static final String RESPONSE_503 ="Error 503 : Service not available";

    /** Opposite of {@link #FAILURE}.  */
    public static final boolean SUCCESS     = true;
    /** Opposite of {@link #SUCCESS}.  */
    public static final boolean FAILURE     = false;
    /** Opposite of {@link #FAILS}.  */
    public static final boolean PASSES      = true;
    /** Opposite of {@link #PASSES}.  */
    public static final boolean FAILS       = false;
    /** System property - <tt>line.separator</tt>*/
    public static final String NEW_LINE         = System.getProperty("line.separator");
    /** System property - <tt>file.separator</tt>*/
    public static final String FILE_SEPARATOR   = System.getProperty("file.separator");
    /** System property - <tt>path.separator</tt>*/
    public static final String PATH_SEPARATOR   = System.getProperty("path.separator");

    /**         Application Specific            */
    public static final String APP_URL          ="http://192.168.0.116/akms/public/";  /** Change this to Website in release */
//    public static final String APP_URL          ="http://192.168.1.102/akms/public/";  /** Change this to Website in release */
    //public static final String APP_URL          ="http://test.karacraft.com/public/";
    public static final String MOBILE_URL       ="mobile/";
    public static final String LOGIN_URL        =APP_URL + MOBILE_URL + "login?";       //Get Token (POST)
    public static final String LOGOUT_URL       =APP_URL + MOBILE_URL + "logout?";      //Remove Token, (POST)
    public static final String FETCH_ALL        =APP_URL + MOBILE_URL + "fetchall?";    //Get All Data (GET)
    public static final String CHECK_STATS      =APP_URL + MOBILE_URL + "checkstats?";  //Get Stats (GET)
    public static final String UPLOAD_IMAGE     =APP_URL + "up?";                       //Upload Pictures (POST)
    public static final String CREATE_ORDER     =APP_URL + MOBILE_URL + "createorder?"; //Create Order (POST)
    public static final String UPDATE_ORDER     =APP_URL + MOBILE_URL + "updateorder?"; //Update Order (POST)
    public static final String DELETE_ORDER     =APP_URL + MOBILE_URL + "deleteorder?"; //Delete Order (POST)
    public static final String UPDATE_TAILOR    =APP_URL + MOBILE_URL + "updatetailor?";//Update Tailor (POST)
    public static final String CREATE_MERCHANDISE     =APP_URL + MOBILE_URL + "createmerchandise?"; //Create Merchandise (POST)
    public static final String UPDATE_MERCHANDISE     =APP_URL + MOBILE_URL + "updatemerchandise?"; //Update Merchandise (POST)
    public static final String DELETE_MERCHANDISE     =APP_URL + MOBILE_URL + "deletemerchandise?"; //Delete Merchandise (POST)
    public static final String REFRESH_TOKEN          =APP_URL + MOBILE_URL + "refreshtoken?"; //Get
    public static final String RESPONSE_REPLY   ="reply";   //Sent by Server

    //Shared Preferences
    public static final String PREF_USERNAME_KEY         ="username";
    public static final String PREF_PASSWORD_KEY         ="password";
    public static final String PREF_USER_ID_KEY          ="id";
    public static final String PREF_USER_NAME_KEY        ="name";
    public static final String PREF_USER_ROLE_KEY        ="role";
    public static final String PREF_USER_CITY_KEY        ="city";
    public static final String PREF_LAST_UPDATE_KEY      ="lastupdatekey";

    //App wide
    public static final String APP_PREF_KEY             ="mastersahab";
    public static final String APP_TOKEN_KEY            ="token";
    public static final String APP_PIC_DIR              ="mastersahab";

    // Tags used in AysncRequest
    public static final String AR_GET_TOKEN         ="gettoken";
    public static final String AR_FETCH_INIT_DATA   ="getinitdata";
    public static final String AR_POST_IMAGE        ="uploadimage";
    public static final String KEY                  ="key";
    public static final String MESSAGE              ="message";

    public static final String GET                  ="GET";
    public static final String POST                 ="POST";
    public static final String PUT                  ="PUT";

    //KEYS To Use one Fragment for multiple Uses

    public static final String ORDER_BY             ="order_by";
    public static final String ORDER_TYPE           ="order_type";
    public static final String START_BY_TAILOR      ="start_by_tailor";
    public static final String START_BY_TAILOR_LIST ="start_by_tailor_list";
    public static final String START_BY_DASHBOARD   ="start_by_dash";
    public static final String START_BY_MERCHANDISE ="start_by_merchandise";
    public static final String START_BY_MERCHANDISE_LIST ="start_by_merchandise_list";
    public static final String START_BY_ORDER       ="start_by_order";
    public static final String START_BY_ORDER_LIST  ="start_by_order_list";
    public static final String START_BY_STAFF       ="start_by_staff";
    public static final String START_BY_ORDER_ADD_EDIT  ="start_by_order_add_edit";

    //Request Codes
    public static final int REQUEST_PENDING_ORDERS      =1001;
    public static final int REQUEST_ARCHIVE_ORDERS      =1002;
    public static final int REQUEST_TAILOR_FROM_LIST    =1003;
    public static final int REQUEST_STAFF_PICTURE       =1004;
    public static final int REQUEST_MERC_PICTURE        =1005;
    public static final int REQUEST_TAILOR_PICTURE      =1006;
    public static final int REQUEST_MERCHANDISE_ADD     =1007;
    public static final int REQUEST_MERCHANDISE_EDIT    =1008;
    public static final int REQUEST_ORDER_ADD           =1009;
    public static final int REQUEST_ORDER_EDIT          =1010;
    public static final int REQUEST_SUIT                =1011;

    //For Syncing Purpose
    public static final String DIRTY_NEW                = "new";
    public static final String DIRTY_UPDATE             = "update";

    /**
     Prevent usage of class by everyone.
     */
    private Constants(){
        //this prevents even the native class from
        //calling this actor as well :
        throw new AssertionError();
    }
}
