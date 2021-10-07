/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.class_6670;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.server.world.ServerWorld;

public class PiglinBruteSpecificSensor
extends Sensor<LivingEntity> {
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEARBY_ADULT_PIGLINS);
    }

    @Override
    protected void sense(ServerWorld world, LivingEntity entity) {
        Brain<?> brain = entity.getBrain();
        ArrayList<AbstractPiglinEntity> list = Lists.newArrayList();
        class_6670 lv = brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).orElse(class_6670.method_38971());
        Optional<MobEntity> optional = lv.method_38975(livingEntity -> livingEntity instanceof WitherSkeletonEntity || livingEntity instanceof WitherEntity).map(MobEntity.class::cast);
        List list2 = brain.getOptionalMemory(MemoryModuleType.MOBS).orElse(ImmutableList.of());
        for (LivingEntity livingEntity2 : list2) {
            if (!(livingEntity2 instanceof AbstractPiglinEntity) || !((AbstractPiglinEntity)livingEntity2).isAdult()) continue;
            list.add((AbstractPiglinEntity)livingEntity2);
        }
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, optional);
        brain.remember(MemoryModuleType.NEARBY_ADULT_PIGLINS, list);
    }
}

