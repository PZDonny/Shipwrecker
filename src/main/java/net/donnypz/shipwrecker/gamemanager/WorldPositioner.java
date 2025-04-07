package net.donnypz.shipwrecker.gamemanager;

import net.donnypz.displayentityutils.utils.Direction;
import net.donnypz.displayentityutils.utils.DisplayUtils;
import net.donnypz.shipwrecker.Shipwrecker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.Random;

public class WorldPositioner {

    SWArena swArena;
    Location environmentOrigin;
    Location shipOrigin;
    Location leftWallStart;
    Location rightWallStart;
    Location ceilingStart;
    Location floorStart;

    ItemStack ceilingItem = new ItemStack(Material.DARK_PRISMARINE);
    ItemStack floorItem = new ItemStack(Material.SAND);
    ItemStack wallItem = new ItemStack(Material.DARK_PRISMARINE);

    static final float pickupOffset = 15;
    static final int sections = 5;
    static final float translationDistance = 180;
    static final long timeBetweenWalls = 60;
    static final long translationTime = timeBetweenWalls*sections;


    static final float height = 60;
    static final float width = 100; //leftWallStartOffset+rightWallOffset
    static final float fishScale = 2.25f;

    static final Transformation wallTransformation = new Transformation(
            new Vector3f(0, 0,0),
            new Quaternionf(),
            new Vector3f(translationDistance/sections, height, 1),
            new Quaternionf());


    static final Transformation coverTransformation = new Transformation(
            new Vector3f(0, 0,0),
            new Quaternionf(),
            new Vector3f(translationDistance/sections, 1f, width),
            new Quaternionf());

    static final Transformation frontWallTransformation = new Transformation(
            new Vector3f(0, 0,0),
            new Quaternionf(),
            new Vector3f(1, height, width),
            new Quaternionf());


    static final ItemStack[] fish = new ItemStack[]{new ItemStack(Material.COD), new ItemStack(Material.SALMON), new ItemStack(Material.TROPICAL_FISH), new ItemStack(Material.PUFFERFISH)};

    static final Random random = new Random();

    final HashSet<Display> activeDisplays = new HashSet<>();

    WorldPositioner(SWArena swArena){
        this.swArena = swArena;
        World w  = swArena.getArena().getArenaAsBukkitWorld();
        environmentOrigin = new Location(w, 60, 65, 1);
        shipOrigin = new Location(w, 0, 65, 0);

        leftWallStart = environmentOrigin.clone();
        leftWallStart.setZ(-50);

        rightWallStart = environmentOrigin.clone();
        rightWallStart.setZ(51);

        ceilingStart = environmentOrigin.clone();
        ceilingStart.setY(ceilingStart.y()+(height /2));

        floorStart = environmentOrigin.clone();
        floorStart.setY(floorStart.y()-(height/2));
    }

    void spawnFrontWall(){
        Location fwPoint = environmentOrigin.clone();
        fwPoint.setX(45);
        fwPoint.getWorld().spawn(fwPoint, ItemDisplay.class, d -> {
            d.setItemStack(new ItemStack(Material.BLACK_CONCRETE));
            d.setTransformation(frontWallTransformation);
        });
    }

    void decorateBorders(){
        final int shipSpeed = swArena.shipSpeed;
        new BukkitRunnable(){
            public void run(){
                if (!swArena.getArena().isUsable()){
                    cancel();
                    return;
                }

                if (shipSpeed != swArena.shipSpeed){
                    decorateBorders();
                    cancel();
                    return;
                }

                spawnWall(leftWallStart);
                spawnWall(rightWallStart);
                spawnCovers();
            }
        }.runTaskTimer(Shipwrecker.getInstance(), 0, 60/swArena.shipSpeed);
    }

    void decorateExtra(){
        if (swArena.getArena().isEndingOrNotUsable()){
            return;
        }

        int fishCount = random.nextInt(5, 11)+1;
        World w = environmentOrigin.getWorld();
        for (int i = 0; i < fishCount; i++){
            int xOffset = random.nextInt(4);
            int yOffset = random.nextInt(-15, 15);
            int zOffset = random.nextInt(-35, 36);
            Location spawnLoc = environmentOrigin.clone();
            spawnLoc.setX(spawnLoc.x()+xOffset);
            spawnLoc.setY(spawnLoc.y()+yOffset);
            spawnLoc.setZ(spawnLoc.z()+zOffset);

            ItemDisplay display = w.spawn(spawnLoc, ItemDisplay.class, d -> {
                d.setItemStack(fish[random.nextInt(fish.length)]);
                float xRot = (float) Math.toRadians(random.nextInt(360));
                float yRot = (float) Math.toRadians(random.nextInt(360));
                float zRot = (float) Math.toRadians(random.nextInt(360));
                d.setTransformation(new Transformation(new Vector3f(), new Quaternionf()
                        .rotationXYZ(xRot, yRot, zRot), new Vector3f(fishScale), new Quaternionf()));
            });

            applyData(display, true, true);
        }
        new BukkitRunnable(){
            public void run(){
                decorateExtra();
            }
        }.runTaskLater(Shipwrecker.getInstance(), (long) random.nextInt(2, 5)*20);
    }

    void spawnPickups(){

    }

    void spawnWall(Location loc){
        ItemDisplay wall = loc.getWorld().spawn(loc, ItemDisplay.class, d -> {
            d.setTransformation(wallTransformation);
            d.setItemStack(wallItem);
        });

        applyData(wall, true, true);
    }

    void spawnCovers(){
        World w = floorStart.getWorld();
        ItemDisplay ceiling = w.spawn(ceilingStart, ItemDisplay.class, d -> {
            d.setTransformation(coverTransformation);
            d.setItemStack(ceilingItem);
        });

        ItemDisplay floor = w.spawn(floorStart, ItemDisplay.class, d -> {
            d.setTransformation(coverTransformation);
            d.setItemStack(floorItem);
            d.setBrightness(new Display.Brightness(7, 7));
        });

        applyData(ceiling, true, true);
        applyData(floor, true, false);
    }

    private void applyData(Display display, boolean despawnDelayed, boolean setBrightness){
        display.setViewRange(2);
        Bukkit.getScheduler().runTaskLater(Shipwrecker.getInstance(), () ->{
            DisplayUtils.translate(display, Direction.WEST, translationDistance, (int) translationTime/swArena.shipSpeed, 0);
        }, 2);
        activeDisplays.add(display);
        if (despawnDelayed){
            Bukkit.getScheduler().runTaskLater(Shipwrecker.getInstance(), () -> {
                display.remove();
                activeDisplays.remove(display);
            }, WorldPositioner.translationTime/swArena.shipSpeed);
        }
        if (setBrightness){
            display.setBrightness(new Display.Brightness(15, 15));
        }
    }

    void updateActiveDisplaySpeed(int speed){
        for (Display display : activeDisplays){
            Transformation t = display.getTransformation();
            display.setInterpolationDuration((int) (translationTime/speed));
            display.setTransformation(t);
        }
    }

    public void endGameCoverItems(){
        ceilingItem = new ItemStack(Material.QUARTZ_BRICKS);
        wallItem = new ItemStack(Material.QUARTZ_PILLAR);
        floorItem = ceilingItem;
    }
}
