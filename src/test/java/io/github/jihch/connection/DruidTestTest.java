package io.github.jihch.connection;


import org.junit.Test;

import java.sql.SQLException;

public class DruidTestTest {

    private DruidTest test = new DruidTest();

    @Test
    public void getConnection() throws SQLException {
        System.out.println(test.getConnection());
    }

}