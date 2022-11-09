/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.fluid.Fluids;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class LayFrogSpawnTask {
    public static Task<LivingEntity> create(Block frogSpawn) {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryAbsent(MemoryModuleType.ATTACK_TARGET), context.queryMemoryValue(MemoryModuleType.WALK_TARGET), context.queryMemoryValue(MemoryModuleType.IS_PREGNANT)).apply(context, (attackTarget, walkTarget, isPregnant) -> (world, entity, time) -> {
            if (entity.isTouchingWater() || !entity.isOnGround()) {
                return false;
            }
            BlockPos blockPos = entity.getBlockPos().down();
            for (Direction direction : Direction.Type.HORIZONTAL) {
                BlockPos blockPos3;
                BlockPos blockPos2 = blockPos.offset(direction);
                if (!world.getBlockState(blockPos2).getCollisionShape(world, blockPos2).getFace(Direction.UP).isEmpty() || !world.getFluidState(blockPos2).isOf(Fluids.WATER) || !world.getBlockState(blockPos3 = blockPos2.up()).isAir()) continue;
                world.setBlockState(blockPos3, frogSpawn.getDefaultState(), Block.NOTIFY_ALL);
                world.playSoundFromEntity(null, entity, SoundEvents.ENTITY_FROG_LAY_SPAWN, SoundCategory.BLOCKS, 1.0f, 1.0f);
                isPregnant.forget();
                return true;
            }
            return true;
        }));
    }
}

