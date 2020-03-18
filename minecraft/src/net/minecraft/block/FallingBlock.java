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
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FallingBlock extends Block {
	public FallingBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		world.getBlockTickScheduler().schedule(pos, this, this.method_26154());
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, IWorld world, BlockPos pos, BlockPos posFrom) {
		world.getBlockTickScheduler().schedule(pos, this, this.method_26154());
		return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
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

	protected int method_26154() {
		return 2;
	}

	public static boolean canFallThrough(BlockState state) {
		Material material = state.getMaterial();
		return state.isAir() || state.isIn(BlockTags.FIRE) || material.isLiquid() || material.isReplaceable();
	}

	public void onLanding(World world, BlockPos pos, BlockState fallingBlockState, BlockState currentStateInPos, FallingBlockEntity fallingBlockEntity) {
	}

	public void onDestroyedOnLanding(World world, BlockPos pos, FallingBlockEntity fallingBlockEntity) {
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
	public int getColor(BlockState state, BlockView blockView, BlockPos blockPos) {
		return -16777216;
	}
}
