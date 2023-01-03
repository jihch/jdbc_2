package io.github.jihch.transaction;

import io.github.jihch.util.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

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

}
