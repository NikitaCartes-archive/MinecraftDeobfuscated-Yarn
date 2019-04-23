/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.block.BellBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class RingBellTask
extends Task<LivingEntity> {
    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryModuleState.VALUE_PRESENT));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
        return serverWorld.random.nextFloat() > 0.95f;
    }

    @Override
    protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        BlockState blockState;
        Brain<?> brain = livingEntity.getBrain();
        BlockPos blockPos = brain.getOptionalMemory(MemoryModuleType.MEETING_POINT).get().getPos();
        if (blockPos.isWithinDistance(new BlockPos(livingEntity), 2.0) && (blockState = serverWorld.getBlockState(blockPos)).getBlock() == Blocks.BELL) {
            BellBlock bellBlock = (BellBlock)blockState.getBlock();
            for (Direction direction : Direction.Type.HORIZONTAL) {
                if (!bellBlock.ring(serverWorld, blockState, serverWorld.getBlockEntity(blockPos), new BlockHitResult(new Vec3d(0.5, 0.5, 0.5), direction, blockPos, false), null)) continue;
                break;
            }
        }
    }
}

