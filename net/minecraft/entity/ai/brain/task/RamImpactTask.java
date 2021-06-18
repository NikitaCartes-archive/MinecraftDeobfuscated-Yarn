/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class RamImpactTask<E extends PathAwareEntity>
extends Task<E> {
    public static final int RUN_TIME = 200;
    public static final float SPEED_STRENGTH_MULTIPLIER = 1.65f;
    private final Function<E, UniformIntProvider> cooldownRangeFactory;
    private final TargetPredicate targetPredicate;
    private final float speed;
    private final ToDoubleFunction<E> strengthMultiplierFactory;
    private Vec3d direction;
    private final Function<E, SoundEvent> soundFactory;

    public RamImpactTask(Function<E, UniformIntProvider> cooldownRangeFactory, TargetPredicate targetPredicate, float speed, ToDoubleFunction<E> strengthMultiplierFactory, Function<E, SoundEvent> soundFactory) {
        super(ImmutableMap.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.RAM_TARGET, MemoryModuleState.VALUE_PRESENT), 200);
        this.cooldownRangeFactory = cooldownRangeFactory;
        this.targetPredicate = targetPredicate;
        this.speed = speed;
        this.strengthMultiplierFactory = strengthMultiplierFactory;
        this.soundFactory = soundFactory;
        this.direction = Vec3d.ZERO;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
        return pathAwareEntity.getBrain().hasMemoryModule(MemoryModuleType.RAM_TARGET);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        return pathAwareEntity.getBrain().hasMemoryModule(MemoryModuleType.RAM_TARGET);
    }

    @Override
    protected void run(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l) {
        BlockPos blockPos = pathAwareEntity.getBlockPos();
        Brain<?> brain = pathAwareEntity.getBrain();
        Vec3d vec3d = brain.getOptionalMemory(MemoryModuleType.RAM_TARGET).get();
        this.direction = new Vec3d((double)blockPos.getX() - vec3d.getX(), 0.0, (double)blockPos.getZ() - vec3d.getZ()).normalize();
        brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, this.speed, 0));
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, E pathAwareEntity, long l) {
        List<LivingEntity> list = serverWorld.getTargets(LivingEntity.class, this.targetPredicate, (LivingEntity)pathAwareEntity, ((Entity)pathAwareEntity).getBoundingBox());
        Brain<?> brain = ((LivingEntity)pathAwareEntity).getBrain();
        if (!list.isEmpty()) {
            LivingEntity livingEntity = list.get(0);
            livingEntity.damage(DamageSource.mob(pathAwareEntity).setNeutral(), (float)((LivingEntity)pathAwareEntity).getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
            int i = ((LivingEntity)pathAwareEntity).hasStatusEffect(StatusEffects.SPEED) ? ((LivingEntity)pathAwareEntity).getStatusEffect(StatusEffects.SPEED).getAmplifier() + 1 : 0;
            int j = ((LivingEntity)pathAwareEntity).hasStatusEffect(StatusEffects.SLOWNESS) ? ((LivingEntity)pathAwareEntity).getStatusEffect(StatusEffects.SLOWNESS).getAmplifier() + 1 : 0;
            float f = 0.25f * (float)(i - j);
            float g = MathHelper.clamp(((LivingEntity)pathAwareEntity).getMovementSpeed() * 1.65f, 0.2f, 3.0f) + f;
            float h = livingEntity.blockedByShield(DamageSource.mob(pathAwareEntity)) ? 0.5f : 1.0f;
            livingEntity.takeKnockback((double)(h * g) * this.strengthMultiplierFactory.applyAsDouble(pathAwareEntity), this.direction.getX(), this.direction.getZ());
            this.finishRam(serverWorld, pathAwareEntity);
            serverWorld.playSoundFromEntity(null, (Entity)pathAwareEntity, this.soundFactory.apply(pathAwareEntity), SoundCategory.HOSTILE, 1.0f, 1.0f);
        } else {
            boolean bl;
            Optional<WalkTarget> optional = brain.getOptionalMemory(MemoryModuleType.WALK_TARGET);
            Optional<Vec3d> optional2 = brain.getOptionalMemory(MemoryModuleType.RAM_TARGET);
            boolean bl2 = bl = !optional.isPresent() || !optional2.isPresent() || optional.get().getLookTarget().getPos().distanceTo(optional2.get()) < 0.25;
            if (bl) {
                this.finishRam(serverWorld, pathAwareEntity);
            }
        }
    }

    protected void finishRam(ServerWorld world, E entity) {
        world.sendEntityStatus((Entity)entity, (byte)59);
        ((LivingEntity)entity).getBrain().remember(MemoryModuleType.RAM_COOLDOWN_TICKS, this.cooldownRangeFactory.apply(entity).get(world.random));
        ((LivingEntity)entity).getBrain().forget(MemoryModuleType.RAM_TARGET);
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (PathAwareEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (E)((PathAwareEntity)entity), time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (PathAwareEntity)entity, time);
    }
}

