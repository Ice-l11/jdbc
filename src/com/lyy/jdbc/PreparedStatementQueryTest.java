package com.lyy.jdbc;

import com.lyy.entity.ExamStudent;
import com.lyy.entity.Order;
import com.lyy.util.JDBCUtil;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName PreparedStatementQueryTest
 * @Description 针对于不同的表的通用的查询操作，返回表中的查询记录
 * @Author lyy
 * @Date 2022/9/13 16:14
 * @Version 1.0
 **/
public class PreparedStatementQueryTest {

    @Test
    public void queryOne(){
        String sql = "select order_id as orderId,order_name as orderName from test.order where order_id = ?";
        Order order = getInstance(sql, Order.class, 1);
        System.out.println("order.toString() = " + order.toString());

        String sql2 = "select flowid as flowId,type,IDCard as idCard,ExamCard as examCard,StudentName as studentName, " +
                " Location as location, grade from examstudent where grade = ?";
        ExamStudent examStudent = getInstance(sql2, ExamStudent.class, 95);
        System.out.println("examStudent = " + examStudent.toString());
    }

    @Test
    public void queryList(){
        String sql2 = "select flowid as flowId,type,IDCard as idCard,ExamCard as examCard,StudentName as studentName, " +
                " Location as location, grade from examstudent";
        List<ExamStudent> examStudentList = queryList(sql2, ExamStudent.class);
        System.out.println("examStudentList = " + examStudentList.toString());
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
    public <T> T getInstance(String sql, Class<T> clazz, Object... args){
        T t = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCUtil.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++){
                ps.setObject(i + 1,args[i]);
            }
            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()){
                t = clazz.newInstance();
                for (int i = 0; i < rsmd.getColumnCount(); i++){
                    String columnLabel = rsmd.getColumnLabel(i + 1);
                    Object rsObject = rs.getObject(i + 1);
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t,rsObject);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            JDBCUtil.closeResource(conn,ps,rs);
        }
        return t;
    }

    public <T> List<T> queryList(String sql, Class<T> clazz, Object... args){
        List<T> list = new ArrayList<>();
        T t = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try{
            conn = JDBCUtil.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()){
                t = clazz.newInstance();
                for (int i = 0; i < rsmd.getColumnCount(); i++){
                    String columnLabel = rsmd.getColumnLabel(i + 1);
                    Object columnValue = rs.getObject(columnLabel);
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t,columnValue);
                }
                list.add(t);
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            JDBCUtil.closeResource(conn,ps,rs);
        }
        return list;
    }
}
