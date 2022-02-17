package com.tining.demonmarket.common;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import com.tining.demonmarket.nms.JsonItemStack;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PluginUtil {

    public static String nmsVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

    public static String cipherKey = "DemonMarket";

    /**
     * 获取字典存储的nbt名称key
     * @param is
     * @return
     */
    public static String getKeyName(ItemStack is) {
        String pre = JsonItemStack.getJsonAsNBTTagCompound(is);
        pre = "{" + pre.substring(pre.indexOf(','));
        String name = is.getType().name() + ":" + pre;
        name = Hashing.md5().newHasher().putString(name, Charsets.UTF_8).hash().toString();

        return is.getType().name() + "|" + name;
    }

}
