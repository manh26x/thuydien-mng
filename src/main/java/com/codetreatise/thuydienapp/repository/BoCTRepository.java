package com.codetreatise.thuydienapp.repository;

import com.codetreatise.thuydienapp.bean.BoCTData;
import com.codetreatise.thuydienapp.config.database.SqliteJdbc;
import com.codetreatise.thuydienapp.config.request.DataCallApi;
import com.codetreatise.thuydienapp.model.APIDataModel;
import com.codetreatise.thuydienapp.model.ParamBoCT;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BoCTRepository implements Serializable {
    private static final BoCTRepository instance = new BoCTRepository();
    public static BoCTRepository getInstance() {
        return instance;
    }
    public void saveAll(List<ParamBoCT> paramBoCTList) {
        paramBoCTList.forEach(param -> {
            try {
                if(findOne(param.getKey()) != null) {
                    update(param);
                } else {
                    insert(param);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public List<APIDataModel> findBoCTDataResultByDate(Date date) {
        List<APIDataModel> results = new ArrayList<>();
        try {
            PreparedStatement pStmt = SqliteJdbc.getInstance().getConn()
                    .prepareStatement("SELECT bd.VALUE, bp.MA_THONG_SO, md.TIME_RECEIVE, bd.CODE_RESPONSE FROM BO_CT_DATA bd " +
                            " JOIN BO_CT_PARAMS bp ON bd.KEY = bp.KEY" +
                            " JOIN MODBUS_DATA md ON md.ID = bd.MODBUS_DATA_ID " +
                            " WHERE strftime('%Y-%m-%d', TIME_RECEIVE / 1000, 'unixepoch') = ?");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            pStmt.setString(1, simpleDateFormat.format(date));
            ResultSet rs = pStmt.executeQuery();
            while(rs.next()) {
                results.add(APIDataModel.builder()
                                .code(rs.getInt("CODE_RESPONSE"))
                                .time(new Date(rs.getLong("TIME_RECEIVE")))
                                .maThamSo(rs.getString("MA_THONG_SO"))
                                .value(rs.getFloat("VALUE"))
                        .build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    private void insert(ParamBoCT param) {
        try {
            PreparedStatement pStmt = SqliteJdbc.getInstance().getConn()
                    .prepareStatement("INSERT INTO BO_CT_PARAMS(`KEY`, `NAME`, NGUON, TEN_CHI_TIEU, DVT, MA_THONG_SO)" +
                            " VALUES (?, ?, ?, ?, ?, ?)");
            pStmt.setString(1, param.getKey());
            pStmt.setString(2, param.getName());
            pStmt.setString(3, param.getNguon());
            pStmt.setString(4, param.getTenChiTieu());
            pStmt.setString(5, param.getDvt());
            pStmt.setString(6, param.getMaThamSo());
            pStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<DataCallApi> findAllDataNotSend() {
        List<DataCallApi> data = new ArrayList<>();
        try {
            PreparedStatement pStmt = SqliteJdbc.getInstance().getConn()
                    .prepareStatement("SELECT *, mb.ID as MB_ID FROM MODBUS_DATA mb " +
                            " JOIN BO_CT_PARAMS bp ON mb.NAME = bp.NAME " +
                            " LEFT JOIN BO_CT_DATA bd ON mb.ID = bd.MODBUS_DATA_ID " +
                            " WHERE bd.ID is null");
            ResultSet rs = pStmt.executeQuery();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while(rs.next()) {
                data.add(DataCallApi.builder()
                        .key(rs.getString("KEY"))
                        .mathongso(rs.getString("MA_THONG_SO"))
                        .thoigian(simpleDateFormat.format(new Date(rs.getLong("TIME_RECEIVE"))))
                        .value((Math.round( rs.getFloat("VALUE") * 100.0f ) / 100.0f))
                        .nguon(rs.getString("NGUON"))
                                .modbusDataId(rs.getInt("MB_ID"))
                        .build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    private void update(ParamBoCT param) {
        try {
            PreparedStatement pStmt = SqliteJdbc.getInstance().getConn()
                    .prepareStatement("UPDATE BO_CT_PARAMS SET" +
                            " NGUON = ?, " +
                            " TEN_CHI_TIEU = ?," +
                            " DVT = ?," +
                            " MA_THONG_SO = ?," +
                            " `NAME` = ?" +
                            " WHERE `KEY` = ?");
            pStmt.setString(1, param.getNguon());
            pStmt.setString(2, param.getTenChiTieu());
            pStmt.setString(3, param.getDvt());
            pStmt.setString(4, param.getMaThamSo());
            pStmt.setString(5, param.getName());
            pStmt.setString(6, param.getKey());

            pStmt.execute();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ParamBoCT findOne(String key) {
        try {
            PreparedStatement preparedStatement = SqliteJdbc.getInstance().getConn()
                    .prepareStatement("SELECT * FROM BO_CT_PARAMS WHERE `KEY` = ?");
            preparedStatement.setString(1, key);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                return ParamBoCT.builder()
                        .key(rs.getString("key"))
                        .name(rs.getString("name"))
                        .nguon(rs.getString("nguon"))
                        .tenChiTieu(rs.getString("ten_chi_tieu"))
                        .dvt(rs.getString("dvt"))
                        .maThamSo(rs.getString("ma_thong_so"))
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ParamBoCT> findAll() {
        List<ParamBoCT> paramBoCTS = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = SqliteJdbc.getInstance().getConn()
                    .prepareStatement("SELECT * FROM BO_CT_PARAMS b JOIN MODBUS_PARAMS m ON b.NAME = m.NAME");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                paramBoCTS.add(
                        ParamBoCT.builder()
                                .key(rs.getString("key"))
                                .name(rs.getString("name"))
                                .nguon(rs.getString("nguon"))
                                .tenChiTieu(rs.getString("ten_chi_tieu"))
                                .dvt(rs.getString("dvt"))
                                .maThamSo(rs.getString("ma_thong_so"))
                                .address(rs.getInt("address"))

                                .build());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paramBoCTS;
    }

    public void insertBoCTData(BoCTData boCTData){
        try {
            PreparedStatement pStmt = SqliteJdbc.getInstance().getConn()
                    .prepareStatement("INSERT INTO BO_CT_DATA(`KEY`, CODE_RESPONSE, `VALUE`, MODBUS_DATA_ID)" +
                            " VALUES (?, ?, ?, ?)");
            pStmt.setString(1, boCTData.getKey());
            pStmt.setInt(2, boCTData.getCodeResponse());
            pStmt.setFloat(3, boCTData.getValue());
            pStmt.setInt(4, boCTData.getModbusDataId());
            pStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(ParamBoCT param) {
        try {
            PreparedStatement preparedStatement = SqliteJdbc.getInstance().getConn()
                    .prepareStatement("DELETE FROM BO_CT_PARAMS WHERE `KEY` = ?");
            preparedStatement.setString(1, param.getKey());
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
