package cn.net.rjnetwork.redis.parse;

import cn.hutool.core.util.StrUtil;
import cn.net.rjnetwork.redis.entity.RedisServerInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @version V1.0
 * 对redis服务器信息作解析
 **/
@Component
public class InfoParse {

    private static HashMap<String,String> keys = new HashMap<>(){{
       put("redis_version","Redis服务器版本");
       put("os","操作系统版本");
       put("arch_bits","操作系统位数");
       put("tcp_port","运行端口");
       put("executable","部署路径");
       put("config_file","配置文件路径");
       put("connected_clients","客户端连接数");
       put("cluster_connections","集群连接数");
       put("maxclients","最大客户端数");
       put("used_memory","已使用内存");
       put("total_system_memory","总系统内存");
       put("total_system_memory_human","可读格式总系统内存");
       put("redis_mode","部署模式");
       put("process_id","进程号");


    }};


    public List<RedisServerInfo> parseInf(String info){
        String[] lines = info.split("\r\n");
        String type = null;
        List<RedisServerInfo> list = new ArrayList<>();
        for(String str:lines){
            if(StrUtil.isBlankOrUndefined(str)){
                continue;
            }
            if(str.startsWith("#")){
                type = str.replace("#","").trim();
            }else{
                RedisServerInfo rsi = new RedisServerInfo();
                String[] split = str.split(":");
                if(split.length == 2){
                    rsi.setKey(split[0].trim());
                    rsi.setValue(split[1].trim());
                }else{
                    rsi.setKey(split[0].trim());
                }
                rsi.setType(type);
                rsi.setName(keys.get(rsi.getKey()));
                if(!StrUtil.isBlankOrUndefined(rsi.getName())){
                    rsi.setLevel(1);
                }else{
                    rsi.setLevel(0);
                }
                list.add(rsi);

            }
        }
     return list;
    }
}
