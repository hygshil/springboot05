package com.xiexin.springtest;

import com.xiexin.service.AdminService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//springboot对dao或者service的测试，也是单元测试，在公司，每写完一个service，dao就去测试一下
@RunWith(SpringRunner.class)  //@RunWith可以用
@SpringBootTest  //证明可以启动boot 
@EnableAutoConfiguration  //开启配置
public class AdminTest {
    @Autowired
    private AdminService adminService;
    @Test
    public void selectMore(){
        Map map = new HashMap<>();
        map.put("adminAccount","hopu01");
        List<Map> maps = adminService.selectMore(map);
        System.out.println("maps = " + maps);
        for (Map map1 : maps) {
            System.out.println("map1.get(\"adminName\") = " + map1.get("adminName"));
        }
    }
}
