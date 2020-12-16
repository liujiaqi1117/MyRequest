package com.leyou.item.mapper;

import com.leyou.item.pojo.SpecParam;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

public interface SpecParamMapper extends Mapper<SpecParam> {
    @Delete("delete from tb_spec_param where id = #{id}")
    void deleteSpecParam(Long id);

    //@Update("update tb_spec_param set cid=#{cid},group_id=#{specParam.groupId},name=#{name},`numeric`=#{numeric},unit=#{unit},searching=#{searching},generic=#{generic},segments=#{segments} where id=#{id}")
   // void updateSkuParam(SpecParam specParam);
}
