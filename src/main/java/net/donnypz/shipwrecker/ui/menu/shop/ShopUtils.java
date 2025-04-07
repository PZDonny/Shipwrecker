package net.donnypz.shipwrecker.ui.menu.shop;

import net.donnypz.mccore.utils.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ShopUtils {
    static final ItemStack currencyItem = new ItemBuilder(Material.SUNFLOWER)
            .setDisplayName(Component.text("Coins", NamedTextColor.GOLD))
            .build();

    static final ItemStack matchesPlayedItem = new ItemBuilder(Material.FILLED_MAP)
            .setDisplayName(Component.text("Matches Played", NamedTextColor.GOLD))
            .build();

    static final ItemStack meleeKillsItem = new ItemBuilder(Material.DIAMOND_SWORD)
            .setDisplayName(Component.text("Melee Kills", NamedTextColor.GOLD))
            .build();
}
