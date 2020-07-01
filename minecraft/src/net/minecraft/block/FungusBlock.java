package net.minecraft.block;

import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.HugeFungusFeatureConfig;

public class FungusBlock extends PlantBlock implements Fertilizable {
	protected static final VoxelShape SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 9.0, 12.0);
	private final Supplier<ConfiguredFeature<HugeFungusFeatureConfig, ?>> field_22135;

	protected FungusBlock(AbstractBlock.Settings settings, Supplier<ConfiguredFeature<HugeFungusFeatureConfig, ?>> supplier) {
		super(settings);
		this.field_22135 = supplier;
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
		Block block = ((HugeFungusFeatureConfig)((ConfiguredFeature)this.field_22135.get()).config).validBaseBlock.getBlock();
		Block block2 = world.getBlockState(pos.down()).getBlock();
		return block2 == block;
	}

	@Override
	public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
		return (double)random.nextFloat() < 0.4;
	}

	@Override
	public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
		((ConfiguredFeature)this.field_22135.get()).generate(world, world.getChunkManager().getChunkGenerator(), random, pos);
	}
}
