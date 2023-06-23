package com.tining.demonmarket.common.util;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.tining.demonmarket.Main;
import com.tining.demonmarket.common.ref.JsonItemStack;
import com.tining.demonmarket.common.ref.Vault;
import com.tining.demonmarket.common.util.bean.Lore;
import com.tining.demonmarket.economy.MarketEconomy;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.collections.CollectionUtils;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PluginUtil {

    /**
     * nms版本
     */
    private static String nmsVersion;

    public static String cipherKey = "DemonMarket";

    /**
     * 为物品快速加上lore，不复制
     * @param itemStack
     * @param addOnLore
     * @return
     */
    public static ItemStack addLore(ItemStack itemStack, List<String> addOnLore){
        if(Objects.isNull(itemStack)){
            return itemStack;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemMeta.getLore();
        if(CollectionUtils.isEmpty(lore)){
            lore = new ArrayList<>();
        }
        lore.addAll(addOnLore);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


    /**
     * 为一个物品添加lore
     * @param is
     * @param loreList
     * @return
     */
    public static ItemStack addColorLore(ItemStack is, List<Lore> loreList) {

        List<String> lore = is.getItemMeta().getLore();
        if(CollectionUtils.isEmpty(lore)){
            lore = new ArrayList<>();
        }

        for(Lore tempLore: loreList){
            lore.add(tempLore.getChatColor() + tempLore.getLore());
        }

        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.setLore(lore);
        is.setItemMeta(itemMeta);
        return is;
    }

    /**
     * 获取nms版本
     *
     * @return
     */
    public static String getNMSVersion() {
        if (StringUtils.isEmpty(nmsVersion)) {
            nmsVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        }
        return nmsVersion;
    }

    /**
     * 获取字典存储的nbt名称key
     *
     * @param is
     * @return
     */
    public static String getKeyName(ItemStack is) {
        String nbtinfo = "";
        try {
            ItemStack newIs = is.clone();
            newIs.setAmount(1);
//            nbtinfo = JsonItemStack.getJsonAsNBTTagCompound(newIs);
            nbtinfo = itemStackSerialize(newIs);
            nbtinfo = compress(nbtinfo);
        }catch (Exception e){
            Main.getInstance().getLogger().info(e.toString());
        }
        return nbtinfo;
    }

    /**
     * 使用yml序列化
     * @param itemStack
     * @return
     */
    public static String itemStackSerialize(ItemStack itemStack) {
        YamlConfiguration yml = new YamlConfiguration();
        yml.set("item", itemStack);
        return yml.saveToString();
    }

    /**
     * 使用yml反序列化
     * @param str
     * @return
     */
    public static ItemStack itemStackDeserialize(String str) {
        YamlConfiguration yml = new YamlConfiguration();
        ItemStack item;
        try {
            yml.loadFromString(str);
            item = yml.getItemStack("item");
        } catch (InvalidConfigurationException ex) {
            item = new ItemStack(Material.AIR, 1);
        }
        return item;
    }

    /**
     * 对NBT编码反解
     *
     * @param code
     * @return
     */
    public static String getNBTBack(String code) {
        return decompress(code);
    }

    /**
     * 使用gzip进行压缩
     */
    public static String compress(String primStr) {
        if (primStr == null || primStr.length() == 0) {
            return primStr;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(primStr.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Base64.getUrlEncoder().encodeToString(out.toByteArray());
    }

    /**
     * 使用gzip进行解压缩
     */
    public static String decompress(String compressedStr) {
        if (compressedStr == null) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = null;
        GZIPInputStream ginzip = null;
        byte[] compressed = null;
        String decompressed = null;
        try {
            compressed = Base64.getUrlDecoder().decode(compressedStr);
            in = new ByteArrayInputStream(compressed);
            ginzip = new GZIPInputStream(in);

            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ginzip != null) {
                try {
                    ginzip.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            try {
                out.close();
            } catch (IOException e) {
            }
        }
        return decompressed;
    }

    /**
     * 根据名字获取一个物品堆
     *
     * @param name
     * @return
     */
    public static ItemStack getItem(String name) {
        if (Objects.isNull(name)) {
            return null;
        }
        Material material = Material.getMaterial(name);
        if(Objects.isNull(material)){
            return null;
        }
        return new ItemStack(material);
    }

    /**
     * 获取一个物品的NBT字符串信息
     *
     * @param nbtItem
     * @return
     */
    public static String getNBTString(NBTItem nbtItem) {
        String nbtString = "";

        Set<String> set = nbtItem.getKeys();
        List<String> tagList = new ArrayList<>();
        set.forEach(e -> tagList.add(e + ":" + nbtItem.getString(e)));
        if (CollectionUtils.isEmpty(tagList)) {
            return nbtString;
        }
        return StringUtils.join(tagList, ",");
    }

    /**
     * 从存储的NBT物品信息恢复一个NBT物品，带“|”格式
     *
     * @param info
     * @return
     */
    public static ItemStack getItemStackFromSaveNBTString(String info) {
        return itemStackDeserialize(decompress(info));
    }

}
