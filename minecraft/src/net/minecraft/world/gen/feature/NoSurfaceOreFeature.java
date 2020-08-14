package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class NoSurfaceOreFeature extends Feature<OreFeatureConfig> {
	NoSurfaceOreFeature(Codec<OreFeatureConfig> codec) {
		super(codec);
	}

	public boolean generate(
		StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, OreFeatureConfig oreFeatureConfig
	) {
		int i = random.nextInt(oreFeatureConfig.size + 1);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int j = 0; j < i; j++) {
			this.getStartPos(mutable, random, blockPos, Math.min(j, 7));
			if (oreFeatureConfig.target.test(structureWorldAccess.getBlockState(mutable), random) && !this.checkAir(structureWorldAccess, mutable)) {
				structureWorldAccess.setBlockState(mutable, oreFeatureConfig.state, 2);
			}
		}

		return true;
	}

	private void getStartPos(BlockPos.Mutable mutable, Random random, BlockPos pos, int size) {
		int i = this.randomCoord(random, size);
		int j = this.randomCoord(random, size);
		int k = this.randomCoord(random, size);
		mutable.set(pos, i, j, k);
	}

	private int randomCoord(Random random, int size) {
		return Math.round((random.nextFloat() - random.nextFloat()) * (float)size);
	}

	private boolean checkAir(WorldAccess world, BlockPos pos) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (Direction direction : Direction.values()) {
			mutable.set(pos, direction);
			if (world.getBlockState(mutable).isAir()) {
				return true;
			}
		}

		return false;
	}
}
