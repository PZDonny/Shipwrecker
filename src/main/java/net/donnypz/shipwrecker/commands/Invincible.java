package net.donnypz.shipwrecker.commands;

import net.donnypz.mccore.minigame.arena.Arena;
import net.donnypz.mccore.minigame.arena.ArenaManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Invincible implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)){
            sender.sendMessage(Component.text("You can only run this command in-game!", NamedTextColor.RED));
            return true;
        }

        Arena arena = ArenaManager.getArenaOfPlayer(p);
        if (arena == null){
            p.sendMessage(Component.text("You can only run this command in a match!", NamedTextColor.RED));
            return true;
        }
        if (p.getGameMode() == GameMode.SPECTATOR){
            p.sendMessage(Component.text("You cannot run this command while respawning!", NamedTextColor.RED));
            return true;
        }
        p.setInvulnerable(true);
        p.sendMessage(Component.text("You will no longer take damage for the remainder of this match", NamedTextColor.AQUA));
        return true;
    }
}
