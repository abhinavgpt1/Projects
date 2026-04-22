package com.example.milkman_legacy.dbConnectionUtil;

import com.example.milkman_legacy.dbConnectionUtil.dbConnection.MySQLConnection;
import com.example.milkman_legacy.dbConnectionUtil.dbConnection.PostgreSQLConnection;
import com.example.milkman_legacy.dbConnectionUtil.dbConnection.SQLServerConnection;

import java.sql.Connection;

public class DBConnectionFactory {
    public static Connection getConnection(Database database) {
        switch (database) {
            case MYSQL:
                return MySQLConnection.getConnection();
            case POSTGRES:
                return PostgreSQLConnection.getConnection();
            case SQLSERVER:
                return SQLServerConnection.getConnection();
            default:
                throw new IllegalArgumentException("Unknown database: " + database);
        }
    }
}
