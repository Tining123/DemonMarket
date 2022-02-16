package com.tining.demonmarket.storage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MysqlInit {
    public static boolean checkTable(String tableName) {
        Mysql m = new Mysql();
        m.prepareSql("select * from information_schema.TABLES where TABLE_NAME = ?");
        m.setData(1, tableName);
        m.execute();
        ResultSet resultSet = m.getResult();
        try {
            return resultSet.next();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }
    public static void init_market_log() {
        Mysql mysql = new Mysql();
        mysql.prepareSql("create table market_log\n" +
                "(\n" +
                "    id       int auto_increment\n" +
                "        primary key,\n" +
                "    username varchar(64) null comment '用户名\n" +
                "',\n" +
                "    type     varchar(8)  null comment '交易类型，buy或者sell',\n" +
                "    time     int         null comment '交易时间，UNIX时间戳精确到秒',\n" +
                "    item     varchar(32) null comment '物品名称',\n" +
                "    amount   int         null comment '物品数量',\n" +
                "    price    double      null comment '物品税前价格',\n" +
                "    tax      double      null comment '贸易税'\n" +
                ")\n" +
                "    comment '用于储存交易记录';\n" +
                "\n");
        mysql.execute();
        mysql.close();
    }
    public static void init_market_item_data() {
        Mysql m = new Mysql();
        m.prepareSql("create table market_item_data\n" +
                "(\n" +
                "    id        int auto_increment\n" +
                "        primary key,\n" +
                "    item_name varchar(64) not null comment '物品的名称，带有命名空间如minecraft:diamond',\n" +
                "    x         int         null comment '市场拥有量',\n" +
                "    k         int         null comment '参数k决定物品的最高价格',\n" +
                "    b         int         null comment '参数b决定物品价格曲线的平滑程度',\n" +
                "    constraint market_item_data_item_name_uindex\n" +
                "        unique (item_name)\n" +
                ")\n" +
                "    comment '储存市场中的物品价格和参数';\n");
        m.execute();
        m.close();
    }
}
