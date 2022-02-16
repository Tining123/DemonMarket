package com.tining.demonmarket.player;

import com.tining.demonmarket.storage.ConfigReader;
import com.tining.demonmarket.storage.Mysql;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerRegData {
    public static boolean isVIP(Player player) {
        if (!ConfigReader.getEnable("NoTax")) return false;

        Mysql m = new Mysql();
        m.prepareSql("SELECT level FROM user WHERE username = ?");
        m.setData(1, player.getName());
        m.execute();
        ResultSet resultSet = m.getResult();
        try {
            resultSet.next();
            return resultSet.getInt("level") > 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        m.close();
        return false;
    }
}
