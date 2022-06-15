package com.codetreatise.thuydienapp.config;

import com.codetreatise.thuydienapp.bean.ApiConfig;
import com.codetreatise.thuydienapp.bean.Data;

import java.util.*;

public final class SystemArg {

    public static final String OKE_STATUS_STYLE = "-fx-background-color: green";
    public static final String ERROR_STATUS_STYLE = "-fx-background-color: red";
    public static final Map<String, String> mapErrorMenu = new HashMap<>();
    private SystemArg() {

    }

    public static String getStatusMenuStyle(String menuName) {
        return mapErrorMenu.get(menuName) == null ? mapErrorMenu.put(menuName, OKE_STATUS_STYLE) : mapErrorMenu.get(menuName);
    }

    public static List<Data> DATA_LIST = new ArrayList<>();
    public static Integer TIME_SCHEDULE_SYNC_MODBUS = 1000 * 60*10;
    public static Date NEXT_TIME_SCHEDULE_SYNC_MODBUS = new Date();
    public static Integer TIME_SCHEDULE_CALL_API = 1000 * 60 * 10;
    public static Date NEXT_TIME_SCHEDULE_CALL_API = new Date();

    public static String API_CALL_URL = "";

    public static String MODBUS_IP = "localhost";
    public static Integer MODBUS_PORT = 502;
    public static Byte UNIT = 1;

    public static boolean MODBUS_SYNC_READY = false;
    public static boolean API_CALL_API_READY = false;

    public static String API_USERNAME = "";
    public static String API_PASSWORD = "";
    public static String TOKEN = "";
    public static Boolean LOGIN = Boolean.FALSE;

    public static List<ApiConfig> API_LIST = new ArrayList<>();
    public static String NAME_API_CHOSEN = "";
    public static String NAME_FTP_CHOSEN = "";

    public static String MENU_ERROR_CHOSEN = "";
    public static String ERROR_TYPE_CHOSEN = "";


    public static Data findByKey(String key) {
        return SystemArg.DATA_LIST.stream().filter(data -> data.getKey().equals(key)).findFirst().orElse(null);
    }

    public static void setNextTimeScheduleSyncModbus() {
        Date now = new Date();
        now.setSeconds(0);
        SystemArg.NEXT_TIME_SCHEDULE_SYNC_MODBUS = new Date(now.getTime() + 60*1000);
    }

    public static boolean checkTimeScheduleSyncModbus() {
        Date now = new Date();
        now.setSeconds(0);

        return LOGIN && MODBUS_SYNC_READY && now.after(NEXT_TIME_SCHEDULE_SYNC_MODBUS)
                && (TIME_SCHEDULE_SYNC_MODBUS == 60 ||
                now.getMinutes() % TIME_SCHEDULE_SYNC_MODBUS == 0);
    }

    public static void setNextTimeScheduleCallApi() {
        Date now = new Date();
        now.setSeconds(0);
        SystemArg.NEXT_TIME_SCHEDULE_CALL_API = new Date(now.getTime() + 60*1000);
    }

    public static boolean checkTimeScheduleCallApi() {
        return LOGIN || !API_CALL_API_READY || new Date().before(NEXT_TIME_SCHEDULE_CALL_API) || API_CALL_URL.equals("");
    }
}
