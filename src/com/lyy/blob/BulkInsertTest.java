package com.lyy.blob;

import com.lyy.util.JDBCUtil;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * @ClassName BulkInsertTest
 * @Description 批量插入测试
 *                  1、使用Statement插入20000条数据
 *                  2、使用PreparedStatement替代Statement插入20000条数据
 *                  3、使用addBatch、executeBatch、clearBatch()，开启批处理
 *                  4、设置连接不允许自动提交数据
 * @Author Ice
 * @Date 2022/9/17 11:19
 * @Version 1.0
 **/
public class BulkInsertTest {


    /*
    * 使用Statement插入20000条数据
    */
    @Test
    public void statementTest(){
        long startTimeMills = System.currentTimeMillis();
        Connection conn = null;
        Statement st = null;
        try{
            conn = JDBCUtil.getConnection();
            st = conn.createStatement();
            for (int i = 1 ; i <= 20000; i++){
                st.executeUpdate("insert into goods(name) values('name_"+ i +"')");
            }
            long endTimeMills = System.currentTimeMillis();
            System.out.println("耗时 = " + (endTimeMills - startTimeMills) + "ms"); //31237ms
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            JDBCUtil.closeResource(conn,st);
        }
    }

    /*
     * 优化一：使用PreparedStatement替代Statement插入20000条数据
     */
    @Test
    public void preStatementTest(){
        long startTimeMills = System.currentTimeMillis();
        Connection conn = null;
        PreparedStatement ps = null;
        try{
            conn = JDBCUtil.getConnection();
            String sql = "insert into goods(name) values(?)";
            ps = conn.prepareStatement(sql);
            for (int i = 1 ; i <= 20000; i++){
                ps.setObject(1,"name_" + i);
                ps.executeUpdate();
            }
            long endTimeMills = System.currentTimeMillis();
            System.out.println("耗时 = " + (endTimeMills - startTimeMills) + "ms"); //31636ms
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            JDBCUtil.closeResource(conn,ps);
        }
    }

    /*
     * 方式三：
     *      1、使用addBatch、executeBatch、clearBatch()
     *      2、mysql服务器默认是关闭服务器的，需要通过一个参数，让mysql开启批处理的支持，?rewriteBatchedStatements=true 写在配置文件的url后面
     */
    @Test
    public void batch(){
        long startTimeMills = System.currentTimeMillis();
        Connection conn = null;
        PreparedStatement ps = null;
        try{
            conn = JDBCUtil.getConnection();
            String sql = "insert into goods(name) values(?)";
            ps = conn.prepareStatement(sql);
            for (int i = 1 ; i <= 20000; i++){
                ps.setObject(1,"name_" + i);
                ps.addBatch();
                if (i % 500 == 0){
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }
            long endTimeMills = System.currentTimeMillis();
            System.out.println("耗时 = " + (endTimeMills - startTimeMills) + "ms"); //31783ms  开启批处理之后1047ms
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            JDBCUtil.closeResource(conn,ps);
        }
    }

    /*
     * 方式四：设置连接不允许自动提交数据
     */
    @Test
    public void batch2(){
        long startTimeMills = System.currentTimeMillis();
        Connection conn = null;
        PreparedStatement ps = null;
        try{
            conn = JDBCUtil.getConnection();
            conn.setAutoCommit(false);
            String sql = "insert into goods(name) values(?)";
            ps = conn.prepareStatement(sql);
            for (int i = 1 ; i <= 20000; i++){
                ps.setObject(1,"name_" + i);
                ps.addBatch();
                if (i % 500 == 0){
                    ps.executeBatch();
                    ps.clearBatch();
                }
            }
            conn.commit(); //提交
            long endTimeMills = System.currentTimeMillis();
            System.out.println("耗时 = " + (endTimeMills - startTimeMills) + "ms"); //3685ms  开启批处理之后998ms
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            JDBCUtil.closeResource(conn,ps);
        }
    }
}
