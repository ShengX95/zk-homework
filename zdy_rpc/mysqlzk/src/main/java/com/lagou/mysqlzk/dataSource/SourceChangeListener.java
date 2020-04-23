package com.lagou.mysqlzk.dataSource;

import com.alibaba.druid.pool.DruidDataSource;
import org.I0Itec.zkclient.IZkDataListener;

/**
 * @author shengx
 * @date 2020/4/23 11:48
 */
public class SourceChangeListener implements IZkDataListener {
    @Override
    public void handleDataChange(String s, Object o) throws Exception {
        System.out.println("dataSource change to: " + o);
        DataSourceBean dataSourceBean = (DataSourceBean) o;
        DruidDataSource dataSource=Application.getApplicationContext().getBean(DruidDataSource.class);
        dataSource.close();
        dataSource.setUsername(dataSourceBean.getUsername());
        dataSource.setPassword(dataSourceBean.getPassword());
        dataSource.setUrl(dataSourceBean.getUrl());
        dataSource.restart();
        dataSource.getConnection();
        System.out.println(dataSource);
    }

    @Override
    public void handleDataDeleted(String s) throws Exception {

    }
}
