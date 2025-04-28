package net.donnypz.shipwrecker.gamemanager.upgrade;

import net.donnypz.mccore.utils.item.ItemBuilder;
import net.donnypz.shipwrecker.gamemanager.SWProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class ArmorUpgrade extends Upgrade{

    int level = 0;

    public ArmorUpgrade(SWProfile profile, Player player){
        super(profile, player);
        EntityEquipment equipment = player.getEquipment();
        equipment.setHelmet(buildItem(Material.LEATHER_HELMET));
        equipment.setChestplate(buildItem(Material.LEATHER_CHESTPLATE));
        equipment.setLeggings(buildItem(Material.LEATHER_LEGGINGS));
        equipment.setBoots(buildItem(Material.LEATHER_BOOTS));
    }

    public void upgrade(){
        Player player = profile.getPlayer().getPlayer();
        if (!canUpgrade()) return;
        if (level == 2){
            player.sendMessage(Component.text("You already have the highest armor upgrade!", NamedTextColor.RED));
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
            return;
        }

        EntityEquipment equipment = player.getEquipment();

        if (level == 0){
            equipment.setHelmet(buildItem(Material.GOLDEN_HELMET));
            equipment.setChestplate(buildItem(Material.GOLDEN_CHESTPLATE));
            equipment.setLeggings(buildItem(Material.GOLDEN_LEGGINGS));
            equipment.setBoots(buildItem(Material.GOLDEN_BOOTS));
        }
        else if (level == 1){
            equipment.setHelmet(buildItem(Material.CHAINMAIL_HELMET));
            equipment.setChestplate(buildItem(Material.CHAINMAIL_CHESTPLATE));
            equipment.setLeggings(buildItem(Material.CHAINMAIL_LEGGINGS));
            equipment.setBoots(buildItem(Material.CHAINMAIL_BOOTS));
        }

        if (++level == 2){
            player.sendMessage(Component.text("🛡 Equipment Fully Upgraded!", NamedTextColor.GOLD));
            player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 1.5f);
        }
        else{
            player.sendMessage(Component.text("🛡 Equipment Upgraded!", NamedTextColor.GREEN));
            player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 1.5f);
        }
        profile.removeGold(500, true, false);
    }

    private ItemStack buildItem(Material material){
        return new ItemBuilder(material)
                .setUnbreakable(true)
                .setUndroppable(true)
                .build();
    }
}
