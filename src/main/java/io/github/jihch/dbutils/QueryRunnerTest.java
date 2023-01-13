package io.github.jihch.dbutils;


import io.github.jihch.betterutil.JDBCUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * commons-dbutils 是 Apache 组织提供的一个开源 JDBC 工具类库，封装了针对于数据库的增删改查操作
 */
public class QueryRunnerTest {

    @Test
    public void testInsert() {
        QueryRunner runner = new QueryRunner();
        Connection conn = JDBCUtils.getConnectionFromDruid();
        String sql = "insert into customers (name, email, birth) values (?, ?, ?)";
        int insertCount = 0;
        try {
            insertCount = runner.update(conn, sql, "蔡徐坤", "caixukun@126.com", "1997-09-08");
            System.out.printf("添加了 %d 条记录\n", insertCount);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

}
