package net.donnypz.shipwrecker.gamemanager;

import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.donnypz.mccore.cosmetics.preset.basic.KillEffect;
import net.donnypz.mccore.cosmetics.preset.basic.ProjectileTrail;
import net.donnypz.mccore.database.PlayerData;
import net.donnypz.mccore.minigame.arena.Arena;
import net.donnypz.mccore.minigame.arena.ArenaContainer;
import net.donnypz.mccore.minigame.arena.MinigamePlayerProfile;
import net.donnypz.mccore.utils.item.ItemBuilder;
import net.donnypz.mccore.utils.ui.actionbar.ActionBarUtils;
import net.donnypz.mccore.utils.ui.scoreboard.PlayerScoreboard;
import net.donnypz.shipwrecker.Shipwrecker;
import net.donnypz.shipwrecker.database.DataManager;
import net.donnypz.shipwrecker.gamemanager.upgrade.ArmorUpgrade;
import net.donnypz.shipwrecker.gamemanager.upgrade.WeaponUpgrade;
import net.donnypz.shipwrecker.ui.scoreboard.GameScoreboard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bson.Document;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;

public class SWProfile extends MinigamePlayerProfile {
    private int meleeKills;
    private int projectileKills;
    private int cratesCollected;
    private int gold;
    private int lives = 3;
    private int totalGoldCollected;
    private PlayerScoreboard sb;

    private final WeaponUpgrade weaponUpgrade;
    private final ArmorUpgrade armorUpgrade;

    //Cosmetics
    private KillEffect killEffect;
    private ProjectileTrail projectileTrail;

    private final int COINS_PER_KILL = 10;
    private final int COINS_PER_CRATE = 25;
    private final int VICTORY_COINS = 100;
    private final double GOLD_TO_COINS_PERCENTAGE = 0.07;

    private final int GOLD_PER_KILL = 25;
    private final int GOLD_PER_SECOND = 5;

    public SWProfile(OfflinePlayer player, ArenaContainer arenaContainer) {
        super(player, arenaContainer);
        Player p = (Player) player;
        sb = GameScoreboard.create(p);
        sb.display();
        weaponUpgrade = new WeaponUpgrade(this, p);
        armorUpgrade = new ArmorUpgrade(this, p);
        giveNonUpgradable();

        //Get selected cosmetics from cached player data
        Document document = PlayerData.get(player.getUniqueId()).getDocument();
        setCosmeticsData(document);
    }

    @Override
    protected void setCosmeticsData(Document document) {
        Shipwrecker shipwrecker = Shipwrecker.getInstance();
        CosmeticRegistry killEffectRegistry = shipwrecker.getKillEffectRegistry();
        CosmeticRegistry projectileRegistry = shipwrecker.getProjectileTrailRegistry();

        String selectedKillEffect = (String) Shipwrecker.getSelectedCosmetic(document, "kill_effect");
        String selectedProjectileTrail = (String) Shipwrecker.getSelectedCosmetic(document,"projectile_trail");
        killEffect = killEffectRegistry.getCosmetic(selectedKillEffect, KillEffect.class);
        projectileTrail = projectileRegistry.getCosmetic(selectedProjectileTrail, ProjectileTrail.class);
    }

    @Override
    public void applyCosmetics() {}

    public KillEffect getKillEffect() {
        return killEffect;
    }

    public ProjectileTrail getProjectileTrail() {
        return projectileTrail;
    }

    void goldPerSecond(){
        new BukkitRunnable(){
            final Arena arena = arenaContainer.getArena();
            @Override
            public void run() {
                if (arena.isEndingOrNotUsable()){
                    cancel();
                    return;
                }
                addGold(GOLD_PER_SECOND, false, false);
            }
        }.runTaskTimer(Shipwrecker.getInstance(), 0, 20);
    }


    @Override
    public void addDeath(){
        if (arenaContainer.getArena().isEndingOrNotUsable()){
            return;
        }
        super.addDeath();
        Arena arena = arenaContainer.getArena();
        lives--;
        sb.updateValue("lives", Component.text(lives, NamedTextColor.YELLOW));
        if (lives == 0){
            for (Player p : arena.getArenaPlayers()){
                if (p.getGameMode() != GameMode.SPECTATOR){ //Prevent game from ending if a player is alive
                    return;
                }
            }
            ((SWArena) arenaContainer).endGame();
        }
    }

    @Override
    public void addKill(){
        if (arenaContainer.getArena().isEndingOrNotUsable()){
            return;
        }
        super.addKill();
        addGold(GOLD_PER_KILL, true, false);
    }

    public void addMeleeKill(){
        addKill();
        meleeKills++;
    }

    public void addProjectileKill(){
        addKill();
        projectileKills++;
    }

    public void addCrateCollected(){
        cratesCollected++;
        sb.updateValue("crates", Component.text(cratesCollected, NamedTextColor.YELLOW));
    }

    public int getMeleeKills() {
        return meleeKills;
    }

    public int getProjectileKills() {
        return projectileKills;
    }

    private void giveNonUpgradable(){
        Player p = (Player) player;
        p.getInventory().addItem(new ItemBuilder(Material.FISHING_ROD)
                        .setUnbreakable(true)
                        .setUndroppable(true)
                .build());
        p.getInventory().addItem(new ItemBuilder(Material.BOW)
                        .addEnchantment(Enchantment.POWER, 1)
                        .addEnchantment(Enchantment.INFINITY, 1)
                        .setUnbreakable(true)
                .build());
        p.getInventory().addItem(new ItemBuilder(Material.ARROW)
                        .setUndroppable(true)
                .build());
    }

    public void addGold(int gold, boolean alert, boolean sound){
        this.gold+=gold;
        this.totalGoldCollected+=gold;
        if (alert){
            Player p = player.getPlayer();
            if (p == null){
                return;
            }

            Title.Times times = Title.Times.times(Duration.ofMillis(150), Duration.ofMillis(500), Duration.ofMillis(150));
            p.showTitle(Title.title(Component.empty(), Component.text("+ "+gold+" ɢᴏʟᴅ", NamedTextColor.GOLD), times));
            p.sendMessage(Component.text("| +"+gold+" Gold", NamedTextColor.GOLD));
            if (sound) p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
        }
        updateSBGold();
    }

    public void removeGold(int gold, boolean visibleAlert, boolean sound){
        this.gold-=gold;
        if (visibleAlert){
            Player p = player.getPlayer();
            if (p == null){
                return;
            }
            p.sendMessage(Component.text("| -"+gold+" Gold", NamedTextColor.RED));
            if (sound){
                p.playSound(p, Sound.ENTITY_VEX_DEATH, 1, 1.5f);
            }
        }
        updateSBGold();
    }

    public int getGold() {
        return gold;
    }

    public int getLives(){
        return lives;
    }

    private void updateSBGold(){
        sb.updateValue("gold", Component.text(gold, NamedTextColor.YELLOW));
    }

    public WeaponUpgrade getWeaponUpgrade() {
        return weaponUpgrade;
    }

    public ArmorUpgrade getArmorUpgrade() {
        return armorUpgrade;
    }

    @Override
    public void updatePlayerData() {
        DataManager.updatePlayerData(this);
        if (player.isOnline()){
            Player p = player.getPlayer();
            sendMatchSummary(p);
        }
    }

    /**
     * Calculate the amount of coins to reward a player with based on their match performance
     * @return the number of coins
     */
    @Override
    public int getCurrencyEarned() {
        int coins = 0;
        coins+= (COINS_PER_KILL * kills);
        coins+= (COINS_PER_CRATE * cratesCollected);
        if (((SWArena) arenaContainer).isVictory){
            coins+= VICTORY_COINS;
        }

        coins+= (int) (GOLD_TO_COINS_PERCENTAGE * totalGoldCollected);
        return coins;
    }

    private void sendMatchSummary(Player player){
        ActionBarUtils.sendActionBarGeneratedBG(player, Component.text("ᴍᴀᴛᴄʜ sᴜᴍᴍᴀʀʏ ɢᴇɴᴇʀᴀᴛᴇᴅ", NamedTextColor.GREEN, TextDecoration.ITALIC), Sound.BLOCK_NOTE_BLOCK_BIT, 0.5f, 1.5f, 40);
        player.sendMessage(Shipwrecker.prefixLong);

        Component victoryStatus =
                ((SWArena) arenaContainer).isVictory ?
                        Component.text("Match Won", NamedTextColor.GREEN)
                        :
                        Component.text("Match Lost", NamedTextColor.RED);
        player.sendMessage(Component.text("Your Match Summary | ", NamedTextColor.AQUA).append(victoryStatus));

        player.sendMessage(Component.text("Melee Kills: ")
                .append(Component.text(meleeKills, NamedTextColor.YELLOW)));

        player.sendMessage(Component.text("Projectile Kills: ")
                .append(Component.text(projectileKills, NamedTextColor.YELLOW)));

        player.sendMessage(Component.text("Deaths: ")
                .append(Component.text(deaths, NamedTextColor.YELLOW)));


        player.sendMessage(Component.text("Crates Collected: ")
                .append(Component.text(cratesCollected, NamedTextColor.YELLOW)));

        player.sendMessage(Component.text("Gold Collected: ")
                .append(Component.text(totalGoldCollected, NamedTextColor.YELLOW))
                .appendNewline());

        int coins = getCurrencyEarned();
        player.sendMessage(Component.text("Coins Earned: ").append(Component.text(coins, NamedTextColor.GOLD)));
        player.sendMessage(Component.text("------------------------------------------", NamedTextColor.GRAY));
    }
}
