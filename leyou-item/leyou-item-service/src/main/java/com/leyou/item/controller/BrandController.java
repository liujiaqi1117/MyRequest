package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.BrandService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandOfpage
    (@RequestParam(value="key",required = false)String key,
     @RequestParam(value="page",defaultValue = "1")Integer page,
     @RequestParam(value="rows",defaultValue = "5")Integer rows,
     @RequestParam(value="sortBy",required = false)String sortBy,
     @RequestParam(value = "desc",required = false)Boolean desc
     )
    {
        PageResult<Brand> result = brandService.queryBrandOfPage(key, page, rows, desc, sortBy);
        if(CollectionUtils.isEmpty(result.getItems())){

            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);


    }


    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids){
         this.brandService.saveBrand(brand,cids);
         return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateBrand(Brand brand, @RequestParam("cids") List<Long> cids){
        System.out.println("----------------%%%%%"+brand.getId());
        this.brandService.updateBrand(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //删除
    @DeleteMapping("bid/{bid}")
    public ResponseEntity<Integer> deleteBrandById(@PathVariable("bid")Long bid){
        int  i = brandService.deleteBrandById(bid);
        return ResponseEntity.ok(i);
    }

    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid") Long cid){
        List<Brand> brandList = brandService.queryBrandByCid(cid);
        if(CollectionUtils.isEmpty(brandList)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return  ResponseEntity.ok(brandList);

    }


    //根据Id查询
    @GetMapping("/{bid}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("bid")Long bid){
        Brand brand = brandService.queryNameByBid(bid);
        if(brand == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(brand);

    }

}
