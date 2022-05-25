/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

public class FrogEatEntityTask
extends Task<FrogEntity> {
    public static final int RUN_TIME = 100;
    public static final int CATCH_DURATION = 6;
    public static final int EAT_DURATION = 10;
    private static final float MAX_DISTANCE = 1.75f;
    private static final float VELOCITY_MULTIPLIER = 0.75f;
    public static final int UNREACHABLE_TONGUE_TARGETS_START_TIME = 100;
    public static final int MAX_UNREACHABLE_TONGUE_TARGETS = 5;
    private int eatTick;
    private int moveToTargetTick;
    private final SoundEvent tongueSound;
    private final SoundEvent eatSound;
    private Vec3d targetPos;
    private Phase phase = Phase.DONE;

    public FrogEatEntityTask(SoundEvent tongueSound, SoundEvent eatSound) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT), 100);
        this.tongueSound = tongueSound;
        this.eatSound = eatSound;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, FrogEntity frogEntity) {
        LivingEntity livingEntity = frogEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
        boolean bl = this.isTargetReachable(frogEntity, livingEntity);
        if (!bl) {
            frogEntity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
            this.markTargetAsUnreachable(frogEntity, livingEntity);
        }
        return bl && frogEntity.getPose() != EntityPose.CROAKING && FrogEntity.isValidFrogFood(livingEntity);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        return frogEntity.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET) && this.phase != Phase.DONE && !frogEntity.getBrain().hasMemoryModule(MemoryModuleType.IS_PANICKING);
    }

    @Override
    protected void run(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        LivingEntity livingEntity = frogEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
        LookTargetUtil.lookAt(frogEntity, livingEntity);
        frogEntity.setFrogTarget(livingEntity);
        frogEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(livingEntity.getPos(), 2.0f, 0));
        this.moveToTargetTick = 10;
        this.phase = Phase.MOVE_TO_TARGET;
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        frogEntity.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
        frogEntity.clearFrogTarget();
        frogEntity.setPose(EntityPose.STANDING);
    }

    private void eat(ServerWorld world, FrogEntity frog) {
        Entity entity;
        world.playSoundFromEntity(null, frog, this.eatSound, SoundCategory.NEUTRAL, 2.0f, 1.0f);
        Optional<Entity> optional = frog.getFrogTarget();
        if (optional.isPresent() && (entity = optional.get()).isAlive()) {
            frog.tryAttack(entity);
            if (!entity.isAlive()) {
                entity.remove(Entity.RemovalReason.KILLED);
            }
        }
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        LivingEntity livingEntity = frogEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
        frogEntity.setFrogTarget(livingEntity);
        switch (this.phase) {
            case MOVE_TO_TARGET: {
                if (livingEntity.distanceTo(frogEntity) < 1.75f) {
                    serverWorld.playSoundFromEntity(null, frogEntity, this.tongueSound, SoundCategory.NEUTRAL, 2.0f, 1.0f);
                    frogEntity.setPose(EntityPose.USING_TONGUE);
                    livingEntity.setVelocity(livingEntity.getPos().relativize(frogEntity.getPos()).normalize().multiply(0.75));
                    this.targetPos = livingEntity.getPos();
                    this.eatTick = 0;
                    this.phase = Phase.CATCH_ANIMATION;
                    break;
                }
                if (this.moveToTargetTick <= 0) {
                    frogEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(livingEntity.getPos(), 2.0f, 0));
                    this.moveToTargetTick = 10;
                    break;
                }
                --this.moveToTargetTick;
                break;
            }
            case CATCH_ANIMATION: {
                if (this.eatTick++ < 6) break;
                this.phase = Phase.EAT_ANIMATION;
                this.eat(serverWorld, frogEntity);
                break;
            }
            case EAT_ANIMATION: {
                if (this.eatTick >= 10) {
                    this.phase = Phase.DONE;
                    break;
                }
                ++this.eatTick;
                break;
            }
        }
    }

    private boolean isTargetReachable(FrogEntity entity, LivingEntity target) {
        Path path = entity.getNavigation().findPathTo(target, 0);
        return path.getManhattanDistanceFromTarget() < 1.75f;
    }

    private void markTargetAsUnreachable(FrogEntity entity, LivingEntity target) {
        boolean bl;
        List list = entity.getBrain().getOptionalMemory(MemoryModuleType.UNREACHABLE_TONGUE_TARGETS).orElseGet(ArrayList::new);
        boolean bl2 = bl = !list.contains(target.getUuid());
        if (list.size() == 5 && bl) {
            list.remove(0);
        }
        if (bl) {
            list.add(target.getUuid());
        }
        entity.getBrain().remember(MemoryModuleType.UNREACHABLE_TONGUE_TARGETS, list, 100L);
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (FrogEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (FrogEntity)entity, time);
    }

    static enum Phase {
        MOVE_TO_TARGET,
        CATCH_ANIMATION,
        EAT_ANIMATION,
        DONE;

    }
}

