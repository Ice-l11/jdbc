package com.lyy.entity;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName Customer
 * @Description Customer实体类
 * @Author Ice
 * @Date 2022/9/17 10:21
 * @Version 1.0
 **/
@Data
public class Customer {

    private Integer id;
    private String name;
    private String email;
    private Date birth;
}
