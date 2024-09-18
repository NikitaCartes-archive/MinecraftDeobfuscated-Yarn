package net.minecraft.entity.vehicle;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MinecartEntity extends AbstractMinecartEntity {
	private float field_52518;
	private float field_52519;

	public MinecartEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		if (!player.shouldCancelInteraction() && !this.hasPassengers() && (this.getWorld().isClient || player.startRiding(this))) {
			this.field_52519 = this.field_52518;
			if (!this.getWorld().isClient) {
				return (ActionResult)(player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS);
			} else {
				return ActionResult.SUCCESS;
			}
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	protected Item asItem() {
		return Items.MINECART;
	}

	@Override
	public ItemStack getPickBlockStack() {
		return new ItemStack(Items.MINECART);
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
	public boolean isRideable() {
		return true;
	}

	@Override
	public void tick() {
		double d = (double)this.getYaw();
		Vec3d vec3d = this.getPos();
		super.tick();
		double e = ((double)this.getYaw() - d) % 360.0;
		if (this.getWorld().isClient && vec3d.distanceTo(this.getPos()) > 0.01) {
			this.field_52518 += (float)e;
			this.field_52518 %= 360.0F;
		}
	}

	@Override
	protected void updatePassengerPosition(Entity passenger, Entity.PositionUpdater positionUpdater) {
		super.updatePassengerPosition(passenger, positionUpdater);
		if (this.getWorld().isClient
			&& passenger instanceof PlayerEntity playerEntity
			&& playerEntity.shouldRotateWithMinecart()
			&& areMinecartImprovementsEnabled(this.getWorld())) {
			float f = (float)MathHelper.lerpAngleDegrees(0.5, (double)this.field_52519, (double)this.field_52518);
			playerEntity.setYaw(playerEntity.getYaw() - (f - this.field_52519));
			this.field_52519 = f;
		}
	}
}
