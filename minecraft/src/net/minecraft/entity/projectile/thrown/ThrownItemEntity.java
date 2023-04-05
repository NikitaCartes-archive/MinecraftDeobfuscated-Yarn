package net.minecraft.entity.projectile.thrown;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public abstract class ThrownItemEntity extends ThrownEntity implements FlyingItemEntity {
	private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(ThrownItemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

	public ThrownItemEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
		super(entityType, world);
	}

	public ThrownItemEntity(EntityType<? extends ThrownItemEntity> entityType, double d, double e, double f, World world) {
		super(entityType, d, e, f, world);
	}

	public ThrownItemEntity(EntityType<? extends ThrownItemEntity> entityType, LivingEntity livingEntity, World world) {
		super(entityType, livingEntity, world);
	}

	public void setItem(ItemStack item) {
		if (!item.isOf(this.getDefaultItem()) || item.hasNbt()) {
			this.getDataTracker().set(ITEM, item.copyWithCount(1));
		}
	}

	protected abstract Item getDefaultItem();

	protected ItemStack getItem() {
		return this.getDataTracker().get(ITEM);
	}

	@Override
	public ItemStack getStack() {
		ItemStack itemStack = this.getItem();
		return itemStack.isEmpty() ? new ItemStack(this.getDefaultItem()) : itemStack;
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(ITEM, ItemStack.EMPTY);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		ItemStack itemStack = this.getItem();
		if (!itemStack.isEmpty()) {
			nbt.put("Item", itemStack.writeNbt(new NbtCompound()));
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		ItemStack itemStack = ItemStack.fromNbt(nbt.getCompound("Item"));
		this.setItem(itemStack);
	}
}
