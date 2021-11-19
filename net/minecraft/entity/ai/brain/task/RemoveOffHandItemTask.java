/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;

public class RemoveOffHandItemTask<E extends PiglinEntity>
extends Task<E> {
    public RemoveOffHandItemTask() {
        super(ImmutableMap.of(MemoryModuleType.ADMIRING_ITEM, MemoryModuleState.VALUE_ABSENT));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, E piglinEntity) {
        return !((LivingEntity)piglinEntity).getOffHandStack().isEmpty() && !((LivingEntity)piglinEntity).getOffHandStack().isOf(Items.SHIELD);
    }

    @Override
    protected void run(ServerWorld serverWorld, E piglinEntity, long l) {
        PiglinBrain.consumeOffHandItem(piglinEntity, true);
    }
}

