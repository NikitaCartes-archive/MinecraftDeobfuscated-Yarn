package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

public abstract class AbstractFireballEntity extends ExplosiveProjectileEntity implements FlyingItemEntity {
	private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(AbstractFireballEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

	public AbstractFireballEntity(EntityType<? extends AbstractFireballEntity> entityType, World world) {
		super(entityType, world);
	}

	public AbstractFireballEntity(EntityType<? extends AbstractFireballEntity> entityType, double d, double e, double f, double g, double h, double i, World world) {
		super(entityType, d, e, f, g, h, i, world);
	}

	public AbstractFireballEntity(EntityType<? extends AbstractFireballEntity> entityType, LivingEntity livingEntity, double d, double e, double f, World world) {
		super(entityType, livingEntity, d, e, f, world);
	}

	public void setItem(ItemStack stack) {
		if (!stack.isOf(Items.FIRE_CHARGE) || stack.hasNbt()) {
			this.getDataTracker().set(ITEM, stack.copyWithCount(1));
		}
	}

	protected ItemStack getItem() {
		return this.getDataTracker().get(ITEM);
	}

	@Override
	public ItemStack getStack() {
		ItemStack itemStack = this.getItem();
		return itemStack.isEmpty() ? new ItemStack(Items.FIRE_CHARGE) : itemStack;
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
