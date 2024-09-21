package cn.net.rjnetwork.redis.entity;

import lombok.Data;

@Data
public class ListInfo {

    private String key;

    private String value;

    private Long expire;

    private Long start;

    private Long end;
}
