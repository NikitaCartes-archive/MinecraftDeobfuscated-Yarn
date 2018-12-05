package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.BlockRenderLayer;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class TorchBlock extends Block {
	protected static final VoxelShape BOUNDING_SHAPE = Block.createCubeShape(6.0, 0.0, 6.0, 10.0, 10.0, 10.0);

	protected TorchBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape getBoundingShape(BlockState blockState, BlockView blockView, BlockPos blockPos) {
		return BOUNDING_SHAPE;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return direction == Direction.DOWN && !this.canPlaceAt(blockState, iWorld, blockPos)
			? Blocks.field_10124.getDefaultState()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean canPlaceAt(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		BlockPos blockPos2 = blockPos.down();
		BlockState blockState2 = viewableWorld.getBlockState(blockPos2);
		Block block = blockState2.getBlock();
		boolean bl = block.matches(BlockTags.field_16584)
			|| block instanceof StainedGlassBlock
			|| block == Blocks.field_10033
			|| block.matches(BlockTags.field_15504)
			|| blockState2.hasSolidTopSurface(viewableWorld, blockPos2);
		return bl && block != Blocks.field_10613;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
		double d = (double)blockPos.getX() + 0.5;
		double e = (double)blockPos.getY() + 0.7;
		double f = (double)blockPos.getZ() + 0.5;
		world.method_8406(ParticleTypes.field_11251, d, e, f, 0.0, 0.0, 0.0);
		world.method_8406(ParticleTypes.field_11240, d, e, f, 0.0, 0.0, 0.0);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
}
