package net.donnypz.shipwrecker.ui.scoreboard;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.donnypz.mccore.utils.ui.scoreboard.PlayerScoreboard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ActiveScoreboard {

    public static PlayerScoreboard create(Player player){
        return new PlayerScoreboard(player, Component.text("sʜɪᴘᴡʀᴇᴄᴋᴇʀ", NamedTextColor.AQUA, TextDecoration.BOLD))
                .setTeamColor(NamedTextColor.GREEN)
                .setNumberFormat(NumberFormat.blank())
                .setUpdatingValue(15, PlayerScoreboard.UpdatingValue.PROFILE_KILLS, "Kills: ")
                .setUpdatingValue(14, PlayerScoreboard.UpdatingValue.PROFILE_DEATHS, "Deaths: ")
                .setPermanentValue(13, ChatColor.WHITE+"")
                .setUpdatingValue(12, "gold", "Gold: ", Component.text("0", NamedTextColor.YELLOW));
    }
}
