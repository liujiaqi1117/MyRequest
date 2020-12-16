package com.leyou.item.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping
public interface CategoryApi {

    @GetMapping("/names")
    List<String> queryNamesById(@RequestParam("ids") List<Long> ids);



}
