package net.minecraft.entity.vehicle;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.world.World;

public class HopperMinecartEntity extends StorageMinecartEntity implements Hopper {
	private boolean enabled = true;

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
	public boolean canBlockFromAbove() {
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.getWorld().isClient && this.isAlive() && this.isEnabled() && this.canOperate()) {
			this.markDirty();
		}
	}

	public boolean canOperate() {
		if (HopperBlockEntity.extract(this.getWorld(), this)) {
			return true;
		} else {
			for (ItemEntity itemEntity : this.getWorld()
				.getEntitiesByClass(ItemEntity.class, this.getBoundingBox().expand(0.25, 0.0, 0.25), EntityPredicates.VALID_ENTITY)) {
				if (HopperBlockEntity.extract(this, itemEntity)) {
					return true;
				}
			}

			return false;
		}
	}

	@Override
	protected Item asItem() {
		return Items.HOPPER_MINECART;
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("Enabled", this.enabled);
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.enabled = nbt.contains("Enabled") ? nbt.getBoolean("Enabled") : true;
	}

	@Override
	public ScreenHandler getScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new HopperScreenHandler(syncId, playerInventory, this);
	}
}
