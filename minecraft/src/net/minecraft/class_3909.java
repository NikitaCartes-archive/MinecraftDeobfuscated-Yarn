package net.minecraft;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.raid.RaiderEntity;

public class class_3909<T extends LivingEntity> extends FollowTargetGoal<T> {
	private int field_17282 = 0;

	public class_3909(RaiderEntity raiderEntity, Class<T> class_, boolean bl, @Nullable Predicate<? super T> predicate) {
		super(raiderEntity, class_, 500, bl, false, predicate);
	}

	public int method_17352() {
		return this.field_17282;
	}

	public void method_17353() {
		this.field_17282--;
	}

	@Override
	public boolean canStart() {
		if (this.field_17282 > 0 || !this.entity.getRand().nextBoolean()) {
			return false;
		} else if (((RaiderEntity)this.entity).hasActiveRaid() && this.field_6643.isAssignableFrom(RaiderEntity.class)) {
			List<T> list = this.entity.world.getEntities(this.field_6643, this.getSearchBox(this.getFollowRange()), this.field_6642);
			if (list.isEmpty()) {
				return false;
			} else {
				Collections.sort(list, this.field_6645);
				this.field_6644 = (T)list.get(0);
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public void start() {
		this.field_17282 = 200;
		super.start();
	}
}
