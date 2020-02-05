package net.minecraft.entity.vehicle;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MinecartEntity extends AbstractMinecartEntity {
	public MinecartEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	public MinecartEntity(World world, double x, double y, double z) {
		super(EntityType.MINECART, world, x, y, z);
	}

	@Override
	public boolean interact(PlayerEntity player, Hand hand) {
		if (player.shouldCancelInteraction()) {
			return false;
		} else if (this.hasPassengers()) {
			return false;
		} else {
			if (!this.world.isClient) {
				player.startRiding(this);
			}

			return true;
		}
	}

	@Override
	public void onActivatorRail(int x, int y, int z, boolean powered) {
		if (powered) {
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
		return AbstractMinecartEntity.Type.RIDEABLE;
	}
}
