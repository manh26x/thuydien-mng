package com.codetreatise.thuydienapp.config.database;

import java.util.TimerTask;

public final class InitDatabase extends TimerTask {
    private static InitDatabase instance;

    public static InitDatabase getInstance() {
        if(instance == null) {
            instance = new InitDatabase();
        }
        return instance;
    }

    private InitDatabase() {}

    @Override
    public void run() {
        initTableDatabase();
    }

    public void initTableDatabase() {
        String sqlTableData = "CREATE TABLE IF NOT EXISTS  DATA " +
                "(`key`  VARCHAR(255)  PRIMARY KEY , " +
                " nguon VARCHAR(255), " +
                " ten_chi_tieu VARCHAR(255), " +
                " dvt VARCHAR(10), " +
                " address INTEGER, " +
                " quantity INTEGER, " +
                " status INTEGER, " +
                " ma_thong_so VARCHAR(300) " +
                ")";
        H2Jdbc.getInstance().executeUpdate(sqlTableData);
        String sqlTableDataReceive = "CREATE TABLE IF NOT EXISTS  Data_Receive " +
                "(id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                " data_id VARCHAR(255)  not NULL, " +
                " thoigian DATETIME, " +
                " gia_tri REAL, " +
                " status INTEGER )";

        H2Jdbc.getInstance().executeUpdate(sqlTableDataReceive);

        String sqlTableResult = "CREATE TABLE IF NOT EXISTS  DATA_RESULT " +
                "(id LONG PRIMARY KEY AUTO_INCREMENT, " +
                " data_receive_id LONG not NULL, " +
                " time_send DATETIME, " +
                " request LONGVARCHAR, " +
                " response LONGVARCHAR, " +
                " api VARCHAR(500), " +
                " codeResponse INTEGER)";

        H2Jdbc.getInstance().executeUpdate(sqlTableResult);
    }
}
