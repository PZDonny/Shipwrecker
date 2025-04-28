package net.donnypz.shipwrecker.gamemanager.enemy;

import net.donnypz.shipwrecker.Shipwrecker;
import net.donnypz.shipwrecker.gamemanager.SWArena;
import org.bukkit.Location;
import org.bukkit.World;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PointHolder {

    private static List<Point> falling;
    private static List<Point> rise;
    private static List<Point> laser;
    private static final Random random = new Random();

    private PointHolder(){}

    static{
        try(InputStream stream = Shipwrecker.getInstance().getResource("points.yml")){
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(stream);
            falling = getPoints((List<Map<String, Object>>) data.get("falling"));
            rise = getPoints((List<Map<String, Object>>) data.get("rise"));
            laser = getPoints((List<Map<String, Object>>) data.get("laser"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Point> getPoints(List<Map<String, Object>> pointList){
        List<Point> points = new ArrayList<>();
        for (Map<String, Object> p : pointList){
            double x = (double) p.get("x");
            double y = (double) p.get("y");
            double z = (double) p.get("z");
            double yaw = (double) p.getOrDefault("yaw", 0.0);
            points.add(new Point(x, y, z, yaw));
        }
        return points;
    }

    public static Location getRandomFallingPoint(SWArena swArena){
        return getRandomPoint(swArena, falling);
    }

    public static Location getRandomRisePoint(SWArena swArena){
        return getRandomPoint(swArena, rise);
    }

    public static Location getRandomLaserPoint(SWArena swArena){
        return getRandomPoint(swArena, laser);
    }

    private static Location getRandomPoint(SWArena swArena, List<Point> points){
        return points
                .get(random.nextInt(points.size()))
                .toLocation(swArena.getArena().getArenaAsBukkitWorld());
    }

    static class Point{
        double x;
        double y;
        double z;
        double yaw;

        Point(double x, double y, double z, double yaw){
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
        }

        Location toLocation(World world){
            return new Location(world, x, y, z, (float) yaw, 0);
        }
    }
}
