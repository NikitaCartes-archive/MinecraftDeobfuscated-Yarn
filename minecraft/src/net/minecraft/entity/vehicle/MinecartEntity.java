package net.minecraft.entity.vehicle;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MinecartEntity extends AbstractMinecartEntity {
	public MinecartEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	public MinecartEntity(World world, double d, double e, double f) {
		super(EntityType.field_6096, world, d, e, f);
	}

	@Override
	public boolean interact(PlayerEntity playerEntity, Hand hand) {
		if (playerEntity.isSneaking()) {
			return false;
		} else if (this.hasPassengers()) {
			return true;
		} else {
			if (!this.world.isClient) {
				playerEntity.startRiding(this);
			}

			return true;
		}
	}

	@Override
	public void onActivatorRail(int i, int j, int k, boolean bl) {
		if (bl) {
			if (this.hasPassengers()) {
				this.removeAllPassengers();
			}

			if (this.getDamageWobbleTicks() == 0) {
				this.setDamageWobbleSide(-this.getDamageWobbleSide());
				this.setDamageWobbleTicks(10);
				this.setDamageWobbleStrength(50.0F);
				this.scheduleVelocityUpdate();
			}
		}
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.field_7674;
	}
}
