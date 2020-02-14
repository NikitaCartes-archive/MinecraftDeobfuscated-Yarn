/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.Blocks;
import net.minecraft.class_4800;
import net.minecraft.class_4836;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class class_4832
extends Sensor<HoglinEntity> {
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_WARPED_FUNGI, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, new MemoryModuleType[0]);
    }

    @Override
    protected void sense(ServerWorld serverWorld, HoglinEntity hoglinEntity) {
        Brain<HoglinEntity> brain = hoglinEntity.getBrain();
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_WARPED_FUNGI, this.method_24641(serverWorld, hoglinEntity));
        Optional<Object> optional = Optional.empty();
        int i = 0;
        ArrayList<HoglinEntity> list = Lists.newArrayList();
        List list2 = brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).orElse(Lists.newArrayList());
        for (LivingEntity livingEntity : list2) {
            if (livingEntity instanceof class_4836 && ((class_4836)livingEntity).method_24712()) {
                ++i;
            }
            if (!optional.isPresent() && livingEntity instanceof class_4836 && !livingEntity.isBaby() && livingEntity.method_24516(hoglinEntity, 15.0)) {
                optional = Optional.of((class_4836)livingEntity);
            }
            if (!(livingEntity instanceof HoglinEntity) || livingEntity.isBaby()) continue;
            list.add((HoglinEntity)livingEntity);
        }
        brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLIN, optional);
        brain.putMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLINS, list);
        brain.putMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, i);
        brain.putMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, list.size());
    }

    private Optional<BlockPos> method_24641(ServerWorld serverWorld, HoglinEntity hoglinEntity) {
        return class_4800.method_24501(hoglinEntity.method_24515(), 8, 4, blockPos -> serverWorld.getBlockState((BlockPos)blockPos).getBlock() == Blocks.WARPED_FUNGI);
    }
}

