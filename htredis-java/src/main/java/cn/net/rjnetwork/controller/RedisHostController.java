package cn.net.rjnetwork.controller;
import cn.net.rjnetwork.consts.SysConst;
import cn.net.rjnetwork.entity.HtredisHostInfo;
import cn.net.rjnetwork.redis.entity.HashInfo;
import cn.net.rjnetwork.redis.entity.HashKeyInfo;
import cn.net.rjnetwork.redis.entity.StrKeyInfo;
import cn.net.rjnetwork.results.ResultBody;
import cn.net.rjnetwork.service.ClusterRedisOperaService;
import cn.net.rjnetwork.service.HtredisHostInfoService;
import cn.net.rjnetwork.service.RedisOperaService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;

@RestController
@RequestMapping("/redis/hosts/")
public class RedisHostController {


    @Autowired
    HtredisHostInfoService htredisHostInfoService;


    @Autowired
    RedisOperaService redisOperaService;


    @Autowired
    ClusterRedisOperaService clusterRedisOperaService;





    private String getType(Long id){
        HtredisHostInfo htredisHostInfo =  htredisHostInfoService.getById(id);
        return htredisHostInfo.getType();
    }


    @PostMapping("doSave")
    public ResultBody doSave(@RequestBody HtredisHostInfo htredisHostInfo){
        htredisHostInfo.setCreateTime(LocalDate.now());
        return ResultBody.success(htredisHostInfoService.save(htredisHostInfo));
    }

    @PostMapping("doUpdate")
    public ResultBody doUpdate(@RequestBody HtredisHostInfo htredisHostInfo){
        htredisHostInfo.setCreateTime(LocalDate.now());
        return ResultBody.success(htredisHostInfoService.updateById(htredisHostInfo));
    }

    @PostMapping("doDelete")
    public ResultBody doDelete(Long id){
        return ResultBody.success(htredisHostInfoService.removeById(id));
    }


    @PostMapping("getList")
    public ResultBody getList(){
        LambdaQueryWrapper<HtredisHostInfo> query = new LambdaQueryWrapper<>();
        query.orderByDesc(HtredisHostInfo::getCreateTime);
        return ResultBody.success(htredisHostInfoService.list(query));
    }


    /**
     * 打开连接
     **/
    @PostMapping("openConnection")
    public ResultBody openConnection(Long id) {
        String type = getType(id);
        if(SysConst.REDIS_ALONE_TYPE.equals(type)){
            return ResultBody.success(redisOperaService.openConnection(htredisHostInfoService.getById(id)));
        }else if(SysConst.REDIS_ClUSTER_TYPE.equals(type)){
            return ResultBody.success(clusterRedisOperaService.openConnection(htredisHostInfoService.getById(id)));
        }
        //clusterRedisOperaService


        return ResultBody.success(null);


    }

    /**
     * 获取redis的db总数 以及每个db的key的总数
     **/
    @PostMapping("getAllDbSizes")
    public ResultBody getAllDbSizes(Long id){
        String type = getType(id);
        if(SysConst.REDIS_ALONE_TYPE.equals(type)){
            return ResultBody.success(redisOperaService.getAllDbSizes(id));
        }else if(SysConst.REDIS_ClUSTER_TYPE.equals(type)){
            return ResultBody.success(clusterRedisOperaService.getAllDbSizes(id));
        }
        return ResultBody.success(null);
    }

    /**
     * 获取redis的基本信息
     **/
    @PostMapping("getRedisBaseInf")
    public ResultBody getRedisBaseInf(Long id){
        String type = getType(id);

        if(SysConst.REDIS_ALONE_TYPE.equals(type)){
            return ResultBody.success(redisOperaService.getRedisBaseInf(id));
        }else if(SysConst.REDIS_ClUSTER_TYPE.equals(type)){
            return ResultBody.success(clusterRedisOperaService.getRedisBaseInf(id));
        }
        return ResultBody.success(null);
    }


    /**
     * 关闭连接
     **/
    @PostMapping("closeConnection")
    public ResultBody closeConnection(Long id){
        String type = getType(id);
        if(SysConst.REDIS_ALONE_TYPE.equals(type)){
            return ResultBody.success(redisOperaService.closeConnection(id));
        }else if(SysConst.REDIS_ClUSTER_TYPE.equals(type)){
            return ResultBody.success(clusterRedisOperaService.closeConnection(id));
        }
        return ResultBody.success(null);

    }



    /**
     * 分页获取key
     * @param cursor 游标 第一次的时候是0
     * @param size 获取的长度
     * @param db 选择的db
     * @param id 主机id
     **/
    @PostMapping("getKeysByDb")
    public ResultBody getKeysByDb(Long id,String cursor,Integer db,Integer size,String searchKey){
        String type = getType(id);

        if(SysConst.REDIS_ALONE_TYPE.equals(type)){
            return ResultBody.success(redisOperaService.getKeysByDb(id,db,cursor,size,searchKey));
        }else if(SysConst.REDIS_ClUSTER_TYPE.equals(type)){
            return ResultBody.success(clusterRedisOperaService.getKeysByDb(id,cursor,size,searchKey));
        }
        return ResultBody.success(null);

    }

    @PostMapping("getValueByType")
    public ResultBody getValueByType(Long id,Integer db,String type,String key){
        String hostType = getType(id);

        if(SysConst.REDIS_ALONE_TYPE.equals(hostType)){
            if(SysConst.REDIS_TYPE_STR.equals(type)){
                return ResultBody.success(redisOperaService.getStrValueByKey(id,db,key,type));
            }else if(SysConst.REDIS_TYPE_HASH.equals(type)){
                return ResultBody.success( redisOperaService.getHashItems(id,db,key));
            }
        }else if(SysConst.REDIS_ClUSTER_TYPE.equals(hostType)){
            if(SysConst.REDIS_TYPE_STR.equals(type)){
                return ResultBody.success(clusterRedisOperaService.getStrValueByKey(id,key,type));
            }



        }

        return ResultBody.success(null);
    }


    @PostMapping("addOrUpateStrValue")
    public ResultBody addOrUpateStrValue(@RequestBody StrKeyInfo strKeyInfo){

        String hostType = getType(strKeyInfo.getId());
        if(SysConst.REDIS_ALONE_TYPE.equals(hostType)){
            return ResultBody.success(redisOperaService.addStrValue(strKeyInfo.getId(),strKeyInfo.getDb(),strKeyInfo.getKey(),strKeyInfo.getValue(),strKeyInfo.getExpire()));
        }else if(SysConst.REDIS_ClUSTER_TYPE.equals(hostType)){
            return ResultBody.success(clusterRedisOperaService.addStrValue(strKeyInfo.getId(),strKeyInfo.getKey(),strKeyInfo.getValue(),strKeyInfo.getExpire()));
        }

        return ResultBody.success(null);
    }


    @PostMapping("removeStrAndValue")
    public ResultBody removeStrAndValue(@RequestBody StrKeyInfo strKeyInfo){
        String hostType = getType(strKeyInfo.getId());
        if(SysConst.REDIS_ALONE_TYPE.equals(hostType)){
            if(SysConst.REDIS_TYPE_STR.equals(strKeyInfo.getType())){
                return ResultBody.success(redisOperaService.removeStrValue(strKeyInfo.getId(), strKeyInfo.getDb(), strKeyInfo.getKey()));
            }else if(SysConst.REDIS_TYPE_HASH.equals(strKeyInfo.getType())){
                return ResultBody.success( redisOperaService.removeHash(strKeyInfo.getId(), strKeyInfo.getDb(), strKeyInfo.getKey()));
            }
        }else if(SysConst.REDIS_ClUSTER_TYPE.equals(hostType)){
            return ResultBody.success(clusterRedisOperaService.removeStrValue(strKeyInfo.getId(), strKeyInfo.getKey()));
        }
        return ResultBody.success(null);
    }




    /**
     * 向hash表中新增一个field的value
     *  times 单位是秒
     */
    @PostMapping("addHashValue")
    public ResultBody addHashValue(@RequestBody HashKeyInfo hashKeyInfo){
        redisOperaService.addHashValue(hashKeyInfo.getId(),hashKeyInfo.getDb(),hashKeyInfo.getKey(),hashKeyInfo.getField(),hashKeyInfo.getValue(),hashKeyInfo.getExpire());
        return ResultBody.success();
    }

    /**
     * 向hash表中移除一个field
     *  times 单位是秒
     */
    @PostMapping("removeHashField")
    public ResultBody removeHashField(Long id,Integer db,String key,String field){
        redisOperaService.removeHashField(id,db,key,field);
        return ResultBody.success();
    }

    /**
     * 移除整个hash表
     *  times 单位是秒
     */
    @PostMapping("removeHash")
    public ResultBody removeHash(Long id,Integer db,String key){
        redisOperaService.removeHash(id,db,key);
        return ResultBody.success();
    }
    /**
     * 获取hash表的items
     */
    @PostMapping("getHashItems")
    public ResultBody getHashItems(Long id, Integer db, String key){
        return ResultBody.success(redisOperaService.getHashItems(id,db,key));
    }

    /**
     * 获取hash表的items
     */
    @PostMapping("getHashItem")
    public ResultBody getHashItem(Long id, Integer db, String key,String item){
        return ResultBody.success(redisOperaService.getHashItem(id,db,key,item));
    }



}
