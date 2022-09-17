package com.lyy.connection;

import com.lyy.entity.User;
import com.lyy.transaction.TransactionTest;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

/**
 * @ClassName DBCPConnectionPool
 * @Description DBCP数据库连接池,是Apache提供的数据库连接池。tomcat服务器自带dbcp数据库连接池。速度相对c3p0较快，但因自身存在BUG，Hibernate3已不再提供支持。
 * @Author Ice
 * @Date 2022/9/17 17:16
 * @Version 1.0
 **/
public class DBCPConnectionPool {

    private static DataSource dataSource = null;
    //方式二
    static {
        try {
            InputStream is = DBCPConnectionPool.class.getClassLoader().getResourceAsStream("dbcp.properties");
            Properties properties = new Properties();
            properties.load(is);
            ////根据提供的BasicDataSourceFactory创建对应的DataSource对象
            dataSource = BasicDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 获取方式一
     */
    public static Connection DBCPConnection() throws Exception{
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        basicDataSource.setUrl("jdbc:mysql://localhost:3306/test");
        basicDataSource.setUsername("root");
        basicDataSource.setPassword("root1234");
        return basicDataSource.getConnection();
    }

    /*
     * 获取方式二：dbcp.properties配置文件
     */
    public static Connection DBCPConnection2() throws Exception{
        return dataSource.getConnection();
    }

    public static void main(String[] args) {
        try {
            String sql = "select user,password from user_table where balance = ?";
            TransactionTest tt = new TransactionTest();
            //jdbc:mysql://localhost:3306/test, UserName=root@localhost, MySQL Connector/J
            //System.out.println("DBCPConnection() = " + DBCPConnection());
             System.out.println("DBCPConnection2() = " + DBCPConnection2());
            User user = tt.getInstance(DBCPConnection2(), User.class, sql, 1100);
            System.out.println("user = " + user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
