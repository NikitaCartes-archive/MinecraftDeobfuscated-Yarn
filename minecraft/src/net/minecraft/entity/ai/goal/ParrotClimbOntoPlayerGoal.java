package net.minecraft.entity.ai.goal;

import net.minecraft.entity.passive.TameableShoulderEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class ParrotClimbOntoPlayerGoal extends Goal {
	private final TameableShoulderEntity parrot;
	private ServerPlayerEntity parrotOwner;
	private boolean mounted;

	public ParrotClimbOntoPlayerGoal(TameableShoulderEntity tameableShoulderEntity) {
		this.parrot = tameableShoulderEntity;
	}

	@Override
	public boolean canStart() {
		ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.parrot.getOwner();
		boolean bl = serverPlayerEntity != null && !serverPlayerEntity.isSpectator() && !serverPlayerEntity.abilities.flying && !serverPlayerEntity.isInsideWater();
		return !this.parrot.isSitting() && bl && this.parrot.isReadyToSitOnPlayer();
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
