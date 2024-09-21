package cn.net.rjnetwork.before;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.net.rjnetwork.HtRedisApp;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 在程序启动之前先初始化一些操作。
 */
@Slf4j
public class BeforeApp {

   private static final String homeDir = System.getProperty("user.home");

   private static final String basePath = homeDir + File.separator + "htredis";

   private static final String dbBasePath = basePath + File.separator +"db";

   @Getter
   private static final String dbPath = dbBasePath +File.separator+ HtRedisApp.VERSION+File.separator + HtRedisApp.VERSION +"_htredis.db";

   private static final String activeDbFileName = HtRedisApp.VERSION +"_htredis.db";
    public static void before(){
        //获取系统的home目录，将初始化文件写入到home目录下的 htredis目录下
        log.info("home dir: {}", homeDir);

        ClassPathResource cpr = new ClassPathResource("dbs/htredis.db");
        try{
            InputStream inputStream = cpr.getInputStream();
            if(!FileUtil.exist(dbPath)){
                File file =  FileUtil.touch(dbPath);
                IoUtil.copy(inputStream, new FileOutputStream(file));
            }else{
             log.error("db {} exists", dbPath);
            }
        } catch (IOException e) {
            log.error("未找到资源模块");
            throw new RuntimeException(e);
        }finally {
         //把其他的数据库文件进行删除。
            List<File> files = FileUtil.loopFiles(dbBasePath);
            files.stream().peek((m)->{
                if(!activeDbFileName.equals(m.getName())){
                    FileUtil.del(m);
                }
            }).collect(Collectors.toList());
//            List<String> dbNames =  FileUtil.listFileNames(dbPath);
//            dbNames.stream().peek((m)->{
//                System.out.println(m);
//                if(!m.equals(activeDbFileName)){
//                    //FileUtil.del(dbPath);
//                }
//            }).collect(Collectors.toList());

        }

    }
}
