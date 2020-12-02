/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk.light;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.HeightLimitView;
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
    protected final HeightLimitView field_27339;
    @Nullable
    private final ChunkLightProvider<?, ?> blockLightProvider;
    @Nullable
    private final ChunkLightProvider<?, ?> skyLightProvider;

    public LightingProvider(ChunkProvider chunkProvider, boolean hasBlockLight, boolean hasSkyLight) {
        this.field_27339 = chunkProvider.getWorld();
        this.blockLightProvider = hasBlockLight ? new ChunkBlockLightProvider(chunkProvider) : null;
        this.skyLightProvider = hasSkyLight ? new ChunkSkyLightProvider(chunkProvider) : null;
    }

    public void checkBlock(BlockPos pos) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.checkBlock(pos);
        }
        if (this.skyLightProvider != null) {
            this.skyLightProvider.checkBlock(pos);
        }
    }

    public void addLightSource(BlockPos pos, int level) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.addLightSource(pos, level);
        }
    }

    public boolean hasUpdates() {
        if (this.skyLightProvider != null && this.skyLightProvider.hasUpdates()) {
            return true;
        }
        return this.blockLightProvider != null && this.blockLightProvider.hasUpdates();
    }

    public int doLightUpdates(int maxUpdateCount, boolean doSkylight, boolean skipEdgeLightPropagation) {
        if (this.blockLightProvider != null && this.skyLightProvider != null) {
            int i = maxUpdateCount / 2;
            int j = this.blockLightProvider.doLightUpdates(i, doSkylight, skipEdgeLightPropagation);
            int k = maxUpdateCount - i + j;
            int l = this.skyLightProvider.doLightUpdates(k, doSkylight, skipEdgeLightPropagation);
            if (j == 0 && l > 0) {
                return this.blockLightProvider.doLightUpdates(l, doSkylight, skipEdgeLightPropagation);
            }
            return l;
        }
        if (this.blockLightProvider != null) {
            return this.blockLightProvider.doLightUpdates(maxUpdateCount, doSkylight, skipEdgeLightPropagation);
        }
        if (this.skyLightProvider != null) {
            return this.skyLightProvider.doLightUpdates(maxUpdateCount, doSkylight, skipEdgeLightPropagation);
        }
        return maxUpdateCount;
    }

    @Override
    public void setSectionStatus(ChunkSectionPos pos, boolean notReady) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.setSectionStatus(pos, notReady);
        }
        if (this.skyLightProvider != null) {
            this.skyLightProvider.setSectionStatus(pos, notReady);
        }
    }

    public void setColumnEnabled(ChunkPos pos, boolean lightEnabled) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.setColumnEnabled(pos, lightEnabled);
        }
        if (this.skyLightProvider != null) {
            this.skyLightProvider.setColumnEnabled(pos, lightEnabled);
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
    public String displaySectionLevel(LightType lightType, ChunkSectionPos chunkSectionPos) {
        if (lightType == LightType.BLOCK) {
            if (this.blockLightProvider != null) {
                return this.blockLightProvider.displaySectionLevel(chunkSectionPos.asLong());
            }
        } else if (this.skyLightProvider != null) {
            return this.skyLightProvider.displaySectionLevel(chunkSectionPos.asLong());
        }
        return "n/a";
    }

    public void enqueueSectionData(LightType lightType, ChunkSectionPos pos, @Nullable ChunkNibbleArray nibbles, boolean bl) {
        if (lightType == LightType.BLOCK) {
            if (this.blockLightProvider != null) {
                this.blockLightProvider.enqueueSectionData(pos.asLong(), nibbles, bl);
            }
        } else if (this.skyLightProvider != null) {
            this.skyLightProvider.enqueueSectionData(pos.asLong(), nibbles, bl);
        }
    }

    public void setRetainData(ChunkPos pos, boolean retainData) {
        if (this.blockLightProvider != null) {
            this.blockLightProvider.setRetainColumn(pos, retainData);
        }
        if (this.skyLightProvider != null) {
            this.skyLightProvider.setRetainColumn(pos, retainData);
        }
    }

    public int getLight(BlockPos pos, int ambientDarkness) {
        int i = this.skyLightProvider == null ? 0 : this.skyLightProvider.getLightLevel(pos) - ambientDarkness;
        int j = this.blockLightProvider == null ? 0 : this.blockLightProvider.getLightLevel(pos);
        return Math.max(j, i);
    }

    public int method_31928() {
        return this.field_27339.method_32890() + 2;
    }

    public int method_31929() {
        return this.field_27339.method_32891() - 1;
    }

    public int method_31930() {
        return this.method_31929() + this.method_31928();
    }
}

