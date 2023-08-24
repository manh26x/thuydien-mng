package com.codetreatise.thuydienapp.repository;

import com.codetreatise.thuydienapp.bean.DataError;
import com.codetreatise.thuydienapp.config.database.SqliteJdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

public class DataErrorRepository  {

    private DataErrorRepository() {

    }

    public static DataErrorRepository instance;
    public static DataErrorRepository getInstance() {
        if(instance == null) {
            instance = new DataErrorRepository();
        }
        return instance;
    }


    public static final byte NON_READ = 0;


    public List<DataError> getAllUnReadTitle() {
        String sql = "SELECT * FROM DATA_ERROR where is_read = 0";
        return getDataErrorBySql(sql);
    }


    public List<DataError> getAllUnReadByTypeAndMenu(String type, String menuName) {
        String sql = "SELECT * FROM DATA_ERROR where is_read = 0 and type_message like '" + type + "' and name_menu like '" + menuName + "' order by  create_time desc";
        return getDataErrorBySql(sql);
    }

    public void readAllType(String type) {
        String sql = "UPDATE DATA_ERROR set is_read = 1 where type_message = '" + type + "'";
        SqliteJdbc.getInstance().executeUpdate(sql);
    }
    public void readSelectionsType(List<DataError> errors, String type) {
        errors.parallelStream().forEach(error ->  {
            String sql = "UPDATE DATA_ERROR set is_read = 1 where type_message = '" + type + "' and id = " + error.getId();
            SqliteJdbc.getInstance().executeUpdate(sql);

        });
    }
    private List<DataError> getDataErrorBySql(String sql) {
        ResultSet rs = SqliteJdbc.getInstance().getResultSet(sql);
        List<DataError> errors = new ArrayList<>();
        while (true) {
            try {
                if (!rs.next()) break;
                errors.add(DataError.builder()
                        .id(rs.getLong("id"))
                        .createTime(rs.getObject("create_time", Instant.class).atZone(ZoneId.systemDefault()).toLocalDateTime())
                        .menuName(rs.getString("name_menu"))
                        .message(rs.getString("message"))
                        .title(rs.getString("title"))
                        .isRead(rs.getByte("is_read"))
                        .build());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        try {
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return errors;
    }


    public int insert(DataError dataError) {
        SqliteJdbc sqlJdbc = SqliteJdbc.getInstance();
        String sql = "INSERT INTO DATA_ERROR (" +
                " type_message, " +
                " create_time, " +
                " message, " +
                " name_menu, " +
                " title," +
                " is_read) VALUES (?,?,?,?,?, ?)";
        PreparedStatement preparedStatement = null;
        int result = 0;
        try {
            preparedStatement = sqlJdbc.getConn().prepareStatement(sql);
            preparedStatement.setString(1, dataError.getType());
            preparedStatement.setObject(2, Instant.now());
            preparedStatement.setString(3, dataError.getMessage());
            preparedStatement.setString(4, dataError.getMenuName());
            preparedStatement.setString(5, dataError.getTitle());
            preparedStatement.setByte(6, NON_READ);
            result = preparedStatement.executeUpdate();
         }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return result;
    }

}
