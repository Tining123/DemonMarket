package com.tining.demonmarket.common;

import com.tining.demonmarket.nms.JsonItemStack;
import com.tining.demonmarket.storage.ConfigReader;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * @author tinga
 */
public class WorthUtil {
    public static double getWorth(ItemStack is){
        Material material = is.getType();
        //先检测nbtworth
        Map<String,Double> nbtWorth = ConfigReader.getNBTWorth();
        String name = PluginUtil.getKeyName(is);;
        if(nbtWorth.containsKey(name)){
            return nbtWorth.get(name);
        }
        //检测普通worth
        Map<String,Double> worth = ConfigReader.getWorth();
        if(worth.containsKey(material.name())){
            return worth.get(material.name());
        }
        return 0;
    }

    public static boolean isWorthContain(ItemStack is){
        Material material = is.getType();
        //先检测nbtworth
        Map<String,Double> nbtWorth = ConfigReader.getNBTWorth();
        String name = PluginUtil.getKeyName(is);
        if(nbtWorth.containsKey(name)){
            return true;
        }
        //检测普通worth
        Map<String,Double> worth = ConfigReader.getWorth();
        if(worth.containsKey(material.name())){
            return true;
        }
        return false;
    }
}
