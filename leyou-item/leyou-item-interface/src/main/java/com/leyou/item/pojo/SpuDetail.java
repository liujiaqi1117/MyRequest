package com.leyou.item.pojo;

import javax.persistence.*;

@Table(name = "tb_spu_detail")
public class SpuDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long spuId;

    private String description;

    //@Column(name="generic_spec")
    private String genericSpec;

   // @Column(name = "special_spec")
    private String specialSpec;

    private String packingList;

    private String afterService;

    public Long getSpuId() {
        return spuId;
    }

    public void setSpuId(Long spuId) {
        this.spuId = spuId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenericSpec() {
        return genericSpec;
    }

    public void setGenericSpec(String genericSpec) {
        this.genericSpec = genericSpec;
    }

    public String getSpecialSpec() {
        return specialSpec;
    }

    public void setSpecialSpec(String specialSpec) {
        this.specialSpec = specialSpec;
    }

    public String getPackingList() {
        return packingList;
    }

    public void setPackingList(String packingList) {
        this.packingList = packingList;
    }

    public String getAfterService() {
        return afterService;
    }

    public void setAfterService(String afterService) {
        this.afterService = afterService;
    }
}
