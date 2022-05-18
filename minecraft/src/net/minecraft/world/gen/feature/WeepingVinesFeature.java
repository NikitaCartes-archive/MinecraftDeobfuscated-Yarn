package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.AbstractPlantStemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class WeepingVinesFeature extends Feature<DefaultFeatureConfig> {
	private static final Direction[] DIRECTIONS = Direction.values();

	public WeepingVinesFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		Random random = context.getRandom();
		if (!structureWorldAccess.isAir(blockPos)) {
			return false;
		} else {
			BlockState blockState = structureWorldAccess.getBlockState(blockPos.up());
			if (!blockState.isOf(Blocks.NETHERRACK) && !blockState.isOf(Blocks.NETHER_WART_BLOCK)) {
				return false;
			} else {
				this.generateNetherWartBlocksInArea(structureWorldAccess, random, blockPos);
				this.generateVinesInArea(structureWorldAccess, random, blockPos);
				return true;
			}
		}
	}

	private void generateNetherWartBlocksInArea(WorldAccess world, Random random, BlockPos pos) {
		world.setBlockState(pos, Blocks.NETHER_WART_BLOCK.getDefaultState(), Block.NOTIFY_LISTENERS);
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		BlockPos.Mutable mutable2 = new BlockPos.Mutable();

		for (int i = 0; i < 200; i++) {
			mutable.set(pos, random.nextInt(6) - random.nextInt(6), random.nextInt(2) - random.nextInt(5), random.nextInt(6) - random.nextInt(6));
			if (world.isAir(mutable)) {
				int j = 0;

				for (Direction direction : DIRECTIONS) {
					BlockState blockState = world.getBlockState(mutable2.set(mutable, direction));
					if (blockState.isOf(Blocks.NETHERRACK) || blockState.isOf(Blocks.NETHER_WART_BLOCK)) {
						j++;
					}

					if (j > 1) {
						break;
					}
				}

				if (j == 1) {
					world.setBlockState(mutable, Blocks.NETHER_WART_BLOCK.getDefaultState(), Block.NOTIFY_LISTENERS);
				}
			}
		}
	}

	private void generateVinesInArea(WorldAccess world, Random random, BlockPos pos) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = 0; i < 100; i++) {
			mutable.set(pos, random.nextInt(8) - random.nextInt(8), random.nextInt(2) - random.nextInt(7), random.nextInt(8) - random.nextInt(8));
			if (world.isAir(mutable)) {
				BlockState blockState = world.getBlockState(mutable.up());
				if (blockState.isOf(Blocks.NETHERRACK) || blockState.isOf(Blocks.NETHER_WART_BLOCK)) {
					int j = MathHelper.nextInt(random, 1, 8);
					if (random.nextInt(6) == 0) {
						j *= 2;
					}

					if (random.nextInt(5) == 0) {
						j = 1;
					}

					int k = 17;
					int l = 25;
					generateVineColumn(world, random, mutable, j, 17, 25);
				}
			}
		}
	}

	public static void generateVineColumn(WorldAccess world, Random random, BlockPos.Mutable pos, int length, int minAge, int maxAge) {
		for (int i = 0; i <= length; i++) {
			if (world.isAir(pos)) {
				if (i == length || !world.isAir(pos.down())) {
					world.setBlockState(
						pos,
						Blocks.WEEPING_VINES.getDefaultState().with(AbstractPlantStemBlock.AGE, Integer.valueOf(MathHelper.nextInt(random, minAge, maxAge))),
						Block.NOTIFY_LISTENERS
					);
					break;
				}

				world.setBlockState(pos, Blocks.WEEPING_VINES_PLANT.getDefaultState(), Block.NOTIFY_LISTENERS);
			}

			pos.move(Direction.DOWN);
		}
	}
}
