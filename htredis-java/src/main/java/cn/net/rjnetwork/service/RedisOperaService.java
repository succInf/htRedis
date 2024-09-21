package cn.net.rjnetwork.service;

import cn.net.rjnetwork.entity.HtredisHostInfo;
import cn.net.rjnetwork.redis.HtRedisClient;
import cn.net.rjnetwork.redis.business.RedisOperator;
import cn.net.rjnetwork.redis.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RedisOperaService {


    @Autowired
    HtRedisClient htRedisClient;


    @Autowired
    RedisOperator redisOperator;

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
    public  Map<Integer,Long> getAllDbSizes(Long id){
        return htRedisClient.getAllDbSizes(id);
    }

    /**
     * 获取redis的基本信息
     **/
    public List<RedisServerInfo>  getRedisBaseInf(Long id){
        return htRedisClient.getRedisBaseInf(id);
    }


    /**
     * 关闭连接
     **/
    public boolean closeConnection(Long id){
        return htRedisClient.close(id);
    }


    public RedisKeysPage getKeysByDb(Long id, Integer db, String cursor, Integer size,String searchKey){
     return htRedisClient.getKeysBydb(id,db,cursor,size,searchKey);
    }



    public StrKeyInfo getStrValueByKey(Long id, Integer db, String key,String type){
        StrKeyInfo strKeyInfo = new StrKeyInfo();
        strKeyInfo.setKey(key);
        strKeyInfo.setType(type);
        strKeyInfo.setValue(redisOperator.getStrValueByKey(id,db,key));
        strKeyInfo.setExpire(redisOperator.getStrExpireByKey(id,db,key));
        return strKeyInfo;
    }



    /**
     * 更新字符串
     *  times 单位是秒
     */
    public boolean addStrValue(Long id,Integer db,String key,String value,Long times){
      return redisOperator.addStrValue(id,db,key,value,times);
    }


    /**
     * 更新字符串
     *  times 单位是秒
     */
    public boolean removeStrValue(Long id,Integer db,String key){
        return redisOperator.removeStrValue(id,db,key)>0?true:false;
    }
    /**
     * 向hash表中新增一个field的value
     *  times 单位是秒
     */
    public boolean addHashValue(Long id,Integer db,String key,String field,String value,Long times){
        return redisOperator.addHashValue(id,db,key,field,value,times);
    }

    /**
     * 向hash表中移除一个field
     *  times 单位是秒
     */
    public boolean removeHashField(Long id,Integer db,String key,String field){
        return redisOperator.removeHashItemValue(id,db,key,field);
    }

    /**
     * 移除整个hash表
     *  times 单位是秒
     */
    public boolean removeHash(Long id,Integer db,String key){
        return redisOperator.removeHashItemValue(id,db,key);
    }
    /**
     * 获取hash表的items
     */
    public HashInfo getHashItems(Long id, Integer db, String key){
        return redisOperator.getHashAllItem(id,db,key);
    }

    /**
     * 获取hash表的items
     */
    public HashInfo getHashItem(Long id, Integer db, String key,String item){
        return redisOperator.getHashItemInfo(id,db,key,item);
    }

}
