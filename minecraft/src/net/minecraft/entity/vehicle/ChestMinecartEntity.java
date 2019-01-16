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
	public ChestMinecartEntity(World world) {
		super(EntityType.CHEST_MINECART, world);
	}

	public ChestMinecartEntity(World world, double d, double e, double f) {
		super(EntityType.CHEST_MINECART, d, e, f, world);
	}

	@Override
	public void dropItems(DamageSource damageSource) {
		super.dropItems(damageSource);
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			this.dropItem(Blocks.field_10034);
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
	public BlockState getDefaultContainedBlock() {
		return Blocks.field_10034.getDefaultState().with(ChestBlock.FACING, Direction.NORTH);
	}

	@Override
	public int getDefaultBlockOffset() {
		return 8;
	}

	@Override
	public Container method_17357(int i, PlayerInventory playerInventory) {
		return new GenericContainer.Generic9x3(i, playerInventory, this);
	}
}
