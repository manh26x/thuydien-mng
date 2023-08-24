package com.codetreatise.thuydienapp.repository;

import com.codetreatise.thuydienapp.config.database.SqliteJdbc;
import com.codetreatise.thuydienapp.config.request.CucTNNApiData;
import com.codetreatise.thuydienapp.model.APIDataModel;
import com.codetreatise.thuydienapp.model.ContentDataCucTNN;
import com.codetreatise.thuydienapp.model.ParamCucTNN;
import org.springframework.http.HttpStatus;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CucTNNRepository {
    private static final CucTNNRepository instance = new CucTNNRepository();

    public static CucTNNRepository getInstance() {
        return instance;
    }

    public List<ParamCucTNN> findAll() {
        List<ParamCucTNN> params = new ArrayList<>();
        PreparedStatement pStmt = null;
        try {
            pStmt = SqliteJdbc.getInstance().getConn()
                    .prepareStatement("SELECT tnn.*, mp.ADDRESS FROM CUC_TNN_PARAMS tnn JOIN MODBUS_PARAMS mp ON tnn.NAME = mp.NAME");
            ResultSet rs = pStmt.executeQuery();
            while (rs.next()) {
                params.add(ParamCucTNN.builder()
                        .kyHieuTram(rs.getString("KY_HIEU_TRAM"))
                        .name(rs.getString("NAME"))
                        .thongSoDo(rs.getString("THONG_SO_DO"))
                        .dvt(rs.getString("DVT"))
                        .address(rs.getInt("ADDRESS"))
                        .build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return params;
    }

    public void saveInfo(String maTinh, String kyHieuCongTrinh) {
        try {
          PreparedStatement pStmt = SqliteJdbc.getInstance().getConn()
                  .prepareStatement("DELETE FROM CUC_TNN_INFO WHERE 1=1");
          pStmt.execute();
          pStmt = SqliteJdbc.getInstance().getConn().prepareStatement("INSERT INTO CUC_TNN_INFO(" +
                  "'MA_TINH', 'KY_HIEU_CONG_TRINH') VALUES(?, ?)");
          pStmt.setString(1, maTinh);
          pStmt.setString(2, kyHieuCongTrinh);
          pStmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CucTNNApiData getInfo() {
        try {
            PreparedStatement pStmt = SqliteJdbc.getInstance().getConn()
                    .prepareStatement("SELECT * FROM CUC_TNN_INFO");
            ResultSet rs = pStmt.executeQuery();
            if (rs.next()) {
                return CucTNNApiData.builder()
                        .MaTinh(rs.getString("MA_TINH"))
                        .KyHieuCongTrinh(rs.getString("KY_HIEU_CONG_TRINH"))
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void saveAllParams(List<ParamCucTNN> params) {
        params.forEach(param -> {
            try {
                if(findParam(param.getName()) != null) {
                   update(param);
                } else {
                    insert(param);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public ParamCucTNN findParam(String name) {
        try {
            PreparedStatement preparedStatement = SqliteJdbc.getInstance().getConn()
                    .prepareStatement("SELECT * FROM CUC_TNN_PARAMS WHERE `NAME` = ?");
            preparedStatement.setString(1, name);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                return ParamCucTNN.builder()
                        .kyHieuTram(rs.getString("KY_HIEU_TRAM"))
                        .thongSoDo(rs.getString("THONG_SO_DO"))
                        .dvt(rs.getString("DVT"))
                        .name(rs.getString("NAME"))
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void insert(ParamCucTNN param) {
        try {
          PreparedStatement pStmt = SqliteJdbc.getInstance().getConn()
                  .prepareStatement("INSERT INTO CUC_TNN_PARAMS(THONG_SO_DO, DVT, KY_HIEU_TRAM, `NAME`)" +
                          " VALUES(?, ?, ?, ?)");
          pStmt.setString(1, param.getThongSoDo());
          pStmt.setString(2, param.getDvt());
          pStmt.setString(3, param.getKyHieuTram());
          pStmt.setString(4, param.getName());
          pStmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void update(ParamCucTNN param) {
        try {
            PreparedStatement pStmt = SqliteJdbc.getInstance().getConn()
                    .prepareStatement("UPDATE CUC_TNN_PARAMS SET" +
                            " THONG_SO_DO=?," +
                            " DVT=?, " +
                            " KY_HIEU_TRAM=? " +
                            " WHERE `NAME`=?");
            pStmt.setString(1, param.getThongSoDo());
            pStmt.setString(2, param.getDvt());
            pStmt.setString(3, param.getKyHieuTram());
            pStmt.setString(4, param.getName());
            pStmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ContentDataCucTNN> findAllDataNotSend() {
        List<ContentDataCucTNN> data = new ArrayList<>();

        try {
          PreparedStatement pStmt = SqliteJdbc.getInstance().getConn()
                  .prepareStatement("SELECT *, mb.ID as MB_ID, cp.DVT as DVT, mb.NAME as NAME  FROM MODBUS_DATA mb " +
                          " JOIN CUC_TNN_PARAMS CP ON mb.NAME = CP.NAME " +
                          " LEFT JOIN CUC_TNN_DATA CD ON mb.ID = CD.MODBUS_DATA_ID " +
                          " WHERE CD.ID is null");
          ResultSet rs = pStmt.executeQuery();
          while (rs.next()) {
              data.add(ContentDataCucTNN.builder()
                              .status("00")
                              .time(new Date(rs.getLong("TIME_RECEIVE")))
                              .value(rs.getFloat("VALUE"))
                              .dvt(rs.getString("DVT"))
                              .thongSoDo(rs.getString("THONG_SO_DO"))
                              .kyHieuTram(rs.getString("KY_HIEU_TRAM"))
                              .modbusId(rs.getInt("MB_ID"))
                              .name(rs.getString("NAME"))
                      .build());
          }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    public void insertCucTNNData(ContentDataCucTNN cucTNNApiData, HttpStatus statusCode) {
        try {
            PreparedStatement pStmt = SqliteJdbc.getInstance().getConn()
                    .prepareStatement("INSERT INTO CUC_TNN_DATA(`NAME`, CODE_RESPONSE, `VALUE`, MODBUS_DATA_ID, TIME_SEND)" +
                            " VALUES (?, ?, ?, ?, ?)");
            pStmt.setString(1, cucTNNApiData.getName());
            pStmt.setInt(2, statusCode.value());
            pStmt.setFloat(3, cucTNNApiData.getValue());
            pStmt.setInt(4, cucTNNApiData.getModbusId());
            pStmt.setLong(5, new Date().getTime());
            pStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<APIDataModel> findCucTNNDataResultByDate(Date date) {
        List<APIDataModel> results = new ArrayList<>();
        try {
            PreparedStatement pStmt = SqliteJdbc.getInstance().getConn()
                    .prepareStatement("SELECT bd.VALUE, bp.NAME, md.TIME_RECEIVE, bd.CODE_RESPONSE FROM CUC_TNN_DATA bd " +
                            " JOIN CUC_TNN_PARAMS bp ON bd.NAME = bp.NAME" +
                            " JOIN MODBUS_DATA md ON md.ID = bd.MODBUS_DATA_ID " +
                            " WHERE strftime('%Y-%m-%d', TIME_RECEIVE / 1000, 'unixepoch') = ?");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            pStmt.setString(1, simpleDateFormat.format(date));
            ResultSet rs = pStmt.executeQuery();
            while(rs.next()) {
                results.add(APIDataModel.builder()
                        .code(rs.getInt("CODE_RESPONSE"))
                        .time(new Date(rs.getLong("TIME_RECEIVE")))
                        .maThamSo(rs.getString("NAME"))
                        .value(rs.getFloat("VALUE"))
                        .build());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;

    }
}
