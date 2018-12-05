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
import net.minecraft.class_2810;
import net.minecraft.class_2843;
import net.minecraft.class_3449;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.Heightmap;
import org.apache.logging.log4j.LogManager;

public interface Chunk extends class_2810 {
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

	Set<BlockPos> method_12021();

	ChunkSection[] getSectionArray();

	@Nullable
	LightingProvider method_12023();

	default int method_12035(BlockPos blockPos, int i, boolean bl) {
		LightingProvider lightingProvider = this.method_12023();
		if (lightingProvider == null) {
			return 0;
		} else {
			int j = bl ? lightingProvider.get(LightType.field_9284).getLightLevel(blockPos) - i : 0;
			int k = lightingProvider.get(LightType.field_9282).getLightLevel(blockPos);
			return Math.max(k, j);
		}
	}

	Collection<Entry<Heightmap.Type, Heightmap>> method_12011();

	void method_12037(Heightmap.Type type, long[] ls);

	Heightmap getHeightmap(Heightmap.Type type);

	int sampleHeightmap(Heightmap.Type type, int i, int j);

	ChunkPos getPos();

	void method_12043(long l);

	Map<String, class_3449> method_12016();

	void method_12034(Map<String, class_3449> map);

	default Biome getBiome(BlockPos blockPos) {
		int i = blockPos.getX() & 15;
		int j = blockPos.getZ() & 15;
		return this.getBiomeArray()[j << 4 | i];
	}

	Biome[] getBiomeArray();

	void method_12008(boolean bl);

	boolean method_12044();

	ChunkStatus getStatus();

	void removeBlockEntity(BlockPos blockPos);

	void method_12027(ChunkManager chunkManager);

	default void markBlockForPostProcessing(BlockPos blockPos) {
		LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", blockPos);
	}

	ShortList[] method_12012();

	default void method_12029(short s, int i) {
		getListFromArray(this.method_12012(), i).add(s);
	}

	default void addPendingBlockEntityTag(CompoundTag compoundTag) {
		LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
	}

	@Nullable
	default CompoundTag method_12024(BlockPos blockPos) {
		throw new UnsupportedOperationException();
	}

	default void setBiomes(Biome[] biomes) {
		throw new UnsupportedOperationException();
	}

	Stream<BlockPos> method_12018();

	TickScheduler<Block> method_12013();

	TickScheduler<Fluid> method_12014();

	default BitSet method_12025(GenerationStep.Carver carver) {
		throw new RuntimeException("Meaningless in this context");
	}

	class_2843 method_12003();

	void method_12028(long l);

	long method_12033();

	static ShortList getListFromArray(ShortList[] shortLists, int i) {
		if (shortLists[i] == null) {
			shortLists[i] = new ShortArrayList();
		}

		return shortLists[i];
	}

	boolean method_12038();

	void method_12020(boolean bl);
}
