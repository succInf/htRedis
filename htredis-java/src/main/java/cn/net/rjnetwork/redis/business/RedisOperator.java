package cn.net.rjnetwork.redis.business;

import cn.net.rjnetwork.redis.HtRedisClient;
import cn.net.rjnetwork.redis.entity.HashInfo;
import cn.net.rjnetwork.redis.entity.HashInfoPage;
import cn.net.rjnetwork.redis.entity.ListInfo;
import cn.net.rjnetwork.redis.entity.StrKeyInfo;
import io.lettuce.core.MapScanCursor;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@DependsOn("htRedisClient")
public class RedisOperator {

    @Autowired
    HtRedisClient htRedisClient;

    ConcurrentHashMap<Long, StatefulRedisConnection<String, String>> htConnections = null;
    @PostConstruct
    public void init(){
        htConnections = htRedisClient.getHtConnections();
    }


    /**
     * 新增一个字符串的数据
     *
     */
    public String addStrValue(Long id,Integer db,String key,String value){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
        return  syncCommands.set(key,value);
    }

    /**
     * 新增一个字符串的数据 并设置过期时间
     *  times 单位是秒
     */
    public boolean addStrValue(Long id,Integer db,String key,String value,Long times){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
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
    public Long removeStrValue(Long id,Integer db,String key){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
        return syncCommands.del(key);
    }

    /**
     *  根据key 获取value
     */
    public String getStrValueByKey(Long id,Integer db,String key){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
        return syncCommands.get(key);
    }


    public Long getStrExpireByKey(Long id,Integer db,String key){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
        return  syncCommands.ttl(key);
    }


    /**
     *  根据key 获取value和有效时间
     */
    public StrKeyInfo getStrKeyInfByKey(Long id, Integer db, String key){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
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
    public boolean addHashValue(Long id,Integer db,String key,String item,String value){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
        return  syncCommands.hset(key,item,value);
    }


    /**
     * 新增一个hash的数据 并设置hash表的过期时间
     *
     */
    public boolean addHashValue(Long id,Integer db,String key,String item,String value,Long times){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
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
    public boolean removeHashItemValue(Long id,Integer db,String key,String item){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
        syncCommands.hdel(key,item);
        return  true;
    }


    /**
     * hash表移除
     *
     */
    public boolean removeHashItemValue(Long id,Integer db,String key){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
        Map<String,String> map = syncCommands.hgetall(key);
        if(!map.isEmpty()){
            map.forEach((k,v)->{
                syncCommands.hdel(key,k);
            });
        }
        return  true;
    }




    /**
     * 新增一个hash的数据 入参是map
     *
     */
    public String addHashMValue(Long id, Integer db, String key, Map<String,String> item){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
        return  syncCommands.hmset(key,item);
    }

    /**
     * 新增一个hash的数据 入参是map
     *
     */
    public boolean addHashMValue(Long id, Integer db, String key, Map<String,String> item,Long times){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
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
    public HashInfo getHashItemInfo(Long id, Integer db, String key, String item){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
        HashInfo hashInfo = new HashInfo();
        hashInfo.setKey(key);
        hashInfo.setItem(item);
        hashInfo.setValue(syncCommands.hget(key,item));
        hashInfo.setExpire(syncCommands.ttl(key));
        return hashInfo;
    }

    /**
     * 获取hash表中的所有数据  getHashAllItem
     * cursor 默认为 0  size 默认为100
     *
     **/
    public HashInfo getHashAllItem(Long id, Integer db, String key){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
        HashInfo hashInfo = new HashInfo();
        hashInfo.setKey(key);
        List<Map<String,String>> items = new ArrayList<>();
        Map<String,String> mp =  syncCommands.hgetall(key);
        mp.forEach((k,v)->{
            HashMap<String, String> map = new HashMap<>();
            map.put(k,v);
            items.add(map);
        });

        hashInfo.setItems(items);
        hashInfo.setExpire(syncCommands.ttl(key));
        return hashInfo;
    }



   /*****************list*************************/
  public boolean addListItem(Long id, Integer db, String key,String itemValue){
      if(!htConnections.containsKey(id)){
          throw new RuntimeException("please create redis connection");
      }
      StatefulRedisConnection<String, String> connection = htConnections.get(id);
      RedisCommands<String, String> syncCommands =  connection.sync();
      syncCommands.select(db);
      syncCommands.lpush(key,itemValue);
      return  true;
  }


    public boolean addListItem(Long id, Integer db, String key,String itemValue,Long times){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
        syncCommands.lpush(key,itemValue);
        if(times!=-1){
            syncCommands.expire(key,times);
        }
        return  true;
    }


    public boolean addList(Long id, Integer db, String key,String[] itemValue){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
        syncCommands.lpush(key,itemValue);
        return  true;
    }

    public boolean addList(Long id, Integer db, String key,String[] itemValue,Long times){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
        syncCommands.lpush(key,itemValue);
        if(times!=-1){
            syncCommands.expire(key,times);
        }
        return  true;
    }

    public boolean addListByList(Long id, Integer db, String key,List<String> itemValue){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
        syncCommands.lpush(key,itemValue.toArray(new String[itemValue.size()]));
        return  true;
    }

    public boolean addListByList(Long id, Integer db, String key,List<String> itemValue,Long times){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
        syncCommands.lpush(key,itemValue.toArray(new String[itemValue.size()]));
        if(times!=-1){
            syncCommands.expire(key,times);
        }
        return  true;
    }

    //删除单个list的value
    public boolean removeListItem(Long id, Integer db, String key,String itemValue){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
        syncCommands.lrem(key,0,itemValue);
        return  true;
    }

    //删除list
    public boolean removeList(Long id, Integer db, String key){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);

        syncCommands.ltrim(key,0,0);
        return  true;
    }


    /**
     * 获取list全部数据
     **/
    public List<ListInfo>  getList(Long id, Integer db, String key){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
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
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        syncCommands.select(db);
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
