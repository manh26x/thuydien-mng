package com.codetreatise.config;

import org.springframework.http.HttpMethod;

import java.util.Date;

public final class SystemArg {

    public static Integer TIME_SCHEDULE_SYNC_MODBUS = 1000 * 60*10;
    public static Date NEXT_TIME_SCHEDULE_SYNC_MODBUS = new Date();
    public static Integer TIME_SCHEDULE_CALL_API = 1000 * 60 * 10;
    public static Date NEXT_TIME_SCHEDULE_CALL_API = new Date();

    public static String API_CALL_URL = "";
    public static HttpMethod HTTP_METHOD_CALL_API = HttpMethod.POST;


    public static void setNextTimeScheduleSyncModbus() {
        SystemArg.NEXT_TIME_SCHEDULE_SYNC_MODBUS = new Date(new Date().getTime() + SystemArg.TIME_SCHEDULE_SYNC_MODBUS);
    }

    public static boolean checkTimeScheduleSyncModbus() {
        return new Date().before(NEXT_TIME_SCHEDULE_SYNC_MODBUS);
    }

    public static void setNextTimeScheduleCallApi() {
        SystemArg.NEXT_TIME_SCHEDULE_CALL_API = new Date(new Date().getTime() + SystemArg.TIME_SCHEDULE_CALL_API);
    }

    public static boolean checkTimeScheduleCallApi() {
        return new Date().before(NEXT_TIME_SCHEDULE_CALL_API) && !API_CALL_URL.equals("");
    }
}
