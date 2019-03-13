package net.minecraft.entity.ai.goal;

import net.minecraft.entity.passive.ParrotBaseEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class ParrotClimbOntoPlayerGoal extends Goal {
	private final ParrotBaseEntity field_6478;
	private ServerPlayerEntity field_6479;
	private boolean mounted;

	public ParrotClimbOntoPlayerGoal(ParrotBaseEntity parrotBaseEntity) {
		this.field_6478 = parrotBaseEntity;
	}

	@Override
	public boolean canStart() {
		ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)this.field_6478.getOwner();
		boolean bl = serverPlayerEntity != null && !serverPlayerEntity.isSpectator() && !serverPlayerEntity.abilities.flying && !serverPlayerEntity.isInsideWater();
		return !this.field_6478.isSitting() && bl && this.field_6478.method_6626();
	}

	@Override
	public boolean canStop() {
		return !this.mounted;
	}

	@Override
	public void start() {
		this.field_6479 = (ServerPlayerEntity)this.field_6478.getOwner();
		this.mounted = false;
	}

	@Override
	public void tick() {
		if (!this.mounted && !this.field_6478.isSitting() && !this.field_6478.isLeashed()) {
			if (this.field_6478.method_5829().intersects(this.field_6479.method_5829())) {
				this.mounted = this.field_6478.method_6627(this.field_6479);
			}
		}
	}
}
