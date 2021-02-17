package net.minecraft.entity;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class ItemEntity extends Entity {
	private static final TrackedData<ItemStack> STACK = DataTracker.registerData(ItemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private int age;
	private int pickupDelay;
	private int health = 5;
	private UUID thrower;
	private UUID owner;
	public final float hoverHeight;

	public ItemEntity(EntityType<? extends ItemEntity> entityType, World world) {
		super(entityType, world);
		this.hoverHeight = (float)(Math.random() * Math.PI * 2.0);
	}

	public ItemEntity(World world, double x, double y, double z) {
		this(EntityType.ITEM, world);
		this.setPosition(x, y, z);
		this.yaw = this.random.nextFloat() * 360.0F;
		this.setVelocity(this.random.nextDouble() * 0.2 - 0.1, 0.2, this.random.nextDouble() * 0.2 - 0.1);
	}

	public ItemEntity(World world, double x, double y, double z, ItemStack stack) {
		this(world, x, y, z);
		this.setStack(stack);
	}

	@Environment(EnvType.CLIENT)
	private ItemEntity(ItemEntity entity) {
		super(entity.getType(), entity.world);
		this.setStack(entity.getStack().copy());
		this.copyPositionAndRotation(entity);
		this.age = entity.age;
		this.hoverHeight = entity.hoverHeight;
	}

	@Override
	public boolean occludeVibrationSignals() {
		return ItemTags.OCCLUDES_VIBRATION_SIGNALS.contains(this.getStack().getItem());
	}

	@Override
	protected Entity.MoveEffect getMoveEffect() {
		return Entity.MoveEffect.NONE;
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(STACK, ItemStack.EMPTY);
	}

	@Override
	public void tick() {
		if (this.getStack().isEmpty()) {
			this.discard();
		} else {
			super.tick();
			if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
				this.pickupDelay--;
			}

			this.prevX = this.getX();
			this.prevY = this.getY();
			this.prevZ = this.getZ();
			Vec3d vec3d = this.getVelocity();
			float f = this.getStandingEyeHeight() - 0.11111111F;
			if (this.isTouchingWater() && this.getFluidHeight(FluidTags.WATER) > (double)f) {
				this.applyWaterBuoyancy();
			} else if (this.isInLava() && this.getFluidHeight(FluidTags.LAVA) > (double)f) {
				this.applyLavaBuoyancy();
			} else if (!this.hasNoGravity()) {
				this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
			}

			if (this.world.isClient) {
				this.noClip = false;
			} else {
				this.noClip = !this.world.isSpaceEmpty(this, this.getBoundingBox().contract(1.0E-7), entity -> true);
				if (this.noClip) {
					this.pushOutOfBlocks(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
				}
			}

			if (!this.onGround || squaredHorizontalLength(this.getVelocity()) > 1.0E-5F || (this.age + this.getId()) % 4 == 0) {
				this.move(MovementType.SELF, this.getVelocity());
				float g = 0.98F;
				if (this.onGround) {
					g = this.world.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0, this.getZ())).getBlock().getSlipperiness() * 0.98F;
				}

				this.setVelocity(this.getVelocity().multiply((double)g, 0.98, (double)g));
				if (this.onGround) {
					Vec3d vec3d2 = this.getVelocity();
					if (vec3d2.y < 0.0) {
						this.setVelocity(vec3d2.multiply(1.0, -0.5, 1.0));
					}
				}
			}

			boolean bl = MathHelper.floor(this.prevX) != MathHelper.floor(this.getX())
				|| MathHelper.floor(this.prevY) != MathHelper.floor(this.getY())
				|| MathHelper.floor(this.prevZ) != MathHelper.floor(this.getZ());
			int i = bl ? 2 : 40;
			if (this.age % i == 0) {
				if (this.world.getFluidState(this.getBlockPos()).isIn(FluidTags.LAVA) && !this.isFireImmune()) {
					this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
				}

				if (!this.world.isClient && this.canMerge()) {
					this.tryMerge();
				}
			}

			if (this.age != -32768) {
				this.age++;
			}

			this.velocityDirty = this.velocityDirty | this.updateWaterState();
			if (!this.world.isClient) {
				double d = this.getVelocity().subtract(vec3d).lengthSquared();
				if (d > 0.01) {
					this.velocityDirty = true;
				}
			}

			if (!this.world.isClient && this.age >= 6000) {
				this.discard();
			}
		}
	}

	private void applyWaterBuoyancy() {
		Vec3d vec3d = this.getVelocity();
		this.setVelocity(vec3d.x * 0.99F, vec3d.y + (double)(vec3d.y < 0.06F ? 5.0E-4F : 0.0F), vec3d.z * 0.99F);
	}

	private void applyLavaBuoyancy() {
		Vec3d vec3d = this.getVelocity();
		this.setVelocity(vec3d.x * 0.95F, vec3d.y + (double)(vec3d.y < 0.06F ? 5.0E-4F : 0.0F), vec3d.z * 0.95F);
	}

	private void tryMerge() {
		if (this.canMerge()) {
			for (ItemEntity itemEntity : this.world
				.getEntitiesByClass(ItemEntity.class, this.getBoundingBox().expand(0.5, 0.0, 0.5), itemEntityx -> itemEntityx != this && itemEntityx.canMerge())) {
				if (itemEntity.canMerge()) {
					this.tryMerge(itemEntity);
					if (this.isRemoved()) {
						break;
					}
				}
			}
		}
	}

	private boolean canMerge() {
		ItemStack itemStack = this.getStack();
		return this.isAlive() && this.pickupDelay != 32767 && this.age != -32768 && this.age < 6000 && itemStack.getCount() < itemStack.getMaxCount();
	}

	private void tryMerge(ItemEntity other) {
		ItemStack itemStack = this.getStack();
		ItemStack itemStack2 = other.getStack();
		if (Objects.equals(this.getOwner(), other.getOwner()) && canMerge(itemStack, itemStack2)) {
			if (itemStack2.getCount() < itemStack.getCount()) {
				merge(this, itemStack, other, itemStack2);
			} else {
				merge(other, itemStack2, this, itemStack);
			}
		}
	}

	public static boolean canMerge(ItemStack stack1, ItemStack stack2) {
		if (!stack2.isOf(stack1.getItem())) {
			return false;
		} else if (stack2.getCount() + stack1.getCount() > stack2.getMaxCount()) {
			return false;
		} else {
			return stack2.hasTag() ^ stack1.hasTag() ? false : !stack2.hasTag() || stack2.getTag().equals(stack1.getTag());
		}
	}

	public static ItemStack merge(ItemStack stack1, ItemStack stack2, int maxCount) {
		int i = Math.min(Math.min(stack1.getMaxCount(), maxCount) - stack1.getCount(), stack2.getCount());
		ItemStack itemStack = stack1.copy();
		itemStack.increment(i);
		stack2.decrement(i);
		return itemStack;
	}

	private static void merge(ItemEntity targetEntity, ItemStack stack1, ItemStack stack2) {
		ItemStack itemStack = merge(stack1, stack2, 64);
		targetEntity.setStack(itemStack);
	}

	private static void merge(ItemEntity targetEntity, ItemStack targetStack, ItemEntity sourceEntity, ItemStack sourceStack) {
		merge(targetEntity, targetStack, sourceStack);
		targetEntity.pickupDelay = Math.max(targetEntity.pickupDelay, sourceEntity.pickupDelay);
		targetEntity.age = Math.min(targetEntity.age, sourceEntity.age);
		if (sourceStack.isEmpty()) {
			sourceEntity.discard();
		}
	}

	@Override
	public boolean isFireImmune() {
		return this.getStack().getItem().isFireproof() || super.isFireImmune();
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (!this.getStack().isEmpty() && this.getStack().isOf(Items.NETHER_STAR) && source.isExplosive()) {
			return false;
		} else if (!this.getStack().getItem().damage(source)) {
			return false;
		} else {
			this.scheduleVelocityUpdate();
			this.health = (int)((float)this.health - amount);
			this.emitGameEvent(GameEvent.ENTITY_DAMAGED, source.getAttacker());
			if (this.health <= 0) {
				this.getStack().onItemEntityDestroyed(this);
				this.discard();
			}

			return false;
		}
	}

	@Override
	public void writeCustomDataToNbt(CompoundTag tag) {
		tag.putShort("Health", (short)this.health);
		tag.putShort("Age", (short)this.age);
		tag.putShort("PickupDelay", (short)this.pickupDelay);
		if (this.getThrower() != null) {
			tag.putUuid("Thrower", this.getThrower());
		}

		if (this.getOwner() != null) {
			tag.putUuid("Owner", this.getOwner());
		}

		if (!this.getStack().isEmpty()) {
			tag.put("Item", this.getStack().writeNbt(new CompoundTag()));
		}
	}

	@Override
	public void readCustomDataFromNbt(CompoundTag tag) {
		this.health = tag.getShort("Health");
		this.age = tag.getShort("Age");
		if (tag.contains("PickupDelay")) {
			this.pickupDelay = tag.getShort("PickupDelay");
		}

		if (tag.containsUuid("Owner")) {
			this.owner = tag.getUuid("Owner");
		}

		if (tag.containsUuid("Thrower")) {
			this.thrower = tag.getUuid("Thrower");
		}

		CompoundTag compoundTag = tag.getCompound("Item");
		this.setStack(ItemStack.fromNbt(compoundTag));
		if (this.getStack().isEmpty()) {
			this.discard();
		}
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		if (!this.world.isClient) {
			ItemStack itemStack = this.getStack();
			Item item = itemStack.getItem();
			int i = itemStack.getCount();
			if (this.pickupDelay == 0 && (this.owner == null || this.owner.equals(player.getUuid())) && player.getInventory().insertStack(itemStack)) {
				player.sendPickup(this, i);
				if (itemStack.isEmpty()) {
					this.discard();
					itemStack.setCount(i);
				}

				player.increaseStat(Stats.PICKED_UP.getOrCreateStat(item), i);
				player.triggerItemPickedUpByEntityCriteria(this);
			}
		}
	}

	@Override
	public Text getName() {
		Text text = this.getCustomName();
		return (Text)(text != null ? text : new TranslatableText(this.getStack().getTranslationKey()));
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Nullable
	@Override
	public Entity moveToWorld(ServerWorld destination) {
		Entity entity = super.moveToWorld(destination);
		if (!this.world.isClient && entity instanceof ItemEntity) {
			((ItemEntity)entity).tryMerge();
		}

		return entity;
	}

	public ItemStack getStack() {
		return this.getDataTracker().get(STACK);
	}

	public void setStack(ItemStack stack) {
		this.getDataTracker().set(STACK, stack);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		super.onTrackedDataSet(data);
		if (STACK.equals(data)) {
			this.getStack().setHolder(this);
		}
	}

	@Nullable
	public UUID getOwner() {
		return this.owner;
	}

	public void setOwner(@Nullable UUID uuid) {
		this.owner = uuid;
	}

	@Nullable
	public UUID getThrower() {
		return this.thrower;
	}

	public void setThrower(@Nullable UUID uuid) {
		this.thrower = uuid;
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

	public void setPickupDelay(int pickupDelay) {
		this.pickupDelay = pickupDelay;
	}

	public boolean cannotPickup() {
		return this.pickupDelay > 0;
	}

	public void setCovetedItem() {
		this.age = -6000;
	}

	public void setDespawnImmediately() {
		this.setPickupDelayInfinite();
		this.age = 5999;
	}

	@Environment(EnvType.CLIENT)
	public float method_27314(float f) {
		return ((float)this.getAge() + f) / 20.0F + this.hoverHeight;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}

	@Environment(EnvType.CLIENT)
	public ItemEntity copy() {
		return new ItemEntity(this);
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.AMBIENT;
	}
}
