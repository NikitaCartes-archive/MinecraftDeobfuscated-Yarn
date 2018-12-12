package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.World;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = class_3856.class
	)})
public abstract class class_3855 extends ExplosiveProjectileEntity implements class_3856 {
	private static final TrackedData<ItemStack> field_17081 = DataTracker.registerData(class_3855.class, TrackedDataHandlerRegistry.ITEM_STACK);

	public class_3855(World world, float f, float g) {
		super(EntityType.FIREBALL, world, f, g);
	}

	public class_3855(double d, double e, double f, double g, double h, double i, World world, float j, float k) {
		super(EntityType.FIREBALL, d, e, f, g, h, i, world, j, k);
	}

	public class_3855(LivingEntity livingEntity, double d, double e, double f, World world, float g, float h) {
		super(EntityType.FIREBALL, livingEntity, d, e, f, world, g, h);
	}

	public void method_16936(ItemStack itemStack) {
		if (itemStack.getItem() != Items.field_8814 || itemStack.hasTag()) {
			this.getDataTracker().set(field_17081, SystemUtil.consume(itemStack.copy(), itemStackx -> itemStackx.setAmount(1)));
		}
	}

	protected ItemStack method_16938() {
		return this.getDataTracker().get(field_17081);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack method_7495() {
		ItemStack itemStack = this.method_16938();
		return itemStack.isEmpty() ? new ItemStack(Items.field_8814) : itemStack;
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(field_17081, ItemStack.EMPTY);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		ItemStack itemStack = this.method_16938();
		if (!itemStack.isEmpty()) {
			compoundTag.put("Item", itemStack.toTag(new CompoundTag()));
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		ItemStack itemStack = ItemStack.fromTag(compoundTag.getCompound("Item"));
		this.method_16936(itemStack);
	}
}
