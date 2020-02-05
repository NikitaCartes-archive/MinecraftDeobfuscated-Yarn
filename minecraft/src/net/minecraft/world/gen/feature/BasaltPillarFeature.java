package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class BasaltPillarFeature extends Feature<DefaultFeatureConfig> {
	public BasaltPillarFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		if (iWorld.isAir(blockPos) && !iWorld.isAir(blockPos.up())) {
			BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos);
			BlockPos.Mutable mutable2 = new BlockPos.Mutable(blockPos);
			boolean bl = true;
			boolean bl2 = true;
			boolean bl3 = true;
			boolean bl4 = true;

			while (iWorld.isAir(mutable)) {
				iWorld.setBlockState(mutable, Blocks.BASALT.getDefaultState(), 2);
				bl = bl && this.stopOrPlaceBasalt(iWorld, random, mutable2.set(mutable).setOffset(Direction.NORTH));
				bl2 = bl2 && this.stopOrPlaceBasalt(iWorld, random, mutable2.set(mutable).setOffset(Direction.SOUTH));
				bl3 = bl3 && this.stopOrPlaceBasalt(iWorld, random, mutable2.set(mutable).setOffset(Direction.WEST));
				bl4 = bl4 && this.stopOrPlaceBasalt(iWorld, random, mutable2.set(mutable).setOffset(Direction.EAST));
				mutable.setOffset(Direction.DOWN);
			}

			mutable.setOffset(Direction.UP);
			this.tryPlaceBasalt(iWorld, random, mutable2.set(mutable).setOffset(Direction.NORTH));
			this.tryPlaceBasalt(iWorld, random, mutable2.set(mutable).setOffset(Direction.SOUTH));
			this.tryPlaceBasalt(iWorld, random, mutable2.set(mutable).setOffset(Direction.WEST));
			this.tryPlaceBasalt(iWorld, random, mutable2.set(mutable).setOffset(Direction.EAST));
			BlockPos.Mutable mutable3 = new BlockPos.Mutable();

			for (int i = -3; i < 4; i++) {
				for (int j = -3; j < 4; j++) {
					int k = MathHelper.abs(i) * MathHelper.abs(j);
					if (random.nextInt(10) < 10 - k) {
						mutable3.set(mutable.add(i, 0, j));
						int l = 3;

						while (iWorld.isAir(mutable2.set(mutable3).setOffset(Direction.DOWN))) {
							mutable3.setOffset(Direction.DOWN);
							if (--l <= 0) {
								break;
							}
						}

						if (!iWorld.isAir(mutable2.set(mutable3).setOffset(Direction.DOWN))) {
							iWorld.setBlockState(mutable3, Blocks.BASALT.getDefaultState(), 2);
						}
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private void tryPlaceBasalt(IWorld world, Random random, BlockPos pos) {
		if (random.nextBoolean()) {
			world.setBlockState(pos, Blocks.BASALT.getDefaultState(), 2);
		}
	}

	private boolean stopOrPlaceBasalt(IWorld world, Random random, BlockPos pos) {
		if (random.nextInt(10) != 0) {
			world.setBlockState(pos, Blocks.BASALT.getDefaultState(), 2);
			return true;
		} else {
			return false;
		}
	}
}
