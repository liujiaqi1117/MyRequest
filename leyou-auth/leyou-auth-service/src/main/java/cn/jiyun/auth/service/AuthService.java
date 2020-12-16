package cn.jiyun.auth.service;

import cn.jiyun.auth.client.UserClient;
import cn.jiyun.auth.properties.JwtProperties;
import cn.leyou.auth.entity.UserInfo;
import cn.leyou.auth.utils.JwtUtils;
import com.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties properties;

    public String authentication(String username, String password) {

        try {
            // 调用微服务，执行查询
            User user = this.userClient.query(username, password);
            System.out.println("user: "+user);
            // 如果查询结果为null，则直接返回null
            if (user == null) {
                return null;
            }

            System.out.println("service层的密钥"+properties.getPrivateKey());
            // 如果有查询结果，则生成token
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), user.getUsername()),
                    properties.getPrivateKey(), properties.getExpire());
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
