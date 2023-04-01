package net.minecraft.block;

import net.minecraft.class_8293;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class EndRodBlock extends RodBlock {
	protected EndRodBlock(AbstractBlock.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.UP));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getSide();
		BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(direction.getOpposite()));
		return blockState.isOf(this) && blockState.get(FACING) == direction
			? this.getDefaultState().with(FACING, direction.getOpposite())
			: this.getDefaultState().with(FACING, direction);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		Direction direction = state.get(FACING);
		double d = (double)pos.getX() + 0.55 - (double)(random.nextFloat() * 0.1F);
		double e = (double)pos.getY() + 0.55 - (double)(random.nextFloat() * 0.1F);
		double f = (double)pos.getZ() + 0.55 - (double)(random.nextFloat() * 0.1F);
		double g = (double)(0.4F - (random.nextFloat() + random.nextFloat()) * 0.4F);
		if (random.nextInt(5) == 0) {
			world.addParticle(
				ParticleTypes.END_ROD,
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
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
		if (class_8293.field_43670.method_50116()) {
			BlockState blockState = world.getBlockState(pos.up());
			boolean bl = blockState.isIn(BlockTags.LOGS) || blockState.isOf(Blocks.END_ROD);
			BlockState blockState2 = world.getBlockState(pos.down());
			boolean bl2 = blockState2.isIn(BlockTags.LOGS) || blockState.isOf(Blocks.END_ROD);
			if (bl || bl2) {
				world.setBlockState(pos, Blocks.END_ROD.getDefaultState(), Block.FORCE_STATE);
			}
		}

		super.onStacksDropped(state, world, pos, tool, dropExperience);
	}
}
