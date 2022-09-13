package com.lyy.jdbc;

import com.lyy.util.JDBCUtil;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @ClassName PreparedStatementUpdateTest
 * @Description 实现PreparedStatement增删改操作
 * @Author ice
 * @Date 2022/9/13 17:29
 * @Version 1.0
 **/
public class PreparedStatementUpdateTest {

    @Test
    public void test(){
//        System.out.println(insertOne());
//        System.out.println(deleteOne());
//        System.out.println(updateOne());
        String sql = "update user_table set password = ?, balance = ? where user = ?";
        System.out.println(commonUpdate(sql,"eee123",9000,"EE"));
    }

    /**
     * @Description 插入一条数据
     * @return 1成功 -1失败
     */
    public int insertOne(){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            String sql = "insert into user_table(user,password,balance)values(?,?,?)";
            conn = JDBCUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setObject(1,"EE");
            ps.setObject(2,"ee123");
            ps.setObject(3,2000);
            int bs = ps.executeUpdate();
            return bs;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            JDBCUtil.closeResource(conn,ps);
        }
        return -1;
    }
    /**
     * @Description 删除一条数据
     * @return 1成功 -1失败
     */
    public int deleteOne(){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            String sql = "delete from user_table where user = ?";
            conn = JDBCUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setObject(1,"EE");
            int bs = ps.executeUpdate();
            return bs;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            JDBCUtil.closeResource(conn,ps);
        }
        return -1;
    }

    /**
     * @Description 更新一条数据
     * @return 1成功 -1失败
     */
    public int updateOne(){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            String sql = "update user_table set password = ?,balance = ? where user = ?";
            conn = JDBCUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setObject(1,"ee123456");
            ps.setObject(2,5000);
            ps.setObject(3,"EE");
            int bs = ps.executeUpdate();
            return bs;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            JDBCUtil.closeResource(conn,ps);
        }
        return -1;
    }

    /**
     * @Description 增删改通用
     * @param sql
     * @param args
     * @return 1成功 -1是失败
     */
    public int commonUpdate(String sql, Object... args){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCUtil.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++){
                ps.setObject(i + 1,args[i]);
            }
            int bs = ps.executeUpdate();
            return bs;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            JDBCUtil.closeResource(conn,ps);
        }
        return -1;
    }
}
