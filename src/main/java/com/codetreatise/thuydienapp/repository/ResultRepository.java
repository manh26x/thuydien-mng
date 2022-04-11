package com.codetreatise.thuydienapp.repository;

import com.codetreatise.thuydienapp.bean.Data;
import com.codetreatise.thuydienapp.bean.DataReceive;
import com.codetreatise.thuydienapp.bean.Result;
import com.codetreatise.thuydienapp.config.database.H2Jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ResultRepository {

    private static ResultRepository instance;

    public static ResultRepository getInstance() {
        if(instance == null) {
            instance = new ResultRepository();
        }
        return instance;
    }

    public List<Result> findAllByApiAndTimeSendAfterAndTimeSendBefore(String apiUrl, Date fromDate, Date toDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String sql = "SELECT * FROM RESULT r where r.api like '" + apiUrl
                + "' and r.time_send between '" + formatter.format(fromDate) + "' and '" + formatter.format(toDate) + "'";
        List<Result> results = new ArrayList<>();
        H2Jdbc sqlH2Jdbdc = H2Jdbc.getInstance();
        ResultSet rs = sqlH2Jdbdc.getResultSet(sql);
        while (true) {
            try {
                if (!rs.next()) break;
                results.add(Result.builder()
                        .api(rs.getString("api"))
                        .codeResponse(rs.getInt("code_response"))
                        .timeSend(rs.getDate("time_send"))
                        .request(rs.getString("request"))
                        .response(rs.getString("response"))
                        .build());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                break;
            } finally {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return results;
    }

    public List<DataReceive> findNotSend(String apiUrl) {

        String sql = "SELECT * FROM Data_Receive d JOIN DATA data on d.data_id = data.id" +
                " where d.id not in "
                + " ( SELECT r.data_receive_id RESULT r " +
                " JOIN r.data_receive_id = d.id where r.api like '" + apiUrl +"' )";
        H2Jdbc sqlH2Jdbdc = H2Jdbc.getInstance();
        ResultSet rs = sqlH2Jdbdc.getResultSet(sql);
        List<DataReceive> dataReceives = new ArrayList<>();
        while (true) {
            try {
                if (!rs.next()) break;
                dataReceives.add(DataReceive.builder()
                        .data(Data.builder()
                                .tenChiTieu(rs.getString("ten_chi_tieu"))
                                .dvt(rs.getString("dvt"))
                                .nguon(rs.getString("nguon"))
                                .maThongSo(rs.getString("ma_thong_so"))
                                .key(rs.getString("key"))
                                .address(rs.getInt("address"))
                                .quantity(rs.getInt("quantity"))
                                .build())
                        .id(rs.getLong("id"))
                        .value(rs.getFloat("value"))
                        .thoigian(rs.getDate("thoigian"))
                        .status(rs.getInt("status"))
                        .build());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
        return dataReceives;
    }

    public void insert(Result entity) {
        H2Jdbc sqlJdbc = H2Jdbc.getInstance();
        String sql = "INSERT INTO Result " + "VALUES (" +
                null +
                ", '" + entity.getApi() +
                "', '" + entity.getResponse() +
                "', '" + entity.getRequest() +
                "', " + entity.getCodeResponse() +
                ", " + entity.getDataReceive().getId() +
                ", " + entity.getTimeSend() +
                ")";
        sqlJdbc.executeUpdate(sql);
    }
}
