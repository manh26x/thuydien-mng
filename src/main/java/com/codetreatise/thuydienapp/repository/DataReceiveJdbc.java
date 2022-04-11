package com.codetreatise.thuydienapp.repository;

import com.codetreatise.thuydienapp.bean.DataReceive;
import com.codetreatise.thuydienapp.config.database.H2Jdbc;

public class DataReceiveJdbc {
    private static DataReceiveJdbc instance;

    public static DataReceiveJdbc getInstance() {
        if(instance == null) {
            instance = new DataReceiveJdbc();
        }
        return instance;
    }

    public void insert(DataReceive dataReceive) {
        H2Jdbc sqlJdbc = H2Jdbc.getInstance();
        String sql = "INSERT INTO Data_Receive " + "VALUES (" +
                null +
                ", '" +
                dataReceive.getData().getKey() +
                "', " +
                dataReceive.getThoigian() +
                ", " +
                dataReceive.getValue() +
                ", " +
                dataReceive.getStatus() +
                ")";
        sqlJdbc.executeUpdate(sql);
    }
}
