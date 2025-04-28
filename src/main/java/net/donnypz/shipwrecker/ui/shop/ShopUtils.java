package net.donnypz.shipwrecker.ui.shop;

import net.donnypz.mccore.utils.item.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class ShopUtils {
    static final ItemStack currencyItem = new ItemBuilder(Material.SUNFLOWER)
            .setDisplayName(Component.text("Coins", NamedTextColor.GOLD))
            .build();

    static final ItemStack matchesPlayedItem = new ItemBuilder(Material.FILLED_MAP)
            .setDisplayName(Component.text("Matches Played", NamedTextColor.GOLD))
            .addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            .addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
            .build();

    static final ItemStack matchesWonItem = new ItemBuilder(Material.TOTEM_OF_UNDYING)
            .setDisplayName(Component.text("Matches Won", NamedTextColor.GOLD))
            .build();

    static final ItemStack meleeKillsItem = new ItemBuilder(Material.DIAMOND_SWORD)
            .setDisplayName(Component.text("Melee Kills", NamedTextColor.GOLD))
            .build();

    static final ItemStack projectileKillsItem = new ItemBuilder(Material.BOW)
            .setDisplayName(Component.text("Projectile Kills", NamedTextColor.GOLD))
            .build();
}
