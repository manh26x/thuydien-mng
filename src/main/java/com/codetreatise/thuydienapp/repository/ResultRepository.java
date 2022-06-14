package com.codetreatise.thuydienapp.repository;

import com.codetreatise.thuydienapp.bean.ApiConfig;
import com.codetreatise.thuydienapp.bean.Data;
import com.codetreatise.thuydienapp.bean.DataReceive;
import com.codetreatise.thuydienapp.bean.Result;
import com.codetreatise.thuydienapp.config.SystemArg;
import com.codetreatise.thuydienapp.config.database.H2Jdbc;
import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ResultRepository {

    private static ResultRepository instance;

    public static ResultRepository getInstance() {
        if(instance == null) {
            instance = new ResultRepository();
        }
        return instance;
    }

    public List<Result> findAllByApiAndTimeSendAfterAndTimeSendBefore(String apiName, String apiUrl, Date fromDate, Date toDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern("dd/MM/yyyy HH:mm:ss");
        String sql = "SELECT * FROM DATA_RESULT r where r.api like ?   and r.time_send between ? and ?";
        List<Result> results = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        try {
            fromDate.setHours(0);
            fromDate.setMinutes(0);
            fromDate.setSeconds(0);
            toDate.setSeconds(59);
            toDate.setHours(23);
            toDate.setMinutes(59);
            preparedStatement = H2Jdbc.getInstance().getConn().prepareStatement(sql);
            preparedStatement.setString(1, apiUrl);
            preparedStatement.setObject(2, fromDate );
            preparedStatement.setObject(3, toDate);

            ResultSet rs = preparedStatement.executeQuery();
        while (true) {
            try {
                if (!rs.next()) break;
                results.add(Result.builder()
                        .api(rs.getString("api"))
                        .apiName(rs.getString("api_name"))
                        .codeResponse(rs.getInt("codeResponse"))
                        .timeSend(Date.from(rs.getTimestamp("time_send").toInstant()))
                        .request(rs.getString("request"))
                        .response(rs.getString("response"))
                        .build());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                log.error(throwables.getMessage());
                break;
            }
        }
            try {
                rs.close();
            } catch (Exception throwables) {
                throwables.printStackTrace();
                log.error(throwables.getMessage());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            log.error(throwables.getMessage());
        } finally {
            try {
                preparedStatement.close();
            } catch (Exception throwables) {
                throwables.printStackTrace();
                log.error(throwables.getMessage());
            }
        }
        return results.stream().filter(r -> r.getApiName().equals(apiName)).collect(Collectors.toList());
    }

    public List<DataReceive> findNotSend(ApiConfig apiConfig)  {

        String sql = "SELECT * FROM Data_Receive d " +
                " where d.id not in ( select r.data_receive_id from DATA_RESULT r where  r.api_name like ?)";
        List<DataReceive> dataReceives = new ArrayList<>();
        PreparedStatement ps = null;
        try {
            ps = H2Jdbc.getInstance().getConn().prepareStatement(sql);
            ps.setString(1, apiConfig.getName());
            ResultSet rs = ps.executeQuery();

            while (true) {
                try {
                    if (!rs.next()) break;
                    dataReceives.add(DataReceive.builder()
                            .data(SystemArg.findByKey(rs.getString("data_id")))
                            .id(rs.getLong("id"))
                            .value(rs.getFloat("gia_tri"))
                            .thoigian(Date.from(rs.getTimestamp("thoigian").toInstant()))
                            .status(rs.getInt("status"))
                            .build());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    log.error(throwables.getMessage());
                }

            }
        }catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            try{
                assert ps != null;
                ps.close();
            }catch (Exception ignored) {

            }
            log.debug("FOUNDED not send apiName {} with result: {}", apiConfig.getName(), dataReceives);
        }
        List<String> keys = apiConfig.getKeySends().stream().map(Data::getKey).collect(Collectors.toList());
        return dataReceives.stream().filter(dataReceive -> keys.contains(dataReceive.getData().getKey())).collect(Collectors.toList());
    }

    public void insert(Result entity) {
        H2Jdbc sqlJdbc = H2Jdbc.getInstance();
        String sql = "INSERT INTO DATA_RESULT(" +
                "api, " +
                "response," +
                "request, " +
                "codeResponse, " +
                "data_receive_id," +
                "time_send, " +
                "api_name)" + "VALUES (?,?,?,?,?, ?, ?)";
        PreparedStatement preparedStatement = null;
        try {
             preparedStatement = sqlJdbc.getConn().prepareStatement(sql);
            preparedStatement.setString(1, entity.getApi());
            preparedStatement.setString(2, entity.getResponse());
            preparedStatement.setString(3, entity.getRequest());
            preparedStatement.setInt(4, entity.getCodeResponse());
            preparedStatement.setLong(5,  entity.getDataReceive().getId());
            preparedStatement.setObject(6, entity.getTimeSend() );
            preparedStatement.setString(7, entity.getApiName());
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            log.error(throwables.getMessage());
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (Exception troubles) {
                log.error(troubles.getMessage());
            }
        }
    }
}
