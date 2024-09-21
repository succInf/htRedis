package cn.net.rjnetwork.service;

import cn.net.rjnetwork.entity.HtredisHostInfo;
import cn.net.rjnetwork.redis.HtClusterClient;
import cn.net.rjnetwork.redis.business.ClusterRedisOperator;
import cn.net.rjnetwork.redis.entity.RedisInf;
import cn.net.rjnetwork.redis.entity.RedisKeysPage;
import cn.net.rjnetwork.redis.entity.RedisServerInfo;
import cn.net.rjnetwork.redis.entity.StrKeyInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ClusterRedisOperaService {

    @Autowired
    HtClusterClient htRedisClient;

    @Autowired
    ClusterRedisOperator redisOperator;

    /**
     * 打开redis连接 如果打开失败则会抛出异常
     **/
    public boolean openConnection(HtredisHostInfo htredisHostInfo) {
        if (htredisHostInfo == null) {
            throw new RuntimeException("htredisHostInfo is null");
        }

        RedisInf redisInf = new RedisInf();
        redisInf.setHost(htredisHostInfo.getHost());
        redisInf.setPort(htredisHostInfo.getPort());
        redisInf.setPassword(htredisHostInfo.getPassword());
        redisInf.setUserName(htredisHostInfo.getUserName());
        redisInf.setId(htredisHostInfo.getId());
        redisInf.setType(htredisHostInfo.getType());
        redisInf.setConnectionType(htredisHostInfo.getConnectionType());
        redisInf.setName(htredisHostInfo.getName());
        return htRedisClient.createRedisClient(redisInf);
    }

    /**
     * 获取redis的db总数 以及每个db的key的总数
     **/
    public Map<Integer,Long> getAllDbSizes(Long id){
        return htRedisClient.getAllDbSizes(id);
    }

    /**
     * 获取redis的基本信息
     **/
    public List<RedisServerInfo> getRedisBaseInf(Long id){
        return htRedisClient.getRedisBaseInf(id);
    }


    /**
     * 关闭连接
     **/
    public boolean closeConnection(Long id){
        return htRedisClient.closeCluster(id);
    }


    public RedisKeysPage getKeysByDb(Long id, String cursor, Integer size, String searchKey){
        return htRedisClient.getKeysBydb(id,cursor,size,searchKey);
    }



    public StrKeyInfo getStrValueByKey(Long id, String key, String type){
        StrKeyInfo strKeyInfo = new StrKeyInfo();
        strKeyInfo.setKey(key);
        strKeyInfo.setType(type);
        strKeyInfo.setValue(redisOperator.getStrValueByKey(id,key));
        strKeyInfo.setExpire(redisOperator.getStrExpireByKey(id,key));
        return strKeyInfo;
    }



    /**
     * 更新字符串
     *  times 单位是秒
     */
    public boolean addStrValue(Long id,String key,String value,Long times){
        return redisOperator.addStrValue(id,key,value,times);
    }


    /**
     * 更新字符串
     *  times 单位是秒
     */
    public boolean removeStrValue(Long id,String key){
        return redisOperator.removeStrValue(id,key)>0?true:false;
    }
}
