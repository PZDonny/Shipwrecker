package net.donnypz.shipwrecker.cosmetics.projectiletrails;

import net.donnypz.mccore.cosmetics.CosmeticRegistry;

public class _ProjectileTrailRegistry extends CosmeticRegistry {

    @Override
    protected void registerCosmetics() {
        new Hearty(this);
        new Wormhole(this);
        new Sparked(this);
    }
}
