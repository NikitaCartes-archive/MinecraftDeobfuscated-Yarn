package net.minecraft.block;

import java.util.function.Supplier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.HugeFungusFeatureConfig;

public class FungusBlock extends PlantBlock implements Fertilizable {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 9.0, 12.0);
	private static final double GROW_CHANCE = 0.4;
	private final Supplier<RegistryEntry<ConfiguredFeature<HugeFungusFeatureConfig, ?>>> feature;

	protected FungusBlock(AbstractBlock.Settings settings, Supplier<RegistryEntry<ConfiguredFeature<HugeFungusFeatureConfig, ?>>> feature) {
		super(settings);
		this.feature = feature;
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
		return floor.isIn(BlockTags.NYLIUM) || floor.isOf(Blocks.MYCELIUM) || floor.isOf(Blocks.SOUL_SOIL) || super.canPlantOnTop(floor, world, pos);
	}

	@Override
	public boolean isFertilizable(BlockView world, BlockPos pos, BlockState state, boolean isClient) {
		Block block = ((HugeFungusFeatureConfig)((ConfiguredFeature)((RegistryEntry)this.feature.get()).value()).config()).validBaseBlock.getBlock();
		BlockState blockState = world.getBlockState(pos.down());
		return blockState.isOf(block);
	}

	@Override
	public boolean canGrow(World world, AbstractRandom random, BlockPos pos, BlockState state) {
		return (double)random.nextFloat() < 0.4;
	}

	@Override
	public void grow(ServerWorld world, AbstractRandom random, BlockPos pos, BlockState state) {
		((ConfiguredFeature)((RegistryEntry)this.feature.get()).value()).generate(world, world.getChunkManager().getChunkGenerator(), random, pos);
	}
}
