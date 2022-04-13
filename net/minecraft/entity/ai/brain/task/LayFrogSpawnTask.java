/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class LayFrogSpawnTask
extends Task<FrogEntity> {
    private final Block frogSpawn;
    private final MemoryModuleType<?> triggerMemory;

    public LayFrogSpawnTask(Block frogSpawn, MemoryModuleType<?> triggerMemory) {
        super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.IS_PREGNANT, MemoryModuleState.VALUE_PRESENT));
        this.frogSpawn = frogSpawn;
        this.triggerMemory = triggerMemory;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, FrogEntity frogEntity) {
        return !frogEntity.isTouchingWater() && frogEntity.isOnGround();
    }

    @Override
    protected void run(ServerWorld serverWorld, FrogEntity frogEntity, long l) {
        BlockPos blockPos = frogEntity.getBlockPos().down();
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos3;
            BlockPos blockPos2 = blockPos.offset(direction);
            if (!serverWorld.getBlockState(blockPos2).isOf(Blocks.WATER) || !serverWorld.getBlockState(blockPos3 = blockPos2.up()).isAir()) continue;
            serverWorld.setBlockState(blockPos3, this.frogSpawn.getDefaultState(), Block.NOTIFY_ALL);
            serverWorld.playSoundFromEntity(null, frogEntity, SoundEvents.ENTITY_FROG_LAY_SPAWN, SoundCategory.BLOCKS, 1.0f, 1.0f);
            frogEntity.getBrain().forget(this.triggerMemory);
            return;
        }
    }
}

