package net.donnypz.shipwrecker.cosmetics.killeffects;

import net.donnypz.mccore.cosmetics.CosmeticRegistry;

public class _KillEffectRegistry extends CosmeticRegistry {

    @Override
    protected void registerCosmetics() {
        new ColorBomb(this);
        new Blade(this);
        new Explosion(this);
        new Oceanic(this);
        new Underworld(this);
    }
}
