package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class FallingBlock extends Block {
	public FallingBlock(Block.Settings settings) {
		super(settings);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean moved) {
		world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction facing, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world));
		return super.getStateForNeighborUpdate(state, facing, neighborState, world, pos, neighborPos);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (canFallThrough(world.getBlockState(pos.down())) && pos.getY() >= 0) {
			FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(
				world, (double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, world.getBlockState(pos)
			);
			this.configureFallingBlockEntity(fallingBlockEntity);
			world.spawnEntity(fallingBlockEntity);
		}
	}

	protected void configureFallingBlockEntity(FallingBlockEntity entity) {
	}

	@Override
	public int getTickRate(WorldView world) {
		return 2;
	}

	public static boolean canFallThrough(BlockState state) {
		Material material = state.getMaterial();
		return state.isAir() || state.matches(BlockTags.FIRE) || material.isLiquid() || material.isReplaceable();
	}

	public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos) {
	}

	public void onDestroyedOnLanding(World world, BlockPos pos) {
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (random.nextInt(16) == 0) {
			BlockPos blockPos = pos.down();
			if (canFallThrough(world.getBlockState(blockPos))) {
				double d = (double)pos.getX() + (double)random.nextFloat();
				double e = (double)pos.getY() - 0.05;
				double f = (double)pos.getZ() + (double)random.nextFloat();
				world.addParticle(new BlockStateParticleEffect(ParticleTypes.FALLING_DUST, state), d, e, f, 0.0, 0.0, 0.0);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public int getColor(BlockState state) {
		return -16777216;
	}
}
