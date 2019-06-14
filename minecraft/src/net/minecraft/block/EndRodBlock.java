package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EndRodBlock extends FacingBlock {
	protected static final VoxelShape field_10971 = Block.method_9541(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
	protected static final VoxelShape field_10970 = Block.method_9541(6.0, 6.0, 0.0, 10.0, 10.0, 16.0);
	protected static final VoxelShape field_10969 = Block.method_9541(0.0, 6.0, 6.0, 16.0, 10.0, 10.0);

	protected EndRodBlock(Block.Settings settings) {
		super(settings);
		this.method_9590(this.field_10647.method_11664().method_11657(field_10927, Direction.field_11036));
	}

	@Override
	public BlockState method_9598(BlockState blockState, BlockRotation blockRotation) {
		return blockState.method_11657(field_10927, blockRotation.rotate(blockState.method_11654(field_10927)));
	}

	@Override
	public BlockState method_9569(BlockState blockState, BlockMirror blockMirror) {
		return blockState.method_11657(field_10927, blockMirror.apply(blockState.method_11654(field_10927)));
	}

	@Override
	public VoxelShape method_9530(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityContext entityContext) {
		switch (((Direction)blockState.method_11654(field_10927)).getAxis()) {
			case X:
			default:
				return field_10969;
			case Z:
				return field_10970;
			case Y:
				return field_10971;
		}
	}

	@Override
	public BlockState method_9605(ItemPlacementContext itemPlacementContext) {
		Direction direction = itemPlacementContext.getSide();
		BlockState blockState = itemPlacementContext.method_8045().method_8320(itemPlacementContext.getBlockPos().offset(direction.getOpposite()));
		return blockState.getBlock() == this && blockState.method_11654(field_10927) == direction
			? this.method_9564().method_11657(field_10927, direction.getOpposite())
			: this.method_9564().method_11657(field_10927, direction);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_9496(BlockState blockState, World world, BlockPos blockPos, Random random) {
		Direction direction = blockState.method_11654(field_10927);
		double d = (double)blockPos.getX() + 0.55 - (double)(random.nextFloat() * 0.1F);
		double e = (double)blockPos.getY() + 0.55 - (double)(random.nextFloat() * 0.1F);
		double f = (double)blockPos.getZ() + 0.55 - (double)(random.nextFloat() * 0.1F);
		double g = (double)(0.4F - (random.nextFloat() + random.nextFloat()) * 0.4F);
		if (random.nextInt(5) == 0) {
			world.addParticle(
				ParticleTypes.field_11207,
				d + (double)direction.getOffsetX() * g,
				e + (double)direction.getOffsetY() * g,
				f + (double)direction.getOffsetZ() * g,
				random.nextGaussian() * 0.005,
				random.nextGaussian() * 0.005,
				random.nextGaussian() * 0.005
			);
		}
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.field_9174;
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.method_11667(field_10927);
	}

	@Override
	public PistonBehavior method_9527(BlockState blockState) {
		return PistonBehavior.field_15974;
	}
}
