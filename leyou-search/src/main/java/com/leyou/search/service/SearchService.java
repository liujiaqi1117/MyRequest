package com.leyou.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private GoodsRepository goodsRepository;

    private ObjectMapper objectMapper = new ObjectMapper();


    /**
     * 读取数据  存入Goods对象中
     * 传入参数spu
     * */
    /* public Goods buildGoods(Spu spu) throws JsonProcessingException {
     *//**
     * 创建Goods对象 将读取到的数据放入Goods对象中
     * *//*

        Goods goods = new Goods();
        //读取分类数据   根据spu里的cid读取
        List<String> nameList = categoryClient.queryNamesById(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        //读取品牌数据   根据spu里的brandId读取
        Brand brand = brandClient.queryBrandById(spu.getBrandId());

        //读取商品详情数据  根据spu的id读取spudetail数据
        SpuDetail spuDetail = goodsClient.querySpuDetailById(spu.getId());

        //查询sku数据  根据spu的id查询sku的数据
        List<Sku> skus = goodsClient.querySkuBySpuId(spu.getId());

        //查询规格参数  根据spu里的第三级分类查询规格参数
        List<SpecParam> paramList = specificationClient.querySpecParam(null,spu.getCid3(),true,null);


        *//**
     * 处理数据  将数据封装到Goods数据中
     * *//*
        //创建价格集合
        ArrayList<Long> priceList = new ArrayList<>();
        //创建规格参数的集合
       List<Map<String, Object>> skuList = new ArrayList<>();

       //遍历skus集合将数据放到分别集合里
        skus.forEach(sku -> {
            priceList.add(sku.getPrice());
            //遍历map集合
            Map<String, Object> skuMap = new HashMap<>();
            //将sku的数据放到map集合里
            skuMap.put("id",sku.getId());
            skuMap.put("title",sku.getTitle());
            skuMap.put("image", StringUtils.isBlank(sku.getImages())?"":StringUtils.split(sku.getImages(),",")[0]);
            skuMap.put("price",sku.getPrice());
            //将数据放到map集合里
            skuList.add(skuMap);

        });

        *//**
     * 解析参数规格
     *    因为detail参数里的表存放的是sku的JSON字符串 通过工具进行解析 放入map集合里 键为id 值为各个参数的值
     * *//*
        Map<String, Object> genericSpecMap = objectMapper.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {
        });

        Map<String, Object> specialMap = objectMapper.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, Object>>() {
        });

        //创建用于搜索的规格参数集合
        HashMap<String,Object> seachSpecs = new HashMap<>();

        //过滤模板
        HashMap<String,Object> seachMap = new HashMap<>();
        //循环参数集合
        paramList.forEach(p->{
            //判断该参数是否用是用于搜索的字段  是用于搜索的字段 将数据封装到集合里面
            if(p.getSearching()){
                if(p.getGeneric()){
                    String value = genericSpecMap.get(p.getId().toString()).toString();
                    if(p.getNumeric()){
                          value = chooseNumeric(value,p);
                    }
                    seachMap.put(p.getName(),StringUtils.isBlank(value)?"其他":value);
                }else{
                    seachMap.put(p.getName(),specialMap.get(p.getId().toString()));
                }


            }

        });
        goods.setId(spu.getId());
        goods.setSubTitle(spu.getSubTitle());
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setAll(spu.getTitle()+" "+StringUtils.join(nameList," "));
        goods.setPrice(priceList);
        goods.setSkus(objectMapper.writeValueAsString(skus));
        goods.setSpecs(seachMap);
        return goods;

    }

    public String chooseNumeric(String value,SpecParam p){
        double val = NumberUtils.toDouble(value);
        String result = "其他";
        for(String segement : p.getSegments().split(",")){
            String[] segs = segement.split("-");
            //获取数值范围
            double begin = NumberUtils.toDouble((segs[0]));
            double end = 0.0;
            if(segs.length==2){
                end = NumberUtils.toDouble(segs[1]);
            }
            if(val>=begin && val<end){
                if(segs.length==1){
                    result =  segs[0] + p.getUnit()+"以上";
                }else if(begin==0){
                    result =  segs[1] + p.getUnit()+"以上";
                }else {
                    result =  segement+ p.getUnit()+"以上";
                }
                break;
            }
        }
        return result;
    }
*/


    public Goods buildGoods(Spu spu) throws JsonProcessingException {
        Goods goods = new Goods();
        //查询商品分类名称
        List<String> names = this.categoryClient.queryNamesById(Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3()));
        //查询品牌名称
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        //查询spuDetail商品详情
        SpuDetail spuDetail = goodsClient.querySpuDetailById(spu.getId());
        //查询sku集合数据
        List<Sku>  skus=this.goodsClient.querySkuBySpuId(spu.getId());
        //查询规格参数
        List<SpecParam> specParams = this.specificationClient.querySpecParam(null, spu.getCid3(),true,null);

        //处理获取的数据，对Goods进行封装   处理sku,仅封装id,标题，图片，并获取价格集合
        ArrayList<Long> prices = new ArrayList<>();
        ArrayList<Map<String,Object>> skuList = new ArrayList<>();

        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            HashMap<String, Object> skuMap = new HashMap<>();
            skuMap.put("id",sku.getId());
            skuMap.put("title",sku.getTitle());
            skuMap.put("price",sku.getPrice());
            skuMap.put("image", StringUtils.isBlank(sku.getImages())?"":StringUtils.split(sku.getImages(),",")[0]);
            skuList.add(skuMap);
        });


        //处理参数规格
        Map<String,Object> genericSpec= objectMapper.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String,Object>>() {
        });
        Map<String,Object> specialSpec= objectMapper.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String,Object>>() {
        });
        //声明用于搜索的规格参数
        HashMap<String, Object> searchSpec = new HashMap<>();

        HashMap<String, Object> specMap = new HashMap<>();
        //保存数据生成searchSpec
        specParams.forEach(p ->{
            if (p.getSearching()){
                if (p.getGeneric()){
                    String value= genericSpec.get(p.getId().toString()).toString();
                    if (p.getNumeric()){
                        value = chooseNumeric(value,p);
                        // searchSpec.get(p.getId(), toString()).toString() + genericSpec.get(p.getId());
                    }
                    specMap.put(p.getName(),StringUtils.isBlank(value)?"其他":value);
                }else {
                    specMap.put(p.getName(),specialSpec.get(p.getId().toString()));
                }
            }
        });
        goods.setId(spu.getId());
        goods.setSubTitle(spu.getSubTitle());
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setAll(spu.getTitle()+" "+StringUtils.join(names," "));
        goods.setPrice(prices);
        goods.setSkus(objectMapper.writeValueAsString(skuList));
        goods.setSpecs(specMap);
        return goods;

    }
    private  String chooseNumeric(String value,SpecParam p){
        double val = NumberUtils.toDouble(value);
        String result = "其他";

        for (String segments :p.getSegments().split(",")){
            String[] segs = segments.split("-");
            //获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end =0.0;
            if (segs.length==2){
                end = NumberUtils.toDouble(segs[1]);
            }
            if (val>=begin&&val<end){
                if (segs.length==1){
                    result=    segs[0]+p.getUnit()+ "以上";
                }else if(begin==0){
                    result =segs[1]+p.getUnit()+"以下";
                }else {
                    result = segments+p.getUnit();
                }
                break;
            }

        }
        return result;
    }



    //查询数据
    public SearchResult searchGoods(SearchRequest searchRequest) {

        //获取key值
        String key = searchRequest.getKey();
        //判断key
        if(StringUtils.isBlank(key)){
            return null;
        }

        //构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
       // MatchQueryBuilder basicQuery = QueryBuilders.matchQuery("all", searchRequest.getKey()).operator(Operator.AND);

        //对key进行全文检索
       // queryBuilder.withQuery(QueryBuilders.matchQuery("all",key).operator(Operator.AND));

        BoolQueryBuilder basicQuery = getboolQueryBuilder(searchRequest);

        queryBuilder.withQuery(basicQuery);
        //通过sourceFilter设置返回的结果字段 id  skus subTitle
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));

        //分页数据
        Integer page = searchRequest.getPage();
        Integer size = searchRequest.getSize();
        queryBuilder.withPageable(PageRequest.of(page-1,size));
             //排序
        String sortBy = searchRequest.getSortBy();
        Boolean desc = searchRequest.getDescending();
        if (StringUtils.isNotBlank(sortBy)){
            //如果为空则进行排序
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc ? SortOrder.DESC : SortOrder.ASC));
        }


        String categoryAggName = "categories";
        String brandAggName = "brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>)this.goodsRepository.search(queryBuilder.build());
        List<Map<String, Object>> categories = getCategoryAggResult(goodsPage.getAggregation(categoryAggName));


        List<Brand> brands = getBrandAggResult(goodsPage.getAggregation(brandAggName));
        //查询数据
        Page<Goods> searchList = this.goodsRepository.search(queryBuilder.build());

        List<Map<String, Object>> spec = null;
        //判断分类聚合结果集的大小 等于 一 则聚合查询
        if(categories.size()==1){
            spec = getSpecAggResult((Long)categories.get(0).get("id"), basicQuery);
        }
        System.out.println("search里的数据"+spec.get(0).toString());
        //封装数据
        //return new PageResult<>(searchList.getTotalElements(),Long.parseLong(searchList.getTotalPages()+""),searchList.getContent());
       // return new SearchResult(goodsPage.getTotalElements(),Long.parseLong(goodsPage.getTotalPages()+""),goodsPage.getContent(),categories,brands);
        return new SearchResult(goodsPage.getTotalElements(),Long.parseLong(goodsPage.getTotalPages()+""),goodsPage.getContent(),categories,brands,spec);

    }


    /**
     * 根据 品牌 分类  查询数据
     * */
    private List<Brand> getBrandAggResult(Aggregation aggregation){
        //用于处理集合结果集
        LongTerms longTerms = (LongTerms)aggregation;
        //获取所有的品牌Id
        List<LongTerms.Bucket> buckets = longTerms.getBuckets();

        //定义集合 搜索所有的品牌对象
        List<Brand> brands = new ArrayList<>();

        //解析所有的id 查询品牌
        buckets.forEach(bucket -> {
            Brand brand = this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue());
            brands.add(brand);
        });
        return brands;
    }

    /**
     * 解析分类
     * */
    private  List<Map<String,Object>> getCategoryAggResult(Aggregation aggregation){
        //处理聚合结果集
        LongTerms longTerms = (LongTerms)aggregation;

        //获取所有的分类Id
        List<LongTerms.Bucket> buckets = longTerms.getBuckets();

        //定义分类的集合  搜索所有的分类对象
        List<Map<String,Object>> categories = new ArrayList<>();
        List<Long> cids = new ArrayList<>();

        buckets.forEach(bucket -> {
            cids.add(bucket.getKeyAsNumber().longValue());
        });
        List<String> names = this.categoryClient.queryNamesById(cids);
        for(int i=0;i<cids.size();i++){
            Map<String,Object> map = new HashMap<>();
            map.put("id",cids.get(i));
            map.put("name",names.get(i));
            categories.add(map);
        }
        return categories;

    }


     //聚合  解析 规格参数
    private List<Map<String,Object>> getSpecAggResult(Long cid, QueryBuilder query){
        System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkk"+query);

        //定义 集合 存储聚合数据
        List<Map<String,Object>> spec = new ArrayList<>();

        //创建自定义构造器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //queryBuilder.withQuery(query);

        //查询参数
        List<SpecParam> specParams = this.specificationClient.querySpecParam(null,cid, true, null);

        specParams.forEach(specParam -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs."+specParam.getName()+".keyword"));

        });
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));
        //执行聚合查询
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>)this.goodsRepository.search(queryBuilder.build());
        System.out.println("查询的大小---"+goodsPage.getSize());

        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
        for(Map.Entry<String,Aggregation> entry:aggregationMap.entrySet()){
            Map<String,Object> map = new HashMap<>();
            //存入参数
            map.put("k",entry.getKey());
            //收集规格参数
            List<Object> options = new ArrayList<>();
            //解析每个聚和桶的数据
            StringTerms terms = (StringTerms)entry.getValue();
            //遍历聚合中的每一个桶
            List<StringTerms.Bucket> buckets = terms.getBuckets();
            buckets.forEach(bucket -> {
                System.out.println("数据是mmmmmmmmmmmmmm"+bucket);
                options.add(bucket.getKeyAsString());
            });
            map.put("options",options);

            spec.add(map);
        }
        System.out.println(spec.get(0).toString());
        return  spec;
    }

    private BoolQueryBuilder getboolQueryBuilder(SearchRequest request){
        /*BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 基本查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));

        if (CollectionUtils.isEmpty(request.getFilter())){
            return  boolQueryBuilder;
        }
        for (Map.Entry<String,String> entry : request.getFilter().entrySet()){
            String key = entry.getKey();

            if (StringUtils.equals("品牌",key)){
                key = "brandId";
            }else  if (StringUtils.equals("分类",key)){
                key = "cid3";
            }else {
                key = "specs." +key+".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key,entry.getValue()));
        }

        return boolQueryBuilder;*/

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // 基本查询条件
        queryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));
        // 过滤条件构建器
        BoolQueryBuilder filterQueryBuilder = QueryBuilders.boolQuery();
        // 整理过滤条件
        Map<String, String> filter = request.getFilter();
        for (Map.Entry<String, String> entry : filter.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            // 商品分类和品牌要特殊处理
            if (key != "cid3" && key != "brandId") {
                key = "specs." + key + ".keyword";
            }
            // 字符串类型，进行term查询
            filterQueryBuilder.must(QueryBuilders.termQuery(key, value));
        }
        // 添加过滤条件
        queryBuilder.filter(filterQueryBuilder);
        return queryBuilder;
    }

    public void createIndex(Long id) throws IOException {

        Spu spu = this.goodsClient.querySpuById(id);
        // 构建商品
        Goods goods = this.buildGoods(spu);

        // 保存数据到索引库
        this.goodsRepository.save(goods);
    }

    public void deleteIndex(Long id) {
        this.goodsRepository.deleteById(id);
    }
}
