/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureHolder;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a scoped, modifiable view of biomes, block states, fluid states and block entities.
 */
public interface Chunk
extends BlockView,
StructureHolder {
    @Nullable
    public BlockState setBlockState(BlockPos var1, BlockState var2, boolean var3);

    public void setBlockEntity(BlockEntity var1);

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
        return chunkSection == null ? this.getBottomHeightLimit() : chunkSection.getYOffset();
    }

    public Set<BlockPos> getBlockEntityPositions();

    public ChunkSection[] getSectionArray();

    public Collection<Map.Entry<Heightmap.Type, Heightmap>> getHeightmaps();

    public void setHeightmap(Heightmap.Type var1, long[] var2);

    public Heightmap getHeightmap(Heightmap.Type var1);

    public int sampleHeightmap(Heightmap.Type var1, int var2, int var3);

    public ChunkPos getPos();

    public Map<StructureFeature<?>, StructureStart<?>> getStructureStarts();

    public void setStructureStarts(Map<StructureFeature<?>, StructureStart<?>> var1);

    default public boolean areSectionsEmptyBetween(int lowerHeight, int upperHeight) {
        if (lowerHeight < this.getBottomHeightLimit()) {
            lowerHeight = this.getBottomHeightLimit();
        }
        if (upperHeight >= this.getTopHeightLimit()) {
            upperHeight = this.getTopHeightLimit() - 1;
        }
        for (int i = lowerHeight; i <= upperHeight; i += 16) {
            if (ChunkSection.isEmpty(this.getSectionArray()[this.getSectionIndex(i)])) continue;
            return false;
        }
        return true;
    }

    @Nullable
    public BiomeArray getBiomeArray();

    public void setShouldSave(boolean var1);

    public boolean needsSaving();

    public ChunkStatus getStatus();

    public void removeBlockEntity(BlockPos var1);

    default public void markBlockForPostProcessing(BlockPos pos) {
        LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", (Object)pos);
    }

    public ShortList[] getPostProcessingLists();

    default public void markBlockForPostProcessing(short s, int i) {
        Chunk.getList(this.getPostProcessingLists(), i).add(s);
    }

    default public void addPendingBlockEntityTag(CompoundTag tag) {
        LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
    }

    @Nullable
    public CompoundTag getBlockEntityTag(BlockPos var1);

    @Nullable
    public CompoundTag getPackedBlockEntityTag(BlockPos var1);

    public Stream<BlockPos> getLightSourcesStream();

    public TickScheduler<Block> getBlockTickScheduler();

    public TickScheduler<Fluid> getFluidTickScheduler();

    public UpgradeData getUpgradeData();

    public void setInhabitedTime(long var1);

    public long getInhabitedTime();

    public static ShortList getList(ShortList[] lists, int index) {
        if (lists[index] == null) {
            lists[index] = new ShortArrayList();
        }
        return lists[index];
    }

    public boolean isLightOn();

    public void setLightOn(boolean var1);
}

