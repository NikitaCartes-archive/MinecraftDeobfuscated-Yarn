/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;

public class class_4810<E extends MobEntity, T extends LivingEntity>
extends Task<E> {
    private final int field_22292 = 8;
    private int field_22293;
    private class_4811 field_22294 = class_4811.field_22295;

    public class_4810() {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT), 1200);
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, E mobEntity) {
        return ((LivingEntity)mobEntity).method_24518(Items.CROSSBOW) && LookTargetUtil.method_24556(mobEntity, 8.0);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, E mobEntity, long l) {
        return this.shouldRun(serverWorld, mobEntity);
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, E mobEntity, long l) {
        LivingEntity livingEntity = class_4810.method_24568(mobEntity);
        this.method_24572((MobEntity)mobEntity, livingEntity);
        this.method_24569(mobEntity, livingEntity);
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, E mobEntity, long l) {
        if (((LivingEntity)mobEntity).isUsingItem()) {
            ((LivingEntity)mobEntity).clearActiveItem();
        }
        if (((LivingEntity)mobEntity).method_24518(Items.CROSSBOW)) {
            ((CrossbowUser)mobEntity).setCharging(false);
            CrossbowItem.setCharged(((LivingEntity)mobEntity).getActiveItem(), false);
        }
    }

    private void method_24569(E mobEntity, LivingEntity livingEntity) {
        if (this.field_22294 == class_4811.field_22295) {
            ((LivingEntity)mobEntity).setCurrentHand(ProjectileUtil.getHandPossiblyHolding(mobEntity, Items.CROSSBOW));
            this.field_22294 = class_4811.field_22296;
            ((CrossbowUser)mobEntity).setCharging(true);
        } else if (this.field_22294 == class_4811.field_22296) {
            ItemStack itemStack;
            int i;
            if (!((LivingEntity)mobEntity).isUsingItem()) {
                this.field_22294 = class_4811.field_22295;
            }
            if ((i = ((LivingEntity)mobEntity).getItemUseTime()) >= CrossbowItem.getPullTime(itemStack = ((LivingEntity)mobEntity).getActiveItem())) {
                ((LivingEntity)mobEntity).stopUsingItem();
                this.field_22294 = class_4811.field_22297;
                this.field_22293 = 20 + ((LivingEntity)mobEntity).getRandom().nextInt(20);
                ((CrossbowUser)mobEntity).setCharging(false);
            }
        } else if (this.field_22294 == class_4811.field_22297) {
            --this.field_22293;
            if (this.field_22293 == 0) {
                this.field_22294 = class_4811.field_22298;
            }
        } else if (this.field_22294 == class_4811.field_22298) {
            ((RangedAttackMob)mobEntity).attack(livingEntity, 1.0f);
            ItemStack itemStack2 = ((LivingEntity)mobEntity).getStackInHand(ProjectileUtil.getHandPossiblyHolding(mobEntity, Items.CROSSBOW));
            CrossbowItem.setCharged(itemStack2, false);
            this.field_22294 = class_4811.field_22295;
        }
    }

    private void method_24572(MobEntity mobEntity, LivingEntity livingEntity) {
        mobEntity.getBrain().putMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(livingEntity));
    }

    private static LivingEntity method_24568(LivingEntity livingEntity) {
        return livingEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (E)((MobEntity)entity), time);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (E)((MobEntity)entity), time);
    }

    static enum class_4811 {
        field_22295,
        field_22296,
        field_22297,
        field_22298;

    }
}

