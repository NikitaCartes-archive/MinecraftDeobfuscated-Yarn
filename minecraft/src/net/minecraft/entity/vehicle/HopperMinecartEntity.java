package net.minecraft.entity.vehicle;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class HopperMinecartEntity extends StorageMinecartEntity implements Hopper {
	private boolean enabled = true;
	private int transferCooldown = -1;
	private final BlockPos currentBlockPos = BlockPos.ORIGIN;

	public HopperMinecartEntity(EntityType<? extends HopperMinecartEntity> entityType, World world) {
		super(entityType, world);
	}

	public HopperMinecartEntity(World world, double x, double y, double z) {
		super(EntityType.HOPPER_MINECART, x, y, z, world);
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.HOPPER;
	}

	@Override
	public BlockState getDefaultContainedBlock() {
		return Blocks.HOPPER.getDefaultState();
	}

	@Override
	public int getDefaultBlockOffset() {
		return 1;
	}

	@Override
	public int size() {
		return 5;
	}

	@Override
	public void onActivatorRail(int x, int y, int z, boolean powered) {
		boolean bl = !powered;
		if (bl != this.isEnabled()) {
			this.setEnabled(bl);
		}
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public World getWorld() {
		return this.world;
	}

	@Override
	public double getHopperX() {
		return this.getX();
	}

	@Override
	public double getHopperY() {
		return this.getY() + 0.5;
	}

	@Override
	public double getHopperZ() {
		return this.getZ();
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.world.isClient && this.isAlive() && this.isEnabled()) {
			BlockPos blockPos = this.getBlockPos();
			if (blockPos.equals(this.currentBlockPos)) {
				this.transferCooldown--;
			} else {
				this.setTransferCooldown(0);
			}

			if (!this.isCoolingDown()) {
				this.setTransferCooldown(0);
				if (this.canOperate()) {
					this.setTransferCooldown(4);
					this.markDirty();
				}
			}
		}
	}

	public boolean canOperate() {
		if (HopperBlockEntity.extract(this)) {
			return true;
		} else {
			List<ItemEntity> list = this.world.getEntitiesByClass(ItemEntity.class, this.getBoundingBox().expand(0.25, 0.0, 0.25), EntityPredicates.VALID_ENTITY);
			if (!list.isEmpty()) {
				HopperBlockEntity.extract(this, (ItemEntity)list.get(0));
			}

			return false;
		}
	}

	@Override
	public void dropItems(DamageSource damageSource) {
		super.dropItems(damageSource);
		if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
			this.dropItem(Blocks.HOPPER);
		}
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("TransferCooldown", this.transferCooldown);
		tag.putBoolean("Enabled", this.enabled);
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.transferCooldown = tag.getInt("TransferCooldown");
		this.enabled = tag.contains("Enabled") ? tag.getBoolean("Enabled") : true;
	}

	public void setTransferCooldown(int cooldown) {
		this.transferCooldown = cooldown;
	}

	public boolean isCoolingDown() {
		return this.transferCooldown > 0;
	}

	@Override
	public ScreenHandler getScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new HopperScreenHandler(syncId, playerInventory, this);
	}
}
