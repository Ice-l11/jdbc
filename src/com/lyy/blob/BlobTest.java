package com.lyy.blob;

import com.lyy.entity.Customer;
import com.lyy.util.JDBCUtil;
import org.junit.Test;

import java.io.*;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

/**
 * @ClassName BlobTest
 * @Description Blob测试
 * @Author Ice
 * @Date 2022/9/17 9:53
 * @Version 1.0
 **/
public class BlobTest {

    @Test
    public void testFile(){
        File file = new File("static/girl.jpg");
        System.out.println("file.exists() = " + file.exists());
    }

    /*
    * 测试BLOB，往数据库插入一条数据
    */
    @Test
    public void blobInsert(){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCUtil.getConnection();
            String sql = "insert into customers(id,name,email,birth,photo) values(?,?,?,?,?)";
            ps = conn.prepareStatement(sql);
            ps.setObject(1,19);
            ps.setObject(2,"蔡文姬");
            ps.setObject(3,"113@qq.com");
            ps.setObject(4,"2010-09-17");
            FileInputStream fileInputStream = new FileInputStream(new File("static/girl.jpg"));
            ps.setBlob(5,fileInputStream);
            ps.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            JDBCUtil.closeResource(conn,ps);
        }

    }
    /*
    * 从数据表中读取大数据类型
    */
    @Test
    public void blobReadData(){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        InputStream binaryStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            conn = JDBCUtil.getConnection();
            String sql = "select id,name,email,birth,photo from customers where id = ?";
            ps = conn.prepareStatement(sql);
            ps.setObject(1,19);
            rs = ps.executeQuery();
            if (rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                Date birth = rs.getDate("birth");
                Customer customer = new Customer();
                customer.setId(id);
                customer.setName(name);
                customer.setEmail(email);
                customer.setBirth(birth);
                System.out.println("customer = " + customer);
                Blob photo = rs.getBlob("photo");
                binaryStream = photo.getBinaryStream();
                fileOutputStream = new FileOutputStream("static/little_girl.jpg");
                byte[] bytes = new byte[1024];
                int len;
                while ((len = binaryStream.read(bytes)) != -1){
                    fileOutputStream.write(bytes,0,len);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            JDBCUtil.closeResource(conn,ps,rs);
            if (binaryStream != null){
                try {
                    binaryStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
