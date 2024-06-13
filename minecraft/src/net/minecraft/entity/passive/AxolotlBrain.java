package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.BreedTask;
import net.minecraft.entity.ai.brain.task.CompositeTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetTask;
import net.minecraft.entity.ai.brain.task.GoTowardsLookTargetTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookAtMobWithIntervalTask;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.MoveToTargetTask;
import net.minecraft.entity.ai.brain.task.PlayDeadTask;
import net.minecraft.entity.ai.brain.task.PlayDeadTimerTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.SeekWaterTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.ai.brain.task.TemptTask;
import net.minecraft.entity.ai.brain.task.TemptationCooldownTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.WalkTowardClosestAdultTask;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;

/**
 * Represents the definition of an {@linkplain AxolotlEntity axolotl entity} brain.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Activities associated to the {@linkplain AxolotlEntity axolotl entity} brain</caption>
 * <tr>
 *   <th>Activity</th><th>Tasks</th>
 * </tr>
 * <tr>
 *   <td>{@link net.minecraft.entity.ai.brain.Activity#CORE}</td>
 *   <td><ul>
 *     <li>{@link net.minecraft.entity.ai.brain.task.LookAroundTask}</li>
 *     <li>{@link net.minecraft.entity.ai.brain.task.WanderAroundTask}</li>
 *     <li>{@link net.minecraft.entity.ai.brain.task.PlayDeadTimerTask}</li>
 *     <li>{@link net.minecraft.entity.ai.brain.task.TemptationCooldownTask}</li>
 *   </ul></td>
 * </tr>
 * <tr>
 *   <td>{@link net.minecraft.entity.ai.brain.Activity#IDLE}</td>
 *   <td><ul>
 *     <li>{@link net.minecraft.entity.ai.brain.task.LookAtMobTask LookAtMobTask(PLAYER)} (time limited)</li>
 *     <li>{@link net.minecraft.entity.ai.brain.task.BreedTask}</li>
 *     <li>{@link net.minecraft.entity.ai.brain.task.TemptTask} (random)</li>
 *     <li>{@link net.minecraft.entity.ai.brain.task.WalkTowardClosestAdultTask}</li>
 *     <li>{@link net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask}</li>
 *     <li>{@link net.minecraft.entity.ai.brain.task.SeekWaterTask}</li>
 *     <li>{@link net.minecraft.entity.ai.brain.task.CompositeTask}</li>
 *   </ul></td>
 * </tr>
 * <tr>
 *   <td>{@link net.minecraft.entity.ai.brain.Activity#FIGHT}</td>
 *   <td><ul>
 *     <li>{@link net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask}</li>
 *     <li>{@link net.minecraft.entity.ai.brain.task.RangedApproachTask}</li>
 *     <li>{@link net.minecraft.entity.ai.brain.task.MeleeAttackTask}</li>
 *     <li>{@link net.minecraft.entity.ai.brain.task.ForgetTask}</li>
 *   </ul></td>
 * </tr>
 * <tr>
 *   <td>{@link net.minecraft.entity.ai.brain.Activity#PLAY_DEAD}</td>
 *   <td><ul>
 *     <li>{@link net.minecraft.entity.ai.brain.task.PlayDeadTask}</li>
 *     <li>{@link net.minecraft.entity.ai.brain.task.ForgetTask}</li>
 *   </ul></td>
 * </tr>
 * </table>
 * </div>
 */
public class AxolotlBrain {
	private static final UniformIntProvider WALK_TOWARD_ADULT_RANGE = UniformIntProvider.create(5, 16);
	private static final float BREEDING_SPEED = 0.2F;
	private static final float ON_LAND_SPEED = 0.15F;
	private static final float IDLE_SPEED = 0.5F;
	private static final float TARGET_APPROACHING_SPEED = 0.6F;
	private static final float ADULT_FOLLOWING_SPEED = 0.6F;

	protected static Brain<?> create(Brain<AxolotlEntity> brain) {
		addCoreActivities(brain);
		addIdleActivities(brain);
		addFightActivities(brain);
		addPlayDeadActivities(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
		return brain;
	}

	private static void addPlayDeadActivities(Brain<AxolotlEntity> brain) {
		brain.setTaskList(
			Activity.PLAY_DEAD,
			ImmutableList.of(Pair.of(0, new PlayDeadTask()), Pair.of(1, ForgetTask.create(LookTargetUtil::hasBreedTarget, MemoryModuleType.PLAY_DEAD_TICKS))),
			ImmutableSet.of(Pair.of(MemoryModuleType.PLAY_DEAD_TICKS, MemoryModuleState.VALUE_PRESENT)),
			ImmutableSet.of(MemoryModuleType.PLAY_DEAD_TICKS)
		);
	}

	private static void addFightActivities(Brain<AxolotlEntity> brain) {
		brain.setTaskList(
			Activity.FIGHT,
			0,
			ImmutableList.of(
				ForgetAttackTargetTask.<MobEntity>create(AxolotlEntity::appreciatePlayer),
				RangedApproachTask.create(AxolotlBrain::getTargetApproachingSpeed),
				MeleeAttackTask.create(20),
				ForgetTask.<MobEntity>create(LookTargetUtil::hasBreedTarget, MemoryModuleType.ATTACK_TARGET)
			),
			MemoryModuleType.ATTACK_TARGET
		);
	}

	private static void addCoreActivities(Brain<AxolotlEntity> brain) {
		brain.setTaskList(
			Activity.CORE,
			0,
			ImmutableList.of(
				new LookAroundTask(45, 90), new MoveToTargetTask(), PlayDeadTimerTask.create(), new TemptationCooldownTask(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)
			)
		);
	}

	private static void addIdleActivities(Brain<AxolotlEntity> brain) {
		brain.setTaskList(
			Activity.IDLE,
			ImmutableList.of(
				Pair.of(0, LookAtMobWithIntervalTask.follow(EntityType.PLAYER, 6.0F, UniformIntProvider.create(30, 60))),
				Pair.of(1, new BreedTask(EntityType.AXOLOTL, 0.2F, 2)),
				Pair.of(
					2,
					new RandomTask<>(
						ImmutableList.of(
							Pair.of(new TemptTask(AxolotlBrain::getTemptedSpeed), 1),
							Pair.of(WalkTowardClosestAdultTask.create(WALK_TOWARD_ADULT_RANGE, AxolotlBrain::getAdultFollowingSpeed), 1)
						)
					)
				),
				Pair.of(3, UpdateAttackTargetTask.create(AxolotlBrain::getAttackTarget)),
				Pair.of(3, SeekWaterTask.create(6, 0.15F)),
				Pair.of(
					4,
					new CompositeTask<>(
						ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT),
						ImmutableSet.of(),
						CompositeTask.Order.ORDERED,
						CompositeTask.RunMode.TRY_ALL,
						ImmutableList.of(
							Pair.of(StrollTask.createDynamicRadius(0.5F), 2),
							Pair.of(StrollTask.create(0.15F, false), 2),
							Pair.of(GoTowardsLookTargetTask.create(AxolotlBrain::canGoToLookTarget, AxolotlBrain::getTemptedSpeed, 3), 3),
							Pair.of(TaskTriggerer.predicate(Entity::isInsideWaterOrBubbleColumn), 5),
							Pair.of(TaskTriggerer.predicate(Entity::isOnGround), 5)
						)
					)
				)
			)
		);
	}

	private static boolean canGoToLookTarget(LivingEntity entity) {
		World world = entity.getWorld();
		Optional<LookTarget> optional = entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.LOOK_TARGET);
		if (optional.isPresent()) {
			BlockPos blockPos = ((LookTarget)optional.get()).getBlockPos();
			return world.isWater(blockPos) == entity.isInsideWaterOrBubbleColumn();
		} else {
			return false;
		}
	}

	public static void updateActivities(AxolotlEntity axolotl) {
		Brain<AxolotlEntity> brain = axolotl.getBrain();
		Activity activity = (Activity)brain.getFirstPossibleNonCoreActivity().orElse(null);
		if (activity != Activity.PLAY_DEAD) {
			brain.resetPossibleActivities(ImmutableList.of(Activity.PLAY_DEAD, Activity.FIGHT, Activity.IDLE));
			if (activity == Activity.FIGHT && brain.getFirstPossibleNonCoreActivity().orElse(null) != Activity.FIGHT) {
				brain.remember(MemoryModuleType.HAS_HUNTING_COOLDOWN, true, 2400L);
			}
		}
	}

	/**
	 * {@return the axolotl's speed when approaching the attack target}
	 */
	private static float getTargetApproachingSpeed(LivingEntity entity) {
		return entity.isInsideWaterOrBubbleColumn() ? 0.6F : 0.15F;
	}

	/**
	 * {@return the axolotl's speed when a baby axolotl is following an adult}
	 */
	private static float getAdultFollowingSpeed(LivingEntity entity) {
		return entity.isInsideWaterOrBubbleColumn() ? 0.6F : 0.15F;
	}

	/**
	 * {@return the axolotl's speed when the axolotl is being tempted}
	 */
	private static float getTemptedSpeed(LivingEntity entity) {
		return entity.isInsideWaterOrBubbleColumn() ? 0.5F : 0.15F;
	}

	private static Optional<? extends LivingEntity> getAttackTarget(AxolotlEntity axolotl) {
		return LookTargetUtil.hasBreedTarget(axolotl) ? Optional.empty() : axolotl.getBrain().getOptionalRegisteredMemory(MemoryModuleType.NEAREST_ATTACKABLE);
	}

	public static Predicate<ItemStack> getTemptItemPredicate() {
		return stack -> stack.isIn(ItemTags.AXOLOTL_FOOD);
	}
}
