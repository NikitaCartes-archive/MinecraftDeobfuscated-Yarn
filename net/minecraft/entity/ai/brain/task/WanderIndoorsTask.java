/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class WanderIndoorsTask
extends Task<MobEntityWithAi> {
    private final float speed;

    public WanderIndoorsTask(float f) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
        this.speed = f;
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi) {
        return !serverWorld.isSkyVisible(new BlockPos(mobEntityWithAi));
    }

    @Override
    protected void run(ServerWorld serverWorld, MobEntityWithAi mobEntityWithAi, long l) {
        BlockPos blockPos2 = new BlockPos(mobEntityWithAi);
        List list = BlockPos.stream(blockPos2.add(-1, -1, -1), blockPos2.add(1, 1, 1)).map(BlockPos::toImmutable).collect(Collectors.toList());
        Collections.shuffle(list);
        Optional<BlockPos> optional = list.stream().filter(blockPos -> !serverWorld.isSkyVisible((BlockPos)blockPos)).filter(blockPos -> serverWorld.isTopSolid((BlockPos)blockPos, mobEntityWithAi)).filter(blockPos -> serverWorld.doesNotCollide(mobEntityWithAi)).findFirst();
        optional.ifPresent(blockPos -> mobEntityWithAi.getBrain().putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget((BlockPos)blockPos, this.speed, 0)));
    }
}

