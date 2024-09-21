package cn.net.rjnetwork.redis;


import cn.hutool.core.util.StrUtil;
import cn.net.rjnetwork.redis.entity.RedisInf;
import cn.net.rjnetwork.redis.entity.RedisKeyInfo;
import cn.net.rjnetwork.redis.entity.RedisKeysPage;
import cn.net.rjnetwork.redis.entity.RedisServerInfo;
import cn.net.rjnetwork.redis.parse.InfoParse;
import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HtRedisClient {

    private static final ConcurrentHashMap<Long, RedisClient> htRedisClients = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Long, StatefulRedisConnection<String, String>> htConnections = new ConcurrentHashMap<>();




    @Autowired
    InfoParse infoParse ;


    public ConcurrentHashMap<Long, StatefulRedisConnection<String, String>> getHtConnections(){
        return htConnections;
    }

    public ConcurrentHashMap<Long, RedisClient> getHtRedisClients(){
        return htRedisClients;
    }





    /**
     * 关闭连接
     **/
    public boolean close(Long id){
        //
        StatefulRedisConnection<String, String>  connection =  htConnections.get(id);
        connection.close();
        RedisClient redisClient =  htRedisClients.get(id);
        redisClient.close();
        htRedisClients.remove(id);
        htConnections.remove(id);
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
        if(htRedisClients.containsKey(redisInf.getId())){
            return true;
        }
            if(redisInf.getPassword()==null){
                client = RedisClient.create(RedisURI.create(redisInf.getHost(),redisInf.getPort()));
            }else{
                client = RedisClient.create(RedisURI.builder()
                        .withHost(redisInf.getHost())
                        .withPort(redisInf.getPort())
                        .withPassword(redisInf.getPassword())
                        .build());
            }
            //创建连接。
            try {
                StatefulRedisConnection<String, String> connection =  client.connect();
                htRedisClients.put(redisInf.getId(),client);
                htConnections.put(redisInf.getId(),connection);
            }catch (Exception e){
                throw new RuntimeException("create redis connection failed");
            }finally {
                log.info("create redis connection exec finished");
            }
        return true;
    }


    /**
     * @version V1.0
     * Title: 获取redis服务器的基本信息
     * @author huzhenjie
     * @description 描述说明该方法的功能
     * @createTime 2024/8/31 23:46
     * @param
     * @return
     **/
    public List<RedisServerInfo> getRedisBaseInf(Long id){
       if(!htConnections.containsKey(id)){
           throw new RuntimeException("please create redis connection");
       }
       StatefulRedisConnection<String, String> connection = htConnections.get(id);
       //同步命令接口
       RedisCommands<String, String> syncCommands =  connection.sync();
       String info = syncCommands.info();
       return infoParse.parseInf(info);
    }

    /**
     * @version V1.0
     * Title: 获取redis服务器配置信息
     * @author huzhenjie
     * @description 描述说明该方法的功能
     * @createTime 2024/8/31 23:46
     * @param
     * @return
     **/
    public Map<String, String> getRedisConfigInfo(Long id){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        return syncCommands.configGet("*");
    }

    /**
     * @version V1.0
     * Title: 获取redis的数据库数量
     * @author huzhenjie
     * @description 描述说明该方法的功能
     * @createTime 2024/8/31 23:46
     * @param
     * @return
     **/
    public  Map<String, String> getDbs(Long id){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        return syncCommands.configGet("databases");
    }

    /**
     * @version V1.0
     * Title: 获取当前默认的key的总数
     * @author huzhenjie
     * @description 描述说明该方法的功能
     * @createTime 2024/8/31 23:46
     * @param
     * @return
     **/
    public Long getDbSizes(Long id){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        return syncCommands.dbsize();
    }




    /**
     * @version V1.0
     * Title: 获取所有数据库的key的总数
     * @author huzhenjie
     * @description 描述说明该方法的功能
     * @createTime 2024/8/31 23:46
     * @param
     * @return
     **/
    public Map<Integer,Long> getAllDbSizes(Long id){
        Map<Integer,Long> dbsMap = new LinkedHashMap<>();
        List<RedisServerInfo> list = getRedisBaseInf(id);
        Map<String, String> map = getDbs(id);
        if(map==null){
            throw new RuntimeException("please create redis connection");
        }
        String dbsStr =  map.get("databases");
        Integer dbs = Integer.parseInt(dbsStr);
        for(int i=0;i<dbs;i++){
            int finalI = i;
           Optional<RedisServerInfo>  oris= list.stream().filter((item)->{
                return item.getKey().equals("db"+ finalI);
            }).findAny();

           if(oris.isPresent()){
               RedisServerInfo rsi = oris.get();
               String val = rsi.getValue();
               System.out.println();
               dbsMap.put(i, Long.parseLong(val.split(",")[0].split("=")[1]));
           }else{
               dbsMap.put(i,0L);
           }

        }

        return dbsMap;
    }


    /**
     * @version V1.0
     * Title: 根据db获取所有的key
     * @author huzhenjie
     * @description 描述说明该方法的功能
     * @createTime 2024/8/31 23:46
     * @param
     * @return
     **/
    public RedisKeysPage getKeysBydb(Long id, Integer db, String cursor, Integer size,String searchKey){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        RedisKeysPage redisKeysPage = new RedisKeysPage();
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        //选择对应的数据库
        if(db!=null){
            syncCommands.select(db);
        }
        ScanCursor scanCursor = ScanCursor.of(cursor);
        ScanArgs scanArgs = ScanArgs.Builder.limit(size);
        if(!StrUtil.isBlankOrUndefined(searchKey)){
            scanArgs.match(searchKey);
        }
        KeyScanCursor<String> ksc =  syncCommands.scan(scanCursor, scanArgs);
        List<String> keys= ksc.getKeys();
        //对key进行遍历。
        List<RedisKeyInfo> redisKeyInfos = new ArrayList<>();

        keys.stream().peek((k)->{
            RedisKeyInfo redisKeyInfo = new RedisKeyInfo();
            redisKeyInfo.setKey(k);
            String type = getTypeByKey(id,k);
            redisKeyInfo.setType(type);
            redisKeyInfos.add(redisKeyInfo);
        }).collect(Collectors.toList());
        redisKeysPage.setRedisKeyInfos(redisKeyInfos);
        redisKeysPage.setKeys(keys);
        redisKeysPage.setCursor(ksc.getCursor());
        redisKeysPage.setFinished(ksc.isFinished()?1:0);
        redisKeysPage.setCursorLong(Long.parseLong(redisKeysPage.getCursor()));
        redisKeysPage.setSize(size);
        //计算页数
        return redisKeysPage;
    }


    /**
     * @version V1.0
     * Title: 获取所有的key
     * @author huzhenjie
     * @description 描述说明该方法的功能
     * @createTime 2024/8/31 23:46
     * @param
     * @return
     **/
    public RedisKeysPage getKeysBydb(Long id, String cursor, Integer size){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        RedisKeysPage redisKeysPage = new RedisKeysPage();
        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        //选择对应的数据库
        ScanCursor scanCursor = ScanCursor.of(cursor);
        ScanArgs scanArgs = ScanArgs.Builder.limit(size);
        KeyScanCursor<String> ksc =  syncCommands.scan(scanCursor, scanArgs);
        List<String> keys= ksc.getKeys();
        redisKeysPage.setKeys(keys);
        redisKeysPage.setCursor(ksc.getCursor());
        redisKeysPage.setFinished(ksc.isFinished()?1:0);
        redisKeysPage.setCursorLong(Long.parseLong(redisKeysPage.getCursor()));
        redisKeysPage.setSize(size);
        //计算页数
        return redisKeysPage;
    }



    /**
     * @version V1.0
     * Title: 获取key的类型
     * @author huzhenjie
     * @description 描述说明该方法的功能
     * @createTime 2024/8/31 23:46
     * @param
     * @return
     **/
    public String getTypeByKey(Long id, String key){
        if(!htConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }

        StatefulRedisConnection<String, String> connection = htConnections.get(id);
        RedisCommands<String, String> syncCommands =  connection.sync();
        //选择对应的数据库
        return syncCommands.type(key);
        //计算页数
    }




}
