package com.lyy.util;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * @ClassName JDBCUtil
 * @Description JDBC连接工具类
 * @Author lyy
 * @Date 2022/9/13 13:54
 * @Version 1.0
 **/
public class JDBCUtil {


    /**
     * @MethodName getConnection
     * @Description 获取数据库连接
     * @Return Connection
     * @throws Exception
     */
    public static Connection getConnection() throws Exception{
        //1、读取配置文件
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");
        Properties properties = new Properties();
        properties.load(inputStream);

        //2、读取连接信息
        String url = properties.getProperty("url"); //连接url
        String user = properties.getProperty("user"); //数据库用户
        String password = properties.getProperty("password"); //数据库密码

        //MYSQL5之后的驱动包可以省略
//        String driverClass = properties.getProperty("driverClasses"); //驱动
//        Class.forName(driverClass);

        //3、获取连接
        return DriverManager.getConnection(url,user,password);
    }

    /**
     * @MethodName closeResource
     * @Description 关闭资源
     * @throws Exception
     */
    public static void closeResource(Connection conn, Statement st){
        if (st != null){
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @MethodName closeResource
     * @Description 关闭资源
     * @throws Exception
     */
    public static void closeResource(Connection conn, Statement st, ResultSet rs){
        if (rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (st != null){
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
