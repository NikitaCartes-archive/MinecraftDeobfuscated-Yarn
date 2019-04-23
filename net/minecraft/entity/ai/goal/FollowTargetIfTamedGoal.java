/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.passive.TameableEntity;
import org.jetbrains.annotations.Nullable;

public class FollowTargetIfTamedGoal<T extends LivingEntity>
extends FollowTargetGoal<T> {
    private final TameableEntity tameable;

    public FollowTargetIfTamedGoal(TameableEntity tameableEntity, Class<T> class_, boolean bl, @Nullable Predicate<LivingEntity> predicate) {
        super(tameableEntity, class_, 10, bl, false, predicate);
        this.tameable = tameableEntity;
    }

    @Override
    public boolean canStart() {
        return !this.tameable.isTamed() && super.canStart();
    }

    @Override
    public boolean shouldContinue() {
        if (this.targetPredicate != null) {
            return this.targetPredicate.test(this.mob, this.targetEntity);
        }
        return super.shouldContinue();
    }
}

