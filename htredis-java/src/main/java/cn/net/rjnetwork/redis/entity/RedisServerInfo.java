package cn.net.rjnetwork.redis.entity;

import lombok.Data;

@Data
public class RedisServerInfo {

    private String key;

    private String name;

    private String value;

    private String type;

    private Integer level;
}
