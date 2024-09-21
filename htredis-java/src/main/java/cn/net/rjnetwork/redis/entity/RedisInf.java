package cn.net.rjnetwork.redis.entity;

import lombok.Data;

@Data
public class RedisInf {

    private Long id;
    //连接名称
    private String name;
    //主机 如果是集群 格式为 192.168.31.1:6379,192.168.31.2:6379
    private String host;
    //端口  如果是
    private Integer port;

    private String userName;

    private String password;

    //连接类型
    private String connectionType;

    // 单机模式 ；集群模式 ；哨兵模式；
    private String type;




}
