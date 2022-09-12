package com.lyy.jdbc;

import com.lyy.entity.User;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

/**
 * @ClassName StatementTest
 * @Description Statement的使用
 * @Author lyy
 * @Date 2022/9/12 17:54
 * @Version 1.0
 **/
public class StatementTest {
    /**
     * Statement的使用
     * 结论：1、需要字符串拼接，繁琐；
     *      2、存在SQL注入问题；
     **/
    @Test
    public void testLogin(){
        String username = "AA";
        String password = "123456";
//        String sql = "select user,password from user_table where user = '" + username +"' and password = '" + password +"'";
        String sql = "SELECT user,password FROM user_table WHERE USER = '1'  AND PASSWORD ='1' or '1' = '1'"; //SQL注入
        User user = get(sql,User.class);
        if (user != null){
            System.out.println("登录成功");
            System.out.println("user.toString() = " + user.toString());
        }else {
            System.out.println("登录失败");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入用户名：");
        String username = scanner.nextLine();
        System.out.println("请输入密码：");
        String password = scanner.nextLine();
        String sql = "select user,password from user_table where user = '" + username +"' and password = '" + password +"'";
        User user = get(sql,User.class);
        if (user != null){
            System.out.println("登录成功");
            System.out.println("user.toString() = " + user.toString());
        }else {
            System.out.println("登录失败");
        }
    }

    public static <T> T get(String sql, Class<T> clazz){
        T t = null;
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            //1、加载配置文件
            InputStream inputStream = StatementTest.class.getClassLoader().getResourceAsStream("jdbc.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            String url = properties.getProperty("url");
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");
            //2、获取连接
            conn = DriverManager.getConnection(url, user, password);
            st = conn.createStatement();
            //执行sql
            rs = st.executeQuery(sql);
            //3、获取结果集的元数据
            ResultSetMetaData rsmd = rs.getMetaData();
            //结果集的列数
            int columnCount = rsmd.getColumnCount();
            if (rs.next()){
                t = clazz.newInstance();
                for (int i = 0; i < columnCount; i++){
                    String columnLabel = rsmd.getColumnLabel(i + 1); //获取列的别名
                    Object columnVal = rs.getObject(columnLabel); //获取列名的值
                    //利用反射将数据表中得到的数据，封装进对象
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t,columnVal);
                }
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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


        return null;
    }
}
