package net.donnypz.shipwrecker.gamemanager.upgrade;

import net.donnypz.mccore.utils.item.ItemBuilder;
import net.donnypz.shipwrecker.gamemanager.SWProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WeaponUpgrade extends Upgrade{

    Material level = Material.STONE_SWORD;

    public WeaponUpgrade(SWProfile profile, Player player){
        super(profile, player);
        giveWeapon(player, level, 0);
    }

    public void upgrade(){
        Player player = profile.getPlayer().getPlayer();
        if (!canUpgrade()) return;
        if (level == Material.NETHERITE_SWORD){
            player.sendMessage(Component.text("You already have the highest weapon upgrade!", NamedTextColor.RED));
            player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 1, 0.5f);
            return;
        }

        player.getInventory().remove(level);

        if (level == Material.STONE_SWORD){
            level = Material.IRON_SWORD;
            player.sendMessage(Component.text("⚔ Weapon Upgraded!", NamedTextColor.GREEN));
            player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 1.5f);
            giveWeapon(player, level, 0);
        }
        else if (level == Material.IRON_SWORD){
            level = Material.DIAMOND_SWORD;
            player.sendMessage(Component.text("⚔ Weapon Upgraded!", NamedTextColor.GREEN));
            player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 1.5f);
            giveWeapon(player, level, 0);
        }
        else if (level == Material.DIAMOND_SWORD){
            player.sendMessage(Component.text("⚔ Weapon Fully Upgraded!", NamedTextColor.GOLD));
            player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 1.5f);
            giveWeapon(player, level, 1);
            level = Material.NETHERITE_SWORD;
        }
        profile.removeGold(500, true, false);
    }

    private void giveWeapon(Player player, Material material, int sharpness){
        ItemBuilder builder = new ItemBuilder(material)
                .setUndroppable(true)
                .setUnbreakable(true);
        if (sharpness > 0){
            builder.addEnchantment(Enchantment.SHARPNESS, sharpness);
        }

        player.getInventory().addItem(builder.build());
    }
}
