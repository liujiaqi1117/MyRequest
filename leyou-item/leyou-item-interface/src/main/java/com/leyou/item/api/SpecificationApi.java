package com.leyou.item.api;

import com.leyou.item.pojo.SpecParam;
import com.leyou.item.pojo.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("spec")
public interface SpecificationApi {


    @GetMapping("/params")
     List<SpecParam> querySpecParam(
            @RequestParam(value="gid", required = false) Long gid,
            @RequestParam(value="cid", required = false) Long cid,
            @RequestParam(value="searching", required = false) Boolean searching,
            @RequestParam(value="generic", required = false) Boolean generic
    );

    @GetMapping("groups/{cid}")
    List<Specification> querySpecificationByCid(@PathVariable("cid") Long cid);

    // 查询规格参数组，及组内参数
    @GetMapping("{cid}")
    List<Specification> querySpecsByCid(@PathVariable("cid") Long cid);
}
