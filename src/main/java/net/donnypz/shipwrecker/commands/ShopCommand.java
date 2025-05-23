package net.donnypz.shipwrecker.commands;

import net.donnypz.shipwrecker.ui.shop.ShopMain;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player){
            ShopMain.open(player);
        }
        return true;
    }
}
