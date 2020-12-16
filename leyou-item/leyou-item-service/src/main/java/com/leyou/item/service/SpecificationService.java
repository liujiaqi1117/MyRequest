package com.leyou.item.service;

import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.mapper.SpecificationMapper;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.pojo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpecificationService {

    @Autowired
    private SpecificationMapper specificationMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    public List<Specification> queryByCid(Long cid){
        Specification specification = new Specification();
        specification.setCid(cid);
        List<Specification> specificationList = specificationMapper.select(specification);
        return  specificationList;
    }

    public List<SpecParam> querySpecParams(Long gid, Long cid, Boolean searching, Boolean generic) {
        SpecParam param = new SpecParam();
        param.setGroupId(gid);
        param.setCid(cid);
        param.setSearching(searching);
        param.setGeneric(generic);
        return this.specParamMapper.select(param);
    }


    public void insertGroup(Specification group) {
        specificationMapper.insert(group);
    }

    public void updateGroup(Specification group) {
        specificationMapper.updateByPrimaryKey(group);
    }

    public void deleteGroupById(Long id) {
        specificationMapper.deleteByPrimaryKey(id);
    }

    @Transactional
    public void insertSkuParam(SpecParam specParam) {
        specParamMapper.insertSelective(specParam);
    }

    @Transactional
    public void updateSkuParam(SpecParam specParam) {
        specParamMapper.updateByPrimaryKeySelective(specParam);
        //specParamMapper.updateSkuParam(specParam);
    }

    @Transactional
    public void deleteSpecParam(Long id) {
       // specParamMapper.deleteByPrimaryKey(id);
        specParamMapper.deleteSpecParam(id);
    }

    public List<Specification> querySpecsByCid(Long cid) {
        // 查询规格组

        List<Specification> groups = this.queryByCid(cid);
        SpecParam param = new SpecParam();
        groups.forEach(g -> {
            // 查询组内参数
            g.setParams(this.querySpecParams(g.getId(), null, null, null));
        });
        return groups;
    }
}
