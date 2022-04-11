package com.codetreatise.thuydienapp.config.database;

import java.sql.*;

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
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConn() throws SQLException {
        //STEP 2: Open a connection
        System.out.println("Connecting to database...");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        return conn;
    }

    public Statement getStmt() {
        try {
            stmt = getConn().createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return stmt;
    }

    public void executeUpdate(String sql) {
        try {
             getStmt().executeUpdate(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            try {
                stmt.close();
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
    
    public ResultSet getResultSet(String sql) {
        ResultSet resultSet = null;
        try {
            resultSet = getStmt().executeQuery(sql);
            return resultSet;
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(stmt != null) stmt.close();
                if(conn!=null) conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
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
