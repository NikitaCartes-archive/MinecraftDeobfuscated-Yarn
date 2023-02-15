package net.minecraft.block;

import net.minecraft.client.util.ParticleUtil;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class FallingBlock extends Block implements LandingBlock {
	public FallingBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		world.scheduleBlockTick(pos, this, this.getFallDelay());
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		world.scheduleBlockTick(pos, this, this.getFallDelay());
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (canFallThrough(world.getBlockState(pos.down())) && pos.getY() >= world.getBottomY()) {
			FallingBlockEntity fallingBlockEntity = FallingBlockEntity.spawnFromBlock(world, pos, state);
			this.configureFallingBlockEntity(fallingBlockEntity);
		}
	}

	protected void configureFallingBlockEntity(FallingBlockEntity entity) {
	}

	/**
	 * Gets the amount of time in ticks this block will wait before attempting to start falling.
	 */
	protected int getFallDelay() {
		return 2;
	}

	public static boolean canFallThrough(BlockState state) {
		Material material = state.getMaterial();
		return state.isAir() || state.isIn(BlockTags.FIRE) || material.isLiquid() || state.isReplaceable();
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (random.nextInt(16) == 0) {
			BlockPos blockPos = pos.down();
			if (canFallThrough(world.getBlockState(blockPos))) {
				ParticleUtil.spawnParticle(world, pos, random, new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, state));
			}
		}
	}

	public int getColor(BlockState state, BlockView world, BlockPos pos) {
		return -16777216;
	}
}
