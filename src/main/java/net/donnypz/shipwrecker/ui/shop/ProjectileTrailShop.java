package net.donnypz.shipwrecker.ui.shop;

import com.mongodb.client.MongoCollection;
import net.donnypz.mccore.cosmetics.Cosmetic;
import net.donnypz.mccore.utils.inventory.InventoryUtils;
import net.donnypz.mccore.utils.inventory.gui.cosmetic.CosmeticGUI;
import net.donnypz.mccore.utils.inventory.gui.cosmetic.CosmeticGUIItem;
import net.donnypz.shipwrecker.Shipwrecker;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

class ProjectileTrailShop {

    static void open(Player player, Document playerDocument){
        Shipwrecker sw = Shipwrecker.getInstance();
        MongoCollection<Document> playerCollection = sw.getPlayersCollection();
        MongoCollection<Document> unlockCollection = sw.getProjectileTrailCollection();

        CosmeticGUI gui = new CosmeticGUI(5,
                Component.text("Projectile Trails"),
                player,
                playerCollection,
                unlockCollection,
                Shipwrecker.EQUIPPED_COSMETICS+".projectile_trail",
                "projectile trail");
        InventoryUtils.setInventoryOutline(gui, Material.LIGHT_BLUE_STAINED_GLASS_PANE, InventoryUtils.OutlineType.TOPROW);

        InventoryUtils.setExitItemSlot(gui, 40);
        InventoryUtils.setBackItemSlot(gui, 36, clickEvent -> {
            ShopMain.open(player, playerDocument);
        });

        int slot = 9;
        for (Cosmetic cosmetic : sw.getProjectileTrailRegistry().getCosmetics()){
            new CosmeticGUIItem(gui, slot, cosmetic.getDisplayMaterial(), cosmetic);
            slot++;
        }

        InventoryUtils.setSlotFromField(gui, ShopUtils.matchesWonItem, 42, playerDocument, "matches_won", "matches won");
        InventoryUtils.setSlotFromField(gui, ShopUtils.projectileKillsItem, 43, playerDocument, "kills_projectile", "projectile kill(s)");
        InventoryUtils.setSlotFromField(gui, ShopUtils.currencyItem, 44, playerDocument, Shipwrecker.COINS, "coin(s)");
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1,1);
        gui.openToPlayer(player);
    }
}
