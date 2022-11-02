package net.minecraft.block;

import java.util.Optional;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.HugeFungusFeatureConfig;

public class FungusBlock extends PlantBlock implements Fertilizable {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 9.0, 12.0);
	private static final double GROW_CHANCE = 0.4;
	private final RegistryKey<ConfiguredFeature<?, ?>> featureKey;

	protected FungusBlock(AbstractBlock.Settings settings, RegistryKey<ConfiguredFeature<?, ?>> featureKey) {
		super(settings);
		this.featureKey = featureKey;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return floor.isIn(BlockTags.NYLIUM) || floor.isOf(Blocks.MYCELIUM) || floor.isOf(Blocks.SOUL_SOIL) || super.canPlantOnTop(floor, world, pos);
	}

	private Optional<? extends RegistryEntry<ConfiguredFeature<?, ?>>> getFeatureEntry(WorldView world) {
		return world.getRegistryManager().get(Registry.CONFIGURED_FEATURE_KEY).getEntry(this.featureKey);
	}

	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
		Optional<? extends RegistryEntry<ConfiguredFeature<?, ?>>> optional = this.getFeatureEntry(world);
		if (optional.isPresent() && ((ConfiguredFeature)((RegistryEntry)optional.get()).value()).config() instanceof HugeFungusFeatureConfig hugeFungusFeatureConfig) {
			Block block = hugeFungusFeatureConfig.validBaseBlock.getBlock();
			BlockState blockState = world.getBlockState(pos.down());
			return blockState.isOf(block);
		} else {
			return false;
		}
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return (double)random.nextFloat() < 0.4;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		this.getFeatureEntry(world)
			.ifPresent(featureEntry -> ((ConfiguredFeature)featureEntry.value()).generate(world, world.getChunkManager().getChunkGenerator(), random, pos));
	}
}
