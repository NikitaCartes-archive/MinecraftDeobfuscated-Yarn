package net.minecraft.entity.vehicle;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.container.HopperContainer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
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

	public HopperMinecartEntity(World world, double d, double e, double f) {
		super(EntityType.field_6058, d, e, f, world);
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.field_7677;
	}

	@Override
	public BlockState method_7517() {
		return Blocks.field_10312.method_9564();
	}

	@Override
	public int getDefaultBlockOffset() {
		return 1;
	}

	@Override
	public int getInvSize() {
		return 5;
	}

	@Override
	public void onActivatorRail(int i, int j, int k, boolean bl) {
		boolean bl2 = !bl;
		if (bl2 != this.isEnabled()) {
			this.setEnabled(bl2);
		}
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean bl) {
		this.enabled = bl;
	}

	@Override
	public World getWorld() {
		return this.field_6002;
	}

	@Override
	public double getHopperX() {
		return this.x;
	}

	@Override
	public double getHopperY() {
		return this.y + 0.5;
	}

	@Override
	public double getHopperZ() {
		return this.z;
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.field_6002.isClient && this.isAlive() && this.isEnabled()) {
			BlockPos blockPos = new BlockPos(this);
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
			List<ItemEntity> list = this.field_6002.method_8390(ItemEntity.class, this.method_5829().expand(0.25, 0.0, 0.25), EntityPredicates.VALID_ENTITY);
			if (!list.isEmpty()) {
				HopperBlockEntity.extract(this, (ItemEntity)list.get(0));
			}

			return false;
		}
	}

	@Override
	public void dropItems(DamageSource damageSource) {
		super.dropItems(damageSource);
		if (this.field_6002.getGameRules().getBoolean(GameRules.field_19393)) {
			this.method_5706(Blocks.field_10312);
		}
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("TransferCooldown", this.transferCooldown);
		compoundTag.putBoolean("Enabled", this.enabled);
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.transferCooldown = compoundTag.getInt("TransferCooldown");
		this.enabled = compoundTag.containsKey("Enabled") ? compoundTag.getBoolean("Enabled") : true;
	}

	public void setTransferCooldown(int i) {
		this.transferCooldown = i;
	}

	public boolean isCoolingDown() {
		return this.transferCooldown > 0;
	}

	@Override
	public Container getContainer(int i, PlayerInventory playerInventory) {
		return new HopperContainer(i, playerInventory, this);
	}
}
