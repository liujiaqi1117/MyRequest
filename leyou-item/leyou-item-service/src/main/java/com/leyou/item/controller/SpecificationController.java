package com.leyou.item.controller;

import com.leyou.item.pojo.SpecParam;
import com.leyou.item.pojo.Specification;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.SpecificationService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;


    //添加
    @PostMapping("/param")
    public ResponseEntity<Void> insertSkuParam(@RequestBody SpecParam specParam){
        try{
            specificationService.insertSkuParam(specParam);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


    }

    //修改
    @PutMapping("/param")
    public ResponseEntity<Void> updateSkuParam(@RequestBody SpecParam specParam){
        System.out.println(specParam.toString());
        System.out.println(specParam.getId());
        System.out.println(specParam.toString());
        specificationService.updateSkuParam(specParam);
        return ResponseEntity.ok().build();
       /* try{
            specificationService.updateSkuParam(specParam);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
*/

    }

    //删除
    @DeleteMapping("param/{id}")
    public ResponseEntity<Void> deleteSpecParam(@PathVariable("id")Long id){
        specificationService.deleteSpecParam(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("groups/{cid}")
    public ResponseEntity<List<Specification>> querySpecificationByCid(@PathVariable("cid")Long cid){
        List<Specification> specifications = specificationService.queryByCid(cid);
        if(specifications==null || CollectionUtils.isEmpty(specifications)){
            return  ResponseEntity.notFound().build();
        }
        return  ResponseEntity.ok(specifications);

    }

    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> querySpecParam(
            @RequestParam(value="gid", required = false) Long gid,
            @RequestParam(value="cid", required = false) Long cid,
            @RequestParam(value="searching", required = false) Boolean searching,
            @RequestParam(value="generic", required = false) Boolean generic
    ){
        List<SpecParam> list =
                this.specificationService.querySpecParams(gid,cid,searching,generic);
        if(list == null || list.size() == 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }



    @PostMapping("/group")
    public ResponseEntity<Void> insertGroup(@RequestBody Specification group){
        System.out.println(group.toString());
        System.out.println(group.getCid());
        System.out.println(group.getName());
        try {
            specificationService.insertGroup(group);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/group")
    public ResponseEntity<Void> updateGroup(@RequestBody Specification group){
        System.out.println(group.toString());
        System.out.println(group.getCid());
        System.out.println(group.getName());
        try {
            specificationService.updateGroup(group);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("group/{id}")
    public ResponseEntity<Void> deleteGroupById(@PathVariable("id") Long id){

        try {
            specificationService.deleteGroupById(id);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            //打印异常信息在程序中出错的位置及原因
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{cid}")
    public ResponseEntity<List<Specification>> querySpecsByCid(@PathVariable("cid") Long cid){
        List<Specification> list = this.specificationService.querySpecsByCid(cid);
        if(list == null || list.size() == 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }


}
