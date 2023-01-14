package io.github.jihch.dbutils;


import io.github.jihch.bean.Customer;
import io.github.jihch.betterutil.JDBCUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * commons-dbutils 是 Apache 组织提供的一个开源 JDBC 工具类库，封装了针对于数据库的增删改查操作
 */
public class QueryRunnerTest {

    @Test
    public void testInsert() {
        QueryRunner runner = new QueryRunner();
        String sql = "insert into customers (name, email, birth) values (?, ?, ?)";
        int insertCount = 0;
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnectionFromDruid();
            insertCount = runner.update(conn, sql, "蔡徐坤", "caixukun@126.com", "1997-09-08");
            System.out.printf("添加了 %d 条记录\n", insertCount);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

    //测试查询
    /**
     * BeanHandler:是 ResultSetHandler 接口的实现类，用于封装表中的一条记录
     */
    @Test
    public void testQueryBean() {
        Connection conn = null;
        String sql = "select id, name, email, birth from customers where id = ?";

        /*
        AbstractKeyedHandler, AbstractListHandler, ArrayHandler, ArrayListHandler, BaseResultSetHandler,
        BeanHandler, BeanListHandler, BeanMapHandler, ColumnListHandler,
        KeyedHandler, MapHandler, MapListHandler, ScalarHandler
         */
        BeanHandler<Customer> rsh = new BeanHandler(Customer.class);
        Customer customer = null;

        try {
            QueryRunner runner = new QueryRunner();

            conn = JDBCUtils.getConnectionFromDruid();

            customer = runner.query(conn, sql, rsh, 9);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
        System.out.println(customer);
    }

    /**
     * BeanListHandler:是 ResultSetHandler 接口的实现类，用于封装表中的多条记录构成的集合
     */
    @Test
    public void testQueryBeanList() {
        Connection conn = null;
        String sql = "select id, name, email, birth from customers where id < ?";

        /*
        AbstractKeyedHandler, AbstractListHandler, ArrayHandler, ArrayListHandler, BaseResultSetHandler,
        BeanHandler, BeanListHandler, BeanMapHandler, ColumnListHandler,
        KeyedHandler, MapHandler, MapListHandler, ScalarHandler
         */
        BeanListHandler<Customer> rsh = new BeanListHandler(Customer.class);
        List<Customer> list = null;
        QueryRunner runner = new QueryRunner();

        try {


            conn = JDBCUtils.getConnectionFromDruid();

            list = runner.query(conn, sql, rsh, 9);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }

        list.forEach(System.out::println);
    }

    /**
     * MapHandler:是 ResultSetHandler 接口的实现类，对应表中的一条记录
     * 将字段及相应字段的值作为 Map 中的 key 和 value
     */
    @Test
    public void testQueryMap() {
        Connection conn = null;
        String sql = "select id, name, email, birth from customers where id = ?";

        /*
        AbstractKeyedHandler, AbstractListHandler, ArrayHandler, ArrayListHandler, BaseResultSetHandler,
        BeanHandler, BeanListHandler, BeanMapHandler, ColumnListHandler,
        KeyedHandler, MapHandler, MapListHandler, ScalarHandler
         */
        MapHandler rsh = new MapHandler();
        Map<String, Object> map = new HashMap<>();
        QueryRunner runner = new QueryRunner();

        try {

            conn = JDBCUtils.getConnectionFromDruid();

            map = runner.query(conn, sql, rsh, 9);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }

        map.forEach((k, v) -> System.out.printf("key:%s, value:%s\n", k, v));
    }

    /**
     * MapListHandler:是 ResultSetHandler 接口的实现类，对应表中的一条记录
     * 将字段及相应字段的值作为 Map 中的 key 和 value
     */
    @Test
    public void testQueryMapList() {
        Connection conn = null;
        String sql = "select id, name, email, birth from customers where id < ?";

        /*
        AbstractKeyedHandler, AbstractListHandler, ArrayHandler, ArrayListHandler, BaseResultSetHandler,
        BeanHandler, BeanListHandler, BeanMapHandler, ColumnListHandler,
        KeyedHandler, MapHandler, MapListHandler, ScalarHandler
         */
        MapListHandler rsh = new MapListHandler();
        List<Map<String, Object>> list = new ArrayList<>();
        QueryRunner runner = new QueryRunner();

        try {

            conn = JDBCUtils.getConnectionFromDruid();

            list = runner.query(conn, sql, rsh, 9);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }

        list.forEach(System.out::println);

    }

}
