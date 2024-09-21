package cn.net.rjnetwork.redis.entity;

import lombok.Data;

import java.util.List;

@Data
public class HashInfoPage {

    private List<HashInfo> hashInfos;

    private String cursor;

    private Integer size;

    private String key;

    private Integer finished;//1 已完成 0未完成

    private Long expire;


}
