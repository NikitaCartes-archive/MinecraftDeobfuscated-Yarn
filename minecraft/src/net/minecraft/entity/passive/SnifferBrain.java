package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.BreedTask;
import net.minecraft.entity.ai.brain.task.FleeTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTargetTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookAtMobTask;
import net.minecraft.entity.ai.brain.task.MoveToTargetTask;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.StayAboveWaterTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.TemptTask;
import net.minecraft.entity.ai.brain.task.TemptationCooldownTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;

public class SnifferBrain {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int field_42676 = 6;
	static final List<SensorType<? extends Sensor<? super SnifferEntity>>> SENSORS = ImmutableList.of(
		SensorType.NEAREST_LIVING_ENTITIES, SensorType.HURT_BY, SensorType.NEAREST_PLAYERS, SensorType.SNIFFER_TEMPTATIONS
	);
	static final List<MemoryModuleType<?>> MEMORY_MODULES = ImmutableList.of(
		MemoryModuleType.LOOK_TARGET,
		MemoryModuleType.WALK_TARGET,
		MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
		MemoryModuleType.PATH,
		MemoryModuleType.IS_PANICKING,
		MemoryModuleType.SNIFFER_SNIFFING_TARGET,
		MemoryModuleType.SNIFFER_DIGGING,
		MemoryModuleType.SNIFFER_HAPPY,
		MemoryModuleType.SNIFF_COOLDOWN,
		MemoryModuleType.SNIFFER_EXPLORED_POSITIONS,
		MemoryModuleType.VISIBLE_MOBS,
		MemoryModuleType.BREED_TARGET,
		MemoryModuleType.TEMPTING_PLAYER,
		MemoryModuleType.TEMPTATION_COOLDOWN_TICKS,
		MemoryModuleType.IS_TEMPTED
	);
	private static final int SNIFF_COOLDOWN_EXPIRY = 9600;
	private static final float field_42678 = 1.0F;
	private static final float FLEE_SPEED = 2.0F;
	private static final float field_42680 = 1.25F;
	private static final float field_44476 = 1.25F;

	public static Predicate<ItemStack> getTemptItemPredicate() {
		return stack -> stack.isIn(ItemTags.SNIFFER_FOOD);
	}

	protected static Brain<?> create(Brain<SnifferEntity> brain) {
		addCoreActivities(brain);
		addIdleActivities(brain);
		addSniffActivities(brain);
		addDigActivities(brain);
		brain.setCoreActivities(Set.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
		return brain;
	}

	static SnifferEntity stopDiggingOrSniffing(SnifferEntity sniffer) {
		sniffer.getBrain().forget(MemoryModuleType.SNIFFER_DIGGING);
		sniffer.getBrain().forget(MemoryModuleType.SNIFFER_SNIFFING_TARGET);
		return sniffer.startState(SnifferEntity.State.IDLING);
	}

	private static void addCoreActivities(Brain<SnifferEntity> brain) {
		brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new StayAboveWaterTask(0.8F), new FleeTask<SnifferEntity>(2.0F) {
			protected void run(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
				SnifferBrain.stopDiggingOrSniffing(snifferEntity);
				super.run(serverWorld, snifferEntity, l);
			}
		}, new MoveToTargetTask(500, 700), new TemptationCooldownTask(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)));
	}

	private static void addSniffActivities(Brain<SnifferEntity> brain) {
		brain.setTaskList(
			Activity.SNIFF,
			ImmutableList.of(Pair.of(0, new SnifferBrain.SearchingTask())),
			Set.of(
				Pair.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT),
				Pair.of(MemoryModuleType.SNIFFER_SNIFFING_TARGET, MemoryModuleState.VALUE_PRESENT),
				Pair.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_PRESENT)
			)
		);
	}

	private static void addDigActivities(Brain<SnifferEntity> brain) {
		brain.setTaskList(
			Activity.DIG,
			ImmutableList.of(Pair.of(0, new SnifferBrain.DiggingTask(160, 180)), Pair.of(0, new SnifferBrain.FinishDiggingTask(40))),
			Set.of(
				Pair.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT),
				Pair.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT),
				Pair.of(MemoryModuleType.SNIFFER_DIGGING, MemoryModuleState.VALUE_PRESENT)
			)
		);
	}

	private static void addIdleActivities(Brain<SnifferEntity> brain) {
		brain.setTaskList(
			Activity.IDLE,
			ImmutableList.of(
				Pair.of(0, new BreedTask(EntityType.SNIFFER) {
					@Override
					protected void run(ServerWorld serverWorld, AnimalEntity animalEntity, long l) {
						SnifferBrain.stopDiggingOrSniffing((SnifferEntity)animalEntity);
						super.run(serverWorld, animalEntity, l);
					}
				}),
				Pair.of(1, new TemptTask(sniffer -> 1.25F, sniffer -> sniffer.isBaby() ? 2.5 : 3.5) {
					@Override
					protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
						SnifferBrain.stopDiggingOrSniffing((SnifferEntity)pathAwareEntity);
						super.run(serverWorld, pathAwareEntity, l);
					}
				}),
				Pair.of(2, new LookAroundTask(45, 90)),
				Pair.of(3, new SnifferBrain.FeelHappyTask(40, 100)),
				Pair.of(
					4,
					new RandomTask<>(
						ImmutableList.of(
							Pair.of(GoTowardsLookTargetTask.create(1.0F, 3), 2),
							Pair.of(new SnifferBrain.ScentingTask(40, 80), 1),
							Pair.of(new SnifferBrain.SniffingTask(40, 80), 1),
							Pair.of(LookAtMobTask.create(EntityType.PLAYER, 6.0F), 1),
							Pair.of(StrollTask.create(1.0F), 1),
							Pair.of(new WaitTask(5, 20), 2)
						)
					)
				)
			),
			Set.of(Pair.of(MemoryModuleType.SNIFFER_DIGGING, MemoryModuleState.VALUE_ABSENT))
		);
	}

	static void updateActivities(SnifferEntity sniffer) {
		sniffer.getBrain().resetPossibleActivities(ImmutableList.of(Activity.DIG, Activity.SNIFF, Activity.IDLE));
	}

	static class DiggingTask extends MultiTickTask<SnifferEntity> {
		DiggingTask(int minRunTime, int maxRunTime) {
			super(
				Map.of(
					MemoryModuleType.IS_PANICKING,
					MemoryModuleState.VALUE_ABSENT,
					MemoryModuleType.WALK_TARGET,
					MemoryModuleState.VALUE_ABSENT,
					MemoryModuleType.SNIFFER_DIGGING,
					MemoryModuleState.VALUE_PRESENT,
					MemoryModuleType.SNIFF_COOLDOWN,
					MemoryModuleState.VALUE_ABSENT
				),
				minRunTime,
				maxRunTime
			);
		}

		protected boolean shouldRun(ServerWorld serverWorld, SnifferEntity snifferEntity) {
			return snifferEntity.canTryToDig();
		}

		protected boolean shouldKeepRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
			return snifferEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.SNIFFER_DIGGING).isPresent()
				&& snifferEntity.canDig()
				&& !snifferEntity.isInLove();
		}

		protected void run(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
			snifferEntity.startState(SnifferEntity.State.DIGGING);
		}

		protected void finishRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
			boolean bl = this.isTimeLimitExceeded(l);
			if (bl) {
				snifferEntity.getBrain().remember(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, 9600L);
			} else {
				SnifferBrain.stopDiggingOrSniffing(snifferEntity);
			}
		}
	}

	static class FeelHappyTask extends MultiTickTask<SnifferEntity> {
		FeelHappyTask(int minRunTime, int maxRunTime) {
			super(Map.of(MemoryModuleType.SNIFFER_HAPPY, MemoryModuleState.VALUE_PRESENT), minRunTime, maxRunTime);
		}

		protected boolean shouldKeepRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
			return true;
		}

		protected void run(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
			snifferEntity.startState(SnifferEntity.State.FEELING_HAPPY);
		}

		protected void finishRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
			snifferEntity.startState(SnifferEntity.State.IDLING);
			snifferEntity.getBrain().forget(MemoryModuleType.SNIFFER_HAPPY);
		}
	}

	static class FinishDiggingTask extends MultiTickTask<SnifferEntity> {
		FinishDiggingTask(int runTime) {
			super(
				Map.of(
					MemoryModuleType.IS_PANICKING,
					MemoryModuleState.VALUE_ABSENT,
					MemoryModuleType.WALK_TARGET,
					MemoryModuleState.VALUE_ABSENT,
					MemoryModuleType.SNIFFER_DIGGING,
					MemoryModuleState.VALUE_PRESENT,
					MemoryModuleType.SNIFF_COOLDOWN,
					MemoryModuleState.VALUE_PRESENT
				),
				runTime,
				runTime
			);
		}

		protected boolean shouldRun(ServerWorld serverWorld, SnifferEntity snifferEntity) {
			return true;
		}

		protected boolean shouldKeepRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
			return snifferEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.SNIFFER_DIGGING).isPresent();
		}

		protected void run(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
			snifferEntity.startState(SnifferEntity.State.RISING);
		}

		protected void finishRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
			boolean bl = this.isTimeLimitExceeded(l);
			snifferEntity.startState(SnifferEntity.State.IDLING).finishDigging(bl);
			snifferEntity.getBrain().forget(MemoryModuleType.SNIFFER_DIGGING);
			snifferEntity.getBrain().remember(MemoryModuleType.SNIFFER_HAPPY, true);
		}
	}

	static class ScentingTask extends MultiTickTask<SnifferEntity> {
		ScentingTask(int minRunTime, int maxRunTime) {
			super(
				Map.of(
					MemoryModuleType.IS_PANICKING,
					MemoryModuleState.VALUE_ABSENT,
					MemoryModuleType.SNIFFER_DIGGING,
					MemoryModuleState.VALUE_ABSENT,
					MemoryModuleType.SNIFFER_SNIFFING_TARGET,
					MemoryModuleState.VALUE_ABSENT,
					MemoryModuleType.SNIFFER_HAPPY,
					MemoryModuleState.VALUE_ABSENT,
					MemoryModuleType.BREED_TARGET,
					MemoryModuleState.VALUE_ABSENT
				),
				minRunTime,
				maxRunTime
			);
		}

		protected boolean shouldRun(ServerWorld serverWorld, SnifferEntity snifferEntity) {
			return !snifferEntity.isTempted();
		}

		protected boolean shouldKeepRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
			return true;
		}

		protected void run(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
			snifferEntity.startState(SnifferEntity.State.SCENTING);
		}

		protected void finishRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
			snifferEntity.startState(SnifferEntity.State.IDLING);
		}
	}

	static class SearchingTask extends MultiTickTask<SnifferEntity> {
		SearchingTask() {
			super(
				Map.of(
					MemoryModuleType.WALK_TARGET,
					MemoryModuleState.VALUE_PRESENT,
					MemoryModuleType.IS_PANICKING,
					MemoryModuleState.VALUE_ABSENT,
					MemoryModuleType.SNIFFER_SNIFFING_TARGET,
					MemoryModuleState.VALUE_PRESENT
				),
				600
			);
		}

		protected boolean shouldRun(ServerWorld serverWorld, SnifferEntity snifferEntity) {
			return snifferEntity.canTryToDig();
		}

		protected boolean shouldKeepRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
			if (!snifferEntity.canTryToDig()) {
				snifferEntity.startState(SnifferEntity.State.IDLING);
				return false;
			} else {
				Optional<BlockPos> optional = snifferEntity.getBrain()
					.getOptionalRegisteredMemory(MemoryModuleType.WALK_TARGET)
					.map(WalkTarget::getLookTarget)
					.map(LookTarget::getBlockPos);
				Optional<BlockPos> optional2 = snifferEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.SNIFFER_SNIFFING_TARGET);
				return !optional.isEmpty() && !optional2.isEmpty() ? ((BlockPos)optional2.get()).equals(optional.get()) : false;
			}
		}

		protected void run(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
			snifferEntity.startState(SnifferEntity.State.SEARCHING);
		}

		protected void finishRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
			if (snifferEntity.canDig() && snifferEntity.canTryToDig()) {
				snifferEntity.getBrain().remember(MemoryModuleType.SNIFFER_DIGGING, true);
			}

			snifferEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
			snifferEntity.getBrain().forget(MemoryModuleType.SNIFFER_SNIFFING_TARGET);
		}
	}

	static class SniffingTask extends MultiTickTask<SnifferEntity> {
		SniffingTask(int minRunTime, int maxRunTime) {
			super(
				Map.of(
					MemoryModuleType.WALK_TARGET,
					MemoryModuleState.VALUE_ABSENT,
					MemoryModuleType.SNIFFER_SNIFFING_TARGET,
					MemoryModuleState.VALUE_ABSENT,
					MemoryModuleType.SNIFF_COOLDOWN,
					MemoryModuleState.VALUE_ABSENT
				),
				minRunTime,
				maxRunTime
			);
		}

		protected boolean shouldRun(ServerWorld serverWorld, SnifferEntity snifferEntity) {
			return !snifferEntity.isBaby() && snifferEntity.canTryToDig();
		}

		protected boolean shouldKeepRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
			return snifferEntity.canTryToDig();
		}

		protected void run(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
			snifferEntity.startState(SnifferEntity.State.SNIFFING);
		}

		protected void finishRunning(ServerWorld serverWorld, SnifferEntity snifferEntity, long l) {
			boolean bl = this.isTimeLimitExceeded(l);
			snifferEntity.startState(SnifferEntity.State.IDLING);
			if (bl) {
				snifferEntity.findSniffingTargetPos().ifPresent(pos -> {
					snifferEntity.getBrain().remember(MemoryModuleType.SNIFFER_SNIFFING_TARGET, pos);
					snifferEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, 1.25F, 0));
				});
			}
		}
	}
}
