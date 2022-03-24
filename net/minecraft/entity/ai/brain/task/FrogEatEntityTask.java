/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

public class FrogEatEntityTask
extends Task<FrogEntity> {
    public static final int field_37479 = 100;
    public static final int field_37480 = 6;
    private static final float field_37481 = 1.75f;
    private static final float field_37482 = 0.75f;
    private int field_37483;
    private int field_37484;
    private final SoundEvent tongueSound;
    private final SoundEvent eatSound;
    private Vec3d targetPos;
    private boolean field_37488;
    private Phase phase = Phase.DONE;

    public FrogEatEntityTask(SoundEvent tongueSound, SoundEvent eatSound) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT), 100);
        this.tongueSound = tongueSound;
        this.eatSound = eatSound;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, FrogEntity frogEntity) {
        return super.shouldRun(serverWorld, frogEntity) && FrogEntity.isValidFrogTarget(frogEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get());
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        return frogEntity.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET) && this.phase != Phase.DONE;
    }

    @Override
    protected void run(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        LivingEntity livingEntity = frogEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
        LookTargetUtil.lookAt(frogEntity, livingEntity);
        frogEntity.setFrogTarget(livingEntity);
        frogEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(livingEntity.getPos(), 2.0f, 0));
        this.field_37484 = 10;
        this.phase = Phase.MOVE_TO_TARGET;
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        frogEntity.setPose(EntityPose.STANDING);
        serverWorld.playSoundFromEntity(null, frogEntity, this.eatSound, SoundCategory.NEUTRAL, 2.0f, 1.0f);
        Optional<Entity> optional = frogEntity.getFrogTarget();
        if (optional.isPresent()) {
            Entity entity = optional.get();
            if (this.field_37488 && entity.isAlive()) {
                entity.remove(Entity.RemovalReason.KILLED);
                ItemStack itemStack = FrogEatEntityTask.createDroppedStack(frogEntity, entity);
                serverWorld.spawnEntity(new ItemEntity(serverWorld, this.targetPos.getX(), this.targetPos.getY(), this.targetPos.getZ(), itemStack));
            }
        }
        frogEntity.clearFrogTarget();
        this.field_37488 = false;
    }

    private static ItemStack createDroppedStack(FrogEntity frog, Entity eatenEntity) {
        if (eatenEntity instanceof MagmaCubeEntity) {
            return new ItemStack(switch (frog.getVariant()) {
                default -> throw new IncompatibleClassChangeError();
                case FrogEntity.Variant.TEMPERATE -> Items.OCHRE_FROGLIGHT;
                case FrogEntity.Variant.WARM -> Items.PEARLESCENT_FROGLIGHT;
                case FrogEntity.Variant.COLD -> Items.VERDANT_FROGLIGHT;
            });
        }
        return new ItemStack(Items.SLIME_BALL);
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
                    this.field_37483 = 6;
                    this.phase = Phase.EAT_ANIMATION;
                    break;
                }
                if (this.field_37484 <= 0) {
                    frogEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(livingEntity.getPos(), 2.0f, 0));
                    this.field_37484 = 10;
                    break;
                }
                --this.field_37484;
                break;
            }
            case EAT_ANIMATION: {
                if (this.field_37483 <= 0) {
                    this.field_37488 = true;
                    this.phase = Phase.DONE;
                    break;
                }
                --this.field_37483;
                break;
            }
        }
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
        EAT_ANIMATION,
        DONE;

    }
}

