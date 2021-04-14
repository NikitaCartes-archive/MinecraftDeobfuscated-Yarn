package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class class_6336<E extends PathAwareEntity> extends Task<E> {
	public static final int field_33461 = 160;
	private final int field_33462;
	private final int field_33463;
	private final int field_33464;
	private final float field_33465;
	private final TargetPredicate field_33466;
	private final int field_33467;
	private final Function<E, SoundEvent> field_33468;
	private Optional<Long> field_33469 = Optional.empty();
	private Optional<class_6336.class_6337> field_33470 = Optional.empty();

	public class_6336(int i, int j, int k, float f, TargetPredicate targetPredicate, int l, Function<E, SoundEvent> function) {
		super(
			ImmutableMap.of(
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.RAM_COOLDOWN_TICKS,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.VISIBLE_MOBS,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.RAM_TARGET,
				MemoryModuleState.VALUE_ABSENT
			),
			160
		);
		this.field_33462 = i;
		this.field_33463 = j;
		this.field_33464 = k;
		this.field_33465 = f;
		this.field_33466 = targetPredicate;
		this.field_33467 = l;
		this.field_33468 = function;
	}

	protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		Brain<?> brain = pathAwareEntity.getBrain();
		brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS)
			.flatMap(list -> list.stream().filter(livingEntity -> this.field_33466.test(pathAwareEntity, livingEntity)).findFirst())
			.ifPresent(livingEntity -> this.method_36268(pathAwareEntity, livingEntity));
	}

	protected void finishRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		Brain<?> brain = pathAwareEntity.getBrain();
		if (!brain.hasMemoryModule(MemoryModuleType.RAM_TARGET)) {
			serverWorld.sendEntityStatus(pathAwareEntity, (byte)59);
			brain.remember(MemoryModuleType.RAM_COOLDOWN_TICKS, this.field_33462);
		}
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
		return this.field_33470.isPresent() && ((class_6336.class_6337)this.field_33470.get()).method_36276().isAlive();
	}

	protected void keepRunning(ServerWorld serverWorld, E pathAwareEntity, long l) {
		if (this.field_33470.isPresent()) {
			pathAwareEntity.getBrain()
				.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(((class_6336.class_6337)this.field_33470.get()).method_36273(), this.field_33465, 0));
			pathAwareEntity.getBrain()
				.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(((class_6336.class_6337)this.field_33470.get()).method_36276(), true));
			boolean bl = !((class_6336.class_6337)this.field_33470.get())
				.method_36276()
				.getBlockPos()
				.equals(((class_6336.class_6337)this.field_33470.get()).method_36275());
			if (bl) {
				serverWorld.sendEntityStatus(pathAwareEntity, (byte)59);
				pathAwareEntity.getNavigation().stop();
				this.method_36268(pathAwareEntity, ((class_6336.class_6337)this.field_33470.get()).field_33473);
			} else {
				BlockPos blockPos = pathAwareEntity.getBlockPos();
				if (blockPos.equals(((class_6336.class_6337)this.field_33470.get()).method_36273())) {
					serverWorld.sendEntityStatus(pathAwareEntity, (byte)58);
					if (!this.field_33469.isPresent()) {
						this.field_33469 = Optional.of(l);
					}

					if (l - (Long)this.field_33469.get() >= (long)this.field_33467) {
						pathAwareEntity.getBrain()
							.remember(MemoryModuleType.RAM_TARGET, this.method_36266(blockPos, ((class_6336.class_6337)this.field_33470.get()).method_36275()));
						serverWorld.playSoundFromEntity(null, pathAwareEntity, (SoundEvent)this.field_33468.apply(pathAwareEntity), SoundCategory.HOSTILE, 1.0F, 1.0F);
						this.field_33470 = Optional.empty();
					}
				}
			}
		}
	}

	private Vec3d method_36266(BlockPos blockPos, BlockPos blockPos2) {
		double d = 0.5;
		double e = 0.5 * (double)MathHelper.sign((double)(blockPos2.getX() - blockPos.getX()));
		double f = 0.5 * (double)MathHelper.sign((double)(blockPos2.getZ() - blockPos.getZ()));
		return Vec3d.ofBottomCenter(blockPos2).add(e, 0.0, f);
	}

	private Optional<BlockPos> method_36262(PathAwareEntity pathAwareEntity, LivingEntity livingEntity) {
		BlockPos blockPos = livingEntity.getBlockPos();
		if (!this.method_36263(pathAwareEntity, blockPos)) {
			return Optional.empty();
		} else {
			List<BlockPos> list = Lists.<BlockPos>newArrayList();
			BlockPos.Mutable mutable = blockPos.mutableCopy();

			for (Direction direction : Direction.Type.HORIZONTAL) {
				mutable.set(blockPos);

				for (int i = 0; i < this.field_33464; i++) {
					if (!this.method_36263(pathAwareEntity, mutable.move(direction))) {
						mutable.move(direction.getOpposite());
						break;
					}
				}

				if (mutable.getManhattanDistance(blockPos) >= this.field_33463) {
					list.add(mutable.toImmutable());
				}
			}

			EntityNavigation entityNavigation = pathAwareEntity.getNavigation();
			return list.stream().sorted(Comparator.comparingDouble(pathAwareEntity.getBlockPos()::getSquaredDistance)).filter(blockPosx -> {
				Path path = entityNavigation.findPathTo(blockPosx, 0);
				return path != null && path.reachesTarget();
			}).findFirst();
		}
	}

	private boolean method_36263(PathAwareEntity pathAwareEntity, BlockPos blockPos) {
		return pathAwareEntity.getNavigation().isValidPosition(blockPos)
			&& pathAwareEntity.getPathfindingPenalty(LandPathNodeMaker.getLandNodeType(pathAwareEntity.world, blockPos.mutableCopy())) == 0.0F;
	}

	private void method_36268(PathAwareEntity pathAwareEntity, LivingEntity livingEntity) {
		this.field_33469 = Optional.empty();
		this.field_33470 = this.method_36262(pathAwareEntity, livingEntity)
			.map(blockPos -> new class_6336.class_6337(blockPos, livingEntity.getBlockPos(), livingEntity));
	}

	public static class class_6337 {
		private final BlockPos field_33471;
		private final BlockPos field_33472;
		private final LivingEntity field_33473;

		public class_6337(BlockPos blockPos, BlockPos blockPos2, LivingEntity livingEntity) {
			this.field_33471 = blockPos;
			this.field_33472 = blockPos2;
			this.field_33473 = livingEntity;
		}

		public BlockPos method_36273() {
			return this.field_33471;
		}

		public BlockPos method_36275() {
			return this.field_33472;
		}

		public LivingEntity method_36276() {
			return this.field_33473;
		}
	}
}
