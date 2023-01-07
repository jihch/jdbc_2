package io.github.jihch.transaction;

import io.github.jihch.bean.User;
import io.github.jihch.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 1.什么叫数据库事务？
 * 事务：一组逻辑操作单元，使数据从一种状态变换到另一种状态。
 *       > 一组逻辑操作单元：一个或多个 DML 操作。
 *
 * 2.事务处理的原则：保证所有事务都作为一个工作单元来执行，即使出现了故障，都不能改变这种执行方式。
 * 当在一个事务中执行多个操作时，要么所有的事务都被提交（commit），那么这些修改就永久地保存下来；
 * 要么数据库管理系统将放弃所作的修改，整个事务回滚（rollback）到最初状态。
 *
 * 3.数据一旦提交，就不可回滚
 *
 * 4.哪些操作会导致数据的自动提交？
 *     > DDL 操作一旦执行，都会自动提交。
 *         > set autocommit = false 的方式对 DDL 操作无效。
 *     > DML 操作在默认情况下，一旦执行，也会自动提交。
 *         > 可以通过 set autocommit = false 的方式取消 DML 操作默认的自动提交。
 *     > 默认在关闭连接时，会自动地提交事务。
 */
public class TransactionTest {

    //*********************未考虑数据库事务情况下的转账操作*********************
    /**
     * 针对于数据表 user_table 来说：
     * AA用户给BB用户转账100
     *
     * update user_table set balance = balance - 100 where user = 'AA';
     * update user_table set balance = balance + 100 where user = 'BB';
     */
    @Test
    public void testUpdate() {
        String sql1 = "update user_table set balance = balance - 100 where user = ?";
        update(sql1, "AA");

        //模拟网络异常
        System.out.println(1 / 0);

        String sql2 = "update user_table set balance = balance + 100 where user = ?";
        update(sql2, "BB");
        System.out.println("转账成功");
    }

    //通用的增删改查操作---version 1.0
    public int update(String sql, Object ...args) {//sql 中占位符的个数与可变形参的长度相同

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            //1.获取数据库的连接
            conn = JDBCUtils.getConnection();

            //2.预编译sql语句，返回PreparedStatement的实例
            ps = conn.prepareStatement(sql);

            //3.填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            //4.执行
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }
        return 0;
    }




    //*********************考虑数据库事务后的转账操作*********************
    @Test
    public void testUpdateWithTx() {
        Connection con = null;
        try {
            con = JDBCUtils.getConnection();
            System.out.println(con.getAutoCommit());

            //取消数据的自动提交
            con.setAutoCommit(false);

            String sql1 = "update user_table set balance = balance - 100 where user = ?";
            update(con, sql1, "AA");

            //模拟网络异常
            System.out.println(1 / 0);

            String sql2 = "update user_table set balance = balance + 100 where user = ?";
            update(con, sql2, "BB");

            //提交数据
            con.commit();
            System.out.println("转账成功");
        } catch (Exception e) {
            e.printStackTrace();

            //回滚数据
            try {
                con.rollback();
            } catch (SQLException ex) {
                e.printStackTrace();
            }

        } finally {

            /*
             * 修改其为自动提交数据
             * 主要针对使用数据库连接池的使用
             */
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            JDBCUtils.closeResource(con, null);
        }

    }

    //通用的增删改查操作---version 2.0（考虑上事务）
    public int update(Connection conn, String sql, Object ...args) {//sql 中占位符的个数与可变形参的长度相同
        PreparedStatement ps = null;

        try {
            //1.预编译sql语句，返回PreparedStatement的实例
            ps = conn.prepareStatement(sql);

            //2.填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            //4.执行
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(null, ps);
        }
        return 0;
    }

    //**************************************************
    @Test
    public void testTransactionSelect() throws Exception {
        Connection conn = JDBCUtils.getConnection();
        /*
        获取当前连接的隔离级别
        int TRANSACTION_NONE             = 0;
        int TRANSACTION_READ_UNCOMMITTED = 1;
        int TRANSACTION_READ_COMMITTED   = 2;
        int TRANSACTION_REPEATABLE_READ  = 4;
        int TRANSACTION_SERIALIZABLE     = 8;
         */
        System.out.printf("conn.getTransactionIsolation():%d\n", conn.getTransactionIsolation());

        //设置事务的隔离级别为读已提交
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        System.out.printf("conn.getTransactionIsolation():%d\n", conn.getTransactionIsolation());

        //设置禁用自动提交
        conn.setAutoCommit(false);

        String sql = "select user, password, balance from user_table where user = ?";
        User user = getInstance(conn, User.class, sql, "AA");

        System.out.println(user);
    }

    @Test
    public void testTransactionUpdate() throws Exception {
        Connection conn = JDBCUtils.getConnection();

        //取消自动提交数据
        conn.setAutoCommit(false);
        String sql = "update user_table set balance = ? where user = ?";
        update(conn, sql, 5000, "AA");
        TimeUnit.SECONDS.sleep(15);
        System.out.println("修改结束");
    }


    //通用的查询操作，用于返回数据表中的一条记录（version 2.0；考虑上事务）
    public <T> T getInstance(Connection conn, Class<T> clazz, String sql, Object... args) {
        T t = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        Map<String, Field> fieldMap = new HashMap<>();
        for (Field declaredField : clazz.getDeclaredFields()) {
            fieldMap.put(declaredField.getName(), declaredField);
        }

        try {
            ps = conn.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            //执行获取结果集
            rs = ps.executeQuery();

            //获取结果集的元数据
            ResultSetMetaData rsmd = rs.getMetaData();

            int columnCount = rsmd.getColumnCount();

            if (rs.next()) {

                t = clazz.newInstance();

                for (int i = 0; i < columnCount; i++) {
                    //获取每个列的列值：通过ResultSet
                    Object value = rs.getObject(i + 1);

                    //获取每个列的列名：通过ResultSetMetaData
                    //获取列的列名：getColumnName() --不推荐使用
                    //获取列的别名：getColumnLabel()
                    String columnName = rsmd.getColumnName(i + 1);
                    String columnLabel = rsmd.getColumnLabel(i + 1);
//                    System.out.printf("columnName:%s, columnLabel:%s\n", columnName, columnLabel);

                    //通过反射，将对象指定名 columnName 的属性赋值为指定的值 columnValue
//                    Field field = Order.class.getDeclaredField(columnName);

                    if (fieldMap.containsKey(columnLabel)) {
                        Field field = clazz.getDeclaredField(columnLabel);
                        field.setAccessible(true);
                        field.set(t, value);
                    }
                }
                //获取每个列的列名

            }//end if

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {

            JDBCUtils.closeResource(null, ps, rs);

        }

        return t;

    }

}
