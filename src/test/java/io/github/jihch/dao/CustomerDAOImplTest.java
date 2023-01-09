package io.github.jihch.dao;

import io.github.jihch.bean.Customer;
import io.github.jihch.util.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

import static org.junit.Assert.*;

public class CustomerDAOImplTest {

    private CustomerDAOImpl dao = new CustomerDAOImpl();

    @Test
    public void insert() {
        Connection con = null;
        try {
            con = JDBCUtils.getConnection();
            Customer customer = new Customer(0, "张三", "zhangsan@126.com", new Date(System.currentTimeMillis()));
            dao.insert(con, customer);
            System.out.println("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(con, null);
        }
    }

    @Test
    public void deleteById() {
        Connection con = null;
        try {
            con = JDBCUtils.getConnection();
            dao.deleteById(con, 8);
            System.out.println("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(con, null);
        }
    }

    @Test
    public void updateById() {
        Connection con = null;
        try {
            con = JDBCUtils.getConnection();
            Customer customer = new Customer(2, "李四", "lisi@126.com", new Date(System.currentTimeMillis()));
            dao.updateById(con, customer);
            System.out.println("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(con, null);
        }
    }

    @Test
    public void getCustomerById() {
        Connection con = null;
        try {
            con = JDBCUtils.getConnection();
            Customer customer = dao.getCustomerById(con, 4);
            System.out.println(customer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(con, null);
        }
    }

    @Test
    public void getAll() {
        Connection con = null;
        try {
            con = JDBCUtils.getConnection();
            List<Customer> all = dao.getAll(con);
            all.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(con, null);
        }
    }

    @Test
    public void getCount() {
        Connection con = null;
        try {
            con = JDBCUtils.getConnection();
            Long count = dao.getCount(con);
            System.out.printf("表中的记录数为：%d\n", count);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(con, null);
        }
    }

    @Test
    public void getMaxBirth() {
        Connection con = null;
        try {
            con = JDBCUtils.getConnection();
            Date maxBirth = dao.getMaxBirth(con);
            System.out.printf("最大的生日为：%s\n", maxBirth);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(con, null);
        }
    }
}