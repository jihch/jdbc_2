package io.github.jihch.transaction;

import io.github.jihch.util.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;

public class ConnectionTest {

    @Test
    public void testGetConnection() throws Exception {
        Connection connection = JDBCUtils.getConnection();
        System.out.println(connection);
    }

}
