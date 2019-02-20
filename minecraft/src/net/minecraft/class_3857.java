package net.minecraft;

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
import net.minecraft.entity.thrown.ThrownEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.World;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = FlyingItemEntity.class
	)})
public abstract class class_3857 extends ThrownEntity implements FlyingItemEntity {
	private static final TrackedData<ItemStack> field_17082 = DataTracker.registerData(class_3857.class, TrackedDataHandlerRegistry.ITEM_STACK);

	public class_3857(EntityType<? extends class_3857> entityType, World world) {
		super(entityType, world);
	}

	public class_3857(EntityType<? extends class_3857> entityType, double d, double e, double f, World world) {
		super(entityType, d, e, f, world);
	}

	public class_3857(EntityType<? extends class_3857> entityType, LivingEntity livingEntity, World world) {
		super(entityType, livingEntity, world);
	}

	public void method_16940(ItemStack itemStack) {
		if (itemStack.getItem() != this.method_16942() || itemStack.hasTag()) {
			this.getDataTracker().set(field_17082, SystemUtil.consume(itemStack.copy(), itemStackx -> itemStackx.setAmount(1)));
		}
	}

	protected abstract Item method_16942();

	protected ItemStack method_16943() {
		return this.getDataTracker().get(field_17082);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getItem() {
		ItemStack itemStack = this.method_16943();
		return itemStack.isEmpty() ? new ItemStack(this.method_16942()) : itemStack;
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(field_17082, ItemStack.EMPTY);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		ItemStack itemStack = this.method_16943();
		if (!itemStack.isEmpty()) {
			compoundTag.put("Item", itemStack.toTag(new CompoundTag()));
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		ItemStack itemStack = ItemStack.fromTag(compoundTag.getCompound("Item"));
		this.method_16940(itemStack);
	}
}
