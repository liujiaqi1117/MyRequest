package com.leyou.search.client;

import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.SpecParam;
import com.leyou.search.LeyouSearchApplication;
import com.netflix.discovery.converters.Auto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.filter.TypeExcludeFilters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LeyouSearchApplication.class)
public  class CategoryClientTest {

    @Autowired
    private CategoryClient categoryClient;


    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private SpecificationClient specificationClient;

    //查询分类
    @Test
    public void textQueryCnames(){
        List<String> nameList = this.categoryClient.queryNamesById(Arrays.asList(74L, 75L, 76L));
        nameList.forEach(System.out::println);
        }

    //查询sku
    @Test
    public  void goodsText(){
        List<Sku> skus = goodsClient.querySkuBySpuId(2L);
        skus.forEach(System.out::println);

    }

    //查询品牌
    @Test
    public  void brandText(){
        Brand brand = brandClient.queryBrandById(8214L);
        System.out.println(brand.getName());
        System.out.println(brand.toString());
    }

    //查询规格参数
    @Test
    public void  specificationText(){
        List<SpecParam> specParamList = specificationClient.querySpecParam(null, 76L, true, true);
        specParamList.forEach(System.out::println);
    }
}


