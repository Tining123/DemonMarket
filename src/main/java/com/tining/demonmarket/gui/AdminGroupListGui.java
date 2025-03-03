package com.tining.demonmarket.gui;

import com.tining.demonmarket.common.util.LangUtil;
import com.tining.demonmarket.common.util.PluginUtil;
import com.tining.demonmarket.gui.bean.SignMaterialEnum;
import com.tining.demonmarket.gui.v1.AbstractGUIV1;
import com.tining.demonmarket.gui.v1.AbstractListGUIV1;
import com.tining.demonmarket.gui.v1.SignEnumInterfaceV1;
import com.tining.demonmarket.storage.ClassifyReader;
import com.tining.demonmarket.storage.bean.Classify;
import com.tining.demonmarket.storage.bean.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AdminGroupListGui extends AbstractListGUIV1<Group> {


    public AdminGroupListGui(Player player, List<Group> dataList) {
        super(player, LangUtil.get("商店分组列表"), dataList);
    }

    @Override
    protected void beforeDraw(Inventory inventory, int pageNum, Player player) {

    }

    @Override
    protected void afterDraw(Inventory inventory, int pageNum, Player player) {
        setSign(inventory,SignEnum.ADD,SignMaterialEnum.ANVIL,LangUtil.get("新增"),new ArrayList<>());
        setSign(inventory,SignEnum.CHECK,SignMaterialEnum.CONFIG,LangUtil.get("查看"),new ArrayList<>());
        setSign(inventory,SignEnum.DELETE,SignMaterialEnum.OFF,LangUtil.get("删除"),new ArrayList<>());
    }

    @Override
    protected ItemStack getSignItemStack(Group item){
        return PluginUtil.getItemStackFromSaveNBTString(item.getInfo());
    }

    /**
     * 获取内部枚举列表
     *
     * @param player
     * @return
     */
    @Override
    public List<SignEnumInterfaceV1> getEnumList(Player player) {
        return Arrays.asList(SignEnum.values());
    }

    @AllArgsConstructor
    public static enum SignEnum implements SignEnumInterfaceV1{
        //---事件区---//
        ADD(49,"新增"){
            @Override
            public void deal(Inventory inventory, Player player) {
                AdminGroupListGui me = (AdminGroupListGui) AbstractGUIV1.getMe(player);
                me.quit(player,inventory);
                new AdminGroupCreateGui(player);
            }
        },
        CHECK(50,"查看"){
            @Override
            public void deal(Inventory inventory, Player player) {
                AdminGroupListGui me = (AdminGroupListGui) AbstractGUIV1.getMe(player);
                Group select = me.selectItem;
                if(Objects.isNull(select)){
                    return;
                }
                me.quit(player,inventory);
                // 查询出对应分类list
                List<Classify> list = ClassifyReader.getInstance().getClassifyList(select);
                if(Objects.isNull(list)){
                    list = new ArrayList<>();
                }
                new AdminClassifyListGui(player,list,select);

            }
        },
        DELETE(53,"删除"){
            @Override
            public void deal(Inventory inventory, Player player) {
                AdminGroupListGui me = (AdminGroupListGui) AbstractGUIV1.getMe(player);
                new AdminGroupDeleteGui(player,me.selectItem);
            }
        },
        ;
        @Getter
        private int slot;
        @Getter
        private String label;
    }
}
