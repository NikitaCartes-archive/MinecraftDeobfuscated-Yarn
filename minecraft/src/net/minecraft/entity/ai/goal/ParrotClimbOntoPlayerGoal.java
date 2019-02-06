package net.minecraft.entity.ai.goal;

import net.minecraft.entity.passive.ParrotBaseEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class ParrotClimbOntoPlayerGoal extends Goal {
	private final ParrotBaseEntity parrot;
	private ServerPlayerEntity parrotOwner;
	private boolean mounted;

	public ParrotClimbOntoPlayerGoal(ParrotBaseEntity parrotBaseEntity) {
		this.parrot = parrotBaseEntity;
	}

	@Override
	public boolean canStart() {
		ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.parrot.getOwner();
		boolean bl = serverPlayerEntity != null && !serverPlayerEntity.isSpectator() && !serverPlayerEntity.abilities.flying && !serverPlayerEntity.isInsideWater();
		return !this.parrot.isSitting() && bl && this.parrot.method_6626();
	}

	@Override
	public boolean canStop() {
		return !this.mounted;
	}

	@Override
	public void start() {
		this.parrotOwner = (ServerPlayerEntity)this.parrot.getOwner();
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
