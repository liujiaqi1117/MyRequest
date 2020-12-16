package com.leyou.item.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import com.netflix.discovery.converters.Auto;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Insert;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;
    //查询
    public PageResult<SpuBo> querySpuByPageAndSort(Integer page,Integer rows,Boolean saleable,String key)
    {
        //设置分页条件
        PageHelper.startPage(page,Math.min(rows,100));
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //判断是否过滤上架
        if(saleable!= null){
            criteria.orEqualTo("saleable",saleable);
        }
        example.setOrderByClause("id");
        //判断是否模糊查询
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("title","%"+key+"%");

        }
        Page<Spu> pageInfo= (Page<Spu>)spuMapper.selectByExample(example);
        List<SpuBo> spuBoList = pageInfo.getResult().stream().map(spu -> {
            //属性拷贝
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);
            //查询分类
            //List<String> nameList = categoryService.queryNameByIds(spu.getCid1(), spu.getCid2(), spu.getCid3());
            List<String> nameList = categoryService.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(nameList, "/"));
            //查询分类
            Brand brand = brandService.queryNameByBid(spu.getBrandId());
            spuBo.setBname(brand.getName());
            return spuBo;

        }).collect(Collectors.toList());
        return new PageResult<SpuBo>(pageInfo.getTotal(),spuBoList);


    }


    //添加
    @Transactional
    public void save(SpuBo spu) {
        // 保存spu
        spu.setSaleable(true);
        spu.setIsValid(true);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        this.spuMapper.insert(spu);
        // 保存spu详情
        spu.getSpuDetail().setSpuId(spu.getId());
        this.spuDetailMapper.insert(spu.getSpuDetail());

        // 保存sku和库存信息
        saveSkuAndStock(spu.getSkus(), spu.getId());

        sendMessage(spu.getId(),"insert");
    }

    //保存sku和库存数据
    private void saveSkuAndStock(List<Sku> skus, Long spuId) {
        for (Sku sku : skus) {
            if (!sku.getEnable()) {
                continue;
            }
            // 保存sku
            sku.setSpuId(spuId);
            // 初始化时间
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insert(sku);

            // 保存库存信息
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insert(stock);
        }
    }

    //删除spudetail
    public SpuDetail querySpuDetailById(Long id) {
        return  spuDetailMapper.selectByPrimaryKey(id);
    }



    //修改
    public void update(SpuBo spu) {
        // 查询以前sku
        List<Sku> skus = this.querySkuBySpuId(spu.getId());
        // 如果以前存在，则删除
        if(!CollectionUtils.isEmpty(skus)) {
            //使用java8 api方法list.stream().map().collect(Collectors.toList())
            //userList User实体类对象集合
            //User 实体类
            //getId 实体类属性的get方法
            List<Long> ids = skus.stream().map(s -> s.getId()).collect(Collectors.toList());
            // 删除以前库存
            Example example = new Example(Stock.class);
            example.createCriteria().andIn("skuId", ids);
            this.stockMapper.deleteByExample(example);

            // 删除以前的sku
            Sku record = new Sku();
            record.setSpuId(spu.getId());
            this.skuMapper.delete(record);

        }
        // 新增sku和库存
        saveSkuAndStock(spu.getSkus(), spu.getId());

        // 更新spu
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);
        spu.setValid(null);
        spu.setSaleable(null);
        this.spuMapper.updateByPrimaryKeySelective(spu);

        // 更新spu详情
        this.spuDetailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());

        this.sendMessage(spu.getId(),"update");
    }

    //删除
    @Transactional
    public void deleteSkuBySpuId(Long id) {
        //要删除的数据  spu  spudetail  sku  stock

        //删除spu
        spuMapper.deleteByPrimaryKey(id);


        Example example = new Example(SpuDetail.class);
        example.createCriteria().orEqualTo("spuId",id);
        spuDetailMapper.deleteByExample(example);

        //删除sku
        Example example1 = new Example(Sku.class);
        example1.createCriteria().orEqualTo("spuId",id);
        List<Sku> skuList = skuMapper.selectByExample(example1);
        for (Sku sku:skuList
             ) {

            //删除sku
            skuMapper.deleteByPrimaryKey(sku.getId());

            //删除stock
            Example example2 = new Example(Stock.class);
            example2.createCriteria().orEqualTo("skuId",sku.getId());
            stockMapper.deleteByExample(example2);
        }


    }

    //上下架
    @Transactional
    public void soldOutGoodsById(Long id) {
        //根据id查询商品
        Spu oldSpu = spuMapper.selectByPrimaryKey(id);

        //根据spu查询sku
        Example example = new Example(Sku.class);
        example.createCriteria().orEqualTo("spuId",id);
        List<Sku> skuList = skuMapper.selectByExample(example);

        System.out.println("---------------"+oldSpu.getSaleable());
        //判断是否下架
        if(oldSpu.getSaleable()){
            //下架spu
            oldSpu.setSaleable(false);
            spuMapper.updateByPrimaryKeySelective(oldSpu);

            //下架sku
            for (Sku sku:skuList
                 ) {
                sku.setEnable(false);
                skuMapper.updateByPrimaryKeySelective(sku);

            }
        }else{
            //上架
            oldSpu.setSaleable(true);
            this.spuMapper.updateByPrimaryKeySelective(oldSpu);
            //上架sku中的具体商品
            for (Sku sku : skuList){
                sku.setEnable(true);
                this.skuMapper.updateByPrimaryKeySelective(sku);
            }
        }
    }


    //根据spuId查询sku数据
    public List<Sku> querySkuBySpuId(Long id) {
        //根据spuId查询sku
        Sku sku = new Sku();
        sku.setSpuId(id);
        List<Sku> skus = this.skuMapper.select(sku);
        for (Sku sk : skus) {
            // 同时查询出库存
            Integer stock = this.stockMapper.selectByPrimaryKey(sk.getId()).getStock();
            System.out.println("库存---"+stock);
            sku.setStock(this.stockMapper.selectByPrimaryKey(sk.getId()).getStock());
        }
        return skus;


    }

    public Spu querySpuById(Long id) {
        return this.spuMapper.selectByPrimaryKey(id);
    }

   private void sendMessage(Long id, String type){
        // 发送消息
        try {
            this.amqpTemplate.convertAndSend("item." + type, id);
        } catch (Exception e) {
            System.out.println("{}商品消息发送异常，商品id："+id);
            e.printStackTrace();
            }
    }
}
