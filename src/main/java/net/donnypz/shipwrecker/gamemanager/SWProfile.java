package net.donnypz.shipwrecker.gamemanager;

import net.donnypz.mccore.cosmetics.CosmeticRegistry;
import net.donnypz.mccore.cosmetics.KillEffect;
import net.donnypz.mccore.cosmetics.ProjectileTrail;
import net.donnypz.mccore.minigame.arenaManager.ArenaContainer;
import net.donnypz.mccore.minigame.arenaManager.MinigamePlayerProfile;
import net.donnypz.mccore.utils.ItemUtils;
import net.donnypz.mccore.utils.ui.scoreboard.PlayerScoreboard;
import net.donnypz.playerdbutils.database.DatabaseUpdater;
import net.donnypz.playerdbutils.database.MongoUtils;
import net.donnypz.playerdbutils.database.PlayerDatabaseUpdater;
import net.donnypz.shipwrecker.Shipwrecker;
import net.donnypz.shipwrecker.ui.scoreboard.ActiveScoreboard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class SWProfile extends MinigamePlayerProfile {
    private int meleeKills;
    private int projectileKills;
    int gold;
    PlayerScoreboard sb;

    //Cosmetics
    KillEffect killEffect;
    ProjectileTrail projectileTrail;

    public SWProfile(OfflinePlayer player, ArenaContainer arenaContainer) {
        super(player, arenaContainer);
        sb = ActiveScoreboard.create((Player) player);
        sb.display();
        giveEquipment();

        //Get selected cosmetics from database
        Document playerDocument = MongoUtils.getPlayerDocument(Shipwrecker.getInstance().getPlayersCollection(), player.getUniqueId());
        setCosmeticsData(playerDocument);
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

    private void giveEquipment(){
        Player p = (Player) player;
        EntityEquipment equipment = p.getEquipment();
        equipment.setHelmet(new ItemStack(Material.LEATHER_HELMET));
        equipment.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        equipment.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        equipment.setBoots(new ItemStack(Material.LEATHER_BOOTS));
        equipment.setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));
        ItemStack i = ItemUtils.makeItem(Material.ZOMBIE_SPAWN_EGG);
        i.setAmount(6);
        p.getInventory().addItem(i);
    }

    public KillEffect getKillEffect() {
        return killEffect;
    }

    public ProjectileTrail getProjectileTrail() {
        return projectileTrail;
    }

    public void addMeleeKill(){
        addKill();
        meleeKills++;
    }

    public void addProjectileKill(){
        addKill();
        projectileKills++;
    }

    public void addGold(int gold, boolean alert){
        this.gold+=gold;
        if (alert){
            Player p = player.getPlayer();
            if (p == null){
                return;
            }
            p.sendMessage(Component.text("+"+gold+" Gold", NamedTextColor.GOLD));
            p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
        }
        updateSBGold();
    }

    public void removeGold(int gold, boolean alert){
        this.gold-=gold;
        if (alert){
            Player p = player.getPlayer();
            if (p == null){
                return;
            }
            p.sendMessage(Component.text("-"+gold+" Gold", NamedTextColor.RED));
        }
        updateSBGold();
    }

    private void updateSBGold(){
        sb.updateValue("gold", Component.text(gold, NamedTextColor.YELLOW));
    }

    //Set Cosmetics Data from Player's Database document


    @Override
    public void updatePlayerData() {
        DatabaseUpdater updater = new PlayerDatabaseUpdater(Shipwrecker.getInstance().getPlayersCollection(), player.getUniqueId())
                .incrementValue("kills_melee", meleeKills)
                .incrementValue("deaths", deaths)
                .incrementValue("matches_played", 1)
                .incrementValue(Shipwrecker.COINS, getCurrencyEarned());

        if (((SWArena) arenaContainer).isVictory){
            updater.incrementValue("matches_won", 1);
        }
        updater.update();
    }

    //Calculate coins to give to player
    @Override
    public int getCurrencyEarned() {
        return 0;
    }
}
