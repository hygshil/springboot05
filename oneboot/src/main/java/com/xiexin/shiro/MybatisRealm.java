package com.xiexin.shiro;

import com.xiexin.bean.Admin;
import com.xiexin.bean.AdminExample;
import com.xiexin.service.AdminService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

//自定义realm
//realm包含认证（登录）和授权2个部分
//
public class MybatisRealm extends AuthorizingRealm {
    @Autowired
    private AdminService adminService;

    @Override  //授权
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //写授权！！！查询3表可得到角色和权限
        //1.拿到account
        String principal =(String) principalCollection.getPrimaryPrincipal();
        Map map = new HashMap<>();
        map.put("adminAccount",principal);
        List<Map> maps = adminService.selectMore(map);
        //maps包含了角色名称，权限名称
        Set<String> roleNames = new HashSet<>();
        List perms = new ArrayList();
        for (Map map1 : maps) {
            String roleName = (String) map1.get("roleName");
            String qxPerms = (String) map1.get("qxPerms");
            //循环遍历到roleNames集合中
            roleNames.add(roleName);
            perms.add(qxPerms);
        }
        //把角色和权限给登录的账户
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRoles(roleNames);
        info.addStringPermissions(perms);
        return info;  //触发权限  1.界面UI触发，适用于单体项目  2.java方法注解触发，适用于前后端分离  3.不常用的自己硬写代码触发
        //界面触发要用到aop和jar包支持
    }

    @Override  //认证（登录）
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //AuthenticationToken  这个参数其实就是UsernamePasswordToken（“账号”，“密码”）
        String account = (String) authenticationToken.getPrincipal();//拿到账户名
        //当拿到账户名后，能否拿到数据库中的密码？？怎么拿？？单表的查询
        AdminExample example = new AdminExample();
        AdminExample.Criteria criteria = example.createCriteria();
        criteria.andAdminAccountEqualTo(account);
        List<Admin> admins = adminService.selectByExample(example);
        Admin dbAdmin = null;
        if (admins != null && admins.size() > 0) {
            dbAdmin = admins.get(0);
            //获取账户名和密码
            String pwd = dbAdmin.getAdminPwd();
            System.out.println("pwd = " + pwd);
            String salt = dbAdmin.getSalt();
            System.out.println("salt = " + salt);
            //进行token认证
            SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(account, pwd, ByteSource.Util.bytes(salt), this.getName());
            System.out.println("ByteSource.Util.bytes(salt) = " + ByteSource.Util.bytes(salt));
            return simpleAuthenticationInfo;
        }
        return null;
    }
}
