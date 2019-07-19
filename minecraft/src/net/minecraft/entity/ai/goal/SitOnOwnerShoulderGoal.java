package net.minecraft.entity.ai.goal;

import net.minecraft.entity.passive.TameableShoulderEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class SitOnOwnerShoulderGoal extends Goal {
	private final TameableShoulderEntity tameable;
	private ServerPlayerEntity owner;
	private boolean mounted;

	public SitOnOwnerShoulderGoal(TameableShoulderEntity tameable) {
		this.tameable = tameable;
	}

	@Override
	public boolean canStart() {
		ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.tameable.getOwner();
		boolean bl = serverPlayerEntity != null && !serverPlayerEntity.isSpectator() && !serverPlayerEntity.abilities.flying && !serverPlayerEntity.isTouchingWater();
		return !this.tameable.isSitting() && bl && this.tameable.isReadyToSitOnPlayer();
	}

	@Override
	public boolean canStop() {
		return !this.mounted;
	}

	@Override
	public void start() {
		this.owner = (ServerPlayerEntity)this.tameable.getOwner();
		this.mounted = false;
	}

	@Override
	public void tick() {
		if (!this.mounted && !this.tameable.isSitting() && !this.tameable.isLeashed()) {
			if (this.tameable.getBoundingBox().intersects(this.owner.getBoundingBox())) {
				this.mounted = this.tameable.mountOnto(this.owner);
			}
		}
	}
}
