package io.github.jihch.dao;

import io.github.jihch.bean.Customer;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

/**
 * 此接口用于规范针对于 customers 表的常用操作
 */
public interface CustomerDAO {

    /**
     * 将 customer 对象添加到数据库中
     * @param conn
     * @param customer
     */
    void insert(Connection conn, Customer customer);

    /**
     * 针对指定的 id, 删除表中的一条记录
     * @param conn
     * @param id
     */
    void deleteById(Connection conn, int id);

    /**
     * 针对于内存中的 customer 对象，去修改数据表中指定的记录
     * @param conn
     * @param customer
     */
    void updateById(Connection conn, Customer customer);

    /**
     * 针对指定的 id 查询得到对应的 Customer 对象
     * @param conn
     * @param id
     */
    Customer getCustomerById(Connection conn, int id);

    /**
     * 查询表中的所有记录构成的集合
     * @param conn
     * @return
     */
    List<Customer> getAll(Connection conn);

    /**
     * 返回数据表中的数据的条目数
     * @param conn
     * @return
     */
    Long getCount(Connection conn);

    Date getMaxBirth(Connection conn);

}
