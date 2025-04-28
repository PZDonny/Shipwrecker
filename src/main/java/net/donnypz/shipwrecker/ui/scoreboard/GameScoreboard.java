package net.donnypz.shipwrecker.ui.scoreboard;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.donnypz.mccore.utils.ui.scoreboard.PlayerScoreboard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class GameScoreboard {

    static Component zero = Component.text("0", NamedTextColor.YELLOW);
    public static PlayerScoreboard create(Player player){
        PlayerScoreboard sb = new PlayerScoreboard(player, Component.text("sʜɪᴘᴡʀᴇᴄᴋᴇʀ", NamedTextColor.AQUA, TextDecoration.BOLD))
                .setTeamColor(NamedTextColor.GREEN)
                .setNumberFormat(NumberFormat.blank())
                .setUpdatingValue(15, PlayerScoreboard.UpdatingValue.PROFILE_KILLS, "Kills: ", zero)
                .setUpdatingValue(14, "lives", "Lives: ", Component.text("3", NamedTextColor.YELLOW))
                .setPermanentValue(13, ChatColor.WHITE+"")
                .setUpdatingValue(12, "gold", "Gold: ", zero)
                .setUpdatingValue(11, "crates", "Crates Collected: ", zero);

        sb.createOtherTeam("enemy").getOtherTeam("enemy").color(NamedTextColor.RED);

        return sb;
    }
}
