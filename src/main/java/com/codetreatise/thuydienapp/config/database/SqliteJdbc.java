package com.codetreatise.thuydienapp.config.database;

import com.codetreatise.thuydienapp.bean.ModbusParam;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SqliteJdbc {
    private static SqliteJdbc instance;
    static final String JDBC_DRIVER = "org.sqlite.JDBC";
    static final String DB_URL = "jdbc:sqlite:./database/thuydien.db";

    //  Database credentials
    static final String USER = "sa";
    static final String PASS = "password";
    private Connection conn ;
    private Statement stmt;

    private SqliteJdbc() {
    }

    public Connection getConn() throws SQLException {
        //STEP 2: Open a connection
        if(conn == null || conn.isClosed()) {
            try {
                Class.forName(JDBC_DRIVER);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        }

        return conn;
    }

    public Statement getStmt() {
        try {
            stmt = getConn().createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            log.error(throwables.getMessage());
        }
        return stmt;
    }

    public int executeUpdate(String sql) {
        try {
             int result = getStmt().executeUpdate(sql);
             log.info("executed update {} : {}",result, sql);
             return result;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            log.error(throwables.getMessage());
        } finally {
            try {
                stmt.close();
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                log.error(throwables.getMessage());
            }
        }
        return 0;
    }
    
    public ResultSet getResultSet(String sql) {
        ResultSet resultSet = null;
        try {
            resultSet = getStmt().executeQuery(sql);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return resultSet;
    }



    public static SqliteJdbc getInstance() {
        if(instance == null) {
            instance = new SqliteJdbc();
        }
        return instance;
    }

    public void clearAllParams() {
        executeUpdate("DELETE FROM BO_CT_PARAMS WHERE 1=1;\n" +
                "DELETE FROM CUC_TNN_PARAMS WHERE 1=1;\n" +
                "DELETE FROM MODBUS_PARAMS WHERE 1=1;");
    }
    public void clearAllData() {
        executeUpdate("DELETE FROM BO_CT_DATA WHERE 1=1;\n" +
                "DELETE FROM MODBUS_DATA WHERE 1=1;\n" +
                "DELETE FROM CUC_TNN_DATA WHERE 1=1;");
    }

    public List<ModbusParam> getModbusParams() throws SQLException {
        String sql = "select NAME, DVT, ADDRESS from MODBUS_PARAMS";

        ResultSet rs = getResultSet(sql);
        List<ModbusParam> modbusParams = new ArrayList<>();

        while(rs.next()) {
            ModbusParam modbusParam = new ModbusParam();
            modbusParam.setName(rs.getString(1));
            modbusParam.setDvt(rs.getString(2));
            modbusParam.setAddress(rs.getInt(3));
            modbusParams.add(modbusParam);
        }
        return modbusParams;
    }

    public int addModbusParam(ModbusParam modbusParam) {
        String sql = "INSERT INTO MODBUS_PARAMS (NAME, DVT, ADDRESS) VALUES('"
                + modbusParam.getName()
                + "', '" + modbusParam.getDvt()
                + "', " + modbusParam.getAddress()
                + ")";
        return executeUpdate(sql);
    }


}
