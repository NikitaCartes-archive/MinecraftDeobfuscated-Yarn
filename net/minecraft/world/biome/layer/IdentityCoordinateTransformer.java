/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.CoordinateTransformer;

public interface IdentityCoordinateTransformer
extends CoordinateTransformer {
    @Override
    default public int transformX(int i) {
        return i;
    }

    @Override
    default public int transformZ(int i) {
        return i;
    }
}

