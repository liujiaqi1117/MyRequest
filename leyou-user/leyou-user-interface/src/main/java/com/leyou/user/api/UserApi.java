package com.leyou.user.api;

import com.leyou.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


public interface UserApi {

    @GetMapping("query")
   User query(
            @RequestParam("username") String username,
            @RequestParam("password") String password);
}
