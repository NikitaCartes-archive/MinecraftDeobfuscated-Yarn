package net.minecraft.block;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.UndergroundConfiguredFeatures;

public class MossBlock extends Block implements Fertilizable {
	public MossBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
		return world.getBlockState(pos.up()).isAir();
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		world.getRegistryManager()
			.getOptional(RegistryKeys.CONFIGURED_FEATURE)
			.flatMap(registry -> registry.getEntry(UndergroundConfiguredFeatures.MOSS_PATCH_BONEMEAL))
			.ifPresent(reference -> ((ConfiguredFeature)reference.value()).generate(world, world.getChunkManager().getChunkGenerator(), random, pos.up()));
	}
}
