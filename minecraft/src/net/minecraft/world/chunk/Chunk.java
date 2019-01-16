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
import net.minecraft.sortme.structures.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockViewWithStructures;
import net.minecraft.world.LightType;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.Heightmap;
import org.apache.logging.log4j.LogManager;

public interface Chunk extends BlockViewWithStructures {
	@Nullable
	BlockState setBlockState(BlockPos blockPos, BlockState blockState, boolean bl);

	void setBlockEntity(BlockPos blockPos, BlockEntity blockEntity);

	void addEntity(Entity entity);

	@Nullable
	default ChunkSection method_12040() {
		ChunkSection[] chunkSections = this.getSectionArray();

		for (int i = chunkSections.length - 1; i >= 0; i--) {
			if (chunkSections[i] != WorldChunk.EMPTY_SECTION) {
				return chunkSections[i];
			}
		}

		return null;
	}

	default int method_12031() {
		ChunkSection chunkSection = this.method_12040();
		return chunkSection == null ? 0 : chunkSection.getYOffset();
	}

	Set<BlockPos> getBlockEntityPositions();

	ChunkSection[] getSectionArray();

	@Nullable
	LightingProvider getLightingProvider();

	default int method_12035(BlockPos blockPos, int i, boolean bl) {
		LightingProvider lightingProvider = this.getLightingProvider();
		if (lightingProvider == null) {
			return 0;
		} else {
			int j = bl ? lightingProvider.get(LightType.SKY_LIGHT).getLightLevel(blockPos) - i : 0;
			int k = lightingProvider.get(LightType.BLOCK_LIGHT).getLightLevel(blockPos);
			return Math.max(k, j);
		}
	}

	Collection<Entry<Heightmap.Type, Heightmap>> getHeightmaps();

	void setHeightmap(Heightmap.Type type, long[] ls);

	Heightmap getHeightmap(Heightmap.Type type);

	int sampleHeightmap(Heightmap.Type type, int i, int j);

	ChunkPos getPos();

	void setLastSaveTime(long l);

	Map<String, StructureStart> getStructureStarts();

	void setStructureStarts(Map<String, StructureStart> map);

	default Biome getBiome(BlockPos blockPos) {
		int i = blockPos.getX() & 15;
		int j = blockPos.getZ() & 15;
		return this.getBiomeArray()[j << 4 | i];
	}

	Biome[] getBiomeArray();

	void setShouldSave(boolean bl);

	boolean needsSaving();

	ChunkStatus getStatus();

	void removeBlockEntity(BlockPos blockPos);

	void setLightingProvider(LightingProvider lightingProvider);

	default void markBlockForPostProcessing(BlockPos blockPos) {
		LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", blockPos);
	}

	ShortList[] getPostProcessingLists();

	default void markBlockForPostProcessing(short s, int i) {
		getListFromArray(this.getPostProcessingLists(), i).add(s);
	}

	default void addPendingBlockEntityTag(CompoundTag compoundTag) {
		LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
	}

	@Nullable
	default CompoundTag getBlockEntityTagAt(BlockPos blockPos) {
		throw new UnsupportedOperationException();
	}

	default void setBiomeArray(Biome[] biomes) {
		throw new UnsupportedOperationException();
	}

	Stream<BlockPos> method_12018();

	TickScheduler<Block> getBlockTickScheduler();

	TickScheduler<Fluid> getFluidTickScheduler();

	default BitSet getCarvingMask(GenerationStep.Carver carver) {
		throw new RuntimeException("Meaningless in this context");
	}

	UpgradeData getUpgradeData();

	void setInhabitedTime(long l);

	long getInhabitedTime();

	static ShortList getListFromArray(ShortList[] shortLists, int i) {
		if (shortLists[i] == null) {
			shortLists[i] = new ShortArrayList();
		}

		return shortLists[i];
	}

	boolean isLightOn();

	void setLightOn(boolean bl);
}
