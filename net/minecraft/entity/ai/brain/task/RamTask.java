/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class RamTask<E extends PathAwareEntity>
extends Task<E> {
    public static final int field_33474 = 200;
    public static final float field_33475 = 1.65f;
    private final UniformIntProvider field_33476;
    private final TargetPredicate field_33477;
    private final Function<E, Integer> field_33478;
    private final float field_33479;
    private final Function<E, Float> field_33480;
    private Vec3d field_33481;
    private final Function<E, SoundEvent> field_33482;

    public RamTask(UniformIntProvider uniformIntProvider, TargetPredicate targetPredicate, Function<E, Integer> function, float f, Function<E, Float> function2, Function<E, SoundEvent> function3) {
        super(ImmutableMap.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.RAM_TARGET, MemoryModuleState.VALUE_PRESENT), 200);
        this.field_33476 = uniformIntProvider;
        this.field_33477 = targetPredicate;
        this.field_33478 = function;
        this.field_33479 = f;
        this.field_33480 = function2;
        this.field_33482 = function3;
        this.field_33481 = Vec3d.ZERO;
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
        this.field_33481 = new Vec3d((double)blockPos.getX() - vec3d.getX(), 0.0, (double)blockPos.getZ() - vec3d.getZ()).normalize();
        brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3d, this.field_33479, 0));
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, E pathAwareEntity, long l) {
        List<LivingEntity> list = serverWorld.getTargets(LivingEntity.class, this.field_33477, (LivingEntity)pathAwareEntity, ((Entity)pathAwareEntity).getBoundingBox());
        Brain<?> brain = ((LivingEntity)pathAwareEntity).getBrain();
        if (!list.isEmpty()) {
            LivingEntity livingEntity = list.get(0);
            livingEntity.damage(DamageSource.mob(pathAwareEntity), this.field_33478.apply(pathAwareEntity).intValue());
            float f = livingEntity.blockedByShield(DamageSource.mob(pathAwareEntity)) ? 0.5f : 1.0f;
            float g = MathHelper.clamp(((LivingEntity)pathAwareEntity).getMovementSpeed() * 1.65f, 0.2f, 3.0f);
            livingEntity.takeKnockback(f * g * this.field_33480.apply(pathAwareEntity).floatValue(), this.field_33481.getX(), this.field_33481.getZ());
            this.method_36279(serverWorld, (PathAwareEntity)pathAwareEntity);
            serverWorld.playSoundFromEntity(null, (Entity)pathAwareEntity, this.field_33482.apply(pathAwareEntity), SoundCategory.HOSTILE, 1.0f, 1.0f);
        } else {
            boolean bl;
            Optional<WalkTarget> optional = brain.getOptionalMemory(MemoryModuleType.WALK_TARGET);
            Optional<Vec3d> optional2 = brain.getOptionalMemory(MemoryModuleType.RAM_TARGET);
            boolean bl2 = bl = !optional.isPresent() || !optional2.isPresent() || optional.get().getLookTarget().getPos().distanceTo(optional2.get()) < 0.25;
            if (bl) {
                this.method_36279(serverWorld, (PathAwareEntity)pathAwareEntity);
            }
        }
    }

    protected void method_36279(ServerWorld serverWorld, PathAwareEntity pathAwareEntity) {
        serverWorld.sendEntityStatus(pathAwareEntity, (byte)59);
        pathAwareEntity.getBrain().remember(MemoryModuleType.RAM_COOLDOWN_TICKS, this.field_33476.get(serverWorld.random));
        pathAwareEntity.getBrain().forget(MemoryModuleType.RAM_TARGET);
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

