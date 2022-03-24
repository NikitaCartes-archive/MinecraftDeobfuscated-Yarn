/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.mob;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.DigTask;
import net.minecraft.entity.ai.brain.task.EmergeTask;
import net.minecraft.entity.ai.brain.task.FindRoarTargetTask;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.brain.task.GoToCelebrateTask;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.LookAtDisturbanceTask;
import net.minecraft.entity.ai.brain.task.MeleeAttackTask;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.RoarTask;
import net.minecraft.entity.ai.brain.task.SniffTask;
import net.minecraft.entity.ai.brain.task.StartSniffingTask;
import net.minecraft.entity.ai.brain.task.StayAboveWaterTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.UpdateRoarTargetTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.mob.Angriness;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class WardenBrain {
    private static final int field_38174 = 8;
    private static final float field_38175 = 0.5f;
    private static final float field_38176 = 0.7f;
    private static final float field_38177 = 1.2f;
    private static final int field_38178 = 18;
    private static final int DIG_DURATION = MathHelper.ceil(100.0f);
    public static final int EMERGE_DURATION = MathHelper.ceil(133.59999f);
    public static final int ROAR_DURATION = MathHelper.ceil(84.0f);
    private static final int SNIFF_DURATION = MathHelper.ceil(83.2f);
    public static final int DIG_COOLDOWN = 1200;
    private static final int field_38181 = 100;
    private static final Task<WardenEntity> RESET_DIG_COOLDOWN_TASK = new Task<WardenEntity>(ImmutableMap.of(MemoryModuleType.DIG_COOLDOWN, MemoryModuleState.REGISTERED)){

        @Override
        protected void run(ServerWorld serverWorld, WardenEntity wardenEntity, long l) {
            WardenBrain.resetDigCooldown(wardenEntity);
        }
    };

    public static void tick(WardenEntity warden) {
        warden.getBrain().resetPossibleActivities(ImmutableList.of(Activity.EMERGE, Activity.DIG, Activity.ROAR, Activity.FIGHT, Activity.INVESTIGATE, Activity.SNIFF, Activity.IDLE));
    }

    protected static Brain<?> create(WardenEntity warden, Brain<WardenEntity> brain) {
        WardenBrain.addCoreActivities(brain);
        WardenBrain.addEmergeActivities(brain);
        WardenBrain.addDigActivities(brain);
        WardenBrain.addIdleActivities(brain);
        WardenBrain.addRoarActivities(brain);
        WardenBrain.addFightActivities(warden, brain);
        WardenBrain.addInvestigateActivities(brain);
        WardenBrain.addSniffActivities(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreActivities(Brain<WardenEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(new StayAboveWaterTask(0.8f), new LookAtDisturbanceTask(), new LookAroundTask(45, 90), new WanderAroundTask()));
    }

    private static void addEmergeActivities(Brain<WardenEntity> brain) {
        brain.setTaskList(Activity.EMERGE, 5, ImmutableList.of(new EmergeTask(EMERGE_DURATION)), MemoryModuleType.IS_EMERGING);
    }

    private static void addDigActivities(Brain<WardenEntity> brain) {
        brain.setTaskList(Activity.DIG, ImmutableList.of(Pair.of(0, new DigTask(DIG_DURATION))), ImmutableSet.of(Pair.of(MemoryModuleType.ROAR_TARGET, MemoryModuleState.VALUE_ABSENT), Pair.of(MemoryModuleType.DIG_COOLDOWN, MemoryModuleState.VALUE_ABSENT)));
    }

    private static void addIdleActivities(Brain<WardenEntity> brain) {
        brain.setTaskList(Activity.IDLE, 10, ImmutableList.of(new FindRoarTargetTask<WardenEntity>(WardenEntity::getPrimeSuspect), new StartSniffingTask(), WardenBrain.getStrollOrWaitTask()));
    }

    private static void addInvestigateActivities(Brain<WardenEntity> brain) {
        brain.setTaskList(Activity.INVESTIGATE, 5, ImmutableList.of(new FindRoarTargetTask<WardenEntity>(WardenEntity::getPrimeSuspect), new GoToCelebrateTask(MemoryModuleType.DISTURBANCE_LOCATION, 2, 0.7f), new WaitTask(10, 20)), MemoryModuleType.DISTURBANCE_LOCATION);
    }

    private static void addSniffActivities(Brain<WardenEntity> brain) {
        brain.setTaskList(Activity.SNIFF, 5, ImmutableList.of(new FindRoarTargetTask<WardenEntity>(WardenEntity::getPrimeSuspect), new SniffTask(SNIFF_DURATION)), MemoryModuleType.IS_SNIFFING);
    }

    private static void addRoarActivities(Brain<WardenEntity> brain) {
        brain.setTaskList(Activity.ROAR, 10, ImmutableList.of(new RoarTask(), new UpdateRoarTargetTask(warden -> true, WardenEntity::getPrimeSuspect, ROAR_DURATION)), MemoryModuleType.ROAR_TARGET);
    }

    private static void addFightActivities(WardenEntity warden, Brain<WardenEntity> brain) {
        brain.setTaskList(Activity.FIGHT, 10, ImmutableList.of(RESET_DIG_COOLDOWN_TASK, new ForgetAttackTargetTask<WardenEntity>(entity -> warden.getAngriness() != Angriness.ANGRY || !WardenBrain.isAngryAt(warden, entity), WardenBrain::removeDeadSuspect, false), new RangedApproachTask(1.2f), new FollowMobTask(entity -> WardenBrain.isTargeting(warden, entity), 8.0f), new MeleeAttackTask(18)), MemoryModuleType.ATTACK_TARGET);
    }

    private static boolean isTargeting(WardenEntity warden, LivingEntity entity2) {
        return warden.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).filter(entity -> entity == entity2).isPresent();
    }

    private static void removeDeadSuspect(WardenEntity warden, LivingEntity suspect) {
        if (suspect.isDead()) {
            warden.removeSuspect(suspect);
        }
        WardenBrain.resetDigCooldown(warden);
    }

    private static RandomTask<WardenEntity> getStrollOrWaitTask() {
        return new RandomTask(ImmutableList.of(Pair.of(new StrollTask(0.5f), 2), Pair.of(new WaitTask(30, 60), 1)));
    }

    public static void resetDigCooldown(LivingEntity warden) {
        if (warden.getBrain().hasMemoryModule(MemoryModuleType.DIG_COOLDOWN)) {
            warden.getBrain().remember(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, 1200L);
        }
    }

    public static void lookAtDisturbance(WardenEntity warden, BlockPos pos) {
        if (WardenBrain.method_42234(warden)) {
            WardenBrain.resetDigCooldown(warden);
            warden.getBrain().remember(MemoryModuleType.SNIFF_COOLDOWN, Unit.INSTANCE, 100L);
            warden.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(pos), 100L);
            warden.getBrain().remember(MemoryModuleType.DISTURBANCE_LOCATION, pos, 100L);
            warden.getBrain().forget(MemoryModuleType.WALK_TARGET);
        }
    }

    private static boolean isAngryAt(WardenEntity warden, LivingEntity entity2) {
        return warden.getPrimeSuspect().filter(entity -> entity == entity2).isPresent();
    }

    private static boolean method_42234(WardenEntity warden) {
        return warden.getPrimeSuspect().isEmpty() && warden.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).isEmpty();
    }
}

