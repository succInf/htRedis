package cn.net.rjnetwork.redis.entity;

import lombok.Data;

@Data
public class HashKeyInfo {

    private String key;

    private String value;

    private String field;

    //单位是秒
    private Long expire;

    private String type;

    private Long id;

    private Integer db;
}
