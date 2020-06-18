/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.raid.Raid;

public class StartRaidTask
extends Task<LivingEntity> {
    public StartRaidTask() {
        super(ImmutableMap.of());
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        return world.random.nextInt(20) == 0;
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        Brain<?> brain = entity.getBrain();
        Raid raid = world.getRaidAt(entity.getBlockPos());
        if (raid != null) {
            if (!raid.hasSpawned() || raid.isPreRaid()) {
                brain.setDefaultActivity(Activity.PRE_RAID);
                brain.doExclusively(Activity.PRE_RAID);
            } else {
                brain.setDefaultActivity(Activity.RAID);
                brain.doExclusively(Activity.RAID);
            }
        }
    }
}

