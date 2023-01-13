package io.github.jihch.betterutil;

import io.github.jihch.bean.Customer;
import io.github.jihch.betterdao.CustomerDAOImpl;
import org.junit.Test;

import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class JDBCUtilsTest {

    private CustomerDAOImpl dao = new CustomerDAOImpl();

    @Test
    public void getConnection() {
        Connection con = null;
        try {
            con = JDBCUtils.getConnectionFromC3P0();
            Customer customer = dao.getCustomerById(con, 4);
            System.out.println(customer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(con, null);
        }
    }

    /**
     * 每次 connnection 的 hashcode 都不同，怎么判断 connnection 被重用了？
     */
    @Test
    public void getConnectionReuse() {
        Connection con = null;
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            try {
                con = JDBCUtils.getConnectionFromC3P0();
                set.add(con.hashCode());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                JDBCUtils.closeResource(con, null);
            }
        }
        set.forEach(System.out::println);
    }


    @Test
    public void getConnectionFromDBCP() {
        Connection con = null;
        try {
            con = JDBCUtils.getConnectionFromDBCP();
            Customer customer = dao.getCustomerById(con, 4);
            System.out.println(customer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(con, null);
        }
    }

    @Test
    public void getConnectionFromDruid() {
        Connection con = null;
        try {
            con = JDBCUtils.getConnectionFromDruid();
            Customer customer = dao.getCustomerById(con, 4);
            System.out.println(customer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(con, null);
        }
    }
}