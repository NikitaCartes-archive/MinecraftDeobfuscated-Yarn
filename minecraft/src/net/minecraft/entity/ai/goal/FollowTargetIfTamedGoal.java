package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

public class FollowTargetIfTamedGoal<T extends LivingEntity> extends FollowTargetGoal<T> {
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
		return this.targetPredicate != null ? this.targetPredicate.test(this.mob, this.targetEntity) : super.shouldContinue();
	}
}
