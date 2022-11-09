/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;

public class BreedTask
extends MultiTickTask<AnimalEntity> {
    private static final int MAX_RANGE = 3;
    private static final int MIN_BREED_TIME = 60;
    private static final int RUN_TIME = 110;
    private final EntityType<? extends AnimalEntity> targetType;
    private final float speed;
    private long breedTime;

    public BreedTask(EntityType<? extends AnimalEntity> targetType, float speed) {
        super(ImmutableMap.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.BREED_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED), 110);
        this.targetType = targetType;
        this.speed = speed;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, AnimalEntity animalEntity) {
        return animalEntity.isInLove() && this.findBreedTarget(animalEntity).isPresent();
    }

    @Override
    protected void run(ServerWorld serverWorld, AnimalEntity animalEntity, long l) {
        AnimalEntity animalEntity2 = this.findBreedTarget(animalEntity).get();
        animalEntity.getBrain().remember(MemoryModuleType.BREED_TARGET, animalEntity2);
        animalEntity2.getBrain().remember(MemoryModuleType.BREED_TARGET, animalEntity);
        LookTargetUtil.lookAtAndWalkTowardsEachOther(animalEntity, animalEntity2, this.speed);
        int i = 60 + animalEntity.getRandom().nextInt(50);
        this.breedTime = l + (long)i;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, AnimalEntity animalEntity, long l) {
        if (!this.hasBreedTarget(animalEntity)) {
            return false;
        }
        AnimalEntity animalEntity2 = this.getBreedTarget(animalEntity);
        return animalEntity2.isAlive() && animalEntity.canBreedWith(animalEntity2) && LookTargetUtil.canSee(animalEntity.getBrain(), animalEntity2) && l <= this.breedTime;
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, AnimalEntity animalEntity, long l) {
        AnimalEntity animalEntity2 = this.getBreedTarget(animalEntity);
        LookTargetUtil.lookAtAndWalkTowardsEachOther(animalEntity, animalEntity2, this.speed);
        if (!animalEntity.isInRange(animalEntity2, 3.0)) {
            return;
        }
        if (l >= this.breedTime) {
            animalEntity.breed(serverWorld, animalEntity2);
            animalEntity.getBrain().forget(MemoryModuleType.BREED_TARGET);
            animalEntity2.getBrain().forget(MemoryModuleType.BREED_TARGET);
        }
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, AnimalEntity animalEntity, long l) {
        animalEntity.getBrain().forget(MemoryModuleType.BREED_TARGET);
        animalEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        animalEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        this.breedTime = 0L;
    }

    private AnimalEntity getBreedTarget(AnimalEntity animal) {
        return (AnimalEntity)animal.getBrain().getOptionalRegisteredMemory(MemoryModuleType.BREED_TARGET).get();
    }

    private boolean hasBreedTarget(AnimalEntity animal) {
        Brain<PassiveEntity> brain = animal.getBrain();
        return brain.hasMemoryModule(MemoryModuleType.BREED_TARGET) && brain.getOptionalRegisteredMemory(MemoryModuleType.BREED_TARGET).get().getType() == this.targetType;
    }

    private Optional<? extends AnimalEntity> findBreedTarget(AnimalEntity animal) {
        return animal.getBrain().getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS).get().findFirst(entity -> {
            AnimalEntity animalEntity2;
            return entity.getType() == this.targetType && entity instanceof AnimalEntity && animal.canBreedWith(animalEntity2 = (AnimalEntity)entity);
        }).map(AnimalEntity.class::cast);
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (AnimalEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (AnimalEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (AnimalEntity)entity, time);
    }
}

