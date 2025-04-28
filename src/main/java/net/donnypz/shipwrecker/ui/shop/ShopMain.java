package net.donnypz.shipwrecker.ui.shop;

import net.donnypz.mccore.database.PlayerData;
import net.donnypz.mccore.utils.item.ItemBuilder;
import net.donnypz.mccore.utils.inventory.gui.ChestGUI;
import net.donnypz.mccore.utils.inventory.gui.GUIItem;
import net.donnypz.mccore.utils.inventory.gui.InventoryUtils;
import net.donnypz.shipwrecker.Shipwrecker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ShopMain {
    public static void open(Player player){
        open(player, PlayerData.get(player).getDocument());
    }

    static void open(Player player, Document playerDocument){
        ChestGUI gui = new ChestGUI(5, Component.text("Cosmetic Shop"));
        InventoryUtils.setInventoryOutline(gui, Material.LIGHT_BLUE_STAINED_GLASS_PANE, InventoryUtils.OutlineType.TOPROW);
        InventoryUtils.setExitItemSlot(gui, 40);

        //Kill Effects
        new GUIItem(gui, 20, new ItemBuilder(Material.IRON_SWORD)
                .setDisplayName(Component.text("Kill Effects", NamedTextColor.YELLOW))
                .build(),
                clickEvent -> KillEffectShop.open(player, playerDocument));

        //Projectile Trails
        new GUIItem(gui, 24, new ItemBuilder(Material.SPECTRAL_ARROW)
                .setDisplayName(Component.text("Projectile Trails", NamedTextColor.YELLOW))
                .build(),
                clickEvent -> ProjectileTrailShop.open(player, playerDocument));

        InventoryUtils.setSlotFromField(gui, ShopUtils.currencyItem, 44, playerDocument, Shipwrecker.COINS, "coin(s)");
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1,1);
        gui.openToPlayer(player);
    }
}
