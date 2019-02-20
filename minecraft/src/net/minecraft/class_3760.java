package net.minecraft;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.raid.RaiderEntity;

public class class_3760<T extends LivingEntity> extends FollowTargetGoal<T> {
	private boolean field_17281 = true;

	public class_3760(RaiderEntity raiderEntity, Class<T> class_, int i, boolean bl, boolean bl2, @Nullable Predicate<LivingEntity> predicate) {
		super(raiderEntity, class_, i, bl, bl2, predicate);
	}

	public void method_17351(boolean bl) {
		this.field_17281 = bl;
	}

	@Override
	public boolean canStart() {
		return this.field_17281 && super.canStart();
	}
}
