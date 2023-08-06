package com.tining.demonmarket.gui;

import com.tining.demonmarket.Main;
import com.tining.demonmarket.common.util.LangUtil;
import net.wesjd.anvilgui.AnvilGUI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author tinga
 */
public class AdminGroupCreateGui {
    public AdminGroupCreateGui(Player player) {
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }
                    String name = stateSnapshot.getText().trim();
                    if(StringUtils.isBlank(name)){
                        return Collections.emptyList();
                    }
                    List<AnvilGUI.ResponseAction> res = new ArrayList<>();
                    res.add(AnvilGUI.ResponseAction.close());
                    res.add(AnvilGUI.ResponseAction.run(() ->
                            new AdminGroupSignSetGui(player, name)));
                    return res;
                })
                .title(LangUtil.get("请为分组命名"))
                .text(LangUtil.get("请为分组命名"))
                .plugin(Main.getInstance())
                .open(player);
    }
}
