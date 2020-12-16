package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuBo;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.CategoryService;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping
public class GoodsController {

    @Autowired
    private GoodsService goodsService;


    @Autowired
    private CategoryService categoryService;


    //查询spu
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuByPage(
    @RequestParam(value = "page",defaultValue = "1")Integer page,
    @RequestParam(value = "rows",defaultValue = "5")Integer rows,
    @RequestParam(value = "key",required = false)String key,
    @RequestParam(value = "saleable",required = false)Boolean saleable
    ){
        PageResult<SpuBo> spuBoPageResult = goodsService.querySpuByPageAndSort(page, rows, saleable, key);

       /* if(spuBoPageResult == null && spuBoPageResult.getItems().size() ==0){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }*/
        return  ResponseEntity.ok(spuBoPageResult);
    }



    //查询detail
    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> querySpuDetailById(@PathVariable("id")Long id){

        System.out.println("---------------"+id);
        SpuDetail spuDetail =goodsService.querySpuDetailById(id);
        if(spuDetail == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuDetail);
    }


    //添加商品
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo) {
        try {
            this.goodsService.save(spuBo);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    //修改商品
    @PutMapping("goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spuBo) {
        System.out.println(spuBo.getId());
        try {
            this.goodsService.update(spuBo);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    //查询sku
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam(value = "id")Long id){

        System.out.println(id);
        List<Sku> spuList = goodsService.querySkuBySpuId(id);

        if(CollectionUtils.isEmpty(spuList)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuList);

    }


    @DeleteMapping("goods/deleteGoods/{id}")
    public ResponseEntity<Void> deleteSkuBySpuId(@PathVariable("id")Long id){
        try{
            goodsService.deleteSkuBySpuId(id);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();

        }

    }

    //下架
    @GetMapping("goods/soldOutGoods/{id}")
    public ResponseEntity<Void> soldOutGoodsById(@PathVariable("id")Long id){
        System.out.println("下架ID"+id);
        try{
            goodsService.soldOutGoodsById(id);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().build();

        }
    }

    //根据分类的集合查询分类的名称
    @GetMapping("/names")
    public ResponseEntity<List<String>> queryNamesById(@RequestParam("ids")List<Long> ids){
        List<String> nameList = categoryService.queryNameByIds(ids);
        if(nameList== null || nameList.size()<1){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(nameList);
    }

    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id){
        Spu spu = this.goodsService.querySpuById(id);
        if(spu == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(spu);
    }

}
