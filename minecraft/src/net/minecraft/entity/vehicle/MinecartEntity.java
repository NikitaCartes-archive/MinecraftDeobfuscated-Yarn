package net.minecraft.entity.vehicle;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
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
	public ActionResult interact(PlayerEntity player, Hand hand) {
		if (player.shouldCancelInteraction()) {
			return ActionResult.PASS;
		} else {
			BlockState blockState = player.method_42800();
			BlockState blockState2 = this.getContainedBlock();
			if (blockState2.isAir() && blockState != null) {
				player.method_42838(null);
				this.setCustomBlock(blockState);
				return ActionResult.SUCCESS;
			} else if (!blockState2.isAir() && blockState == null) {
				player.method_42838(blockState2);
				this.setCustomBlock(Blocks.AIR.getDefaultState());
				this.setCustomBlockPresent(false);
				return ActionResult.SUCCESS;
			} else if (this.hasPassengers()) {
				return ActionResult.PASS;
			} else if (!this.world.isClient) {
				return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
			} else {
				return ActionResult.SUCCESS;
			}
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
