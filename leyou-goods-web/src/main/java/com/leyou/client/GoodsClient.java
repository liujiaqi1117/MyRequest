package com.leyou.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "item-service")
//@RequestMapping("/goods")
public interface GoodsClient extends GoodsApi {

   /* //分页查询商品
    @GetMapping("spu/page")
    ResponseEntity<List<SpuBo>> querySpuByPage(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable",required = false)Boolean saleable
    );

    //根据spuId查询spu_detail
    @GetMapping("/spu/detail/{id}")
    ResponseEntity<SpuDetail> querySpuDetail(@PathVariable("id")Long id);

    //根据spuId查询sku数据
    @GetMapping("sku/list")
    ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam(value = "id")Long id);*/
}
