/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer.util;

import net.minecraft.world.biome.layer.util.CoordinateTransformer;

public interface IdentityCoordinateTransformer
extends CoordinateTransformer {
    @Override
    default public int transformX(int x) {
        return x;
    }

    @Override
    default public int transformZ(int y) {
        return y;
    }
}

