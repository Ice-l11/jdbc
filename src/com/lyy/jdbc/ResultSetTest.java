package com.lyy.jdbc;

import com.lyy.entity.ExamStudent;
import com.lyy.util.JDBCUtil;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * @ClassName ResultSetTest
 * @Description ResultSet vs ResultSetMetaData
 * @Author ice
 * @Date 2022/9/15 14:05
 * @Version 1.0
 **/
public class ResultSetTest {
    @Test
    public void test(){
        String sql = "select flowid as flowId,type,IDCard as idCard,ExamCard as examCard,StudentName as studentName, " +
                " Location as location, grade from examstudent where grade = ?";
        ExamStudent examStudent = getInstance(sql, ExamStudent.class, 95);
        System.out.println("examStudent = " + examStudent.toString());
    }

    /*
     * ResultSetMetaData meta = rs.getMetaData();
     *   - **getColumnName**(int column)：获取指定列的名称
     *   - **getColumnLabel**(int column)：获取指定列的别名
     *   - **getColumnCount**()：返回当前 ResultSet 对象中的列数。
     *   - getColumnTypeName(int column)：检索指定列的数据库特定的类型名称。
     *   - getColumnDisplaySize(int column)：指示指定列的最大标准宽度，以字符为单位。
     *   - **isNullable**(int column)：指示指定列中的值是否可以为 null。
     *   - isAutoIncrement(int column)：指示是否自动为指定列进行编号，这样这些列仍然是只读的。
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
                ps.setObject(i + 1, args[i]);
            }
            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()){
                t = clazz.newInstance();
                for (int i = 0; i < rsmd.getColumnCount(); i++){
                    int columnDisplaySize = rsmd.getColumnDisplaySize(i + 1);
                    System.out.println("columnDisplaySize = " + columnDisplaySize);
                    String columnTypeName = rsmd.getColumnTypeName(i + 1);
                    String columnName = rsmd.getColumnName(i + 1); //数据库字段名
                    String columnLabel = rsmd.getColumnLabel(i + 1); //列的别名
                    Object columnValue = rs.getObject(columnLabel); //值
                    System.out.println("columnName = " + columnName + "columnTypeName = " + columnTypeName + ",columnLabel = " + columnLabel + ",columnValue = " + columnValue);
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t,columnValue);
                }
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            JDBCUtil.closeResource(conn,ps,rs);
        }
        return null;
    }
}
