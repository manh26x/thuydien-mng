package com.codetreatise.thuydienapp.config;

import com.codetreatise.thuydienapp.bean.ApiConfig;
import com.codetreatise.thuydienapp.bean.Data;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class SystemArg {

    private SystemArg() {

    }

    public static List<Data> DATA_LIST = new ArrayList<>();
    public static Integer TIME_SCHEDULE_SYNC_MODBUS = 1000 * 60*10;
    public static Date NEXT_TIME_SCHEDULE_SYNC_MODBUS = new Date();
    public static Integer TIME_SCHEDULE_CALL_API = 1000 * 60 * 10;
    public static Date NEXT_TIME_SCHEDULE_CALL_API = new Date();

    public static String API_CALL_URL = "";
    public static HttpMethod HTTP_METHOD_CALL_API = HttpMethod.POST;

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


    public static void setNextTimeScheduleSyncModbus() {
        Date now = new Date();
        now.setSeconds(0);
        SystemArg.NEXT_TIME_SCHEDULE_SYNC_MODBUS = new Date(now.getTime() + SystemArg.TIME_SCHEDULE_SYNC_MODBUS);
    }

    public static boolean checkTimeScheduleSyncModbus() {
        return LOGIN && MODBUS_SYNC_READY && new Date().after(NEXT_TIME_SCHEDULE_SYNC_MODBUS);
    }

    public static void setNextTimeScheduleCallApi() {
        Date now = new Date();
        now.setSeconds(0);
        SystemArg.NEXT_TIME_SCHEDULE_CALL_API = new Date(now.getTime() + SystemArg.TIME_SCHEDULE_CALL_API);
    }

    public static boolean checkTimeScheduleCallApi() {
        return LOGIN || !API_CALL_API_READY || new Date().before(NEXT_TIME_SCHEDULE_CALL_API) || API_CALL_URL.equals("");
    }
}
