package cn.net.rjnetwork.redis.business;

import cn.net.rjnetwork.redis.HtClusterClient;
import cn.net.rjnetwork.redis.entity.HashInfo;
import cn.net.rjnetwork.redis.entity.ListInfo;
import cn.net.rjnetwork.redis.entity.StrKeyInfo;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@DependsOn("htClusterClient")
public class ClusterRedisOperator {

    @Autowired
    HtClusterClient htClusterClient;

    ConcurrentHashMap<Long, StatefulRedisClusterConnection<String, String>> htClusterConnections = null;

    @PostConstruct
    public void init(){
        htClusterConnections = htClusterClient.getHtClusterConnections();
    }


    /**
     * 新增一个字符串的数据
     *
     */
    public String addStrValue(Long id,String key,String value){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        return  syncCommands.set(key,value);
    }

    /**
     * 新增一个字符串的数据 并设置过期时间
     *  times 单位是秒
     */
    public boolean addStrValue(Long id,String key,String value,Long times){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        String result = syncCommands.set(key,value);
        if(times!=-1){
            syncCommands.expire(key,times);
        }
        log.info("result:{}",result);//
        return true;
    }


    /**
     *  根据key 移除数据
     */
    public Long removeStrValue(Long id,String key){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        return syncCommands.del(key);
    }

    /**
     *  根据key 获取value
     */
    public String getStrValueByKey(Long id,String key){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        return syncCommands.get(key);
    }


    public Long getStrExpireByKey(Long id,String key){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        return  syncCommands.ttl(key);
    }


    /**
     *  根据key 获取value和有效时间
     */
    public StrKeyInfo getStrKeyInfByKey(Long id, String key){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        StrKeyInfo strKeyInfo = new StrKeyInfo();
        strKeyInfo.setKey(key);
        strKeyInfo.setValue(syncCommands.get(key));
        strKeyInfo.setExpire(syncCommands.ttl(key));
        return strKeyInfo;
    }

    /**************hash*******************/
    /**
     * 新增一个hash的数据
     *
     */
    public boolean addHashValue(Long id,String key,String item,String value){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        return  syncCommands.hset(key,item,value);
    }


    /**
     * 新增一个hash的数据 并设置hash表的过期时间
     *
     */
    public boolean addHashValue(Long id,String key,String item,String value,Long times){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        syncCommands.hset(key,item,value);
        if(times!=-1){
            syncCommands.expire(key,times);
        }
        return  true;
    }


    /**
     * 移除单个item的值
     *
     */
    public boolean removeHashItemValue(Long id,String key,String item){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        syncCommands.hdel(key,item);
        return  true;
    }


    /**
     * hash表移除
     *
     */
    public boolean removeHashItemValue(Long id,String key){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        syncCommands.hdel(key);
        return  true;
    }




    /**
     * 新增一个hash的数据 入参是map
     *
     */
    public String addHashMValue(Long id,  String key, Map<String,String> item){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        return  syncCommands.hmset(key,item);
    }

    /**
     * 新增一个hash的数据 入参是map
     *
     */
    public boolean addHashMValue(Long id, String key, Map<String,String> item,Long times){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        syncCommands.hmset(key,item);
        if(times!=-1){
            syncCommands.expire(key,times);
        }
        return  true;
    }


    /**
     * 获取hash表中的所有数据  这里需要考虑是否分页。因为单个key里面可能会有很多个item
     * cursor 默认为 0  size 默认为100
     *
     **/
    public HashInfo getHashInfo(Long id, Integer db, String key, String item){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        HashInfo hashInfo = new HashInfo();
        hashInfo.setKey(key);
        hashInfo.setItem(item);
        hashInfo.setValue(syncCommands.hget(key,item));
        hashInfo.setExpire(syncCommands.ttl(key));
        return hashInfo;
    }

    /*****************list*************************/
    public boolean addListItem(Long id,String key,String itemValue){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        syncCommands.lpush(key,itemValue);
        return  true;
    }


    public boolean addListItem(Long id, String key,String itemValue,Long times){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        syncCommands.lpush(key,itemValue);
        if(times!=-1){
            syncCommands.expire(key,times);
        }
        return  true;
    }


    public boolean addList(Long id, String key,String[] itemValue){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        syncCommands.lpush(key,itemValue);
        return  true;
    }

    public boolean addList(Long id, String key,String[] itemValue,Long times){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        syncCommands.lpush(key,itemValue);
        if(times!=-1){
            syncCommands.expire(key,times);
        }
        return  true;
    }

    public boolean addListByList(Long id,  String key, List<String> itemValue){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        syncCommands.lpush(key,itemValue.toArray(new String[itemValue.size()]));
        return  true;
    }

    public boolean addListByList(Long id,  String key,List<String> itemValue,Long times){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();

        syncCommands.lpush(key,itemValue.toArray(new String[itemValue.size()]));
        if(times!=-1){
            syncCommands.expire(key,times);
        }
        return  true;
    }

    //删除单个list的value
    public boolean removeListItem(Long id, String key,String itemValue){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        syncCommands.lrem(key,0,itemValue);
        return  true;
    }

    //删除list
    public boolean removeList(Long id,String key){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        syncCommands.ltrim(key,0,0);
        return  true;
    }


    /**
     * 获取list全部数据
     **/
    public List<ListInfo>  getList(Long id, String key){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        List<ListInfo> listInfoslist = new ArrayList<>();
        Long expire = syncCommands.ttl(key);
        List<String> list = syncCommands.lrange(key,0,-1);
        list.forEach((item)->{
            ListInfo info = new ListInfo();
            info.setKey(key);
            info.setValue(item);
            info.setExpire(expire);
            listInfoslist.add(info);
        });

        return listInfoslist;
    }


    /**
     * 获取list的起始位置和结束位置的数据。
     **/
    public List<ListInfo> getList(Long id, Integer db, String key, Long start, Long end){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        List<ListInfo> listInfoslist = new ArrayList<>();
        Long expire = syncCommands.ttl(key);
        List<String> list = syncCommands.lrange(key,start,end);
        list.forEach((item)->{
            ListInfo info = new ListInfo();
            info.setKey(key);
            info.setValue(item);
            info.setStart(start);
            info.setEnd(end);
            info.setExpire(expire);
            listInfoslist.add(info);
        });
        return listInfoslist;
    }
}
