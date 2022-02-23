package com.tining.demonmarket.common.util;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.tining.demonmarket.common.ref.JsonItemStack;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
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
     * 获取nms版本
     * @return
     */
    public static String getNMSVersion(){
        if(StringUtils.isEmpty(nmsVersion)){
            nmsVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        }
        return nmsVersion;
    }

    /**
     * 获取字典存储的nbt名称key
     * @param is
     * @return
     */
    public static String getKeyName(ItemStack is) {
        String pre = JsonItemStack.getJsonAsNBTTagCompound(is);
        if(!StringUtils.isEmpty(pre)) {
            pre = "{" + pre.substring(pre.indexOf(',') + 1);
        }
        String name = is.getType().name() + ":" + pre;
        //name = Hashing.md5().newHasher().putString(name, Charsets.UTF_8).hash().toString();
        name = compress(name);
        return is.getType().name() + "|" + name;
    }

    /**
     * 对NBT编码反解
     * @param code
     * @return
     */
    public static String getNBTBack(String code){
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
        return  Base64.getUrlEncoder().encodeToString(out.toByteArray());
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

}
