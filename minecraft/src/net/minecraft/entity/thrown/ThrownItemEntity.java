package net.minecraft.entity.thrown;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Util;
import net.minecraft.world.World;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = FlyingItemEntity.class
	)})
public abstract class ThrownItemEntity extends ThrownEntity implements FlyingItemEntity {
	private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(ThrownItemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

	public ThrownItemEntity(EntityType<? extends ThrownItemEntity> type, World world) {
		super(type, world);
	}

	public ThrownItemEntity(EntityType<? extends ThrownItemEntity> type, double d, double e, double f, World world) {
		super(type, d, e, f, world);
	}

	public ThrownItemEntity(EntityType<? extends ThrownItemEntity> type, LivingEntity owner, World world) {
		super(type, owner, world);
	}

	public void setItem(ItemStack item) {
		if (item.getItem() != this.getDefaultItem() || item.hasTag()) {
			this.getDataTracker().set(ITEM, Util.make(item.copy(), itemStack -> itemStack.setCount(1)));
		}
	}

	protected abstract Item getDefaultItem();

	protected ItemStack getItem() {
		return this.getDataTracker().get(ITEM);
	}

	@Environment(EnvType.CLIENT)
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
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		ItemStack itemStack = this.getItem();
		if (!itemStack.isEmpty()) {
			tag.put("Item", itemStack.toTag(new CompoundTag()));
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		ItemStack itemStack = ItemStack.fromTag(tag.getCompound("Item"));
		this.setItem(itemStack);
	}
}
