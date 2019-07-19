/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.StructureHolder;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public interface Chunk
extends StructureHolder {
    @Nullable
    public BlockState setBlockState(BlockPos var1, BlockState var2, boolean var3);

    public void setBlockEntity(BlockPos var1, BlockEntity var2);

    public void addEntity(Entity var1);

    @Nullable
    default public ChunkSection getHighestNonEmptySection() {
        ChunkSection[] chunkSections = this.getSectionArray();
        for (int i = chunkSections.length - 1; i >= 0; --i) {
            ChunkSection chunkSection = chunkSections[i];
            if (ChunkSection.isEmpty(chunkSection)) continue;
            return chunkSection;
        }
        return null;
    }

    default public int getHighestNonEmptySectionYOffset() {
        ChunkSection chunkSection = this.getHighestNonEmptySection();
        return chunkSection == null ? 0 : chunkSection.getYOffset();
    }

    public Set<BlockPos> getBlockEntityPositions();

    public ChunkSection[] getSectionArray();

    @Nullable
    public LightingProvider getLightingProvider();

    default public int getLightLevel(BlockPos blockPos, int i, boolean bl) {
        LightingProvider lightingProvider = this.getLightingProvider();
        if (lightingProvider == null || !this.getStatus().isAtLeast(ChunkStatus.LIGHT)) {
            return 0;
        }
        int j = bl ? lightingProvider.get(LightType.SKY).getLightLevel(blockPos) - i : 0;
        int k = lightingProvider.get(LightType.BLOCK).getLightLevel(blockPos);
        return Math.max(k, j);
    }

    public Collection<Map.Entry<Heightmap.Type, Heightmap>> getHeightmaps();

    public void setHeightmap(Heightmap.Type var1, long[] var2);

    public Heightmap getHeightmap(Heightmap.Type var1);

    public int sampleHeightmap(Heightmap.Type var1, int var2, int var3);

    public ChunkPos getPos();

    public void setLastSaveTime(long var1);

    public Map<String, StructureStart> getStructureStarts();

    public void setStructureStarts(Map<String, StructureStart> var1);

    default public Biome getBiome(BlockPos blockPos) {
        int i = blockPos.getX() & 0xF;
        int j = blockPos.getZ() & 0xF;
        return this.getBiomeArray()[j << 4 | i];
    }

    default public boolean method_12228(int i, int j) {
        if (i < 0) {
            i = 0;
        }
        if (j >= 256) {
            j = 255;
        }
        for (int k = i; k <= j; k += 16) {
            if (ChunkSection.isEmpty(this.getSectionArray()[k >> 4])) continue;
            return false;
        }
        return true;
    }

    public Biome[] getBiomeArray();

    public void setShouldSave(boolean var1);

    public boolean needsSaving();

    public ChunkStatus getStatus();

    public void removeBlockEntity(BlockPos var1);

    public void setLightingProvider(LightingProvider var1);

    default public void markBlockForPostProcessing(BlockPos blockPos) {
        LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", (Object)blockPos);
    }

    public ShortList[] getPostProcessingLists();

    default public void markBlockForPostProcessing(short s, int i) {
        Chunk.getList(this.getPostProcessingLists(), i).add(s);
    }

    default public void addPendingBlockEntityTag(CompoundTag compoundTag) {
        LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
    }

    @Nullable
    public CompoundTag getBlockEntityTagAt(BlockPos var1);

    @Nullable
    public CompoundTag method_20598(BlockPos var1);

    default public void setBiomeArray(Biome[] biomes) {
        throw new UnsupportedOperationException();
    }

    public Stream<BlockPos> getLightSourcesStream();

    public TickScheduler<Block> getBlockTickScheduler();

    public TickScheduler<Fluid> getFluidTickScheduler();

    default public BitSet getCarvingMask(GenerationStep.Carver carver) {
        throw new RuntimeException("Meaningless in this context");
    }

    public UpgradeData getUpgradeData();

    public void setInhabitedTime(long var1);

    public long getInhabitedTime();

    public static ShortList getList(ShortList[] shortLists, int i) {
        if (shortLists[i] == null) {
            shortLists[i] = new ShortArrayList();
        }
        return shortLists[i];
    }

    public boolean isLightOn();

    public void setLightOn(boolean var1);
}

