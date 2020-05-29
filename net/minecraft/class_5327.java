/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

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
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class class_5327
extends Task<VillagerEntity> {
    private final float field_25158;

    public class_5327(float f) {
        super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.JOB_SITE, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.MOBS, MemoryModuleState.VALUE_PRESENT));
        this.field_25158 = f;
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
        LookTargetUtil.method_29248(villagerEntity3, villagerEntity -> this.method_29260((PointOfInterestType)optional.get(), (VillagerEntity)villagerEntity, blockPos)).findFirst().ifPresent(villagerEntity2 -> this.method_29266(serverWorld, villagerEntity3, (VillagerEntity)villagerEntity2, blockPos, villagerEntity2.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).isPresent()));
    }

    private boolean method_29260(PointOfInterestType pointOfInterestType, VillagerEntity villagerEntity, BlockPos blockPos) {
        boolean bl = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).isPresent();
        if (bl) {
            return false;
        }
        Optional<GlobalPos> optional = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE);
        VillagerProfession villagerProfession = villagerEntity.getVillagerData().getProfession();
        if (villagerEntity.getVillagerData().getProfession() != VillagerProfession.NONE && villagerProfession.getWorkStation().getCompletionCondition().test(pointOfInterestType)) {
            if (!optional.isPresent()) {
                return this.method_29262(villagerEntity, blockPos, pointOfInterestType);
            }
            return optional.get().getPos().equals(blockPos);
        }
        return false;
    }

    private void method_29266(ServerWorld serverWorld, VillagerEntity villagerEntity, VillagerEntity villagerEntity2, BlockPos blockPos, boolean bl) {
        this.method_29261(villagerEntity);
        if (!bl) {
            LookTargetUtil.walkTowards((LivingEntity)villagerEntity2, blockPos, this.field_25158, 1);
            villagerEntity2.getBrain().remember(MemoryModuleType.POTENTIAL_JOB_SITE, GlobalPos.create(serverWorld.getRegistryKey(), blockPos));
            DebugInfoSender.sendPointOfInterest(serverWorld, blockPos);
        }
    }

    private boolean method_29262(VillagerEntity villagerEntity, BlockPos blockPos, PointOfInterestType pointOfInterestType) {
        Path path = villagerEntity.getNavigation().findPathTo(blockPos, pointOfInterestType.getSearchDistance());
        return path != null && path.reachesTarget();
    }

    private void method_29261(VillagerEntity villagerEntity) {
        villagerEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        villagerEntity.getBrain().forget(MemoryModuleType.POTENTIAL_JOB_SITE);
    }
}

