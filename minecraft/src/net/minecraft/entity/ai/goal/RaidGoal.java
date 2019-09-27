package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.raid.RaiderEntity;

public class RaidGoal<T extends LivingEntity> extends FollowTargetGoal<T> {
	private int cooldown = 0;

	public RaidGoal(RaiderEntity raiderEntity, Class<T> class_, boolean bl, @Nullable Predicate<LivingEntity> predicate) {
		super(raiderEntity, class_, 500, bl, false, predicate);
	}

	public int getCooldown() {
		return this.cooldown;
	}

	public void decreaseCooldown() {
		this.cooldown--;
	}

	@Override
	public boolean canStart() {
		if (this.cooldown > 0 || !this.mob.getRandom().nextBoolean()) {
			return false;
		} else if (!((RaiderEntity)this.mob).hasActiveRaid()) {
			return false;
		} else {
			this.findClosestTarget();
			return this.targetEntity != null;
		}
	}

	@Override
	public void start() {
		this.cooldown = 200;
		super.start();
	}
}
