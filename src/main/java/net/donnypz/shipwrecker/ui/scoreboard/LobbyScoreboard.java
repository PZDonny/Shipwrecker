package net.donnypz.shipwrecker.ui.scoreboard;

import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.donnypz.mccore.database.PlayerData;
import net.donnypz.mccore.minigame.arena.ArenaManager;
import net.donnypz.mccore.utils.ui.scoreboard.PlayerScoreboard;
import net.donnypz.mccore.utils.ui.scoreboard.ScoreboardUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LobbyScoreboard {

    private static final PotionEffect SATURATION = new PotionEffect(PotionEffectType.SATURATION, PotionEffect.INFINITE_DURATION, 255);

    public static void create(Player player){
        ArenaManager.refreshPlayer(player, GameMode.ADVENTURE, true, true);
        player.addPotionEffect(SATURATION);
        PlayerData data = PlayerData.get(player);
        Document document = data.getDocument();

        new PlayerScoreboard(player, Component.text("sʜɪᴘᴡʀᴇᴄᴋᴇʀ", NamedTextColor.AQUA, TextDecoration.BOLD))
                .setTeamColor(NamedTextColor.GREEN)
                .setNumberFormat(NumberFormat.blank())
                .setPermanentValue(15, "Melee Kills: "+ChatColor.YELLOW+document.getInteger("kills_melee"))
                .setPermanentValue(14, "Projectile Kills: "+ChatColor.YELLOW+document.getInteger("kills_projectile"))
                .setPermanentValue(13, "Deaths: "+ChatColor.YELLOW+document.getInteger("deaths"))
                .setPermanentValue(12, ChatColor.WHITE+"")
                .setUpdatingValue(11, "coins", "Coins: ", Component.text(document.getInteger("coins"), NamedTextColor.YELLOW))
                .setPermanentValue(10, "Matches Played: "+ChatColor.YELLOW+document.getInteger("matches_played"))
                .setPermanentValue(9, "Matches Won: "+ChatColor.YELLOW+document.getInteger("matches_won"))
                .display();
    }

    public static void updateCoins(Player player, int coins){
        PlayerScoreboard sb = ScoreboardUtils.getPlayerScoreboard(player);
        if (sb != null){
            sb.updateValue("coins", Component.text(coins, NamedTextColor.YELLOW));
        }
    }
}
