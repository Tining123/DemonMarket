package com.tining.demonmarket.command.dispatcher;

import com.google.common.base.Strings;
import com.tining.demonmarket.command.UserCommand;
import com.tining.demonmarket.common.ref.Vault;
import com.tining.demonmarket.common.util.InventoryUtil;
import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.common.util.MarketUtil;
import com.tining.demonmarket.common.util.WorthUtil;
import com.tining.demonmarket.economy.MarketTrade;
import com.tining.demonmarket.gui.MarketGui;
import com.tining.demonmarket.gui.ShopGui;
import com.tining.demonmarket.storage.ConfigReader;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Locale;
import java.util.Objects;

/**
 * 市场命令
 * @author tinga
 */
public class MarketCommand extends AbstractCommander{
    @Override
    protected boolean solve(CommandSender sender, Command command, String label, String[] args) {
        if(ConfigReader.getDisableMarket()){
            sender.sendMessage(LangUtil.get("市场已经被禁用"));
            return true;
        }
        Player player = (Player) sender;
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if(args.length == 1){
            MarketGui.getMarketGui((Player)sender);
            return true;
        }else if(args.length == 3 && args[1].toLowerCase().equals("sell")){
            ItemStack copy = itemStack.clone();
            if (Objects.isNull(copy) || copy.getType().name().equals("AIR")) {
                sender.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("[DemonMarket]你手里的物品无法交易")));
                return false;
            }
            double price = 0.0;
            //校验价值是否合法
            try {
                price = Double.parseDouble(args[2]);
                if (price <= 0) {
                    sender.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("[DemonMarket]你输入的价格不合法")));
                    return true;
                }
            } catch (Exception e) {
                sender.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("[DemonMarket]你输入的价格不合法")));
                return true;
            }
            // 检查售卖上线
            int playerSellCount = MarketUtil.userSellCount(player);
            if(playerSellCount >= ConfigReader.getMaxUserSell()){
                sender.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("[DemonMarket]已达到最大限制")));
                return false;
            }

            // 清除手中物品
            PlayerInventory inventory = player.getInventory();
            // 清除主手中的物品
            inventory.setItemInMainHand(null);
            // 清除物品栏对应槽位的物品

            int heldSlot = inventory.getHeldItemSlot();
            inventory.setItem(heldSlot, null);
            MarketUtil.addToMarket(player, copy, price);
            sender.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("[DemonMarket]上架成功")));
            // 如果设置了税费，进行收税
            double sellTax = (ConfigReader.getSellTaxRate() / 100) * price;
            if(sellTax != 0){
                Vault.subtractCurrency(player.getUniqueId(), sellTax);
            }
            sender.sendMessage(LangUtil.preColor(ChatColor.YELLOW , LangUtil.get("税费：") + sellTax));

            return true;
        }

        sender.sendMessage(UserCommand.getHelp());
        return true;
    }

}
