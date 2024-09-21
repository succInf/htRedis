package cn.net.rjnetwork.redis;

import cn.hutool.core.util.StrUtil;
import cn.net.rjnetwork.redis.entity.RedisInf;
import cn.net.rjnetwork.redis.parse.InfoParse;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.sentinel.api.StatefulRedisSentinelConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class HtSentinelClient {

    private static final ConcurrentHashMap<Long, RedisClient> htSentinelRedisClients = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Long, StatefulRedisSentinelConnection<String, String>> htSentinelConnections = new ConcurrentHashMap<>();

    public ConcurrentHashMap<Long, StatefulRedisSentinelConnection<String, String>> getHtSentinelConnections(){
        return htSentinelConnections;
    }

    public ConcurrentHashMap<Long, RedisClient> getSentinelHtRedisClients(){
        return htSentinelRedisClients;
    }


    @Autowired
    InfoParse infoParse ;

    /**
     * 关闭连接
     **/
    public boolean closeSentinel(Long id){
        //
        StatefulRedisSentinelConnection<String, String>  connection =  htSentinelConnections.get(id);
        connection.close();
        RedisClient redisClient =  htSentinelRedisClients.get(id);
        redisClient.close();
        htSentinelRedisClients.remove(id);
        htSentinelConnections.remove(id);
        return true;
    }

    /**
     * @author huzhenjie
     * @description 创建redis客户端 但是此时还没有连接。
     **/
    public boolean createRedisClient(RedisInf redisInf){
        RedisClient client = null;
        if(redisInf == null){
            throw new NullPointerException("redisInf is null");
        }

        if(htSentinelRedisClients.containsKey(redisInf.getId())){
            return true;
        }
        RedisURI redisURI = null;
        if(StrUtil.isBlankOrUndefined(redisInf.getPassword())){
            redisURI =  RedisURI.builder()
                    .withSentinel(redisInf.getHost(),redisInf.getPort())
                    .build();
        }else{
            redisURI =  RedisURI.builder()
                    .withSentinel(redisInf.getHost(),redisInf.getPort(),redisInf.getPassword())
                    .build();
        }
            client = RedisClient.create(redisURI);
        try{
            StatefulRedisSentinelConnection<String, String> connection = client.connectSentinel();
            htSentinelRedisClients.put(redisInf.getId(),client);
            htSentinelConnections.put(redisInf.getId(),connection);
        }catch (Exception e){
            throw new RuntimeException("create redis connection failed");
        }finally {
            log.info("create redis connection exec finished");
        }
        return true;
    }
}
