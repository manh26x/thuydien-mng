package com.codetreatise.thuydienapp.config.database;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;
@Slf4j
public class H2Jdbc {
    private static H2Jdbc instance;
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:file:./database/thuydien_db";

    //  Database credentials
    static final String USER = "sa";
    static final String PASS = "password";
    private Connection conn ;
    private Statement stmt;

    private H2Jdbc() {
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

    public void executeUpdate(String sql) {
        try {
             getStmt().executeUpdate(sql);
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



    public static H2Jdbc getInstance() {
        if(instance == null) {
            instance = new H2Jdbc();
        }
        return instance;
    }


}
