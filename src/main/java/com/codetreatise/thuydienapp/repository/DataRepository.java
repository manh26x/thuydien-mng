package com.codetreatise.thuydienapp.repository;

import com.codetreatise.thuydienapp.bean.Data;
import com.codetreatise.thuydienapp.config.database.H2Jdbc;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public List<Data> findAllByStatus(Integer status) {
        String sql = "SELECT * FROM DATA d where d.status = " + status;
        ResultSet rs = H2Jdbc.getInstance().getResultSet(sql);
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
            }

        }
        return dataList;
    }


    public void update(Data data) {
        H2Jdbc sqlJdbc = H2Jdbc.getInstance();
        String sql = "UPDATE Data SET" +
                " key = '" + data.getKey() +
                "', nguon = '" + data.getNguon() +
                "', ten_chi_tieu = '" + data.getTenChiTieu() +
                "', dvt = '" + data.getDvt() +
                "', address = " + data.getAddress() +
                ", quantity = " + data.getQuantity() +
                ", status " + data.getStatus() +
                ", ma_thong_so = '" + data.getMaThongSo() +
                "'";
        sqlJdbc.executeUpdate(sql);
    }

    public void insert(Data data) {
        H2Jdbc sqlJdbc = H2Jdbc.getInstance();
        String sql = "INSERT INTO Data " + "VALUES (" +
                "'" + data.getKey() +
                "', '" + data.getNguon() +
                "', '" + data.getTenChiTieu() +
                "', '" + data.getDvt() +
                "', " + data.getAddress() +
                ", " + data.getQuantity() +
                ", " + data.getStatus() +
                ", '" + data.getMaThongSo() +
                "')";
        sqlJdbc.executeUpdate(sql);
    }
}
