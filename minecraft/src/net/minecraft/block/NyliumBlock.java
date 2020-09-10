package net.minecraft.block;

import java.util.Random;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.NetherForestVegetationFeature;
import net.minecraft.world.gen.feature.TwistingVinesFeature;

public class NyliumBlock extends Block implements Fertilizable {
	protected NyliumBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	private static boolean stayAlive(BlockState state, WorldView world, BlockPos pos) {
		BlockPos blockPos = pos.up();
		BlockState blockState = world.getBlockState(blockPos);
		int i = ChunkLightProvider.getRealisticOpacity(world, state, pos, blockState, blockPos, Direction.UP, blockState.getOpacity(world, blockPos));
		return i < world.getMaxLightLevel();
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!stayAlive(state, world, pos)) {
			world.setBlockState(pos, Blocks.NETHERRACK.getDefaultState());
		}
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
		BlockState blockState = world.getBlockState(pos);
		BlockPos blockPos = pos.up();
		if (blockState.isOf(Blocks.CRIMSON_NYLIUM)) {
			NetherForestVegetationFeature.generate(world, random, blockPos, ConfiguredFeatures.Configs.CRIMSON_ROOTS_CONFIG, 3, 1);
		} else if (blockState.isOf(Blocks.WARPED_NYLIUM)) {
			NetherForestVegetationFeature.generate(world, random, blockPos, ConfiguredFeatures.Configs.WARPED_ROOTS_CONFIG, 3, 1);
			NetherForestVegetationFeature.generate(world, random, blockPos, ConfiguredFeatures.Configs.NETHER_SPROUTS_CONFIG, 3, 1);
			if (random.nextInt(8) == 0) {
				TwistingVinesFeature.method_26265(world, random, blockPos, 3, 1, 2);
			}
		}
	}
}
