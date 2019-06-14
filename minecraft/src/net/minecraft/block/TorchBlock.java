package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class TorchBlock extends Block {
	protected static final VoxelShape field_11618 = Block.method_9541(6.0, 0.0, 6.0, 10.0, 10.0, 10.0);

	protected TorchBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		return field_11618;
	}

	@Override
	public BlockState method_9559(BlockState blockState, Direction direction, BlockState blockState2, IWorld iWorld, BlockPos blockPos, BlockPos blockPos2) {
		return direction == Direction.field_11033 && !this.method_9558(blockState, iWorld, blockPos)
			? Blocks.field_10124.method_9564()
			: super.method_9559(blockState, direction, blockState2, iWorld, blockPos, blockPos2);
	}

	@Override
	public boolean method_9558(BlockState blockState, ViewableWorld viewableWorld, BlockPos blockPos) {
		return isSolidSmallSquare(viewableWorld, blockPos.down(), Direction.field_11036);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		double d = (double)blockPos.getX() + 0.5;
		double e = (double)blockPos.getY() + 0.7;
		double f = (double)blockPos.getZ() + 0.5;
		world.addParticle(ParticleTypes.field_11251, d, e, f, 0.0, 0.0, 0.0);
		world.addParticle(ParticleTypes.field_11240, d, e, f, 0.0, 0.0, 0.0);
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}
}
