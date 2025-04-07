package net.donnypz.shipwrecker.ui.menu.shop;

import net.donnypz.mccore.utils.ItemBuilder;
import net.donnypz.mccore.utils.inventory.gui.ChestGUI;
import net.donnypz.mccore.utils.inventory.gui.GUIItem;
import net.donnypz.mccore.utils.inventory.gui.InventoryUtils;
import net.donnypz.playerdbutils.database.MongoUtils;
import net.donnypz.shipwrecker.Shipwrecker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ShopMain {
    public static void open(Player player){
        open(player, MongoUtils.getPlayerDocument(Shipwrecker.getInstance().getPlayersCollection(), player.getUniqueId()));
    }

    static void open(Player player, Document playerDocument){
        ChestGUI gui = new ChestGUI(5, Component.text("Cosmetic Shop"));
        InventoryUtils.setInventoryOutline(gui, Material.LIGHT_BLUE_STAINED_GLASS_PANE, InventoryUtils.OutlineType.TOPROW);
        InventoryUtils.setExitItemSlot(gui, 40);

        //Kill Effects
        new GUIItem(gui, 19, new ItemBuilder(Material.IRON_SWORD)
                .setDisplayName(Component.text("Kill Effects", NamedTextColor.YELLOW))
                .build(),
                clickEvent -> KillEffectShop.open(player, playerDocument));

        InventoryUtils.setSlotCurrency(gui, ShopUtils.currencyItem, 44, playerDocument, Shipwrecker.COINS, "coin(s)");
        gui.openToPlayer(player);
    }
}
