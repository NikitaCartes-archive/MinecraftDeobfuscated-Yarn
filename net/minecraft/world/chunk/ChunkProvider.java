/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import org.jetbrains.annotations.Nullable;

public interface ChunkProvider {
    @Nullable
    public BlockView getChunk(int var1, int var2);

    default public void onLightUpdate(LightType type, ChunkSectionPos chunkSectionPos) {
    }

    public BlockView getWorld();
}

