package net.donnypz.shipwrecker.ui.shop;

import com.mongodb.client.MongoCollection;
import net.donnypz.mccore.cosmetics.Cosmetic;
import net.donnypz.mccore.utils.inventory.cosmetic.CosmeticGUI;
import net.donnypz.mccore.utils.inventory.cosmetic.CosmeticGUIItem;
import net.donnypz.mccore.utils.inventory.gui.InventoryUtils;
import net.donnypz.shipwrecker.Shipwrecker;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

class KillEffectShop {

    static void open(Player player, Document playerDocument){
        MongoCollection<Document> playerCollection = Shipwrecker.getInstance().getPlayersCollection();
        MongoCollection<Document> unlockCollection = Shipwrecker.getInstance().getKillEffectCollection();

        CosmeticGUI gui = new CosmeticGUI(5,
                Component.text("Kill Effects"),
                player,
                playerCollection,
                unlockCollection,
                Shipwrecker.EQUIPPED_COSMETICS+".kill_effect",
                "kill effect");
        InventoryUtils.setInventoryOutline(gui, Material.LIGHT_BLUE_STAINED_GLASS_PANE, InventoryUtils.OutlineType.TOPROW);

        InventoryUtils.setExitItemSlot(gui, 40);
        InventoryUtils.setBackItemSlot(gui, 36, clickEvent -> {
            ShopMain.open(player, playerDocument);
        });

        int slot = 9;
        for (Cosmetic cosmetic : Shipwrecker.getInstance().getKillEffectRegistry().getCosmetics()){
            new CosmeticGUIItem(gui, slot, cosmetic.getDisplayMaterial(), cosmetic);
            slot++;
        }

        InventoryUtils.setSlotFromField(gui, ShopUtils.matchesPlayedItem, 42, playerDocument, "matches_played", "matches played");
        InventoryUtils.setSlotFromField(gui, ShopUtils.meleeKillsItem, 43, playerDocument, "kills_melee", "melee kill(s)");
        InventoryUtils.setSlotFromField(gui, ShopUtils.currencyItem, 44, playerDocument, Shipwrecker.COINS, "coin(s)");
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1,1);
        gui.openToPlayer(player);
    }
}
