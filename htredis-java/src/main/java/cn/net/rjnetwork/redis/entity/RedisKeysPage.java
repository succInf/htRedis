package cn.net.rjnetwork.redis.entity;

import lombok.Data;

import java.util.List;

@Data
public class RedisKeysPage {

    private List<String> keys;

    private String cursor;

    private Long cursorLong;

     private Integer size;

     // 0 未扫描完成 1扫描完成
     private Integer finished;

     List<RedisKeyInfo> redisKeyInfos;
}
