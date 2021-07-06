package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.raid.RaiderEntity;

public class ToggleableActiveTargetGoal<T extends LivingEntity> extends ActiveTargetGoal<T> {
	private boolean enabled = true;

	public ToggleableActiveTargetGoal(
		RaiderEntity actor,
		Class<T> targetEntityClass,
		int reciprocalChance,
		boolean checkVisibility,
		boolean checkCanNavigate,
		@Nullable Predicate<LivingEntity> targetPredicate
	) {
		super(actor, targetEntityClass, reciprocalChance, checkVisibility, checkCanNavigate, targetPredicate);
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean canStart() {
		return this.enabled && super.canStart();
	}
}
