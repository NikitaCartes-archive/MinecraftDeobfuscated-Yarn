/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.RandomTask;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.ai.brain.task.WaitTask;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;

public class WanderWithOffHandItemTask<E extends MobEntityWithAi>
extends RandomTask<E> {
    public WanderWithOffHandItemTask(float walkSpeed) {
        super(ImmutableList.of(Pair.of(new StrollTask(walkSpeed, 1, 0), 1), Pair.of(new WaitTask(10, 20), 1)));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, E mobEntityWithAi) {
        return !((LivingEntity)mobEntityWithAi).getOffHandStack().isEmpty();
    }
}

