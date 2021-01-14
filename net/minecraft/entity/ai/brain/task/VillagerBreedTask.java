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
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestType;

public class VillagerBreedTask
extends Task<VillagerEntity> {
    private long breedEndTime;

    public VillagerBreedTask() {
        super(ImmutableMap.of(MemoryModuleType.BREED_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.VISIBLE_MOBS, MemoryModuleState.VALUE_PRESENT), 350, 350);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        return this.isReadyToBreed(villagerEntity);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        return l <= this.breedEndTime && this.isReadyToBreed(villagerEntity);
    }

    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        PassiveEntity passiveEntity = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.BREED_TARGET).get();
        LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, passiveEntity, 0.5f);
        serverWorld.sendEntityStatus(passiveEntity, (byte)18);
        serverWorld.sendEntityStatus(villagerEntity, (byte)18);
        int i = 275 + villagerEntity.getRandom().nextInt(50);
        this.breedEndTime = l + (long)i;
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        VillagerEntity villagerEntity2 = (VillagerEntity)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.BREED_TARGET).get();
        if (villagerEntity.squaredDistanceTo(villagerEntity2) > 5.0) {
            return;
        }
        LookTargetUtil.lookAtAndWalkTowardsEachOther(villagerEntity, villagerEntity2, 0.5f);
        if (l >= this.breedEndTime) {
            villagerEntity.eatForBreeding();
            villagerEntity2.eatForBreeding();
            this.goHome(serverWorld, villagerEntity, villagerEntity2);
        } else if (villagerEntity.getRandom().nextInt(35) == 0) {
            serverWorld.sendEntityStatus(villagerEntity2, (byte)12);
            serverWorld.sendEntityStatus(villagerEntity, (byte)12);
        }
    }

    private void goHome(ServerWorld world, VillagerEntity first, VillagerEntity second) {
        Optional<BlockPos> optional = this.getReachableHome(world, first);
        if (!optional.isPresent()) {
            world.sendEntityStatus(second, (byte)13);
            world.sendEntityStatus(first, (byte)13);
        } else {
            Optional<VillagerEntity> optional2 = this.createChild(world, first, second);
            if (optional2.isPresent()) {
                this.setChildHome(world, optional2.get(), optional.get());
            } else {
                world.getPointOfInterestStorage().releaseTicket(optional.get());
                DebugInfoSender.sendPointOfInterest(world, optional.get());
            }
        }
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        villagerEntity.getBrain().forget(MemoryModuleType.BREED_TARGET);
    }

    private boolean isReadyToBreed(VillagerEntity villager) {
        Brain<VillagerEntity> brain = villager.getBrain();
        Optional<PassiveEntity> optional = brain.getOptionalMemory(MemoryModuleType.BREED_TARGET).filter(passiveEntity -> passiveEntity.getType() == EntityType.VILLAGER);
        if (!optional.isPresent()) {
            return false;
        }
        return LookTargetUtil.canSee(brain, MemoryModuleType.BREED_TARGET, EntityType.VILLAGER) && villager.isReadyToBreed() && optional.get().isReadyToBreed();
    }

    private Optional<BlockPos> getReachableHome(ServerWorld world, VillagerEntity villager) {
        return world.getPointOfInterestStorage().getPosition(PointOfInterestType.HOME.getCompletionCondition(), blockPos -> this.canReachHome(villager, (BlockPos)blockPos), villager.getBlockPos(), 48);
    }

    private boolean canReachHome(VillagerEntity villager, BlockPos pos) {
        Path path = villager.getNavigation().findPathTo(pos, PointOfInterestType.HOME.getSearchDistance());
        return path != null && path.reachesTarget();
    }

    private Optional<VillagerEntity> createChild(ServerWorld world, VillagerEntity parent, VillagerEntity partner) {
        VillagerEntity villagerEntity = parent.createChild(world, partner);
        if (villagerEntity == null) {
            return Optional.empty();
        }
        parent.setBreedingAge(6000);
        partner.setBreedingAge(6000);
        villagerEntity.setBreedingAge(-24000);
        villagerEntity.refreshPositionAndAngles(parent.getX(), parent.getY(), parent.getZ(), 0.0f, 0.0f);
        world.spawnEntityAndPassengers(villagerEntity);
        world.sendEntityStatus(villagerEntity, (byte)12);
        return Optional.of(villagerEntity);
    }

    private void setChildHome(ServerWorld world, VillagerEntity child, BlockPos pos) {
        GlobalPos globalPos = GlobalPos.create(world.getRegistryKey(), pos);
        child.getBrain().remember(MemoryModuleType.HOME, globalPos);
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void run(ServerWorld world, LivingEntity entity, long time) {
        this.run(world, (VillagerEntity)entity, time);
    }
}

