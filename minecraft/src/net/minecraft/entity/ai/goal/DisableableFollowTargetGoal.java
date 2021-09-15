package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.raid.RaiderEntity;

/**
 * An active target goal that can be disabled so that it cannot start.
 */
public class DisableableFollowTargetGoal<T extends LivingEntity> extends ActiveTargetGoal<T> {
	private boolean enabled = true;

	public DisableableFollowTargetGoal(
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
