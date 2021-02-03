package net.minecraft.block;

import java.util.Random;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;

public class MossBlock extends Block implements Fertilizable {
	public MossBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		return world.getBlockState(pos.up()).isAir();
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		placePlantsAround(world, random, pos.up());
	}

	public static boolean placePlantsAround(StructureWorldAccess world, Random random, BlockPos pos) {
		if (!world.getBlockState(pos).isAir()) {
			return false;
		} else {
			int i = 0;
			int j = MathHelper.nextBetween(random, 1, 3);
			int k = MathHelper.nextBetween(random, 1, 3);

			for (int l = -j; l <= j; l++) {
				for (int m = -k; m <= k; m++) {
					BlockPos blockPos = pos.add(l, 0, m);
					i += placePlant(world, random, blockPos);
				}
			}

			return i > 0;
		}
	}

	private static int placePlant(StructureWorldAccess world, Random random, BlockPos pos) {
		int i = 0;
		BlockPos blockPos = pos.down();
		BlockState blockState = world.getBlockState(blockPos);
		if (world.isAir(pos) && blockState.isSideSolidFullSquare(world, blockPos, Direction.UP)) {
			method_33631(world, random, pos.down());
			if (random.nextFloat() < 0.8F) {
				BlockState blockState2 = getPlant(random);
				if (blockState2.canPlaceAt(world, pos)) {
					if (blockState2.getBlock() instanceof TallPlantBlock && world.isAir(pos.up())) {
						TallPlantBlock tallPlantBlock = (TallPlantBlock)blockState2.getBlock();
						tallPlantBlock.placeAt(world, pos, 2);
						i++;
					} else {
						world.setBlockState(pos, blockState2, 2);
						i++;
					}
				}
			}
		}

		return i;
	}

	private static void method_33631(StructureWorldAccess world, Random random, BlockPos pos) {
		if (world.getBlockState(pos).isIn(BlockTags.LUSH_PLANTS_REPLACEABLE)) {
			world.setBlockState(pos, Blocks.MOSS_BLOCK.getDefaultState(), 2);
		}
	}

	private static BlockState getPlant(Random random) {
		int i = random.nextInt(100) + 1;
		if (i < 5) {
			return Blocks.FLOWERING_AZALEA.getDefaultState();
		} else if (i < 15) {
			return Blocks.AZALEA.getDefaultState();
		} else if (i < 40) {
			return Blocks.MOSS_CARPET.getDefaultState();
		} else {
			return i < 90 ? Blocks.GRASS.getDefaultState() : Blocks.TALL_GRASS.getDefaultState();
		}
	}
}
