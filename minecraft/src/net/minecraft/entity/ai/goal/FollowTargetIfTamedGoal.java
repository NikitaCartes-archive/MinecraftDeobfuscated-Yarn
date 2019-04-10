package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

public class FollowTargetIfTamedGoal<T extends LivingEntity> extends FollowTargetGoal<T> {
	private final TameableEntity field_6656;

	public FollowTargetIfTamedGoal(TameableEntity tameableEntity, Class<T> class_, boolean bl, @Nullable Predicate<LivingEntity> predicate) {
		super(tameableEntity, class_, 10, bl, false, predicate);
		this.field_6656 = tameableEntity;
	}

	@Override
	public boolean canStart() {
		return !this.field_6656.isTamed() && super.canStart();
	}

	@Override
	public boolean shouldContinue() {
		return this.targetPredicate != null ? this.targetPredicate.test(this.entity, this.targetEntity) : super.shouldContinue();
	}
}
