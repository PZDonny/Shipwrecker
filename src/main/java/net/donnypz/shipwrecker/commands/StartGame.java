package net.donnypz.shipwrecker.commands;

import net.donnypz.mccore.minigame.arenaManager.Arena;
import net.donnypz.mccore.minigame.arenaManager.ArenaManager;
import net.donnypz.mccore.minigame.arenaManager.ArenaSettings;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StartGame implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        Arena arena = new Arena(null, Arena.ArenaType.SLIMEMANUAL, new ArenaSettings("shipwrecker", "normal", "sw_default", 1, 4, null));
        for (Player p : Bukkit.getOnlinePlayers()) {
            ArenaManager.refreshPlayer(p, GameMode.ADVENTURE, true, true);
            arena.addPlayerManual(p, true, true);
        }
        return true;
    }
}
