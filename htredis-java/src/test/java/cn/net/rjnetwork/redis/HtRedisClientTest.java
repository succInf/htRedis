//package cn.net.rjnetwork.redis;
//
//import cn.net.rjnetwork.consts.SysConst;
//import cn.net.rjnetwork.redis.entity.RedisInf;
//import cn.net.rjnetwork.redis.entity.RedisKeysPage;
//import cn.net.rjnetwork.redis.entity.RedisServerInfo;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//import java.util.Map;
//
//@SpringBootTest
//@Slf4j
//public class HtRedisClientTest {
//
//
//    private String host = "127.0.0.1";
//
//
//    @Autowired
//    HtRedisClient htRedisClient;
//    @Test
//    void getRedisBaseInf(){
//        RedisInf redisInf = new RedisInf();
//        redisInf.setId(1l);
//        redisInf.setHost(host);
//        redisInf.setPort(6379);
//        redisInf.setType(SysConst.REDIS_ALONE_TYPE);
//        htRedisClient.createRedisClient(redisInf);
//        List<RedisServerInfo> list = htRedisClient.getRedisBaseInf(1l);
//        log.info("list: {}", list);
//    }
//
//    @Test
//    void getRedisConfigInfo(){
//        RedisInf redisInf = new RedisInf();
//        redisInf.setId(1l);
//        redisInf.setHost(host);
//        redisInf.setPort(6379);
//        redisInf.setType(SysConst.REDIS_ALONE_TYPE);
//        htRedisClient.createRedisClient(redisInf);
//        Map<String, String> map = htRedisClient.getRedisConfigInfo(1l);
//        log.info("map: {}", map);
//    }
//    @Test
//    void getDbs(){
//        RedisInf redisInf = new RedisInf();
//        redisInf.setId(1l);
//        redisInf.setHost(host);
//        redisInf.setPort(6379);
//        redisInf.setType(SysConst.REDIS_ALONE_TYPE);
//        htRedisClient.createRedisClient(redisInf);
//        Map<String, String> map = htRedisClient.getDbs(1l);
//        log.info("map: {}", map);
//    }
//
//    @Test
//    void getDbSizes(){
//        RedisInf redisInf = new RedisInf();
//        redisInf.setId(1l);
//        redisInf.setHost(host);
//        redisInf.setPort(6379);
//        redisInf.setType(SysConst.REDIS_ALONE_TYPE);
//        htRedisClient.createRedisClient(redisInf);
//        Long count = htRedisClient.getDbSizes(1l);
//        log.info("count: {}", count);
//    }
//
//    @Test
//    void getAllDbSizes(){
//        RedisInf redisInf = new RedisInf();
//        redisInf.setId(1l);
//        redisInf.setHost(host);
//        redisInf.setPort(6379);
//        redisInf.setType(SysConst.REDIS_ALONE_TYPE);
//        htRedisClient.createRedisClient(redisInf);
//        Map<Integer, Long> map = htRedisClient.getAllDbSizes(1l);
//        log.info("map: {}", map);
//    }
//
//    @Test
//    void getKeysBydb(){
//        RedisInf redisInf = new RedisInf();
//        redisInf.setId(1l);
//        redisInf.setHost(host);
//        redisInf.setPort(6379);
//        redisInf.setType(SysConst.REDIS_ALONE_TYPE);
//        htRedisClient.createRedisClient(redisInf);
//        //RedisKeysPage redisKeysPage = htRedisClient.getKeysBydb(1l,0,"3",1);
//        //log.info("redisKeysPage:{}",redisKeysPage);
//
//    }
//    @Test
//    void getKeys(){
//        RedisInf redisInf = new RedisInf();
//        redisInf.setId(1l);
//        redisInf.setHost(host);
//        redisInf.setPort(6379);
//        redisInf.setType(SysConst.REDIS_ALONE_TYPE);
//        htRedisClient.createRedisClient(redisInf);
//        RedisKeysPage redisKeysPage = htRedisClient.getKeysBydb(1l,"0",100);
//        log.info("redisKeysPage:{}",redisKeysPage);
//
//    }
//
//
//    @Test
//    void getTypeByKey(){
//        RedisInf redisInf = new RedisInf();
//        redisInf.setId(1l);
//        redisInf.setHost(host);
//        redisInf.setPort(6379);
//        redisInf.setType(SysConst.REDIS_ALONE_TYPE);
//        htRedisClient.createRedisClient(redisInf);
//        String redisKeysPage = htRedisClient.getTypeByKey(1l,"backup2");
//        log.info("redisKeysPage:{}",redisKeysPage);
//
//    }
//}
