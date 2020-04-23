package com.lagou.mysqlzk.dataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.lagou.mysqlzk.zk.ZkUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @author shengx
 * @date 2020/4/23 11:38
 */
@Component
public class MyDruidSource {
    public static DruidDataSource dataSource;
    @Bean
    public DataSource druidDataSource() throws SQLException {
        dataSource = ZkUtils.initDataSource();;
        return dataSource;
    }
}
