package cn.net.rjnetwork.redis;

import cn.hutool.core.util.StrUtil;
import cn.net.rjnetwork.redis.entity.RedisInf;
import cn.net.rjnetwork.redis.entity.RedisKeyInfo;
import cn.net.rjnetwork.redis.entity.RedisKeysPage;
import cn.net.rjnetwork.redis.entity.RedisServerInfo;
import cn.net.rjnetwork.redis.parse.InfoParse;
import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.RedisURI;
import io.lettuce.core.ScanArgs;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HtClusterClient {

    private static final ConcurrentHashMap<Long, RedisClusterClient> htClusterRedisClients = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, StatefulRedisClusterConnection<String, String>> htClusterConnections = new ConcurrentHashMap<>();

    public ConcurrentHashMap<Long, StatefulRedisClusterConnection<String, String>> getHtClusterConnections(){
        return htClusterConnections;
    }

    @Autowired
    InfoParse infoParse ;

    public ConcurrentHashMap<Long, RedisClusterClient> getClusterHtRedisClients(){
        return htClusterRedisClients;
    }

    /**
     * 关闭连接
     **/
    public boolean closeCluster(Long id){
        //
        StatefulRedisClusterConnection<String, String>  connection =  htClusterConnections.get(id);
        connection.close();
        RedisClusterClient redisClient =  htClusterRedisClients.get(id);
        redisClient.close();
        htClusterRedisClients.remove(id);
        htClusterConnections.remove(id);
        return true;
    }



    /**
     * @author huzhenjie
     * @description 创建redis客户端 但是此时还没有连接。
     **/
    public boolean createRedisClient(RedisInf redisInf){
        RedisClusterClient redisClusterClient = null;
        if(redisInf == null){
            throw new NullPointerException("redisInf is null");
        }
        if(htClusterRedisClients.containsKey(redisInf.getId())){
            return true;
        }
            //集群。
            String hosts = redisInf.getHost();
            List<RedisURI> hh = Arrays.stream(hosts.split(","))
                    .map((m)->{
                        String[] split = m.split(":");
                        return RedisURI.builder().withHost(split[0]).withPort(Integer.parseInt(split[1])).build();
                    }).toList();
            redisClusterClient = RedisClusterClient.create(hh);
            try{
                StatefulRedisClusterConnection<String, String>  redisClusterConnection = redisClusterClient.connect();
                htClusterRedisClients.put(redisInf.getId(),redisClusterClient);
                htClusterConnections.put(redisInf.getId(),redisClusterConnection);
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
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        //同步命令接口
        RedisAdvancedClusterCommands<String,String> syncCommands = connection.sync();
        //RedisCommands<String, String> syncCommands =  connection.sync();
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
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
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
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
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
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
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
            Optional<RedisServerInfo> oris= list.stream().filter((item)->{
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
    public RedisKeysPage getKeysBydb(Long id, String cursor, Integer size, String searchKey){
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        RedisKeysPage redisKeysPage = new RedisKeysPage();
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
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
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }
        RedisKeysPage redisKeysPage = new RedisKeysPage();
        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
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
        if(!htClusterConnections.containsKey(id)){
            throw new RuntimeException("please create redis connection");
        }

        StatefulRedisClusterConnection<String, String> connection = htClusterConnections.get(id);
        RedisAdvancedClusterCommands<String, String> syncCommands =  connection.sync();
        //选择对应的数据库
        return syncCommands.type(key);
        //计算页数
    }

}
