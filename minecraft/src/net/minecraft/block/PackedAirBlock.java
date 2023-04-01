package net.minecraft.block;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.class_8293;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class PackedAirBlock extends AirBlock {
	private static final ThreadLocal<Boolean> IS_DISSIPATING = ThreadLocal.withInitial(() -> false);
	public static final int field_44236 = 128;
	private static final int TICK_DELAY = 2;
	private static final Direction[] DIRECTIONS = Direction.values();

	protected PackedAirBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
		if (!oldState.isOf(state.getBlock())) {
			world.scheduleBlockTick(pos, this, 2);
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		this.tryDissipate(world, pos);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
		world.scheduleBlockTick(pos, this, 2);
	}

	@Override
	public BlockState getStateForNeighborUpdate(
		BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
	) {
		if (world instanceof ServerWorld serverWorld && neighborState.shouldLetAirThrough(serverWorld, neighborPos, direction.getOpposite())) {
			serverWorld.scheduleBlockTick(pos, this, 2);
		}

		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	private void tryDissipate(ServerWorld world, BlockPos originPos) {
		if (!(Boolean)IS_DISSIPATING.get()) {
			if (!class_8293.field_43539.method_50116()) {
				if (world.getBlockState(originPos).isOf(this)) {
					world.setBlockState(originPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
				}
			} else {
				Function<Predicate<BlockState>, Set<BlockPos>> function = predicate -> {
					Set<BlockPos> setx = new HashSet();
					BlockPos.iterateRecursively(originPos, 128, 128, (pos, consumer) -> {
						BlockState blockState = world.getBlockState(pos);

						for (Direction direction : DIRECTIONS) {
							if (blockState.shouldLetAirThrough(world, pos, direction)) {
								BlockPos blockPosx = pos.offset(direction);
								BlockState blockState2 = world.getBlockState(blockPosx);
								if (blockState2.shouldLetAirThrough(world, pos, direction.getOpposite())) {
									consumer.accept(pos.offset(direction));
								}
							}
						}
					}, pos -> {
						if (pos.equals(originPos)) {
							setx.add(pos);
							return true;
						} else {
							BlockState blockState = world.getBlockState(pos);
							if (predicate.test(blockState)) {
								setx.add(pos);
								return true;
							} else {
								return false;
							}
						}
					});
					return setx;
				};
				Set<BlockPos> set = (Set<BlockPos>)function.apply((Predicate)state -> !state.isOf(this));
				if (!world.isTheMoon()) {
					set.forEach(pos -> {
						if (world.getBlockState(pos).isOf(this)) {
							world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
						}

						world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.1F, 1.5F + 0.5F * world.random.nextFloat());
						world.spawnParticles(ParticleTypes.CLOUD, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 1, 0.25, 0.25, 0.25, 1.0);
					});
				} else {
					int i = set.size();
					if (i != 1) {
						try {
							IS_DISSIPATING.set(true);
							if (i >= 128) {
								((Set)function.apply((Predicate)state -> true)).forEach(pos -> {
									if (world.getBlockState(pos).isOf(this)) {
										world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
									}

									world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.1F, 1.5F + 0.5F * world.random.nextFloat());
									world.spawnParticles(ParticleTypes.CLOUD, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 1, 0.25, 0.25, 0.25, 1.0);
								});
							} else if (i > 1) {
								AtomicInteger atomicInteger = new AtomicInteger(0);
								set.forEach(pos -> {
									if (atomicInteger.get() > 2) {
										world.playSound(null, pos, SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, SoundCategory.BLOCKS, 0.2F, 1.5F + 0.5F * world.random.nextFloat());
										world.spawnParticles(ParticleTypes.CLOUD, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 1, 0.25, 0.25, 0.25, 0.0);
									}

									BlockState blockState = world.getBlockState(pos);
									if (blockState.isAir() && !blockState.isOf(this)) {
										atomicInteger.incrementAndGet();
										world.setBlockState(pos, this.getDefaultState(), Block.NOTIFY_ALL);
									}
								});
							}
						} finally {
							IS_DISSIPATING.set(false);
						}
					}
				}
			}
		}
	}
}
