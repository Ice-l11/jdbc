package com.lyy.connection;

import com.lyy.entity.User;
import com.lyy.transaction.TransactionTest;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;

/**
 * @ClassName C3P0ConnectionPool
 * @Description C3P0数据库连接池,是一个开源组织提供的一个数据库连接池，速度相对较慢，稳定性还可以。hibernate官方推荐使用
 * @Author Ice
 * @Date 2022/9/17 16:25
 * @Version 1.0
 **/
public class C3P0ConnectionPool {


    private static ComboPooledDataSource cpds = new ComboPooledDataSource("c3p0"); //    <named-config name="c3p0">

    /*
     * 使用C3P0数据库连接池的方式，获取数据库的连接：不推荐
     */
    public static Connection c3p0Connection() throws Exception{
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass("com.mysql.cj.jdbc.Driver");
        cpds.setUser("root");
        cpds.setPassword("root1234");
        cpds.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        Connection conn = cpds.getConnection();
        return conn;
    }

    /*
     * 使用C3P0数据库连接池的配置文件方式，在src下配置c3p0-config.xml，获取数据库的连接：推荐
     */
    public static Connection c3p0Connection2() throws Exception{
        Connection conn = cpds.getConnection();
        return conn;
    }


    public static void main(String[] args) {

        try {
            String sql = "select user,password from user_table where balance = ?";
            TransactionTest tt = new TransactionTest();
           // System.out.println("c3p0Connection() = " + c3p0Connection());
           // System.out.println("c3p0Connection2() = " + c3p0Connection2());
            User user = tt.getInstance(c3p0Connection2(), User.class, sql, 1100);
            System.out.println("user = " + user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
