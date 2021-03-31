/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.surfacebuilder;

import net.minecraft.block.BlockState;

public interface SurfaceConfig {
    public BlockState getTopMaterial();

    public BlockState getUnderMaterial();

    public BlockState getUnderwaterMaterial();
}

