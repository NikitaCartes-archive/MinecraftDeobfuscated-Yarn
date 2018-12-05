package net.minecraft;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.passive.TameableEntity;

public class class_1404<T extends LivingEntity> extends FollowTargetGoal<T> {
	private final TameableEntity field_6656;

	public class_1404(TameableEntity tameableEntity, Class<T> class_, boolean bl, @Nullable Predicate<? super T> predicate) {
		super(tameableEntity, class_, 10, bl, false, predicate);
		this.field_6656 = tameableEntity;
	}

	@Override
	public boolean canStart() {
		return !this.field_6656.isTamed() && super.canStart();
	}

	@Override
	public boolean shouldContinue() {
		return this.field_6642 != null ? this.field_6642.test(this.field_6644) : super.shouldContinue();
	}
}
