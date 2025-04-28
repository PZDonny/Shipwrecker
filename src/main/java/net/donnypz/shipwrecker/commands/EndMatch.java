package net.donnypz.shipwrecker.commands;

import net.donnypz.mccore.minigame.arena.Arena;
import net.donnypz.mccore.minigame.arena.ArenaManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EndMatch implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)){
            return true;
        }
        Arena arena = ArenaManager.getArenaOfPlayer(player);
        if (arena != null) arena.endGame(0, 0);
        return true;
    }
}
