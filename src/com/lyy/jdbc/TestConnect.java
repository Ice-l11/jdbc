package com.lyy.jdbc;

import org.junit.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @ClassName TestConnect
 * @Description 测试JDBC连接方式
 *      1、第三方数据库API
 *      2、使用反射实例化Driver，不在代码中体现第三方数据库的API。体现面向接口编程思想
 *      3、使用DriverManager
 *      4、不显示注册驱动，因为DriverManager源码中存在静态代码块，实现了驱动注册
 *      5、
 * @Author lyy
 * @Date 2022/9/12 13:49
 * @Version 1.0
 **/
public class TestConnect {

    /**
     * @MethodName testConnect1
     * @Description 1、第三方数据库API
     **/
    @Test
    public void testConnect1(){
        Driver driver = null;
        try {
            //1。提供java.sql.Driver 接口实现类对象
//            driver = new com.mysql.jdbc.Driver();   //mysql-connector-java 5
            driver = new com.mysql.cj.jdbc.Driver(); //mysql-connector-java 8

            //2。提供url，指明具体操作的数据
            String url = "jdbc:mysql://localhost:3306/test";

            //3。提供Properties对象，指明用户名和密码
            Properties properties = new Properties();
            properties.setProperty("user","root");
            properties.setProperty("password","root1234");

            //4.调用driver的connect()，获取连接
            Connection connect = driver.connect(url, properties);
            System.out.println("connect = " + connect);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @MethodName testConnect2
     * @Description 2、使用反射实例化Driver，不在代码中体现第三方数据库的API。体现面向接口编程思想
     **/
    @Test
    public void testConnect2(){
        Driver driver = null;
        try {
            //1。实例化Driver
            String className = "com.mysql.cj.jdbc.Driver"; //驱动类全类名
            Class clazz = Class.forName(className);
            driver = (Driver) clazz.newInstance();

            //2。提供url，指明具体操作的数据
            String url = "jdbc:mysql://localhost:3306/test";

            //3。提供Properties对象，指明用户名和密码
            Properties properties = new Properties();
            properties.setProperty("user","root");
            properties.setProperty("password","root1234");

            //4.调用driver的connect()，获取连接
            Connection connect = driver.connect(url, properties);
            System.out.println("connect = " + connect);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @MethodName testConnect3
     * @Description 3、使用DriverManager
     **/
    @Test
    public void testConnect3(){
        Driver driver = null;
        try {
            //1、数据库连接四个基本要素
            String url = "jdbc:mysql://localhost:3306/test";
            String user = "root";
            String password = "root1234";
            String driverName = "com.mysql.cj.jdbc.Driver";

            //2、实例化Driver
            Class clazz = Class.forName(driverName);
            driver = (Driver) clazz.newInstance();

            //3、注册驱动
            DriverManager.registerDriver(driver);

            //4.获取连接
            Connection connect = DriverManager.getConnection(url,user,password);
            System.out.println("connect = " + connect);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @MethodName testConnect4
     * @Description 4、使用DriverManager
     **/
    @Test
    public void testConnect4(){
        Driver driver = null;
        try {
            //1、数据库连接四个基本要素
            String url = "jdbc:mysql://localhost:3306/test";
            String user = "root";
            String password = "root1234";
//            String driverName = "com.mysql.cj.jdbc.Driver";

            //2、实例化Driver
//            Class clazz = Class.forName(driverName);
//            driver = (Driver) clazz.newInstance();

            //3、注册驱动,MYSQL5之后的驱动包可以省略注册驱动的步骤,因为会自动加载jar包中META-INF/services/java.sql.Driver文件中的驱动类
//            DriverManager.registerDriver(driver);

            //4.获取连接
            Connection connect = DriverManager.getConnection(url,user,password);
            System.out.println("connect = " + connect);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @MethodName testConnect5
     * @Description 5、最终版，使用配置文件
     **/
    @Test
    public void testConnect5(){
        Driver driver = null;
        try {
            //1、加载配置文件
            ClassLoader classLoader = this.getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("jdbc.properties");
            Properties properties = new Properties();
            properties.load(inputStream);

            //2、获取连接信息
            String url = properties.getProperty("url");
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");

            //3.获取连接
            Connection connect = DriverManager.getConnection(url,user,password);
            System.out.println("connect = " + connect);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
