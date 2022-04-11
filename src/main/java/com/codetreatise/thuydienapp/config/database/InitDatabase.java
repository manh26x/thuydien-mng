package com.codetreatise.thuydienapp.config.database;

public final class InitDatabase {
    private static InitDatabase instance;

    public static InitDatabase getInstance() {
        if(instance == null) {
            instance = new InitDatabase();
        }
        return instance;
    }

    private InitDatabase() {}

    public void initTableDatabase() {
        String sqlTableData = "CREATE TABLE IF NOT EXISTS  DATA " +
                "(`key`  VARCHAR(255)  PRIMARY KEY , " +
                " nguon VARCHAR(255), " +
                " ten_chi_tieu VARCHAR(255), " +
                " dvt VARCHAR(10), " +
                " address INTEGER, " +
                " quantity INTEGER, " +
                " status INTEGER )";
        H2Jdbc.getInstance().executeUpdate(sqlTableData);
        H2Jdbc.getInstance().executeUpdate("DROP TABLE DATA_RECEIVE");
        H2Jdbc.getInstance().executeUpdate("DROP TABLE RESULT");
        String sqlTableDataReceive = "CREATE TABLE IF NOT EXISTS  Data_Receive " +
                "(id LONG PRIMARY KEY AUTO_INCREMENT, " +
                " data_id LONG not NULL, " +
                " thoigian DATETIME, " +
                " gia_tri REAL, " +
                " status INTEGER )";

        H2Jdbc.getInstance().executeUpdate(sqlTableDataReceive);

        String sqlTableResult = "CREATE TABLE IF NOT EXISTS  RESULT " +
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
