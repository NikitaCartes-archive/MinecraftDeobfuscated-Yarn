package net.minecraft.entity.vehicle;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.container.Container;
import net.minecraft.container.GenericContainer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ChestMinecartEntity extends StorageMinecartEntity {
	public ChestMinecartEntity(EntityType<? extends ChestMinecartEntity> entityType, World world) {
		super(entityType, world);
	}

	public ChestMinecartEntity(World world, double d, double e, double f) {
		super(EntityType.CHEST_MINECART, d, e, f, world);
	}

	@Override
	public void dropItems(DamageSource damageSource) {
		super.dropItems(damageSource);
		if (this.field_6002.getGameRules().getBoolean("doEntityDrops")) {
			this.method_5706(Blocks.field_10034);
		}
	}

	@Override
	public int getInvSize() {
		return 27;
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.field_7678;
	}

	@Override
	public BlockState method_7517() {
		return Blocks.field_10034.method_9564().method_11657(ChestBlock.field_10768, Direction.NORTH);
	}

	@Override
	public int getDefaultBlockOffset() {
		return 8;
	}

	@Override
	public Container method_17357(int i, PlayerInventory playerInventory) {
		return GenericContainer.method_19245(i, playerInventory, this);
	}
}
