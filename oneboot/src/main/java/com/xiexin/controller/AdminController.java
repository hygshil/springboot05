package com.xiexin.controller;

import com.xiexin.bean.Admin;
import com.xiexin.bean.AdminExample;
import com.xiexin.respcode.Result;
import com.xiexin.service.AdminService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    //登录
    @RequestMapping("/login")
    public Map login(Admin admin, HttpSession session) {
        Map codeMap = new HashMap();
        AdminExample example = new AdminExample();
        AdminExample.Criteria criteria = example.createCriteria();
        criteria.andAdminNameEqualTo(admin.getAdminName());
        criteria.andAdminPwdEqualTo(admin.getAdminPwd());
        List<Admin> admins = adminService.selectByExample(example);
        if (admins != null && admins.size() > 0) {
            Admin dbAdminName = admins.get(0);
            //把这个账号信息放入到session中
            session.setAttribute("dbAdminName", dbAdminName);
            codeMap.put("code", 0);
            codeMap.put("msg", "登陆成功");
            codeMap.put("account", dbAdminName.getAdminName());
            return codeMap;
        } else {
            codeMap.put("code", 4001);
            codeMap.put("msg", "登录失败");
            return codeMap;
        }
    }

    //shiro登录
    @RequestMapping("/loginByShiro")
    public Result loginByShiro(@RequestBody Map map) {

        //登录交给shiro的securityManger管理
        Subject subject = SecurityUtils.getSubject();  //subject是根据过滤器拿到的
        UsernamePasswordToken token = new UsernamePasswordToken((String)map.get("adminAccount"),(String) map.get("adminPwd"));
        try {
            subject.login(token);
//            if((Boolean)map.get("rememberMe")){
//                token.setRememberMe(true);
//            }
            return new Result(0, "登录成功");
        } catch (UnknownAccountException e) { //账号错误
            e.printStackTrace();
            return new Result(40001,"账号不对");
        } catch (IncorrectCredentialsException e) { //密码错误
            e.printStackTrace();
            return new Result(40002,"密码不对");
        }


    }

    @RequestMapping("/insert")
    public Map insert(Admin admin) { //  对象传参, 规则: 前端属性要和后台的属性一致!!!
        Map map = new HashMap();
        int i = adminService.insertSelective(admin);
        if (i > 0) {
            map.put("code", 200);
            map.put("msg", "注册成功");
            return map;
        } else {
            map.put("code", 400);
            map.put("msg", "注册失败");
            return map;
        }
    }

    //老师讲的注册
    @RequestMapping("/reg")
    public Result reg(@RequestBody Admin admin) {
        String adminPwdWen = admin.getAdminPwd();  //没加密的密码
        //需要生成几位
        int n = 7;
        //最终生成的字符串
        String str = "";
        for (int i = 0; i < n; i++) {
            str = str + (char) (Math.random() * 26 + 'a');
        }
        System.out.println(str);
        //随机的几位字母作为salt
        Md5Hash md5Pwd = new Md5Hash(adminPwdWen, str, 1024); //社工 大数据！！
        admin.setAdminPwd(md5Pwd.toString());
        admin.setSalt(str);
        int i = adminService.insertSelective(admin);  //传进去的的是明文
        if (i == 1) {
            return new Result();
        } else {
            return new Result(40001, "注册失败");
        }

    }

    //shiro退出,登录是shiro管理，退出也是shiro
    @RequestMapping("/logout")
    public Result logout(){
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return new Result();
    }

//    @RequestMapping("/insertByShiro")
//    public Result insertByShiro(@RequestBody Admin admin) {
//        String salt = getRandomCharacterAndNumber(6);
//        Md5Hash pwd = new Md5Hash(admin.getAdminPwd(), salt, 1024);
//        admin.setAdminName(admin.getAdminName());
//        admin.setAdminPwd(pwd.toString());
//        admin.setAdminPhone(admin.getAdminPhone());
//        admin.setAdminAccount(admin.getAdminAccount());
//        admin.setSalt(salt);
//        admin.setTiwen(0.0D);
//        admin.setStatus(0);
//        int i = adminService.insertSelective(admin);
//
//        return new Result();
//    }

//    //随机盐
//    public static String getRandomCharacterAndNumber(int length) {
//        String val = "";
//        Random random = new Random();
//        for (int i = 0; i < length; i++) {
//            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; // 输出字母还是数字
//
//            if ("char".equalsIgnoreCase(charOrNum)) // 字符串
//            {
//                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; // 取得大写字母还是小写字母
//                val += (char) (choice + random.nextInt(26));
//                // int choice = 97; // 指定字符串为小写字母
//                val += (char) (choice + random.nextInt(26));
//            } else if ("num".equalsIgnoreCase(charOrNum)) // 数字
//            {
//                val += String.valueOf(random.nextInt(10));
//            }
//        }
//        return val;
//    }


}
