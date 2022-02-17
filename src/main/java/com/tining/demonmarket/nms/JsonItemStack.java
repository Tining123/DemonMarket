package com.tining.demonmarket.nms;

import com.tining.demonmarket.common.PluginUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used by /signin itemcollection.
 * Allows the player to display the item’s parameters while moving the mouse pointer over the text.
 * @author Dean
 */
public class JsonItemStack
{
    public static Class<?> craftItemStack;
    public static Class<?> nbtTagCompound;
    public static Class<?> itemStack;
    public static boolean nmsFound;
    
    public static void reloadNMS() {
        
        //craftbukkit
        try {
            craftItemStack = Class.forName("org.bukkit.craftbukkit." + PluginUtil.nmsVersion + ".inventory.CraftItemStack");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JsonItemStack.class.getName()).log(Level.SEVERE, null, ex);
            nmsFound = false;
        }
        
        //net.minecraft.server
        try {
            if (PluginUtil.nmsVersion.startsWith("v1_17") || PluginUtil.nmsVersion.startsWith("v1_18")) {
                nbtTagCompound = Class.forName("net.minecraft.nbt.NBTTagCompound");
                itemStack = Class.forName("net.minecraft.world.item.ItemStack");
            } else {
                nbtTagCompound = Class.forName("net.minecraft.server." + PluginUtil.nmsVersion + ".NBTTagCompound");
                itemStack = Class.forName("net.minecraft.server." + PluginUtil.nmsVersion + ".ItemStack");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JsonItemStack.class.getName()).log(Level.SEVERE, null, ex);
            nmsFound = false;
        }
    }

    public static BaseComponent getJsonItemStack(ItemStack is) {
        String text;
        try {
            text = is.getItemMeta().hasDisplayName() ? is.getItemMeta().getDisplayName() : (String) is.getClass().getMethod("getI18NDisplayName").invoke(is);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            text = is.getItemMeta().hasDisplayName() ? is.getItemMeta().getDisplayName() : is.getType().toString().toLowerCase().replace("_", " ");
        }
        if (is != null && !is.getType().equals(Material.AIR)) {
            BaseComponent tc = new TextComponent(text);
            ComponentBuilder cb = new ComponentBuilder(getJsonAsNBTTagCompound(is));
            tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, cb.create()));
            return tc;
        }
        return null;
    }
    
    public static BaseComponent getJsonItemStack(ItemStack is, String text) {
        if (is != null && !is.getType().equals(Material.AIR)) {
            BaseComponent tc = new TextComponent(text);
            ComponentBuilder cb = new ComponentBuilder(getJsonAsNBTTagCompound(is));
            tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, cb.create()));
            return tc;
        }
        return null;
    }
    
    public static String getJsonAsNBTTagCompound(ItemStack is) {
        try {
            Object mcStack = craftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class).invoke(null, is);
            Object NBTTagCompound = nbtTagCompound.newInstance();
            Method saveMethod = Arrays.stream(itemStack.getDeclaredMethods()).filter(method -> method.getParameterTypes().length == 1 && method.getParameterTypes()[0].equals(nbtTagCompound) && method.getReturnType().equals(nbtTagCompound)).findFirst().orElse(null);
            if (saveMethod != null) {
                saveMethod.invoke(mcStack, NBTTagCompound);
            } else {
                return null;
            }
            return NBTTagCompound.toString();
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException ex) {
            Logger.getLogger(JsonItemStack.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
