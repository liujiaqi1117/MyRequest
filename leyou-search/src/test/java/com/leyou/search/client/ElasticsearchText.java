package com.leyou.search.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Goods;
import com.leyou.item.pojo.SpuBo;
import com.leyou.search.LeyouSearchApplication;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

//存储测试类
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LeyouSearchApplication.class)
public class ElasticsearchText {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private SearchService searchService;

    @Test
    public void createIndex(){

        //创建索引库
        elasticsearchTemplate.createIndex(Goods.class);

        //创建映射
        elasticsearchTemplate.putMapping(Goods.class);
    }
    @Test
    public void deleteIndex() {
        elasticsearchTemplate.deleteIndex("goods");
    }
    @Test
    public  void loadData() {
        boolean index = this.elasticsearchTemplate.createIndex(Goods.class);
        boolean b = this.elasticsearchTemplate.putMapping(Goods.class);
        int page = 1;
        int rows = 100;
        int size = 0;
        do{
            PageResult<SpuBo> pageResult = goodsClient.querySpuByPage(page,rows,null,true);
            List<SpuBo> items = pageResult.getItems();
            size = items.size();
            //创建Goods集合
            List<Goods> goodList = new ArrayList<>();
            for (SpuBo spu: items){
                Goods goods = null;
                try {
                    goods = this.searchService.buildGoods(spu);
                } catch (JsonProcessingException e) {
                    break;
                }
                goodList.add(goods);
            }
            this.goodsRepository.saveAll(goodList);
            page++;
        }while (size == 100);
    }







}
