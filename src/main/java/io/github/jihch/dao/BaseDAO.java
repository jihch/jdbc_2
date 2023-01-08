package io.github.jihch.dao;

import io.github.jihch.util.JDBCUtils;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 封装了针对于数据表的通用操作
 * abstract 声明为一个抽象类，不能用来实例化对象使用，只能通过实例化继承了 BaseDAO 的子类来执行
 */
public abstract class BaseDAO {

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

    //通用的查询操作，用于返回数据表中多条记录构成的集合（version 2.0；考虑上事务）
    public <T> List<T> getForList(Connection conn, Class<T> clazz, String sql, Object... args) {

        List<T> list = new ArrayList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;

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
            Map<String, Field> fieldMap = Arrays.asList(clazz.getDeclaredFields()).stream().collect(Collectors.toMap(f -> f.getName(), f->f));

            while (rs.next()) {
                T t = clazz.newInstance();

                for (int i = 0; i < columnCount; i++) {
                    //获取每个列的列值：通过ResultSet
                    Object value = rs.getObject(i + 1);

                    //获取每个列的列名：通过ResultSetMetaData
                    //获取列的列名：getColumnName() --不推荐使用
                    //获取列的别名：getColumnLabel()
                    String columnName = rsmd.getColumnName(i + 1);
                    String columnLabel = rsmd.getColumnLabel(i + 1);
                    System.out.printf("columnName:%s, columnLabel:%s\n", columnName, columnLabel);

                    //通过反射，将对象指定名 columnName 的属性赋值为指定的值 columnValue
                    if (fieldMap.containsKey(columnLabel)) {
                        Field field = clazz.getDeclaredField(columnLabel);
                        field.setAccessible(true);
                        field.set(t, value);
                    }
                }
                //获取每个列的列名

                list.add(t);

            }//end if


        } catch (SQLException e) {
            throw new RuntimeException(e);

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            JDBCUtils.closeResource(null, ps, rs);

        }

        return list;
    }

    //用于查询特殊值的通用方法
    public <E> E getValue(Connection conn, String sql, Object...args) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                return (E) rs.getObject(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        } finally {

            JDBCUtils.closeResource(null, ps, rs);
        }

        return null;

    }

}
