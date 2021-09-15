package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

/**
 * An active target goal that only starts for untamed tameable animals.
 * In addition, the continue condition for maintaining the target uses the
 * target predicate than that of the standard track target goal.
 */
public class UntamedActiveTargetGoal<T extends LivingEntity> extends ActiveTargetGoal<T> {
	private final TameableEntity tameable;

	public UntamedActiveTargetGoal(TameableEntity tameable, Class<T> targetClass, boolean checkVisibility, @Nullable Predicate<LivingEntity> targetPredicate) {
		super(tameable, targetClass, 10, checkVisibility, false, targetPredicate);
		this.tameable = tameable;
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
