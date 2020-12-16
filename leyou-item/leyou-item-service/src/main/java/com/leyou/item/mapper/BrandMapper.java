package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {

    @Insert("INSERT INTO tb_category_brand (category_id,brand_id) VALUES (#{cid},#{bid})")
    int insertCategoryAndBrand(@Param("cid")Long cid,@Param("bid")Long bid);

    @Update("UPDATE tb_category_brand SET category_id = #{cid} where brand_id = #{id}" )
    void updateCategoryBrand(@Param("cid") Long cid, @Param("id")Long id);

    @Delete("DELETE from tb_category_brand where brand_id = #{bid}")
    void deleteCategoryBrandByBid(@Param("bid") Long bid);

    @Select("SELECT * from tb_brand tb LEFT JOIN tb_category_brand tc ON tb.id = tc.brand_id WHERE tc.category_id = #{cid}")
    List<Brand> queryBrandByCid(Long cid);
}
