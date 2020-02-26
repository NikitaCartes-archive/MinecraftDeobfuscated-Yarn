package net.minecraft.block;

import java.util.Random;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.thrown.ThrownEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class TargetBlock extends Block {
	private static final IntProperty POWER = Properties.POWER;

	public TargetBlock(Block.Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(POWER, Integer.valueOf(0)));
	}

	@Override
	public void onProjectileHit(World world, BlockState state, BlockHitResult hitResult, Entity entity) {
		int i = trigger(world, state, hitResult, entity);
		Entity entity2 = null;
		if (entity instanceof ProjectileEntity) {
			entity2 = ((ProjectileEntity)entity).getOwner();
		} else if (entity instanceof ThrownEntity) {
			entity2 = ((ThrownEntity)entity).getOwner();
		}

		if (entity2 instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity2;
			serverPlayerEntity.incrementStat(Stats.TARGET_HIT);
			Criterions.TARGET_HIT.trigger(serverPlayerEntity, i);
		}
	}

	private static int trigger(IWorld world, BlockState state, BlockHitResult blockHitResult, Entity entity) {
		int i = calculatePower(blockHitResult, blockHitResult.getPos());
		int j = entity instanceof ProjectileEntity ? 20 : 8;
		if (!world.getBlockTickScheduler().isScheduled(blockHitResult.getBlockPos(), state.getBlock())) {
			setPower(world, state, i, blockHitResult.getBlockPos(), j);
		}

		return i;
	}

	private static int calculatePower(BlockHitResult blockHitResult, Vec3d pos) {
		Direction direction = blockHitResult.getSide();
		double d = Math.abs(MathHelper.fractionalPart(pos.x) - 0.5);
		double e = Math.abs(MathHelper.fractionalPart(pos.y) - 0.5);
		double f = Math.abs(MathHelper.fractionalPart(pos.z) - 0.5);
		Direction.Axis axis = direction.getAxis();
		double g;
		if (axis == Direction.Axis.Y) {
			g = Math.max(d, f);
		} else if (axis == Direction.Axis.Z) {
			g = Math.max(d, e);
		} else {
			g = Math.max(e, f);
		}

		return MathHelper.ceil(15.0 * MathHelper.clamp((0.5 - g) / 0.5, 0.0, 1.0));
	}

	private static void setPower(IWorld world, BlockState state, int power, BlockPos pos, int delay) {
		world.setBlockState(pos, state.with(POWER, Integer.valueOf(power)), 3);
		world.getBlockTickScheduler().schedule(pos, state.getBlock(), delay);
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if ((Integer)state.get(POWER) != 0) {
			world.setBlockState(pos, state.with(POWER, Integer.valueOf(0)), 3);
		}
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction facing) {
		return (Integer)state.get(POWER);
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(POWER);
	}
}