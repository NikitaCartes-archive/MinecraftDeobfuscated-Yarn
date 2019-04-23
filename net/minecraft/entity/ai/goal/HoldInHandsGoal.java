/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

public class HoldInHandsGoal<T extends MobEntity>
extends Goal {
    private final T actor;
    private final ItemStack item;
    private final Predicate<? super T> condition;
    private final SoundEvent sound;

    public HoldInHandsGoal(T mobEntity, ItemStack itemStack, @Nullable SoundEvent soundEvent, Predicate<? super T> predicate) {
        this.actor = mobEntity;
        this.item = itemStack;
        this.sound = soundEvent;
        this.condition = predicate;
    }

    @Override
    public boolean canStart() {
        return this.condition.test(this.actor);
    }

    @Override
    public boolean shouldContinue() {
        return ((LivingEntity)this.actor).isUsingItem();
    }

    @Override
    public void start() {
        ((MobEntity)this.actor).setEquippedStack(EquipmentSlot.MAINHAND, this.item.copy());
        ((LivingEntity)this.actor).setCurrentHand(Hand.MAIN_HAND);
    }

    @Override
    public void stop() {
        ((MobEntity)this.actor).setEquippedStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        if (this.sound != null) {
            ((Entity)this.actor).playSound(this.sound, 1.0f, ((LivingEntity)this.actor).getRand().nextFloat() * 0.2f + 0.9f);
        }
    }
}

