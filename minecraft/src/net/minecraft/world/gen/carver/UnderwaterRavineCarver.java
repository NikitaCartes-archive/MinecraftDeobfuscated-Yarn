package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class UnderwaterRavineCarver extends RavineCarver {
	public UnderwaterRavineCarver(Codec<ProbabilityConfig> codec) {
		super(codec);
		this.alwaysCarvableBlocks = ImmutableSet.of(
			Blocks.field_10340,
			Blocks.field_10474,
			Blocks.field_10508,
			Blocks.field_10115,
			Blocks.field_10566,
			Blocks.field_10253,
			Blocks.field_10520,
			Blocks.field_10219,
			Blocks.field_10415,
			Blocks.field_10611,
			Blocks.field_10184,
			Blocks.field_10015,
			Blocks.field_10325,
			Blocks.field_10143,
			Blocks.field_10014,
			Blocks.field_10444,
			Blocks.field_10349,
			Blocks.field_10590,
			Blocks.field_10235,
			Blocks.field_10570,
			Blocks.field_10409,
			Blocks.field_10123,
			Blocks.field_10526,
			Blocks.field_10328,
			Blocks.field_10626,
			Blocks.field_9979,
			Blocks.field_10344,
			Blocks.field_10402,
			Blocks.field_10477,
			Blocks.field_10102,
			Blocks.field_10255,
			Blocks.field_10382,
			Blocks.field_10164,
			Blocks.field_10540,
			Blocks.field_10124,
			Blocks.field_10543
		);
	}

	@Override
	protected boolean isRegionUncarvable(Chunk chunk, int mainChunkX, int mainChunkZ, int relMinX, int relMaxX, int minY, int maxY, int relMinZ, int relMaxZ) {
		return false;
	}

	@Override
	protected boolean carveAtPoint(
		Chunk chunk,
		Function<BlockPos, Biome> posToBiome,
		BitSet carvingMask,
		Random random,
		BlockPos.Mutable mutable,
		BlockPos.Mutable mutable2,
		BlockPos.Mutable mutable3,
		int seaLevel,
		int mainChunkX,
		int mainChunkZ,
		int x,
		int z,
		int relativeX,
		int y,
		int relativeZ,
		MutableBoolean mutableBoolean
	) {
		return UnderwaterCaveCarver.carveAtPoint(this, chunk, carvingMask, random, mutable, seaLevel, mainChunkX, mainChunkZ, x, z, relativeX, y, relativeZ);
	}
}
