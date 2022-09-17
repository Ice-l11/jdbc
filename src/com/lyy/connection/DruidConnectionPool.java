package com.lyy.connection;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.lyy.entity.User;
import com.lyy.transaction.TransactionTest;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

/**
 * @ClassName DruidConnectionPool
 * @Description Druid连接池，Druid是阿里巴巴开源平台上一个数据库连接池实现，它结合了C3P0、DBCP、Proxool等DB池的优点，
 *              同时加入了日志监控，可以很好的监控DB池连接和SQL的执行情况，可以说是针对监控而生的DB连接池，可以说是目前最好的连接池之一
 * @Author Ice
 * @Date 2022/9/17 17:33
 * @Version 1.0
 **/
public class DruidConnectionPool {

    public static Connection getConnection() throws Exception{
        InputStream is = DruidConnectionPool.class.getClassLoader().getResourceAsStream("druid.properties");
        Properties properties = new Properties();
        properties.load(is);
        DataSource dataSource = DruidDataSourceFactory.createDataSource(properties);
        return dataSource.getConnection();
    }

    public static void main(String[] args) {
        try {
            String sql = "select user,password from user_table where balance = ?";
            TransactionTest tt = new TransactionTest();
            //com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl@5ccddd20
            System.out.println("getConnection() = " + getConnection());
            User user = tt.getInstance(getConnection(), User.class, sql, 1100);
            System.out.println("user = " + user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
