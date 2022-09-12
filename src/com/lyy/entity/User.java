package com.lyy.entity;

/**
 * @ClassName User
 * @Description User实体类
 * @Author lyy
 * @Date 2022/9/12 17:53
 * @Version 1.0
 **/
public class User {

    private String user;
    private String password;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
