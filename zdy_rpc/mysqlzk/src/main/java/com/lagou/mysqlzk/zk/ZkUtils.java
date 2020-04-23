package com.lagou.mysqlzk.zk;

import com.alibaba.druid.pool.DruidDataSource;
import com.lagou.mysqlzk.dataSource.DataSourceBean;
import com.lagou.mysqlzk.dataSource.SourceChangeListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import javax.sql.DataSource;

/**
 * @author shengx
 * @date 2020/4/23 11:40
 */
public class ZkUtils {
    private static ZkClient zkclient;
    private static final String sourcePath = "/mysql/source";
    public static DruidDataSource initDataSource(){
        DruidDataSource dataSource = new DruidDataSource();
        DataSourceBean dataSourceBean = null;
        if(zkclient == null){
            zkclient = new ZkClient("192.168.79.100:2181,192.168.79.110:2181,192.168.79.120:2181", 5000);
        }
        Object o = zkclient.readData(sourcePath, true);
        System.out.println(o);
        if(o !=null ){
            dataSourceBean = (DataSourceBean) o;
            dataSource.setUrl(dataSourceBean.getUrl());
            dataSource.setPassword(dataSourceBean.getPassword());
            dataSource.setUsername(dataSourceBean.getUsername());
        }
        zkclient.subscribeDataChanges(sourcePath, new SourceChangeListener());
        return dataSource;
    }

    public static void setDataSource(){
        DataSourceBean dataSourceBean = new DataSourceBean();
        dataSourceBean.setUrl("jdbc:mysql://62.234.121.17:3306/blog_system?useUnicode=true&characterEncoding=utf-8");
        dataSourceBean.setPassword("Wjdh84928399**");
        dataSourceBean.setUsername("root");
        if(zkclient == null){
            zkclient = new ZkClient("192.168.79.100:2181,192.168.79.110:2181,192.168.79.120:2181", 5000);
        }
        zkclient.writeData(sourcePath, dataSourceBean);
    }

    public static void close(){
        zkclient.close();
    }

    public static void main(String[] args) {
        ZkUtils.setDataSource();
    }
}
