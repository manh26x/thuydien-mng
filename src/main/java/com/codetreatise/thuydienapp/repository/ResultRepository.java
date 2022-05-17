package com.codetreatise.thuydienapp.repository;

import com.codetreatise.thuydienapp.bean.Data;
import com.codetreatise.thuydienapp.bean.DataReceive;
import com.codetreatise.thuydienapp.bean.Result;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.config.database.H2Jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
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
        String sql = "SELECT * FROM DATA_RESULT r where r.api like ? and r.time_send between ? and ?";
        List<Result> results = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = H2Jdbc.getInstance().getConn().prepareStatement(sql);
            preparedStatement.setString(1, apiUrl);
            preparedStatement.setObject(2, fromDate.toInstant().atZone(ZoneId.of("UTC+7")).toLocalDateTime());
            preparedStatement.setObject(3, toDate.toInstant().atZone(ZoneId.of("UTC+7")).toLocalDateTime());
            ResultSet rs = preparedStatement.executeQuery();
        while (true) {
            try {
                if (!rs.next()) break;
                results.add(Result.builder()
                        .api(rs.getString("api"))
                        .codeResponse(rs.getInt("codeResponse"))
                        .timeSend(rs.getDate("time_send"))
                        .request(rs.getString("request"))
                        .response(rs.getString("response"))
                        .build());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                break;
            }
        }
            try {
                rs.close();
            } catch (Exception throwables) {
                throwables.printStackTrace();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                preparedStatement.close();
            } catch (Exception throwables) {
                throwables.printStackTrace();
            }
        }
        return results;
    }

    public List<DataReceive> findNotSend(String apiUrl)  {

        String sql = "SELECT * FROM Data_Receive  d where not exists  " +
                "( SELECT r.data_receive_id FROM DATA_RESULT r " +
                "JOIN Data_Receive d on r.data_receive_id = d.id where r.api ='"+apiUrl+"')";

        List<DataReceive> dataReceives = new ArrayList<>();
        try {
            ResultSet rs = H2Jdbc.getInstance().getResultSet(sql);
            if(rs == null) {
                rs = H2Jdbc.getInstance().getResultSet("SELECT * FROM Data_Receive d");
            }
            while (true) {
                try {
                    if (!rs.next()) break;
                    dataReceives.add(DataReceive.builder()
                            .data(SystemArg.findByKey(rs.getString("data_id")))
                            .id(rs.getLong("id"))
                            .value(rs.getFloat("gia_tri"))
                            .thoigian(rs.getDate("thoigian"))
                            .status(rs.getInt("status"))
                            .build());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        return dataReceives;
    }

    public void insert(Result entity) {
        H2Jdbc sqlJdbc = H2Jdbc.getInstance();
        String sql = "INSERT INTO DATA_RESULT(" +
                "api, " +
                "response," +
                "request, " +
                "codeResponse, " +
                "data_receive_id," +
                "time_send) " + "VALUES (?,?,?,?,?, ?)";
        PreparedStatement preparedStatement = null;
        try {
             preparedStatement = sqlJdbc.getConn().prepareStatement(sql);
            preparedStatement.setString(1, entity.getApi());
            preparedStatement.setString(2, entity.getResponse());
            preparedStatement.setString(3, entity.getRequest());
            preparedStatement.setInt(4, entity.getCodeResponse());
            preparedStatement.setLong(5,  entity.getDataReceive().getId());
            preparedStatement.setObject(6, entity.getTimeSend().toInstant().atZone(ZoneId.of("UTC+7")).toLocalDateTime() );
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                preparedStatement.close();
            } catch (Exception troubles) {
                troubles.printStackTrace();
            }
        }
    }
}
