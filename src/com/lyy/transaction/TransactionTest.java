package com.lyy.transaction;

import com.lyy.entity.User;
import com.lyy.util.JDBCUtil;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * @ClassName TransactionTest
 * @Description 数据库事务
 * @Author Ice
 * @Date 2022/9/17 14:36
 * @Version 1.0
 **/
public class TransactionTest {

    /*
     * 事务：一组逻辑操作单元,使数据从一种状态变换到另一种状态。
     * 事务处理（事务操作）：保证所有事务都作为一个工作单元来执行，即使出现了故障，都不能改变这种执行方式。当在一个事务中执行多个操作时，
     *                   要么所有的事务都被提交(commit)，那么这些修改就永久地保存下来；要么数据库管理系统将放弃所作的所有修改，整个事务回滚(rollback)到最初状态。
     * 案例：用户AA向用户BB转账100
     * 事务的ACID：
     *  1、原子性（Atomicity）：事务是一个不可分割的工作单位，事务中的操作要么都发生，要么都不发生
     *  2、一致性（Consistency）：事务必须使数据库从一个一致性状态变更到另一个一致性状态
     *  3、隔离性（Isolation）：一个事务的执行不能被其它事务干扰，即一个事务内部的操作及使用的数据对并发的其它事务是隔离的，并发执行的各个事务不能相互干扰
     *  4、持久性（Durability）:一个事务一但被提交，对数据库中的数据的改变是永久性的，接下来的其他操作和数据库故障不应该对其有任何影响
     *
     * 并发问题：
     *  对于同时运行的多个事务，当这些事务访问数据库中相同的数据时，如果没有采取必要的隔离机制，就会导致各种并发问题；
     *  1、脏读：对于两个事务T1、T2，T1读取了被T2更新后还未提交的数据，此时，若T2回滚，T1读取的内容就是临时的无效的；
     *  2、不可重复读；对于两个事务T1、T2，T1读取了某个字段，此时T2更新了这个字段，当T1再次读取这个字段时，值就不同了；
     *  3、幻读：对于两个事务T1、T2，T1读取了表中的某个字段，此时T2在该表中插入了一些新的行。之后，T1再次读取这个表的字段，就会多出几行；
     *
     * 四种隔离级别：
     *  1、READ UNCOMMITTED（读未提交）：允许事务读取未被其他事务未提交的变更，脏读、不可重复读和幻读的问题都有可能出现；
     *  2、READ COMMITTED（读已提交）：只允许事务读取被其他事务提交的变更，可以避免脏读，但是不可重复读和幻读的问题仍有可能出现；
     *  3、REPEATABLE（可重复读）：确保事务可以多次从一个字段中读取相同的值，在这个事务持续期间，禁止其他事务对这个字段进行更新，可以避免脏读和不可重复读，
     *                          但幻读的问题仍然存在；
     *  4、SERIALIZABLE（串行化）：确保事务可以从一个表中读取相同的行，在这个事务的持续期间，禁止其他事务对该表进行插入、更新和删除的操作，所有的并发问题
     *                          都可以避免，但是性能十分低下；
     *
     * - Oracle支持的2种事务隔离级别：READ COMMITTED, SERIALIZABLE。 Oracle默认的事务隔离级别为: READ COMMITTED。
     * - Mysql支持4种事务隔离级别。Mysql默认的事务隔离级别为: REPEATABLE READ。
     *
     */
    @Test
    public void transferAccounts(){
        Connection connection = null;
        try {
            connection = JDBCUtil.getConnection();
            connection.setAutoCommit(false); //取消自动提交事务
            String sql1 = "update user_table set balance = balance - 100 where user = ?";
            update(connection,sql1,"AA");
            int a = 10/0;  //模拟网络异常

            String sql2 = "update user_table set balance = balance + 100 where user = ?";
            update(connection,sql2,"BB");
            connection.commit(); //无异常，提交事务
        } catch (Exception e) {
            e.printStackTrace();
            //有异常，需要回滚
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
            JDBCUtil.closeResource(connection, null);
        }
    }

    /*
     * 考虑事务，更新一条数据
     */
    public void update(Connection conn, String sql, Object... args){
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++){
                ps.setObject(i + 1, args[i]);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(null,ps);
        }
    }

    /*查询一条记录*/
    @Test
    public void queryOne(){
        String sql = "select user,password from user_table where balance = ?";
        Connection conn = null;
        try {
            conn = JDBCUtil.getConnection();
            int transactionIsolation = conn.getTransactionIsolation();
            System.out.println("transactionIsolation = " + transactionIsolation);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            User user = getInstance(conn, User.class, sql, 1100);
            System.out.println("user = " + user);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.closeResource(conn,null);
        }
    }

    /*
     * 事务查询
     */
    public <T> T getInstance(Connection conn, Class<T> clazz, String sql, Object... args){
        T t = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++){
                ps.setObject(i + 1, args[i]);
            }
            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()){
                t = clazz.newInstance();
                for (int i = 0; i < rsmd.getColumnCount(); i++){
                    String columnLabel = rsmd.getColumnLabel(i + 1);
                    Object object = rs.getObject(columnLabel);
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t,object);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            JDBCUtil.closeResource(null,ps,rs);
        }
        return t;
    }
}
