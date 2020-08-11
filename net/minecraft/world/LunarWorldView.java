/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionType;

public interface LunarWorldView
extends WorldView {
    public long getLunarTime();

    /**
     * Gets the current size of the moon in the world.
     * 
     * @return the size of the moon on a scale of {@code 0.0F} to {@code 1.0F}
     */
    default public float getMoonSize() {
        return DimensionType.MOON_SIZES[this.getDimension().getMoonPhase(this.getLunarTime())];
    }

    default public float getSkyAngle(float tickDelta) {
        return this.getDimension().getSkyAngle(this.getLunarTime());
    }

    /**
     * Gets the moon phase index of Minecraft's moon.
     * 
     * <p>This is typically used to determine the size of the moon that should be rendered.
     */
    @Environment(value=EnvType.CLIENT)
    default public int getMoonPhase() {
        return this.getDimension().getMoonPhase(this.getLunarTime());
    }
}

