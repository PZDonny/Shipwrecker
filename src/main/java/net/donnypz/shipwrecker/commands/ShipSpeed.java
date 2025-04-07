package net.donnypz.shipwrecker.commands;

import net.donnypz.shipwrecker.gamemanager.SWArena;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShipSpeed implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)){
            return true;
        }
        SWArena swArena = SWArena.getArenaContainer(player, SWArena.class);
        if (swArena != null){
            int speed = Integer.parseInt(args[0]);
            swArena.setShipSpeed(speed);
            player.sendMessage(Component.text("Adjusted Ship Speed! ("+speed+")", NamedTextColor.AQUA));
            player.playSound(player, Sound.BLOCK_BREWING_STAND_BREW, 1, 1);
        }
        return true;
    }
}
