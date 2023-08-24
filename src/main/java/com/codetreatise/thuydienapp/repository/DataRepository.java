package com.codetreatise.thuydienapp.repository;

import com.codetreatise.thuydienapp.bean.Data;
import com.codetreatise.thuydienapp.bean.ModbusData;
import com.codetreatise.thuydienapp.bean.ModbusParam;
import com.codetreatise.thuydienapp.config.database.SqliteJdbc;
import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class DataRepository {

    private static DataRepository instance;
    private DataRepository() {

    }

    public static DataRepository getInstance() {
        if(instance == null) {
            instance = new DataRepository();
        }
        return instance;
    }


    public List<ModbusData> findAllModbusDataByDate(Date date) throws SQLException {
        String sql = "SELECT * FROM MODBUS_DATA WHERE strftime('%Y-%m-%d', TIME_RECEIVE / 1000, 'unixepoch') " +
                "= ?";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        PreparedStatement pStmt = SqliteJdbc.getInstance().getConn().prepareStatement(sql);
        pStmt.setString(1, simpleDateFormat.format(date));
        ResultSet rs = pStmt.executeQuery();
        List<ModbusData> modbusDataList = new ArrayList<>();

        while (true) {
            try {
                if (!rs.next()) break;
                modbusDataList.add(ModbusData.builder()
                                .name(rs.getString("name"))
                                .timeReceive(new Date(rs.getLong("TIME_RECEIVE")))
                                .value(rs.getFloat("VALUE"))
                        .build());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        return modbusDataList;
    }

    public boolean deleteModbusParam(String name) throws SQLException {
        PreparedStatement pstmt = SqliteJdbc.getInstance().getConn()
                .prepareStatement("DELETE FROM MODBUS_PARAMS WHERE NAME = ?");
        pstmt.setString(1, name);

        return pstmt.execute();
    }

    public List<Data> findAllByStatus(Integer status) {
        String sql = "SELECT * FROM DATA d where d.status = " + status;
        ResultSet rs = SqliteJdbc.getInstance().getResultSet(sql);
        List<Data> dataList = new ArrayList<>();
        while (true) {
            try {
                if (!rs.next()) break;
                dataList.add(Data.builder()
                        .key(rs.getString("key"))
                        .address(rs.getInt("address"))
                        .dvt(rs.getString("dvt"))
                        .maThongSo(rs.getString("ma_thong_so"))
                        .status(rs.getInt("status"))
                        .nguon(rs.getString("nguon"))
                        .tenChiTieu(rs.getString("ten_chi_tieu"))
                        .quantity(rs.getInt("quantity"))
                        .build());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                log.error(throwables.getMessage());
            }finally {
                try {
                    SqliteJdbc.getInstance().getStmt().close();
                    SqliteJdbc.getInstance().getConn().close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    log.error(throwables.getMessage());
                }
            }

        }
        return dataList;
    }


    public void update(ModbusParam data) {
        SqliteJdbc sqlJdbc = SqliteJdbc.getInstance();
        String sql = "UPDATE MODBUS_PARAMS SET" +
                " dvt = '" + data.getDvt() +
                "', address = " + data.getAddress() +
                " WHERE NAME = '" + data.getName() +
                "'";
        sqlJdbc.executeUpdate(sql);
    }

    public int  insert(Data data) {
        SqliteJdbc sqlJdbc = SqliteJdbc.getInstance();
        String sql = "INSERT INTO Data" +
                "( `key`, nguon, ten_chi_tieu, dvt, address, quantity, status, ma_thong_so) VALUES (" +
                "'" + data.getKey() +
                "', '" + data.getNguon() +
                "', '" + data.getTenChiTieu() +
                "', '" + data.getDvt() +
                "', " + data.getAddress() +
                ", " + data.getQuantity() +
                ", " + data.getStatus() +
                ", '" + data.getMaThongSo() +
                "')";
        return sqlJdbc.executeUpdate(sql);
    }
    public int delete(Data data) {
        SqliteJdbc sqlJdbc = SqliteJdbc.getInstance();
        String sql = "DELETE FROM Data where `key` = " + data.getKey();
        return sqlJdbc.executeUpdate(sql);
    }
}
