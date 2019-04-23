/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk.light;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.light.ChunkBlockLightProvider;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.chunk.light.ChunkLightingView;
import net.minecraft.world.chunk.light.ChunkSkyLightProvider;
import net.minecraft.world.chunk.light.LightingView;
import org.jetbrains.annotations.Nullable;

public class LightingProvider
implements LightingView {
    @Nullable
    private final ChunkLightProvider<?, ?> blockLightProvider;
    @Nullable
    private final ChunkLightProvider<?, ?> skyLightProvider;

    public LightingProvider(ChunkProvider chunkProvider, boolean bl, boolean bl2) {
        this.blockLightProvider = bl ? new ChunkBlockLightProvider(chunkProvider) : null;
        this.skyLightProvider = bl2 ? new ChunkSkyLightProvider(chunkProvider) : null;
    }

    public void enqueueLightUpdate(BlockPos blockPos) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.queueLightCheck(blockPos);
        }
        if (this.skyLightProvider != null) {
            this.skyLightProvider.queueLightCheck(blockPos);
        }
    }

    public void method_15560(BlockPos blockPos, int i) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.method_15514(blockPos, i);
        }
    }

    public boolean hasUpdates() {
        if (this.skyLightProvider != null && this.skyLightProvider.hasUpdates()) {
            return true;
        }
        return this.blockLightProvider != null && this.blockLightProvider.hasUpdates();
    }

    public int doLightUpdates(int i, boolean bl, boolean bl2) {
        if (this.blockLightProvider != null && this.skyLightProvider != null) {
            int j = i / 2;
            int k = this.blockLightProvider.doLightUpdates(j, bl, bl2);
            int l = i - j + k;
            int m = this.skyLightProvider.doLightUpdates(l, bl, bl2);
            if (k == 0 && m > 0) {
                return this.blockLightProvider.doLightUpdates(m, bl, bl2);
            }
            return m;
        }
        if (this.blockLightProvider != null) {
            return this.blockLightProvider.doLightUpdates(i, bl, bl2);
        }
        if (this.skyLightProvider != null) {
            return this.skyLightProvider.doLightUpdates(i, bl, bl2);
        }
        return i;
    }

    @Override
    public void updateSectionStatus(ChunkSectionPos chunkSectionPos, boolean bl) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.updateSectionStatus(chunkSectionPos, bl);
        }
        if (this.skyLightProvider != null) {
            this.skyLightProvider.updateSectionStatus(chunkSectionPos, bl);
        }
    }

    public void suppressLight(ChunkPos chunkPos, boolean bl) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.method_15512(chunkPos, bl);
        }
        if (this.skyLightProvider != null) {
            this.skyLightProvider.method_15512(chunkPos, bl);
        }
    }

    public ChunkLightingView get(LightType lightType) {
        if (lightType == LightType.BLOCK) {
            if (this.blockLightProvider == null) {
                return ChunkLightingView.Empty.INSTANCE;
            }
            return this.blockLightProvider;
        }
        if (this.skyLightProvider == null) {
            return ChunkLightingView.Empty.INSTANCE;
        }
        return this.skyLightProvider;
    }

    @Environment(value=EnvType.CLIENT)
    public String method_15564(LightType lightType, ChunkSectionPos chunkSectionPos) {
        if (lightType == LightType.BLOCK) {
            if (this.blockLightProvider != null) {
                return this.blockLightProvider.method_15520(chunkSectionPos.asLong());
            }
        } else if (this.skyLightProvider != null) {
            return this.skyLightProvider.method_15520(chunkSectionPos.asLong());
        }
        return "n/a";
    }

    public void queueData(LightType lightType, ChunkSectionPos chunkSectionPos, ChunkNibbleArray chunkNibbleArray) {
        if (lightType == LightType.BLOCK) {
            if (this.blockLightProvider != null) {
                this.blockLightProvider.setSection(chunkSectionPos.asLong(), chunkNibbleArray);
            }
        } else if (this.skyLightProvider != null) {
            this.skyLightProvider.setSection(chunkSectionPos.asLong(), chunkNibbleArray);
        }
    }
}

