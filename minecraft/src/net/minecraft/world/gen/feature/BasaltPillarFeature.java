package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class BasaltPillarFeature extends Feature<DefaultFeatureConfig> {
	public BasaltPillarFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		BlockPos blockPos = context.getOrigin();
		StructureWorldAccess structureWorldAccess = context.getWorld();
		Random random = context.getRandom();
		if (structureWorldAccess.isAir(blockPos) && !structureWorldAccess.isAir(blockPos.up())) {
			BlockPos.Mutable mutable = blockPos.mutableCopy();
			BlockPos.Mutable mutable2 = blockPos.mutableCopy();
			boolean bl = true;
			boolean bl2 = true;
			boolean bl3 = true;
			boolean bl4 = true;

			while (structureWorldAccess.isAir(mutable)) {
				if (structureWorldAccess.isOutOfHeightLimit(mutable)) {
					return true;
				}

				structureWorldAccess.setBlockState(mutable, Blocks.BASALT.getDefaultState(), Block.NOTIFY_LISTENERS);
				bl = bl && this.stopOrPlaceBasalt(structureWorldAccess, random, mutable2.set(mutable, Direction.NORTH));
				bl2 = bl2 && this.stopOrPlaceBasalt(structureWorldAccess, random, mutable2.set(mutable, Direction.SOUTH));
				bl3 = bl3 && this.stopOrPlaceBasalt(structureWorldAccess, random, mutable2.set(mutable, Direction.WEST));
				bl4 = bl4 && this.stopOrPlaceBasalt(structureWorldAccess, random, mutable2.set(mutable, Direction.EAST));
				mutable.move(Direction.DOWN);
			}

			mutable.move(Direction.UP);
			this.tryPlaceBasalt(structureWorldAccess, random, mutable2.set(mutable, Direction.NORTH));
			this.tryPlaceBasalt(structureWorldAccess, random, mutable2.set(mutable, Direction.SOUTH));
			this.tryPlaceBasalt(structureWorldAccess, random, mutable2.set(mutable, Direction.WEST));
			this.tryPlaceBasalt(structureWorldAccess, random, mutable2.set(mutable, Direction.EAST));
			mutable.move(Direction.DOWN);
			BlockPos.Mutable mutable3 = new BlockPos.Mutable();

			for (int i = -3; i < 4; i++) {
				for (int j = -3; j < 4; j++) {
					int k = MathHelper.abs(i) * MathHelper.abs(j);
					if (random.nextInt(10) < 10 - k) {
						mutable3.set(mutable.add(i, 0, j));
						int l = 3;

						while (structureWorldAccess.isAir(mutable2.set(mutable3, Direction.DOWN))) {
							mutable3.move(Direction.DOWN);
							if (--l <= 0) {
								break;
							}
						}

						if (!structureWorldAccess.isAir(mutable2.set(mutable3, Direction.DOWN))) {
							structureWorldAccess.setBlockState(mutable3, Blocks.BASALT.getDefaultState(), Block.NOTIFY_LISTENERS);
						}
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	private void tryPlaceBasalt(WorldAccess world, Random random, BlockPos pos) {
		if (random.nextBoolean()) {
			world.setBlockState(pos, Blocks.BASALT.getDefaultState(), Block.NOTIFY_LISTENERS);
		}
	}

	private boolean stopOrPlaceBasalt(WorldAccess world, Random random, BlockPos pos) {
		if (random.nextInt(10) != 0) {
			world.setBlockState(pos, Blocks.BASALT.getDefaultState(), Block.NOTIFY_LISTENERS);
			return true;
		} else {
			return false;
		}
	}
}
