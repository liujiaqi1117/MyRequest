package com.leyou.search.repository;

import com.leyou.item.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

//数据存储
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
