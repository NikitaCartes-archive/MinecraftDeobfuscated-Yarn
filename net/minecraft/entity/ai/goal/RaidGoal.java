/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.raid.RaiderEntity;
import org.jetbrains.annotations.Nullable;

public class RaidGoal<T extends LivingEntity>
extends FollowTargetGoal<T> {
    private static final int field_30231 = 200;
    private int cooldown = 0;

    public RaidGoal(RaiderEntity raider, Class<T> targetEntityClass, boolean checkVisibility, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(raider, targetEntityClass, 500, checkVisibility, false, targetPredicate);
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public void decreaseCooldown() {
        --this.cooldown;
    }

    @Override
    public boolean canStart() {
        if (this.cooldown > 0 || !this.mob.getRandom().nextBoolean()) {
            return false;
        }
        if (!((RaiderEntity)this.mob).hasActiveRaid()) {
            return false;
        }
        this.findClosestTarget();
        return this.targetEntity != null;
    }

    @Override
    public void start() {
        this.cooldown = 200;
        super.start();
    }
}

