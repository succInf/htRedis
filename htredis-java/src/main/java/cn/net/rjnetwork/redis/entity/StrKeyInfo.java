package cn.net.rjnetwork.redis.entity;

import lombok.Data;

@Data
public class StrKeyInfo {

    private String key;

    private String value;

    //单位是秒
    private Long expire;

    private String type;

    private Long id;

    private Integer db;


}
