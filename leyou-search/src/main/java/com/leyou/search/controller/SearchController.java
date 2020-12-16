package com.leyou.search.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.common.pojo.SearchRequest;
import com.leyou.item.pojo.Goods;
import com.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping("page")
    public ResponseEntity<PageResult<Goods>> searchGoodsByKey(@RequestBody SearchRequest searchRequest){
        System.out.println("搜索的数据-0--------"+searchRequest.getKey());
        System.out.println("页数--------"+searchRequest.getPage());
        PageResult<Goods> goodsList = this.searchService.searchGoods(searchRequest);
        System.out.println("搜索的数据-0- 条数-------"+goodsList.getItems().size());
        if(goodsList == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return  ResponseEntity.ok(goodsList);
    }
}
