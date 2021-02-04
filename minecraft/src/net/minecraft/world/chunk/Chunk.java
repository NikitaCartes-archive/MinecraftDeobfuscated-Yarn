package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.class_5713;
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
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;

/**
 * Represents a scoped, modifiable view of biomes, block states, fluid states and block entities.
 */
public interface Chunk extends BlockView, StructureHolder {
	default class_5713 method_32914(int i) {
		return class_5713.EMPTY;
	}

	@Nullable
	BlockState setBlockState(BlockPos pos, BlockState state, boolean moved);

	void setBlockEntity(BlockEntity blockEntity);

	void addEntity(Entity entity);

	@Nullable
	default ChunkSection getHighestNonEmptySection() {
		ChunkSection[] chunkSections = this.getSectionArray();

		for (int i = chunkSections.length - 1; i >= 0; i--) {
			ChunkSection chunkSection = chunkSections[i];
			if (!ChunkSection.isEmpty(chunkSection)) {
				return chunkSection;
			}
		}

		return null;
	}

	default int getHighestNonEmptySectionYOffset() {
		ChunkSection chunkSection = this.getHighestNonEmptySection();
		return chunkSection == null ? this.getBottomY() : chunkSection.getYOffset();
	}

	Set<BlockPos> getBlockEntityPositions();

	ChunkSection[] getSectionArray();

	Collection<Entry<Heightmap.Type, Heightmap>> getHeightmaps();

	void setHeightmap(Heightmap.Type type, long[] heightmap);

	Heightmap getHeightmap(Heightmap.Type type);

	int sampleHeightmap(Heightmap.Type type, int x, int z);

	ChunkPos getPos();

	Map<StructureFeature<?>, StructureStart<?>> getStructureStarts();

	void setStructureStarts(Map<StructureFeature<?>, StructureStart<?>> structureStarts);

	default boolean areSectionsEmptyBetween(int lowerHeight, int upperHeight) {
		if (lowerHeight < this.getBottomY()) {
			lowerHeight = this.getBottomY();
		}

		if (upperHeight >= this.getTopY()) {
			upperHeight = this.getTopY() - 1;
		}

		for (int i = lowerHeight; i <= upperHeight; i += 16) {
			if (!ChunkSection.isEmpty(this.getSectionArray()[this.getSectionIndex(i)])) {
				return false;
			}
		}

		return true;
	}

	@Nullable
	BiomeArray getBiomeArray();

	void setShouldSave(boolean shouldSave);

	boolean needsSaving();

	ChunkStatus getStatus();

	void removeBlockEntity(BlockPos pos);

	default void markBlockForPostProcessing(BlockPos pos) {
		LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", pos);
	}

	ShortList[] getPostProcessingLists();

	default void markBlockForPostProcessing(short packedPos, int index) {
		getList(this.getPostProcessingLists(), index).add(packedPos);
	}

	default void addPendingBlockEntityTag(CompoundTag tag) {
		LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
	}

	@Nullable
	CompoundTag getBlockEntityTag(BlockPos pos);

	@Nullable
	CompoundTag getPackedBlockEntityTag(BlockPos pos);

	Stream<BlockPos> getLightSourcesStream();

	TickScheduler<Block> getBlockTickScheduler();

	TickScheduler<Fluid> getFluidTickScheduler();

	UpgradeData getUpgradeData();

	void setInhabitedTime(long inhabitedTime);

	long getInhabitedTime();

	static ShortList getList(ShortList[] lists, int index) {
		if (lists[index] == null) {
			lists[index] = new ShortArrayList();
		}

		return lists[index];
	}

	boolean isLightOn();

	void setLightOn(boolean lightOn);
}
