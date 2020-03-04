/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.BlockSenses;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

public class PiglinSpecificSensor
extends Sensor<LivingEntity> {
    @Override
    public Set<MemoryModuleType<?>> getOutputMemoryModules() {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleType.NEAREST_VISIBLE_WITHER_SKELETON, MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLIN, MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, new MemoryModuleType[]{MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_REPELLENT});
    }

    @Override
    protected void sense(ServerWorld world, LivingEntity entity) {
        Brain<?> brain = entity.getBrain();
        brain.remember(MemoryModuleType.NEAREST_REPELLENT, PiglinSpecificSensor.findSoulFire(world, entity));
        Optional<Object> optional = Optional.empty();
        Optional<Object> optional2 = Optional.empty();
        Optional<Object> optional3 = Optional.empty();
        Optional<Object> optional4 = Optional.empty();
        Optional<Object> optional5 = Optional.empty();
        Optional<Object> optional6 = Optional.empty();
        Optional<Object> optional7 = Optional.empty();
        int i = 0;
        ArrayList<PiglinEntity> list = Lists.newArrayList();
        List list2 = brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).orElse(Lists.newArrayList());
        for (LivingEntity livingEntity : list2) {
            if (livingEntity instanceof HoglinEntity) {
                HoglinEntity hoglinEntity = (HoglinEntity)livingEntity;
                if (hoglinEntity.isBaby() && !optional3.isPresent()) {
                    optional3 = Optional.of(hoglinEntity);
                    continue;
                }
                if (!hoglinEntity.isAdult()) continue;
                ++i;
                if (optional2.isPresent()) continue;
                optional2 = Optional.of(hoglinEntity);
                continue;
            }
            if (livingEntity instanceof PiglinEntity) {
                PiglinEntity piglinEntity = (PiglinEntity)livingEntity;
                if (piglinEntity.isBaby() && !optional4.isPresent()) {
                    optional4 = Optional.of(piglinEntity);
                    continue;
                }
                if (!piglinEntity.isAdult()) continue;
                list.add(piglinEntity);
                continue;
            }
            if (livingEntity instanceof PlayerEntity) {
                PlayerEntity playerEntity = (PlayerEntity)livingEntity;
                if (!optional6.isPresent() && EntityPredicates.EXCEPT_CREATIVE_SPECTATOR_OR_PEACEFUL.test(livingEntity) && !PiglinBrain.wearsGoldArmor(playerEntity)) {
                    optional6 = Optional.of(playerEntity);
                }
                if (optional7.isPresent() || playerEntity.isSpectator() || !PiglinBrain.isGoldHoldingPlayer(playerEntity)) continue;
                optional7 = Optional.of(playerEntity);
                continue;
            }
            if (!optional.isPresent() && livingEntity instanceof WitherSkeletonEntity) {
                optional = Optional.of((WitherSkeletonEntity)livingEntity);
                continue;
            }
            if (optional5.isPresent() || !(livingEntity instanceof ZombifiedPiglinEntity)) continue;
            optional5 = Optional.of((ZombifiedPiglinEntity)livingEntity);
        }
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_WITHER_SKELETON, optional);
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLIN, optional2);
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, optional3);
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_BABY_PIGLIN, optional4);
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED_PIGLIN, optional5);
        brain.remember(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, optional6);
        brain.remember(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, optional7);
        brain.remember(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, list);
        brain.remember(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, list.size());
        brain.remember(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, i);
    }

    private static Optional<BlockPos> findSoulFire(ServerWorld world, LivingEntity entity) {
        return BlockSenses.findBlock(entity.getSenseCenterPos(), 8, 4, blockPos -> world.getBlockState((BlockPos)blockPos).matches(BlockTags.PIGLIN_REPELLENTS));
    }
}

