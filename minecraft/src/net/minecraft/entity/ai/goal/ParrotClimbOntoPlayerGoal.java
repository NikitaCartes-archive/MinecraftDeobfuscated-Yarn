package net.minecraft.entity.ai.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.ParrotBaseEntity;
import net.minecraft.entity.player.PlayerEntity;

public class ParrotClimbOntoPlayerGoal extends Goal {
	private final ParrotBaseEntity parrot;
	private PlayerEntity parrotOwner;
	private boolean mounted;

	public ParrotClimbOntoPlayerGoal(ParrotBaseEntity parrotBaseEntity) {
		this.parrot = parrotBaseEntity;
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.parrot.getOwner();
		boolean bl = livingEntity != null
			&& !((PlayerEntity)livingEntity).isSpectator()
			&& !((PlayerEntity)livingEntity).abilities.flying
			&& !livingEntity.isInsideWater();
		return !this.parrot.isSitting() && bl && this.parrot.method_6626();
	}

	@Override
	public boolean canStop() {
		return !this.mounted;
	}

	@Override
	public void start() {
		this.parrotOwner = (PlayerEntity)this.parrot.getOwner();
		this.mounted = false;
	}

	@Override
	public void tick() {
		if (!this.mounted && !this.parrot.isSitting() && !this.parrot.isLeashed()) {
			if (this.parrot.getBoundingBox().intersects(this.parrotOwner.getBoundingBox())) {
				this.mounted = this.parrot.mountOnto(this.parrotOwner);
			}
		}
	}
}
