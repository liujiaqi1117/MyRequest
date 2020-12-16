package com.leyou.item.service;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    public List<Category> queryCategories(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        return categoryMapper.select(category);
    }




    public List<Category> queryBrandByBid(Long bid){
        return categoryMapper.queryBrandByBid(bid);

    }


    public int deleteCategoryByCid(Long cid) {
        int i = categoryMapper.deleteByPrimaryKey(cid);
        return  i;
    }

    public List<String> queryNameByIds(List<Long> ids){
        List<String> names = new ArrayList<>();
        ids.forEach(cid->{
            Category category = this.categoryMapper.selectByPrimaryKey(cid);
            names.add(category.getName());
        });
       /* Category category_one = categoryMapper.selectByPrimaryKey(cid1);
        Category category_two = categoryMapper.selectByPrimaryKey(cid2);
        Category category_three = categoryMapper.selectByPrimaryKey(cid3);*/
        return  names;

    }


    @Transactional
    public void saveChild(Category category) {
        categoryMapper.insert(category);
    }

    @Transactional
    public void updateChild(Category category) {
        categoryMapper.updateByPrimaryKey(category);
    }

    public List<Category> queryAllByCid3(Long id) {
        Category c3 = this.categoryMapper.selectByPrimaryKey(id);
        Category c2 = this.categoryMapper.selectByPrimaryKey(c3.getParentId());
        Category c1 = this.categoryMapper.selectByPrimaryKey(c2.getParentId());
        return Arrays.asList(c1,c2,c3);

    }
}
