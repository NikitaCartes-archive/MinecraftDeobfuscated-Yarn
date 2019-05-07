package net.minecraft.entity;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class ItemEntity extends Entity {
	private static final TrackedData<ItemStack> STACK = DataTracker.registerData(ItemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private int age;
	private int pickupDelay;
	private int health = 5;
	private UUID thrower;
	private UUID owner;
	public final float hoverHeight = (float)(Math.random() * Math.PI * 2.0);

	public ItemEntity(EntityType<? extends ItemEntity> entityType, World world) {
		super(entityType, world);
	}

	public ItemEntity(World world, double d, double e, double f) {
		this(EntityType.field_6052, world);
		this.setPosition(d, e, f);
		this.yaw = this.random.nextFloat() * 360.0F;
		this.setVelocity(this.random.nextDouble() * 0.2 - 0.1, 0.2, this.random.nextDouble() * 0.2 - 0.1);
	}

	public ItemEntity(World world, double d, double e, double f, ItemStack itemStack) {
		this(world, d, e, f);
		this.setStack(itemStack);
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(STACK, ItemStack.EMPTY);
	}

	@Override
	public void tick() {
		if (this.getStack().isEmpty()) {
			this.remove();
		} else {
			super.tick();
			if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
				this.pickupDelay--;
			}

			this.prevX = this.x;
			this.prevY = this.y;
			this.prevZ = this.z;
			Vec3d vec3d = this.getVelocity();
			if (this.isInFluid(FluidTags.field_15517)) {
				this.method_6974();
			} else if (!this.hasNoGravity()) {
				this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
			}

			if (this.world.isClient) {
				this.noClip = false;
			} else {
				this.noClip = !this.world.doesNotCollide(this);
				if (this.noClip) {
					this.pushOutOfBlocks(this.x, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.z);
				}
			}

			if (!this.onGround || squaredHorizontalLength(this.getVelocity()) > 1.0E-5F || (this.age + this.getEntityId()) % 4 == 0) {
				this.move(MovementType.field_6308, this.getVelocity());
				float f = 0.98F;
				if (this.onGround) {
					f = this.world.getBlockState(new BlockPos(this.x, this.getBoundingBox().minY - 1.0, this.z)).getBlock().getSlipperiness() * 0.98F;
				}

				this.setVelocity(this.getVelocity().multiply((double)f, 0.98, (double)f));
				if (this.onGround) {
					this.setVelocity(this.getVelocity().multiply(1.0, -0.5, 1.0));
				}
			}

			boolean bl = MathHelper.floor(this.prevX) != MathHelper.floor(this.x)
				|| MathHelper.floor(this.prevY) != MathHelper.floor(this.y)
				|| MathHelper.floor(this.prevZ) != MathHelper.floor(this.z);
			int i = bl ? 2 : 40;
			if (this.age % i == 0) {
				if (this.world.getFluidState(new BlockPos(this)).matches(FluidTags.field_15518)) {
					this.setVelocity(
						(double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F), 0.2F, (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F)
					);
					this.playSound(SoundEvents.field_14821, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
				}

				if (!this.world.isClient && this.method_20397()) {
					this.tryMerge();
				}
			}

			if (this.age != -32768) {
				this.age++;
			}

			this.velocityDirty = this.velocityDirty | this.method_5713();
			if (!this.world.isClient) {
				double d = this.getVelocity().subtract(vec3d).lengthSquared();
				if (d > 0.01) {
					this.velocityDirty = true;
				}
			}

			if (!this.world.isClient && this.age >= 6000) {
				this.remove();
			}
		}
	}

	private void method_6974() {
		Vec3d vec3d = this.getVelocity();
		this.setVelocity(vec3d.x * 0.99F, vec3d.y + (double)(vec3d.y < 0.06F ? 5.0E-4F : 0.0F), vec3d.z * 0.99F);
	}

	private void tryMerge() {
		List<ItemEntity> list = this.world
			.getEntities(ItemEntity.class, this.getBoundingBox().expand(0.5, 0.0, 0.5), itemEntityx -> itemEntityx != this && itemEntityx.method_20397());
		if (!list.isEmpty()) {
			for (ItemEntity itemEntity : list) {
				if (!this.method_20397()) {
					return;
				}

				this.tryMerge(itemEntity);
			}
		}
	}

	private boolean method_20397() {
		ItemStack itemStack = this.getStack();
		return this.isAlive() && this.pickupDelay != 32767 && this.age != -32768 && this.age < 6000 && itemStack.getAmount() < itemStack.getMaxAmount();
	}

	private void tryMerge(ItemEntity itemEntity) {
		ItemStack itemStack = this.getStack();
		ItemStack itemStack2 = itemEntity.getStack();
		if (itemStack2.getItem() == itemStack.getItem()) {
			if (itemStack2.getAmount() + itemStack.getAmount() <= itemStack2.getMaxAmount()) {
				if (!(itemStack2.hasTag() ^ itemStack.hasTag())) {
					if (!itemStack2.hasTag() || itemStack2.getTag().equals(itemStack.getTag())) {
						if (itemStack2.getAmount() < itemStack.getAmount()) {
							merge(this, itemStack, itemEntity, itemStack2);
						} else {
							merge(itemEntity, itemStack2, this, itemStack);
						}
					}
				}
			}
		}
	}

	private static void merge(ItemEntity itemEntity, ItemStack itemStack, ItemEntity itemEntity2, ItemStack itemStack2) {
		int i = Math.min(itemStack.getMaxAmount() - itemStack.getAmount(), itemStack2.getAmount());
		ItemStack itemStack3 = itemStack.copy();
		itemStack3.addAmount(i);
		itemEntity.setStack(itemStack3);
		itemStack2.subtractAmount(i);
		itemEntity2.setStack(itemStack2);
		itemEntity.pickupDelay = Math.max(itemEntity.pickupDelay, itemEntity2.pickupDelay);
		itemEntity.age = Math.min(itemEntity.age, itemEntity2.age);
		if (itemStack2.isEmpty()) {
			itemEntity2.remove();
		}
	}

	public void method_6980() {
		this.age = 4800;
	}

	@Override
	protected void burn(int i) {
		this.damage(DamageSource.IN_FIRE, (float)i);
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else if (!this.getStack().isEmpty() && this.getStack().getItem() == Items.field_8137 && damageSource.isExplosive()) {
			return false;
		} else {
			this.scheduleVelocityUpdate();
			this.health = (int)((float)this.health - f);
			if (this.health <= 0) {
				this.remove();
			}

			return false;
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		compoundTag.putShort("Health", (short)this.health);
		compoundTag.putShort("Age", (short)this.age);
		compoundTag.putShort("PickupDelay", (short)this.pickupDelay);
		if (this.getThrower() != null) {
			compoundTag.put("Thrower", TagHelper.serializeUuid(this.getThrower()));
		}

		if (this.getOwner() != null) {
			compoundTag.put("Owner", TagHelper.serializeUuid(this.getOwner()));
		}

		if (!this.getStack().isEmpty()) {
			compoundTag.put("Item", this.getStack().toTag(new CompoundTag()));
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		this.health = compoundTag.getShort("Health");
		this.age = compoundTag.getShort("Age");
		if (compoundTag.containsKey("PickupDelay")) {
			this.pickupDelay = compoundTag.getShort("PickupDelay");
		}

		if (compoundTag.containsKey("Owner", 10)) {
			this.owner = TagHelper.deserializeUuid(compoundTag.getCompound("Owner"));
		}

		if (compoundTag.containsKey("Thrower", 10)) {
			this.thrower = TagHelper.deserializeUuid(compoundTag.getCompound("Thrower"));
		}

		CompoundTag compoundTag2 = compoundTag.getCompound("Item");
		this.setStack(ItemStack.fromTag(compoundTag2));
		if (this.getStack().isEmpty()) {
			this.remove();
		}
	}

	@Override
	public void onPlayerCollision(PlayerEntity playerEntity) {
		if (!this.world.isClient) {
			ItemStack itemStack = this.getStack();
			Item item = itemStack.getItem();
			int i = itemStack.getAmount();
			if (this.pickupDelay == 0
				&& (this.owner == null || 6000 - this.age <= 200 || this.owner.equals(playerEntity.getUuid()))
				&& playerEntity.inventory.insertStack(itemStack)) {
				playerEntity.sendPickup(this, i);
				if (itemStack.isEmpty()) {
					this.remove();
					itemStack.setAmount(i);
				}

				playerEntity.increaseStat(Stats.field_15392.getOrCreateStat(item), i);
			}
		}
	}

	@Override
	public Component getName() {
		Component component = this.getCustomName();
		return (Component)(component != null ? component : new TranslatableComponent(this.getStack().getTranslationKey()));
	}

	@Override
	public boolean canPlayerAttack() {
		return false;
	}

	@Nullable
	@Override
	public Entity changeDimension(DimensionType dimensionType) {
		Entity entity = super.changeDimension(dimensionType);
		if (!this.world.isClient && entity instanceof ItemEntity) {
			((ItemEntity)entity).tryMerge();
		}

		return entity;
	}

	public ItemStack getStack() {
		return this.getDataTracker().get(STACK);
	}

	public void setStack(ItemStack itemStack) {
		this.getDataTracker().set(STACK, itemStack);
	}

	@Nullable
	public UUID getOwner() {
		return this.owner;
	}

	public void setOwner(@Nullable UUID uUID) {
		this.owner = uUID;
	}

	@Nullable
	public UUID getThrower() {
		return this.thrower;
	}

	public void setThrower(@Nullable UUID uUID) {
		this.thrower = uUID;
	}

	@Environment(EnvType.CLIENT)
	public int getAge() {
		return this.age;
	}

	public void setToDefaultPickupDelay() {
		this.pickupDelay = 10;
	}

	public void resetPickupDelay() {
		this.pickupDelay = 0;
	}

	public void setPickupDelayInfinite() {
		this.pickupDelay = 32767;
	}

	public void setPickupDelay(int i) {
		this.pickupDelay = i;
	}

	public boolean cannotPickup() {
		return this.pickupDelay > 0;
	}

	public void method_6976() {
		this.age = -6000;
	}

	public void method_6987() {
		this.setPickupDelayInfinite();
		this.age = 5999;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
