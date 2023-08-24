package com.codetreatise.thuydienapp.repository;

import com.codetreatise.thuydienapp.bean.DataReceive;
import com.codetreatise.thuydienapp.bean.ModbusData;
import com.codetreatise.thuydienapp.bean.ModbusDataReceiveTable;
import com.codetreatise.thuydienapp.config.database.SqliteJdbc;
import com.codetreatise.thuydienapp.model.ModbusParamData;
import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class DataReceiveJdbc {
    private static DataReceiveJdbc instance;

    public static DataReceiveJdbc getInstance() {
        if(instance == null) {
            instance = new DataReceiveJdbc();
        }
        return instance;
    }

    public List<ModbusDataReceiveTable> findAllByTime(Date fromDate, Date toDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String sql = "SELECT dr.data_id, d.address, d.ma_thong_so, dr.gia_tri, dr.thoigian from Data_Receive dr left join DATA d on dr.data_id like d.`key` ";
//                + " where dr.thoi_gian between " + formatter.format(fromDate) + " and " + formatter.format(toDate);
        ResultSet rs = SqliteJdbc.getInstance().getResultSet(sql);
        List<ModbusDataReceiveTable> result = new ArrayList<>();
        while (true) {
            try {
                if (!rs.next()) break;
                result.add(ModbusDataReceiveTable.builder()
                        .tenChiTieu(rs.getString("data_id"))
                        .address(rs.getInt("address"))
                        .maThongSo(rs.getString("ma_thong_so"))
                        .value(rs.getFloat("gia_tri"))
                        .time(Date.from(rs.getTimestamp("thoigian").toInstant()))
                        .build());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                log.error(throwables.getMessage());
            }
        }
        try {
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void insert(ModbusData dataReceive) {
        String sql = "INSERT INTO MODBUS_DATA" +
                " (  TIME_RECEIVE, NAME, VALUE) VALUES ( ? , ?, ? )";
        try {
            PreparedStatement preparedStatement = SqliteJdbc.getInstance().getConn().prepareStatement(sql);
            preparedStatement.setLong(1, dataReceive.getTimeReceive().getTime());
            preparedStatement.setString(2, dataReceive.getName());
            preparedStatement.setFloat(3, dataReceive.getValue());
            int result = preparedStatement.executeUpdate();
            log.info("saved " + dataReceive.getValue() + " ? " + result);
            System.out.println("saved " + dataReceive.getValue() + " ? " + result);
            preparedStatement.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            log.error(throwables.getMessage());
        } finally {
            try {
                SqliteJdbc.getInstance().getConn().close();
            } catch (Exception throwables) {
                throwables.printStackTrace();
                log.error(throwables.getMessage());
            }

        }
    }
}
