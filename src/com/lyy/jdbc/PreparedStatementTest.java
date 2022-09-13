package com.lyy.jdbc;

import com.lyy.entity.User;
import com.lyy.util.JDBCUtil;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Scanner;

/**
 * @ClassName PreparedStatementTest
 * @Description PreparedStatement的使用
 * @Author lyy
 * @Date 2022/9/13 14:14
 * @Version 1.0
 **/
public class PreparedStatementTest {

    /*
    *  PreparedStatement vs Statement
    *   1、提高代码的可读性和可维护性；
    *   2、PreparedStatement能大可能的提高性能：
    *       2.1 DBServer会对预编译的SQL提供性能优化。因为预编译语句有可能被重复调用，所以语句在被DBServer的编译器编译后的执行代码被缓存下来，那么下次调用只要是相同
    *           的预编译语句就不需要预编译，只要将参数参入编译过的语句执行代码中就会得到执行；
    *       2.2 在Statement中，即使是相同的操作但是因为数据不一样，所以整个语句本身不能匹配，没有缓存语句的意义。事实是没有数据库会对普通语句编译后执行代码缓存。
    *           这样每执行一次就要对传入的语句编译一次；
    *       2.3 PreparedStatement可以防止SQL注入；
    **/
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入用户名：");
        String username = scanner.nextLine();
        System.out.println("请输入密码：");
        String password = scanner.nextLine();
        String sql = "select user,password from user_table where user = ? and password = ?";
        User user = getInstance(sql,User.class,username,password);
        if (user != null){
            System.out.println("登录成功");
            System.out.println("user.toString() = " + user.toString());
        }else {
            System.out.println("登录失败");
        }
    }

    /**
     * @Description 针对于不同的表的通用的查询操作，返回表中的一条记录
     * @author shkstart
     * @date 上午11:42:23
     * @param clazz
     * @param sql
     * @param args
     * @return
     */
    public static <T> T getInstance(String sql, Class<T> clazz, Object... args){
        T t = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtil.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++){
                ps.setObject(i+1,args[i]);
            }
            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            if (rs.next()){
                t = clazz.newInstance(); //实例化对象
                for (int c = 0; c < columnCount; c++){
                    String columnLabel = rsmd.getColumnLabel(c + 1); //获取列名
                    Object columnValue = rs.getObject(columnLabel); //列名对应值
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t,columnValue);
                }
                return t;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            JDBCUtil.closeResource(conn,ps,rs);
        }
        return null;
    }
}
