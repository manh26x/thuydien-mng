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
        String sqlTableData = "create table if not exists MODBUS_PARAMS\n" +
                "(\n" +
                "    NAME    TEXT not null\n" +
                "        constraint MODBUS_PARAMS_pk\n" +
                "            primary key,\n" +
                "    DVT     TEXT,\n" +
                "    ADDRESS INTEGER\n" +
                ");\n" +
                "\n" +
                "create table if not exists BO_CT_PARAMS\n" +
                "(\n" +
                "    KEY          TEXT\n" +
                "        constraint BO_CT_PARAMS_pk\n" +
                "            primary key,\n" +
                "    NAME         INTEGER\n" +
                "        constraint BO_CT_PARAMS_MODBUS_PARAMS_null_fk\n" +
                "            references MODBUS_PARAMS,\n" +
                "    NGUON        TEXT,\n" +
                "    TEN_CHI_TIEU TEXT,\n" +
                "    DVT          TEXT,\n" +
                "    MA_THONG_SO  TEXT\n" +
                ");\n" +
                "\n" +
                "create table if not exists MODBUS_DATA\n" +
                "(\n" +
                "    NAME         TEXT    not null\n" +
                "        constraint MODBUS_DATA_MODBUS_PARAMS_null_fk\n" +
                "            references MODBUS_PARAMS,\n" +
                "    TIME_RECEIVE INT     not null,\n" +
                "    VALUE        REAL,\n" +
                "    ID           INTEGER not null\n" +
                "        constraint MODBUS_DATA_pk\n" +
                "            primary key\n" +
                ");\n" +
                "\n" +
                "create table if not exists BO_CT_DATA\n" +
                "(\n" +
                "    ID             INTEGER not null\n" +
                "        constraint BO_CT_DATA_pk\n" +
                "            primary key autoincrement,\n" +
                "    KEY            TEXT\n" +
                "        constraint BO_CT_DATA_BO_CT_PARAMS_null_fk\n" +
                "            references BO_CT_PARAMS,\n" +
                "    CODE_RESPONSE  INTEGER,\n" +
                "    VALUE          REAL,\n" +
                "    MODBUS_DATA_ID INTEGER\n" +
                "        constraint BO_CT_DATA_MODBUS_DATA_ID_fk\n" +
                "            references MODBUS_DATA\n" +
                ");\n" +
                "\n";
        SqliteJdbc.getInstance().executeUpdate(sqlTableData);
        String sqlTableDataReceive = "CREATE TABLE IF NOT EXISTS  Data_Receive " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " data_id VARCHAR(255)  not NULL, " +
                " thoigian DATETIME, " +
                " gia_tri REAL, " +
                " status INTEGER )";

        SqliteJdbc.getInstance().executeUpdate(sqlTableDataReceive);

        String sqlTableResult = "CREATE TABLE IF NOT EXISTS  DATA_RESULT " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " data_receive_id LONG not NULL, " +
                " time_send DATETIME, " +
                " request LONGVARCHAR, " +
                " response LONGVARCHAR, " +
                " api VARCHAR(500), " +
                " api_name VARCHAR(500), " +
                " codeResponse INTEGER)";

        SqliteJdbc.getInstance().executeUpdate(sqlTableResult);

        String sqlTableError = "CREATE TABLE IF NOT EXISTS  DATA_ERROR " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " type_message VARCHAR(50) not NULL, " +
                " create_time DATETIME, " +
                " message LONGVARCHAR, " +
                " name_menu VARCHAR(255), " +
                " title LONGVARCHAR," +
                " is_read INT )";

        SqliteJdbc.getInstance().executeUpdate(sqlTableError);
    }
}
