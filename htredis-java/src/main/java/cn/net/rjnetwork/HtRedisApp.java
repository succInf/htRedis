package cn.net.rjnetwork;

import cn.net.rjnetwork.before.BeforeApp;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
})

public class HtRedisApp {

    //数据库有更新 版本需要加1
    public static final String VERSION = "1.0.0";

    public static void main(String[] args) {
        BeforeApp.before();
        SpringApplication application = new SpringApplication(HtRedisApp.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
}
