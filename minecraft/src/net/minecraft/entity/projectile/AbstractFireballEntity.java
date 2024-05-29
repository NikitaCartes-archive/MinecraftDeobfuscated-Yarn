package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class AbstractFireballEntity extends ExplosiveProjectileEntity implements FlyingItemEntity {
	private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(AbstractFireballEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

	public AbstractFireballEntity(EntityType<? extends AbstractFireballEntity> entityType, World world) {
		super(entityType, world);
	}

	public AbstractFireballEntity(EntityType<? extends AbstractFireballEntity> entityType, double d, double e, double f, Vec3d vec3d, World world) {
		super(entityType, d, e, f, vec3d, world);
	}

	public AbstractFireballEntity(EntityType<? extends AbstractFireballEntity> entityType, LivingEntity livingEntity, Vec3d vec3d, World world) {
		super(entityType, livingEntity, vec3d, world);
	}

	public void setItem(ItemStack stack) {
		if (stack.isEmpty()) {
			this.getDataTracker().set(ITEM, this.getItem());
		} else {
			this.getDataTracker().set(ITEM, stack.copyWithCount(1));
		}
	}

	@Override
	public ItemStack getStack() {
		return this.getDataTracker().get(ITEM);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		builder.add(ITEM, this.getItem());
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.put("Item", this.getStack().encode(this.getRegistryManager()));
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("Item", NbtElement.COMPOUND_TYPE)) {
			this.setItem((ItemStack)ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("Item")).orElse(this.getItem()));
		} else {
			this.setItem(this.getItem());
		}
	}

	private ItemStack getItem() {
		return new ItemStack(Items.FIRE_CHARGE);
	}

	@Override
	public StackReference getStackReference(int mappedIndex) {
		return mappedIndex == 0 ? StackReference.of(this::getStack, this::setItem) : super.getStackReference(mappedIndex);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return false;
	}
}
