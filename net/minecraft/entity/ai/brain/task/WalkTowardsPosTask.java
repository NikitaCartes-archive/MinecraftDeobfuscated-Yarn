/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.SingleTickTask;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class WalkTowardsPosTask {
    private static BlockPos fuzz(MobEntity mob, BlockPos pos) {
        Random random = mob.world.random;
        return pos.add(WalkTowardsPosTask.fuzz(random), 0, WalkTowardsPosTask.fuzz(random));
    }

    private static int fuzz(Random random) {
        return random.nextInt(3) - 1;
    }

    public static <E extends MobEntity> SingleTickTask<E> create(MemoryModuleType<BlockPos> posModule, int completionRange, float speed) {
        return TaskTriggerer.task(context -> context.group(context.queryMemoryValue(posModule), context.queryMemoryAbsent(MemoryModuleType.ATTACK_TARGET), context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET), context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET)).apply(context, (pos, attackTarget, walkTarget, lookTarget) -> (world, entity, time) -> {
            BlockPos blockPos = (BlockPos)context.getValue(pos);
            boolean bl = blockPos.isWithinDistance(entity.getBlockPos(), (double)completionRange);
            if (!bl) {
                LookTargetUtil.walkTowards(entity, WalkTowardsPosTask.fuzz(entity, blockPos), speed, completionRange);
            }
            return true;
        }));
    }
}

