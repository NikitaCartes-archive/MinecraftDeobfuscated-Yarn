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
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
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
		this(EntityType.ITEM, world);
		this.setPosition(d, e, f);
		this.yaw = (float)(Math.random() * 360.0);
		this.velocityX = (double)((float)(Math.random() * 0.2F - 0.1F));
		this.velocityY = 0.2F;
		this.velocityZ = (double)((float)(Math.random() * 0.2F - 0.1F));
	}

	public ItemEntity(World world, double d, double e, double f, ItemStack itemStack) {
		this(world, d, e, f);
		this.setStack(itemStack);
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(STACK, ItemStack.EMPTY);
	}

	@Override
	public void update() {
		if (this.getStack().isEmpty()) {
			this.invalidate();
		} else {
			super.update();
			if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
				this.pickupDelay--;
			}

			this.prevX = this.x;
			this.prevY = this.y;
			this.prevZ = this.z;
			double d = this.velocityX;
			double e = this.velocityY;
			double f = this.velocityZ;
			if (this.isInFluid(FluidTags.field_15517)) {
				this.method_6974();
			} else if (!this.isUnaffectedByGravity()) {
				this.velocityY -= 0.04F;
			}

			if (this.world.isClient) {
				this.noClip = false;
			} else {
				this.noClip = !this.world.method_17892(this);
				if (this.noClip) {
					this.method_5632(this.x, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.z);
				}
			}

			this.move(MovementType.field_6308, this.velocityX, this.velocityY, this.velocityZ);
			boolean bl = (int)this.prevX != (int)this.x || (int)this.prevY != (int)this.y || (int)this.prevZ != (int)this.z;
			if (bl || this.age % 25 == 0) {
				if (this.world.getFluidState(new BlockPos(this)).matches(FluidTags.field_15518)) {
					this.velocityY = 0.2F;
					this.velocityX = (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
					this.velocityZ = (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
					this.playSound(SoundEvents.field_14821, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
				}

				if (!this.world.isClient) {
					this.tryMerge();
				}
			}

			float g = 0.98F;
			if (this.onGround) {
				g = this.world.getBlockState(new BlockPos(this.x, this.getBoundingBox().minY - 1.0, this.z)).getBlock().getFrictionCoefficient() * 0.98F;
			}

			this.velocityX *= (double)g;
			this.velocityY *= 0.98F;
			this.velocityZ *= (double)g;
			if (this.onGround) {
				this.velocityY *= -0.5;
			}

			if (this.age != -32768) {
				this.age++;
			}

			this.velocityDirty = this.velocityDirty | this.method_5713();
			if (!this.world.isClient) {
				double h = this.velocityX - d;
				double i = this.velocityY - e;
				double j = this.velocityZ - f;
				double k = h * h + i * i + j * j;
				if (k > 0.01) {
					this.velocityDirty = true;
				}
			}

			if (!this.world.isClient && this.age >= 6000) {
				this.invalidate();
			}
		}
	}

	private void method_6974() {
		if (this.velocityY < 0.06F) {
			this.velocityY += 5.0E-4F;
		}

		this.velocityX *= 0.99F;
		this.velocityZ *= 0.99F;
	}

	private void tryMerge() {
		List<ItemEntity> list = this.world.method_18467(ItemEntity.class, this.getBoundingBox().expand(0.5, 0.0, 0.5));
		if (!list.isEmpty()) {
			for (ItemEntity itemEntity : list) {
				this.tryMerge(itemEntity);
			}
		}
	}

	private boolean tryMerge(ItemEntity itemEntity) {
		if (itemEntity == this) {
			return false;
		} else if (!itemEntity.isValid() || !this.isValid()) {
			return false;
		} else if (this.pickupDelay == 32767 || itemEntity.pickupDelay == 32767) {
			return false;
		} else if (this.age != -32768 && itemEntity.age != -32768) {
			ItemStack itemStack = this.getStack();
			if (itemStack.getAmount() == itemStack.getMaxAmount()) {
				return false;
			} else {
				ItemStack itemStack2 = itemEntity.getStack();
				if (itemStack2.getItem() != itemStack.getItem()) {
					return false;
				} else if (itemStack2.getAmount() + itemStack.getAmount() > itemStack2.getMaxAmount()) {
					return false;
				} else if (itemStack2.hasTag() ^ itemStack.hasTag()) {
					return false;
				} else if (itemStack2.hasTag() && !itemStack2.getTag().equals(itemStack.getTag())) {
					return false;
				} else {
					if (itemStack2.getAmount() < itemStack.getAmount()) {
						method_18006(this, itemStack, itemEntity, itemStack2);
					} else {
						method_18006(itemEntity, itemStack2, this, itemStack);
					}

					return true;
				}
			}
		} else {
			return false;
		}
	}

	private static void method_18006(ItemEntity itemEntity, ItemStack itemStack, ItemEntity itemEntity2, ItemStack itemStack2) {
		ItemStack itemStack3 = itemStack.copy();
		itemStack3.addAmount(itemStack2.getAmount());
		itemEntity.setStack(itemStack3);
		itemEntity.pickupDelay = Math.max(itemEntity.pickupDelay, itemEntity2.pickupDelay);
		itemEntity.age = Math.min(itemEntity.age, itemEntity2.age);
		itemEntity2.invalidate();
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
				this.invalidate();
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
			this.invalidate();
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
				playerEntity.pickUpEntity(this, i);
				if (itemStack.isEmpty()) {
					this.invalidate();
					itemStack.setAmount(i);
				}

				playerEntity.incrementStat(Stats.field_15392.getOrCreateStat(item), i);
			}
		}
	}

	@Override
	public TextComponent getName() {
		TextComponent textComponent = this.getCustomName();
		return (TextComponent)(textComponent != null ? textComponent : new TranslatableTextComponent(this.getStack().getTranslationKey()));
	}

	@Override
	public boolean method_5732() {
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
