package cn.net.rjnetwork.redis.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class HashInfo {

    private String item;

    private String key;

    private String value;

    private String hash;

    private Long expire;

    private List<Map<String,String>> items;
}
