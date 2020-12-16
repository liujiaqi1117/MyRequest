package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    //查询
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategories(@RequestParam("pid") Long pid){
        if(pid==null || pid.longValue()<0){
            //错误的请求
            return ResponseEntity.badRequest().build();
        }
        List<Category> categoryList  = categoryService.queryCategories(pid);
        if(categoryList==null && categoryList.size()<0){
            //没有找到
            return ResponseEntity.notFound().build();

        }
        return ResponseEntity.ok(categoryList);

    }

    //修改
    @PutMapping
    public ResponseEntity<Void> updateChild(//@RequestParam("name")String name,
             /*@RequestParam("id")Long id,
             @RequestParam("parentId")Long parentId,
             @RequestParam("isParent")Boolean isParent,
             @RequestParam("sort")Integer sort*/
             @RequestBody Category category
    )
    {
        /*Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setSort(sort);
        category.setParentId(parentId);
        category.setIsParent(isParent);*/
        try{
            categoryService.updateChild(category);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   //添加
   @PostMapping
   public ResponseEntity<Void> saveChild(Category category){
       try{
           categoryService.saveChild(category);
           return ResponseEntity.ok().build();
       }catch (Exception e){
           e.printStackTrace();
           return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
       }
   }






    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryBrandByBid(@PathVariable("bid")Long bid){
        List<Category> categoryList = categoryService.queryBrandByBid(bid);
        if(categoryList ==null || categoryList.size()<1){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(categoryList);
    }

    @DeleteMapping("cid/{cid}")
    public ResponseEntity<Integer> deleteCategoryByCid(@PathVariable("cid")Long cid){
        int i = categoryService.deleteCategoryByCid(cid);
        return ResponseEntity.ok(i);
    }



    /**
     * 根据3级分类id，查询1~3级的分类
     * @param id
     * @return
     */
    @GetMapping("all/level")
    public ResponseEntity<List<Category>> queryAllByCid3(@RequestParam("id") Long id){
        List<Category> list = this.categoryService.queryAllByCid3(id);
        if (list == null || list.size() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }


}
