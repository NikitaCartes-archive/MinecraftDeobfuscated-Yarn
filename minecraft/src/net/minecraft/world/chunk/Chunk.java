package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Stream;
import javax.annotation.Nullable;
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
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import org.apache.logging.log4j.LogManager;

public interface Chunk extends StructureHolder {
	@Nullable
	BlockState setBlockState(BlockPos pos, BlockState state, boolean bl);

	void setBlockEntity(BlockPos pos, BlockEntity blockEntity);

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
		return chunkSection == null ? 0 : chunkSection.getYOffset();
	}

	Set<BlockPos> getBlockEntityPositions();

	ChunkSection[] getSectionArray();

	@Nullable
	LightingProvider getLightingProvider();

	default int getLightLevel(BlockPos pos, int darkness, boolean includeSkyLight) {
		LightingProvider lightingProvider = this.getLightingProvider();
		if (lightingProvider != null && this.getStatus().isAtLeast(ChunkStatus.LIGHT)) {
			int i = includeSkyLight ? lightingProvider.get(LightType.SKY).getLightLevel(pos) - darkness : 0;
			int j = lightingProvider.get(LightType.BLOCK).getLightLevel(pos);
			return Math.max(j, i);
		} else {
			return 0;
		}
	}

	Collection<Entry<Heightmap.Type, Heightmap>> getHeightmaps();

	void setHeightmap(Heightmap.Type type, long[] heightmap);

	Heightmap getHeightmap(Heightmap.Type type);

	int sampleHeightmap(Heightmap.Type type, int x, int z);

	ChunkPos getPos();

	void setLastSaveTime(long lastSaveTime);

	Map<String, StructureStart> getStructureStarts();

	void setStructureStarts(Map<String, StructureStart> map);

	default Biome getBiome(BlockPos pos) {
		int i = pos.getX() & 15;
		int j = pos.getZ() & 15;
		return this.getBiomeArray()[j << 4 | i];
	}

	default boolean method_12228(int i, int j) {
		if (i < 0) {
			i = 0;
		}

		if (j >= 256) {
			j = 255;
		}

		for (int k = i; k <= j; k += 16) {
			if (!ChunkSection.isEmpty(this.getSectionArray()[k >> 4])) {
				return false;
			}
		}

		return true;
	}

	Biome[] getBiomeArray();

	void setShouldSave(boolean shouldSave);

	boolean needsSaving();

	ChunkStatus getStatus();

	void removeBlockEntity(BlockPos blockPos);

	void setLightingProvider(LightingProvider lightingProvider);

	default void markBlockForPostProcessing(BlockPos blockPos) {
		LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", blockPos);
	}

	ShortList[] getPostProcessingLists();

	default void markBlockForPostProcessing(short s, int i) {
		getList(this.getPostProcessingLists(), i).add(s);
	}

	default void addPendingBlockEntityTag(CompoundTag compoundTag) {
		LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
	}

	@Nullable
	CompoundTag getBlockEntityTagAt(BlockPos pos);

	@Nullable
	CompoundTag method_20598(BlockPos blockPos);

	default void setBiomeArray(Biome[] biomeArray) {
		throw new UnsupportedOperationException();
	}

	Stream<BlockPos> getLightSourcesStream();

	TickScheduler<Block> getBlockTickScheduler();

	TickScheduler<Fluid> getFluidTickScheduler();

	default BitSet getCarvingMask(GenerationStep.Carver carver) {
		throw new RuntimeException("Meaningless in this context");
	}

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
