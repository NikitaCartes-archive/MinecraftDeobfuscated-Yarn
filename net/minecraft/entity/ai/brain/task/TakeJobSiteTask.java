/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class TakeJobSiteTask
extends Task<VillagerEntity> {
    private final float speed;

    public TakeJobSiteTask(float speed) {
        super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.MOBS, MemoryModuleState.VALUE_PRESENT));
        this.speed = speed;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        if (villagerEntity.isBaby()) {
            return false;
        }
        return villagerEntity.getVillagerData().getProfession() == VillagerProfession.NONE;
    }

    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity3, long l) {
        BlockPos blockPos = villagerEntity3.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().getPos();
        Optional<PointOfInterestType> optional = serverWorld.getPointOfInterestStorage().getType(blockPos);
        if (!optional.isPresent()) {
            return;
        }
        LookTargetUtil.streamSeenVillagers(villagerEntity3, villagerEntity -> this.canUseJobSite((PointOfInterestType)optional.get(), (VillagerEntity)villagerEntity, blockPos)).findFirst().ifPresent(villagerEntity2 -> this.claimSite(serverWorld, villagerEntity3, (VillagerEntity)villagerEntity2, blockPos, villagerEntity2.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).isPresent()));
    }

    private boolean canUseJobSite(PointOfInterestType poiType, VillagerEntity villager, BlockPos pos) {
        boolean bl = villager.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).isPresent();
        if (bl) {
            return false;
        }
        Optional<GlobalPos> optional = villager.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE);
        VillagerProfession villagerProfession = villager.getVillagerData().getProfession();
        if (villager.getVillagerData().getProfession() != VillagerProfession.NONE && villagerProfession.getWorkStation().getCompletionCondition().test(poiType)) {
            if (!optional.isPresent()) {
                return this.canReachJobSite(villager, pos, poiType);
            }
            return optional.get().getPos().equals(pos);
        }
        return false;
    }

    private void claimSite(ServerWorld world, VillagerEntity previousOwner, VillagerEntity newOwner, BlockPos pos, boolean jobSitePresent) {
        this.forgetJobSiteAndWalkTarget(previousOwner);
        if (!jobSitePresent) {
            LookTargetUtil.walkTowards((LivingEntity)newOwner, pos, this.speed, 1);
            newOwner.getBrain().remember(MemoryModuleType.POTENTIAL_JOB_SITE, GlobalPos.create(world.getRegistryKey(), pos));
            DebugInfoSender.sendPointOfInterest(world, pos);
        }
    }

    private boolean canReachJobSite(VillagerEntity villager, BlockPos pos, PointOfInterestType poiType) {
        Path path = villager.getNavigation().findPathTo(pos, poiType.getSearchDistance());
        return path != null && path.reachesTarget();
    }

    private void forgetJobSiteAndWalkTarget(VillagerEntity villager) {
        villager.getBrain().forget(MemoryModuleType.WALK_TARGET);
        villager.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        villager.getBrain().forget(MemoryModuleType.POTENTIAL_JOB_SITE);
    }
}

