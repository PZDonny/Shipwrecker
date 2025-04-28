package net.donnypz.shipwrecker.gamemanager.crates;

import net.donnypz.shipwrecker.gamemanager.SWArena;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

class ItemReward implements Reward{

    private final ItemStack itemStack;

    ItemReward(@NotNull ItemStack itemStack){
        this.itemStack = itemStack;
    }

    @Override
    public ItemReward giveReward(Player player, SWArena swArena) {
        player.getInventory().addItem(itemStack.clone());
        player.playSound(player, Sound.ENTITY_ITEM_PICKUP, 2, 1f);
        return this;
    }

    @Override
    public Component message() {
        return Component.text("| ").append(itemStack.displayName().color(NamedTextColor.YELLOW));
    }
}
