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
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class ChestMinecartEntity extends StorageMinecartEntity {
	public ChestMinecartEntity(EntityType<? extends ChestMinecartEntity> entityType, World world) {
		super(entityType, world);
	}

	public ChestMinecartEntity(World world, double d, double e, double f) {
		super(EntityType.field_6126, d, e, f, world);
	}

	@Override
	public void dropItems(DamageSource damageSource) {
		super.dropItems(damageSource);
		if (this.field_6002.getGameRules().getBoolean(GameRules.field_19393)) {
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
		return Blocks.field_10034.method_9564().method_11657(ChestBlock.field_10768, Direction.field_11043);
	}

	@Override
	public int getDefaultBlockOffset() {
		return 8;
	}

	@Override
	public Container getContainer(int i, PlayerInventory playerInventory) {
		return GenericContainer.createGeneric9x3(i, playerInventory, this);
	}
}
