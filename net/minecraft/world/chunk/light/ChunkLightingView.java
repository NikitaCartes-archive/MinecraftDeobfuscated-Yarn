/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk.light;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.light.LightingView;
import org.jetbrains.annotations.Nullable;

public interface ChunkLightingView
extends LightingView {
    @Nullable
    public ChunkNibbleArray getLightArray(ChunkSectionPos var1);

    public int getLightLevel(BlockPos var1);

    public static enum Empty implements ChunkLightingView
    {
        INSTANCE;


        @Override
        @Nullable
        public ChunkNibbleArray getLightArray(ChunkSectionPos pos) {
            return null;
        }

        @Override
        public int getLightLevel(BlockPos blockPos) {
            return 0;
        }

        @Override
        public void updateSectionStatus(ChunkSectionPos pos, boolean status) {
        }
    }
}

