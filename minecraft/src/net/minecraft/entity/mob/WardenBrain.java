package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.WardenAngerManager;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.DigTask;
import net.minecraft.entity.ai.brain.task.EmergeTask;
import net.minecraft.entity.ai.brain.task.FindInteractionTargetTask;
import net.minecraft.entity.ai.brain.task.FindRoarTargetTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.GoToFuzzedLocationTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookAtDisturbanceTask;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.RemoveInvalidSuspectTask;
import net.minecraft.entity.ai.brain.task.RoarTask;
import net.minecraft.entity.ai.brain.task.SniffTask;
import net.minecraft.entity.ai.brain.task.StartSniffingTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.UpdateAttackTargetTask;
import net.minecraft.entity.ai.brain.task.UpdateRoarTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class WardenBrain {
	private static final int field_36793 = 8;
	private static final int field_36794 = 8;
	private static final float field_36795 = 0.5F;
	public static final float field_36784 = 0.7F;
	public static final int field_36785 = 18;
	public static final int field_36786 = 15;
	public static final int ROAR_DURATION = MathHelper.ceil(84.0F);
	public static final int SNIFF_DURATION = MathHelper.ceil(68.0F);
	public static final int EMERGE_DURATION = MathHelper.ceil(134.0F);
	public static final int DIG_DURATION = MathHelper.ceil(110.0F);
	public static final int field_36791 = 1200;
	public static final int field_36792 = 100;

	protected static Brain<?> create(WardenEntity warden, Brain<WardenEntity> brain) {
		addCoreActivities(brain);
		addEmergeActivities(brain);
		addDigActivities(brain);
		addIdleActivities(brain);
		addRoarActivities(brain);
		addFightActivities(warden, brain);
		addInvestigateActivities(brain);
		addSniffActivities(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
		return brain;
	}

	private static void addCoreActivities(Brain<WardenEntity> brain) {
		brain.setTaskList(
			Activity.CORE,
			0,
			ImmutableList.of(
				new LookAtDisturbanceTask(), new LookAroundTask(45, 90), new WanderAroundTask(), new UpdateAttackTargetTask<>(warden -> true, WardenBrain::getLastAttacker)
			)
		);
	}

	private static void addEmergeActivities(Brain<WardenEntity> brain) {
		brain.setTaskList(Activity.EMERGE, 5, ImmutableList.of(new EmergeTask<>(EMERGE_DURATION)), MemoryModuleType.IS_EMERGING);
	}

	private static void addDigActivities(Brain<WardenEntity> brain) {
		brain.setTaskList(Activity.DIG, 5, ImmutableList.of(new DigTask<>(DIG_DURATION)), MemoryModuleType.IS_DIGGING);
	}

	private static void addIdleActivities(Brain<WardenEntity> brain) {
		brain.setTaskList(
			Activity.IDLE,
			10,
			ImmutableList.of(
				new FindRoarTargetTask<>(WardenBrain::hasNoTarget, WardenBrain::getPrimeSuspect),
				new StartSniffingTask(),
				getStrollOrWaitTask(),
				new FindInteractionTargetTask(EntityType.PLAYER, 4)
			)
		);
	}

	private static void addInvestigateActivities(Brain<WardenEntity> brain) {
		brain.setTaskList(
			Activity.INVESTIGATE,
			5,
			ImmutableList.of(
				new FindRoarTargetTask<>(WardenBrain::hasNoTarget, WardenBrain::getPrimeSuspect),
				new GoToFuzzedLocationTask(MemoryModuleType.DISTURBANCE_LOCATION, 2, 0.7F),
				new WaitTask(10, 20)
			),
			MemoryModuleType.DISTURBANCE_LOCATION
		);
	}

	private static void addSniffActivities(Brain<WardenEntity> brain) {
		brain.setTaskList(
			Activity.SNIFF,
			5,
			ImmutableList.of(new FindRoarTargetTask<>(WardenBrain::hasNoTarget, WardenBrain::getPrimeSuspect), new SniffTask(SNIFF_DURATION)),
			MemoryModuleType.IS_SNIFFING
		);
	}

	private static void addRoarActivities(Brain<WardenEntity> brain) {
		brain.setTaskList(
			Activity.ROAR,
			10,
			ImmutableList.of(new RemoveInvalidSuspectTask(), new RoarTask(), new UpdateRoarTargetTask(warden -> true, WardenBrain::getPrimeSuspect, ROAR_DURATION)),
			MemoryModuleType.ROAR_TARGET
		);
	}

	private static void addFightActivities(WardenEntity warden, Brain<WardenEntity> brain) {
		brain.setTaskList(
			Activity.FIGHT,
			10,
			ImmutableList.of(
				new ForgetAttackTargetTask<>(entity -> !isAngryAt(warden, entity), WardenBrain::removeDeadSuspect, false),
				new RangedApproachTask(1.2F),
				new FollowMobTask(entity -> isTargeting(warden, entity), 8.0F),
				new MeleeAttackTask(18)
			),
			MemoryModuleType.ATTACK_TARGET
		);
	}

	private static void removeDeadSuspect(WardenEntity warden) {
		LivingEntity livingEntity = (LivingEntity)warden.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
		if (livingEntity.isDead()) {
			warden.getAngerManager().removeSuspect(livingEntity.getUuid());
			warden.setPose(EntityPose.STANDING);
		}

		disturb(warden);
	}

	private static boolean isTargeting(WardenEntity warden, LivingEntity entity) {
		Optional<LivingEntity> optional = warden.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
		return optional.filter(target -> target == entity).isPresent();
	}

	public static boolean isValidTarget(LivingEntity entity) {
		return EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(entity) && !entity.isDead();
	}

	private static RandomTask<WardenEntity> getStrollOrWaitTask() {
		return new RandomTask<>(ImmutableList.of(Pair.of(new StrollTask(0.5F), 2), Pair.of(new WaitTask(30, 60), 1)));
	}

	public static void tick(WardenEntity warden) {
		Brain<WardenEntity> brain = warden.getBrain();
		Optional<Activity> optional = brain.getFirstPossibleNonCoreActivity();
		if (!hasNoTarget(warden) && optional.filter(activityx -> activityx == Activity.ROAR).isPresent()) {
			brain.doExclusively(Activity.FIGHT);
		}

		if (optional.isPresent() && isUndisturbed(warden)) {
			Activity activity = (Activity)optional.get();
			if (activity != Activity.ROAR) {
				brain.remember(MemoryModuleType.IS_DIGGING, true, (long)DIG_DURATION);
			}
		}

		brain.resetPossibleActivities(
			ImmutableList.of(Activity.EMERGE, Activity.DIG, Activity.ROAR, Activity.FIGHT, Activity.INVESTIGATE, Activity.SNIFF, Activity.IDLE)
		);
	}

	public static void disturb(WardenEntity warden) {
		setLastDisturbance(warden, warden.getWorld().getTime());
	}

	public static void setLastDisturbance(WardenEntity warden, long time) {
		warden.getBrain().remember(MemoryModuleType.LAST_DISTURBANCE, time, 1200L);
	}

	public static void lookAtDisturbance(WardenEntity warden, BlockPos pos) {
		if (method_40712(warden)) {
			warden.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(pos), 100L);
			warden.getBrain().remember(MemoryModuleType.DISTURBANCE_LOCATION, pos, 100L);
			warden.getBrain().forget(MemoryModuleType.WALK_TARGET);
		}
	}

	private static boolean isAngryAt(WardenEntity warden, LivingEntity entity) {
		return getLastAttacker(warden).filter(attacker -> attacker == entity).isPresent() || getPrimeSuspect(warden).filter(suspect -> suspect == entity).isPresent();
	}

	private static boolean isUndisturbed(WardenEntity warden) {
		return warden.getBrain().getOptionalMemory(MemoryModuleType.LAST_DISTURBANCE).isEmpty();
	}

	private static boolean hasNoTarget(WardenEntity warden) {
		return getLastAttacker(warden).isEmpty() && warden.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).isEmpty();
	}

	private static boolean method_40712(WardenEntity warden) {
		return getLastAttacker(warden).isEmpty() && warden.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).isEmpty();
	}

	private static Optional<? extends LivingEntity> getLastAttacker(WardenEntity warden) {
		return warden.getBrain()
			.getOptionalMemory(MemoryModuleType.HURT_BY)
			.map(DamageSource::getAttacker)
			.map(attacker -> attacker instanceof LivingEntity livingEntity ? livingEntity : null);
	}

	private static Optional<? extends LivingEntity> getPrimeSuspect(WardenEntity warden) {
		WardenAngerManager wardenAngerManager = warden.getAngerManager();
		if (wardenAngerManager.getPrimeSuspectUuid().isEmpty()) {
			return Optional.empty();
		} else if (wardenAngerManager.getPrimeSuspectAnger() < 80) {
			return Optional.empty();
		} else {
			UUID uUID = (UUID)wardenAngerManager.getPrimeSuspectUuid().get();
			Entity entity = ((ServerWorld)warden.world).getEntity(uUID);
			return entity instanceof LivingEntity ? Optional.of((LivingEntity)entity) : Optional.empty();
		}
	}
}
