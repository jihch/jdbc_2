package io.github.jihch.betterutil;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class JDBCUtils {

    private static ComboPooledDataSource cpds = new ComboPooledDataSource("helloc3p0");

    /**
     * 使用C3P0连接池技术
     * @return
     * @throws SQLException
     */
    public static Connection getConnectionFromC3P0() throws SQLException {
        Connection conn = cpds.getConnection();
        return conn;
    }


    private static DataSource dbcp;

    static {
        Properties pros = new Properties();

        //方式1：
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("dbcp.properties");

        try {
            pros.load(is);
            //创建一个 DBCP 数据库连接池
            dbcp = BasicDataSourceFactory.createDataSource(pros);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 使用 DBCP 数据库连接池技术获取数据库连接
     * @return
     * @throws SQLException
     */
    public static Connection getConnectionFromDBCP() throws Exception {
        Connection conn = dbcp.getConnection();
        return conn;
    }

    /**
     * 使用 Druid 数据库连接池技术
     */
    private static DataSource druid;

    static {
        Properties pros = new Properties();
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");
        try {
            pros.load(is);
            druid = DruidDataSourceFactory.createDataSource(pros);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Connection getConnectionFromDruid() {
        Connection conn = null;
        try {
            conn = druid.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
