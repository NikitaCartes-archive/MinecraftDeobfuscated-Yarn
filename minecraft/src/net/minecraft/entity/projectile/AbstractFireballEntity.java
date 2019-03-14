package net.minecraft.entity.projectile;

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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.SystemUtil;
import net.minecraft.world.World;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = FlyingItemEntity.class
	)})
public abstract class AbstractFireballEntity extends ExplosiveProjectileEntity implements FlyingItemEntity {
	private static final TrackedData<ItemStack> stack = DataTracker.registerData(AbstractFireballEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

	public AbstractFireballEntity(EntityType<? extends AbstractFireballEntity> entityType, World world) {
		super(entityType, world);
	}

	public AbstractFireballEntity(EntityType<? extends AbstractFireballEntity> entityType, double d, double e, double f, double g, double h, double i, World world) {
		super(entityType, d, e, f, g, h, i, world);
	}

	public AbstractFireballEntity(EntityType<? extends AbstractFireballEntity> entityType, LivingEntity livingEntity, double d, double e, double f, World world) {
		super(entityType, livingEntity, d, e, f, world);
	}

	public void method_16936(ItemStack itemStack) {
		if (itemStack.getItem() != Items.field_8814 || itemStack.hasTag()) {
			this.getDataTracker().set(stack, SystemUtil.consume(itemStack.copy(), itemStackx -> itemStackx.setAmount(1)));
		}
	}

	protected ItemStack method_16938() {
		return this.getDataTracker().get(stack);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getItem() {
		ItemStack itemStack = this.method_16938();
		return itemStack.isEmpty() ? new ItemStack(Items.field_8814) : itemStack;
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(stack, ItemStack.EMPTY);
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
