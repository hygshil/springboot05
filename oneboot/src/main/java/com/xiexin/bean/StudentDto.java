package com.xiexin.bean;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * student
 *
 * @author
 */
@ToString  //lombok的插件的注解，不用写toString
@Data  //不用写get set @Getter 不用写getter  @Setter，不用写
public class StudentDto implements Serializable {
    private Integer studentId;

    private String studentName;

    private Integer studentSex;

    private String studentCardId;

    private String studentPhone;

    private String studentAddress;
    private Date studentComeDate;

    private static final long serialVersionUID = 1L;

}