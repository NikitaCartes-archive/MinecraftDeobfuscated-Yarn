package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.raid.RaiderEntity;

public class DisableableFollowTargetGoal<T extends LivingEntity> extends FollowTargetGoal<T> {
	private boolean enabled = true;

	public DisableableFollowTargetGoal(RaiderEntity raiderEntity, Class<T> class_, int i, boolean bl, boolean bl2, @Nullable Predicate<LivingEntity> predicate) {
		super(raiderEntity, class_, i, bl, bl2, predicate);
	}

	public void setEnabled(boolean bl) {
		this.enabled = bl;
	}

	@Override
	public boolean canStart() {
		return this.enabled && super.canStart();
	}
}
