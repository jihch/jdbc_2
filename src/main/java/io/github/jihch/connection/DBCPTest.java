package io.github.jihch.connection;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBCPTest {

    /**
     * 测试DBCP的数据库连接池技术
     */
    //方式一：不推荐
    @Test
    public void testGetConnection() throws SQLException {
        //创建了 DBCP 的数据库连接池
        BasicDataSource source = new BasicDataSource();

        //设置基本信息
        source.setDriverClassName("com.mysql.cj.jdbc.Driver");
        source.setUrl("jdbc:mysql://localhost:3306/jdbc_test?rewriteBatchedStatements=true");
        source.setUsername("root");
        source.setPassword("123456");

        //还可以设置其他涉及数据库连接池管理的相关属性
        source.setInitialSize(10);
        source.setMaxActive(10);
        //……

        Connection conn = source.getConnection();
        System.out.println(conn);

    }

    //推荐使用的方式二：使用配置文件，
    @Test
    public void testGetConnection1() throws Exception {
        Properties pros = new Properties();

        //方式1：
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("dbcp.properties");

        //方式2：
//        File file = new File("src/main/resources/dbcp.properties");
//        FileInputStream is = new FileInputStream(file);

        pros.load(is);

        DataSource dataSource = BasicDataSourceFactory.createDataSource(pros);
        Connection conn = dataSource.getConnection();
        System.out.println(conn);

    }

}
