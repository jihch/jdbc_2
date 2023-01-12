package io.github.jihch.betterutil;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtils {

    private static ComboPooledDataSource cpds = new ComboPooledDataSource("helloc3p0");

    /**
     * 使用C3P0连接池技术
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = cpds.getConnection();
        return conn;
    }

    /**
     * 关闭连接和 Statement 的操作
     * @param conn
     * @param ps
     */
    public static void closeResource(Connection conn, Statement ps){
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }



}
