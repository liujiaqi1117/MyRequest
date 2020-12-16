package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandOfPage
            //查询条件    //当前页      //每页显示条数  //排序方式   //根据列排序
            (String key,Integer page,Integer rows,Boolean desc,String sortBy)
    {
        //添加查询分页条件
        PageHelper.startPage(page,rows);
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        //根据姓名查询
        //先判断查询条件是否为空
        if(StringUtils.isNotBlank(key)){
            //不为空查询        name--指根据数据库的name列   对数据库的name列进行模糊查询  或者根据letter列查询
            criteria.andLike("name","%"+key+"%").orEqualTo("letter",key);
        }
        //添加排序条件
        if(StringUtils.isNotBlank(sortBy)){
            example.setOrderByClause(sortBy+" "+(desc?"desc":"asc"));

        }
        //查询
        List<Brand> brandList = brandMapper.selectByExample(example);
        //将结果封装为pageInfo
        PageInfo<Brand> pageInfo = new PageInfo<>(brandList);

        //将数据包装到结果集中
        PageResult<Brand> pageResult = new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
        return  pageResult;
    }


    @Transactional
    public void saveBrand(Brand brand,List<Long> cids){
        //调用mapper层增加Brand
        Boolean isTrue = brandMapper.insertSelective(brand)==1;
        //调用中间表的新增
        cids.forEach(cid->{
            brandMapper.insertCategoryAndBrand(cid,brand.getId());
        });

    }


    @Transactional
    public void updateBrand(Brand brand, List<Long> cids) {

        //修改品牌
        brandMapper.updateByPrimaryKeySelective(brand);
        cids.forEach(cid->{
            System.out.println("cid"+cid);
            System.out.println("bid"+brand.getId());
           // brandMapper.updateCategoryBrand(cid, brand.getId());
        });
        //维护中间表
        /*for (Long cid : cids) {
            brandMapper.updateCategoryBrand(cid, brand.getId());
        }*/
    }

    @Transactional
    public int deleteBrandById(Long bid) {
        //删除品牌表
        int i = brandMapper.deleteByPrimaryKey(bid);

        //维护中间表
        brandMapper.deleteCategoryBrandByBid(bid);
        return i;
    }


    public Brand queryNameByBid(Long brandId) {
        Brand brand = brandMapper.selectByPrimaryKey(brandId);
        return brand;
    }

    public List<Brand> queryBrandByCid(Long cid) {


        return brandMapper.queryBrandByCid(cid);
    }
}
