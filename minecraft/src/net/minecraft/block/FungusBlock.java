package net.minecraft.block;

import java.util.Optional;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class FungusBlock extends PlantBlock implements Fertilizable {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 9.0, 12.0);
	private static final double GROW_CHANCE = 0.4;
	private final Block nylium;
	private final RegistryKey<ConfiguredFeature<?, ?>> featureKey;

	protected FungusBlock(AbstractBlock.Settings settings, RegistryKey<ConfiguredFeature<?, ?>> featureKey, Block nylium) {
		super(settings);
		this.featureKey = featureKey;
		this.nylium = nylium;
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
		return world.getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE).getEntry(this.featureKey);
	}

	@Override
	public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
		BlockState blockState = world.getBlockState(pos.down());
		return blockState.isOf(this.nylium);
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
