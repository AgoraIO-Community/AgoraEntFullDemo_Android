package com.agora.entfulldemo.common;

public class KtvConstant {
    public static final String IS_CONNECT_SUCCESS = "isConnectSuccess";
    public static String RTM_TOKEN = "";
    public static String RTC_TOKEN = "";
    public static String PLAYER_TOKEN = "";

    public static final String CURRENT_USER = "current_user";
    public static final String CURRENT_MEMBER_ID = "current_member_id";
    public static final String FIRST_REPORT = "first_report";
    public static final String USER_ID = "user_id";
    public static final String SCAN_RESULT = "scan_result";
    public static final String DEVICE_ID = "device_id";
    public static final String WIFI_NAME = "wifi_name";
    public static final String WIFI_PWD = "wifi_pwd";
    public static final String FROM_QR_C = "from_qr_c";
    public static final String FROM_QR_K = "from_qr_k";
    public static final String ID = "id";
    public static final String SSID = "ssid";

    public static final String TIME = "time";

    public static final String CODE = "code";

    public static final String USER_NAME = "user_name";

    public static final String OBJECT = "object";
    public static final String PHONE = "phone";
    public static final String URL = "url";

    public static final String IS_FORGE_PASSWORD = "is_forge_password";

    public static final String COUNTRY = "country";

    public static final String IS_SUCCESS = "is_success";

    public static final String IS_FIRST = "is_first";

    public static final String FILE_URL = "file_url";

    public static final String FILE_DESCRIPTION = "file_description";

    public static final String IS_AGREE = "is_agree";

    public static final String MESSAGE_TITLE = "message_title";

    public static final String MESSAGE_TIME = "message_time";

    public static final String ACCOUNT = "account";

    public static final String TYPE = "type";

    public static final String AVATAR = "avatar";

    public static final String V_CODE = "v_code";

    public static final String COUNTRY_NAME = "country_name";


    /* ?????????????????? */
    public static final int CALLBACK_TYPE_EXIT_STEP = -1;
    /* ?????????????????? */
    public static final int CALLBACK_TYPE_USER_INFO_CHANGE = 900;
    /* ?????? */
    public static final int CALLBACK_TYPE_USER_CANCEL_ACCOUNTS = 901;
    /* ????????????*/
    public static final int CALLBACK_TYPE_USER_LOGOUT = 902;


    /* ?????????????????? */
    public static final int CALLBACK_TYPE_ROOM_GET_ROOM_LIST_SUCCESS = 10;
    /* ???????????????????????? */
    public static final int CALLBACK_TYPE_ROOM_GET_ROOM_LIST_FAIL = 11;
    /* ?????????????????? */
    public static final int CALLBACK_TYPE_ROOM_CREATE_SUCCESS = 12;
    /* ?????????????????? */
    public static final int CALLBACK_TYPE_ROOM_CREATE_FAIL = 13;
    /* ?????????????????? */
    public static final int CALLBACK_TYPE_ROOM_JOIN_SUCCESS = 14;
    /* ?????????????????? */
    public static final int CALLBACK_TYPE_ROOM_JOIN_FAIL = 15;
    /* ???????????????????????? */
    public static final int CALLBACK_TYPE_ROOM_ON_SEAT = 16;
    /* ???????????????????????? */
    public static final int CALLBACK_TYPE_ROOM_LEAVE_SEAT = 17;
    /* ???????????? */
    public static final int CALLBACK_TYPE_ROOM_EXIT = 18;
    /* ?????????????????? */
    public static final int CALLBACK_TYPE_SHOW_MUSIC_MENU_DIALOG = 19;
    /* ????????????????????? */
    public static final int CALLBACK_TYPE_SHOW_CHANGE_MUSIC_DIALOG = 20;
    /* ?????? */
    public static final int CALLBACK_TYPE_TOGGLE_MIC = 21;
    /* ???????????? */
    public static final int CALLBACK_TYPE_ROOM_BG_CHANGE = 22;
    /* ???????????? */
    public static final int CALLBACK_TYPE_ROOM_SEAT_CHANGE = 23;
    /* ??????????????? */
    public static final int CALLBACK_TYPE_ROOM_MEMBER_COUNT_UPDATE = 24;
    /* ???????????? */
    public static final int CALLBACK_TYPE_ROOM_NETWORK_STATUS = 25;
    /* ??????token?????? */
    public static final int CALLBACK_TYPE_ROOM_RTM_RTC_TOKEN = 26;
    /* ???????????? */
    public static final int CALLBACK_TYPE_ROOM_PASSWORD_ERROR = 27;


    /* ????????????*/
    public static final int CALLBACK_TYPE_LOGIN_REQUEST_LOGIN_SUCCESS = 403;
    /* ????????????*/
    public static final int CALLBACK_TYPE_LOGIN_REQUEST_LOGIN_FAIL = 404;


    public static final int CALLBACK_TYPE_ROOM_LIVING_ON_LOCAL_PITCH = 1001;
    public static final int CALLBACK_TYPE_ROOM_LIVING_ON_ROOM_INFO_CHANGED = 1002;
    public static final int CALLBACK_TYPE_ROOM_LIVING_ON_MEMBER_LEAVE = 1003;
    public static final int CALLBACK_TYPE_ROOM_LIVING_EXIT = 1004;

    public static final int CALLBACK_TYPE_ROOM_LIVING_ON_CONTROL_VIEW_ENABLED = 1006;

    public static final int CALLBACK_TYPE_ROOM_LIVING_ON_MEMBER_JOIN = 1007;

    public static final int CALLBACK_TYPE_ROOM_LIVING_ON_SEAT_STATUS = 1008;

    public static final int CALLBACK_TYPE_ROOM_LIVING_ON_MIC_STATUS = 1009;

    public static final int CALLBACK_TYPE_ROOM_LIVING_ON_VIDEO_STATUS_CHANGED = 1010;

    public static final int CALLBACK_TYPE_ROOM_LIVING_ON_MUSIC_DEL = 1011;

    public static final int CALLBACK_TYPE_ROOM_LIVING_ON_CONTROL_VIEW_STATUS = 1012;

    public static final int CALLBACK_TYPE_ROOM_LIVING_ON_MUSIC_CHANGED = 1013;

    public static final int CALLBACK_TYPE_ROOM_LIVING_ON_CONTROL_VIEW_PITCH_LRC_DATA = 1014;

    public static final int CALLBACK_TYPE_ROOM_LIVING_ON_CONTROL_VIEW_TOTAL_DURATION = 1015;

    public static final int CALLBACK_TYPE_ROOM_LIVING_ON_CONTROL_VIEW_UPDATE_TIME = 1016;

    public static final int CALLBACK_TYPE_ROOM_LIVING_ON_COUNT_DOWN = 1017;
    public static final int CALLBACK_TYPE_ROOM_LIVING_ON_MUSICEMPTY = 1018;
    public static final int CALLBACK_TYPE_ROOM_LIVING_ON_JOINED_CHORUS = 1019;

    public static final int CALLBACK_TYPE_ROOM_LIVING_ON_SHOW_MEMBER_STATUS = 1020;

    public static final int CALLBACK_TYPE_ROOM_LIVING_ON_PLAY_COMPLETED = 1021;

    public static final int TYPE_CONTROL_VIEW_STATUS_ON_PREPARE = 1;

    public static final int TYPE_CONTROL_VIEW_STATUS_ON_WAIT_CHORUS = 2;

    public static final int TYPE_CONTROL_VIEW_STATUS_ON_PLAY_STATUS = 3;
    public static final int TYPE_CONTROL_VIEW_STATUS_ON_PAUSE_STATUS = 4;
    public static final int TYPE_CONTROL_VIEW_STATUS_ON_LRC_RESET = 5;

    public static final int TYPE_CONTROL_VIEW_STATUS_ON_MIC_MUTE = 6;
    public static final int TYPE_CONTROL_VIEW_STATUS_ON_VIDEO = 7;
    public static final int TYPE_CONTROL_VIEW_STATUS_ON_CREATOR_EXIT = 8;


}
