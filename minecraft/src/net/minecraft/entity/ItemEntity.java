package net.minecraft.entity;

import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;

public class ItemEntity extends Entity implements Ownable {
	private static final TrackedData<ItemStack> STACK = DataTracker.registerData(ItemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private static final float field_48703 = 0.1F;
	public static final float field_48702 = 0.2125F;
	private static final int DESPAWN_AGE = 6000;
	private static final int CANNOT_PICK_UP_DELAY = 32767;
	private static final int NEVER_DESPAWN_AGE = -32768;
	/**
	 * The number of ticks since this item entity has been created.
	 * It is a short value with key {@code Age} in the NBT structure.
	 * 
	 * <p>It differs from {@link Entity#age}.
	 */
	private int itemAge;
	private int pickupDelay;
	private int health = 5;
	@Nullable
	private UUID throwerUuid;
	@Nullable
	private Entity thrower;
	@Nullable
	private UUID owner;
	public final float uniqueOffset;

	public ItemEntity(EntityType<? extends ItemEntity> entityType, World world) {
		super(entityType, world);
		this.uniqueOffset = this.random.nextFloat() * (float) Math.PI * 2.0F;
		this.setYaw(this.random.nextFloat() * 360.0F);
	}

	public ItemEntity(World world, double x, double y, double z, ItemStack stack) {
		this(world, x, y, z, stack, world.random.nextDouble() * 0.2 - 0.1, 0.2, world.random.nextDouble() * 0.2 - 0.1);
	}

	public ItemEntity(World world, double x, double y, double z, ItemStack stack, double velocityX, double velocityY, double velocityZ) {
		this(EntityType.ITEM, world);
		this.setPosition(x, y, z);
		this.setVelocity(velocityX, velocityY, velocityZ);
		this.setStack(stack);
	}

	private ItemEntity(ItemEntity entity) {
		super(entity.getType(), entity.getWorld());
		this.setStack(entity.getStack().copy());
		this.copyPositionAndRotation(entity);
		this.itemAge = entity.itemAge;
		this.uniqueOffset = entity.uniqueOffset;
	}

	@Override
	public boolean occludeVibrationSignals() {
		return this.getStack().isIn(ItemTags.DAMPENS_VIBRATIONS);
	}

	@Nullable
	@Override
	public Entity getOwner() {
		if (this.thrower != null && !this.thrower.isRemoved()) {
			return this.thrower;
		} else if (this.throwerUuid != null && this.getWorld() instanceof ServerWorld serverWorld) {
			this.thrower = serverWorld.getEntity(this.throwerUuid);
			return this.thrower;
		} else {
			return null;
		}
	}

	@Override
	public void copyFrom(Entity original) {
		super.copyFrom(original);
		if (original instanceof ItemEntity itemEntity) {
			this.thrower = itemEntity.thrower;
		}
	}

	@Override
	protected Entity.MoveEffect getMoveEffect() {
		return Entity.MoveEffect.NONE;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		builder.add(STACK, ItemStack.EMPTY);
	}

	@Override
	protected double getGravity() {
		return 0.04;
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
			if (this.isTouchingWater() && this.getFluidHeight(FluidTags.WATER) > 0.1F) {
				this.applyWaterBuoyancy();
			} else if (this.isInLava() && this.getFluidHeight(FluidTags.LAVA) > 0.1F) {
				this.applyLavaBuoyancy();
			} else {
				this.applyGravity();
			}

			if (this.getWorld().isClient) {
				this.noClip = false;
			} else {
				this.noClip = !this.getWorld().isSpaceEmpty(this, this.getBoundingBox().contract(1.0E-7));
				if (this.noClip) {
					this.pushOutOfBlocks(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
				}
			}

			if (!this.isOnGround() || this.getVelocity().horizontalLengthSquared() > 1.0E-5F || (this.age + this.getId()) % 4 == 0) {
				this.move(MovementType.SELF, this.getVelocity());
				this.tickBlockCollision();
				float f = 0.98F;
				if (this.isOnGround()) {
					f = this.getWorld().getBlockState(this.getVelocityAffectingPos()).getBlock().getSlipperiness() * 0.98F;
				}

				this.setVelocity(this.getVelocity().multiply((double)f, 0.98, (double)f));
				if (this.isOnGround()) {
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
			if (this.age % i == 0 && !this.getWorld().isClient && this.canMerge()) {
				this.tryMerge();
			}

			if (this.itemAge != -32768) {
				this.itemAge++;
			}

			this.velocityDirty = this.velocityDirty | this.updateWaterState();
			if (!this.getWorld().isClient) {
				double d = this.getVelocity().subtract(vec3d).lengthSquared();
				if (d > 0.01) {
					this.velocityDirty = true;
				}
			}

			if (!this.getWorld().isClient && this.itemAge >= 6000) {
				this.discard();
			}
		}
	}

	@Override
	public BlockPos getVelocityAffectingPos() {
		return this.getPosWithYOffset(0.999999F);
	}

	private void applyWaterBuoyancy() {
		this.applyBuoyancy(0.99F);
	}

	private void applyLavaBuoyancy() {
		this.applyBuoyancy(0.95F);
	}

	private void applyBuoyancy(double horizontalMultiplier) {
		Vec3d vec3d = this.getVelocity();
		this.setVelocity(vec3d.x * horizontalMultiplier, vec3d.y + (double)(vec3d.y < 0.06F ? 5.0E-4F : 0.0F), vec3d.z * horizontalMultiplier);
	}

	private void tryMerge() {
		if (this.canMerge()) {
			for (ItemEntity itemEntity : this.getWorld()
				.getEntitiesByClass(ItemEntity.class, this.getBoundingBox().expand(0.5, 0.0, 0.5), otherItemEntity -> otherItemEntity != this && otherItemEntity.canMerge())) {
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
		return this.isAlive() && this.pickupDelay != 32767 && this.itemAge != -32768 && this.itemAge < 6000 && itemStack.getCount() < itemStack.getMaxCount();
	}

	private void tryMerge(ItemEntity other) {
		ItemStack itemStack = this.getStack();
		ItemStack itemStack2 = other.getStack();
		if (Objects.equals(this.owner, other.owner) && canMerge(itemStack, itemStack2)) {
			if (itemStack2.getCount() < itemStack.getCount()) {
				merge(this, itemStack, other, itemStack2);
			} else {
				merge(other, itemStack2, this, itemStack);
			}
		}
	}

	public static boolean canMerge(ItemStack stack1, ItemStack stack2) {
		return stack2.getCount() + stack1.getCount() > stack2.getMaxCount() ? false : ItemStack.areItemsAndComponentsEqual(stack1, stack2);
	}

	public static ItemStack merge(ItemStack stack1, ItemStack stack2, int maxCount) {
		int i = Math.min(Math.min(stack1.getMaxCount(), maxCount) - stack1.getCount(), stack2.getCount());
		ItemStack itemStack = stack1.copyWithCount(stack1.getCount() + i);
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
		targetEntity.itemAge = Math.min(targetEntity.itemAge, sourceEntity.itemAge);
		if (sourceStack.isEmpty()) {
			sourceEntity.discard();
		}
	}

	@Override
	public boolean isFireImmune() {
		return !this.getStack().takesDamageFrom(this.getDamageSources().inFire()) || super.isFireImmune();
	}

	@Override
	protected boolean shouldPlayBurnSoundInLava() {
		return this.health <= 0 ? true : this.age % 10 == 0;
	}

	@Override
	public final boolean clientDamage(DamageSource source) {
		return this.isAlwaysInvulnerableTo(source) ? false : this.getStack().takesDamageFrom(source);
	}

	@Override
	public final boolean damage(ServerWorld world, DamageSource source, float amount) {
		if (this.isAlwaysInvulnerableTo(source)) {
			return false;
		} else if (!world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) && source.getAttacker() instanceof MobEntity) {
			return false;
		} else if (!this.getStack().takesDamageFrom(source)) {
			return false;
		} else {
			this.scheduleVelocityUpdate();
			this.health = (int)((float)this.health - amount);
			this.emitGameEvent(GameEvent.ENTITY_DAMAGE, source.getAttacker());
			if (this.health <= 0) {
				this.getStack().onItemEntityDestroyed(this);
				this.discard();
			}

			return true;
		}
	}

	@Override
	public boolean isImmuneToExplosion(Explosion explosion) {
		return explosion.preservesDecorativeEntities() ? super.isImmuneToExplosion(explosion) : true;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putShort("Health", (short)this.health);
		nbt.putShort("Age", (short)this.itemAge);
		nbt.putShort("PickupDelay", (short)this.pickupDelay);
		if (this.throwerUuid != null) {
			nbt.putUuid("Thrower", this.throwerUuid);
		}

		if (this.owner != null) {
			nbt.putUuid("Owner", this.owner);
		}

		if (!this.getStack().isEmpty()) {
			nbt.put("Item", this.getStack().toNbt(this.getRegistryManager()));
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		this.health = nbt.getShort("Health");
		this.itemAge = nbt.getShort("Age");
		if (nbt.contains("PickupDelay")) {
			this.pickupDelay = nbt.getShort("PickupDelay");
		}

		if (nbt.containsUuid("Owner")) {
			this.owner = nbt.getUuid("Owner");
		}

		if (nbt.containsUuid("Thrower")) {
			this.throwerUuid = nbt.getUuid("Thrower");
			this.thrower = null;
		}

		if (nbt.contains("Item", NbtElement.COMPOUND_TYPE)) {
			NbtCompound nbtCompound = nbt.getCompound("Item");
			this.setStack((ItemStack)ItemStack.fromNbt(this.getRegistryManager(), nbtCompound).orElse(ItemStack.EMPTY));
		} else {
			this.setStack(ItemStack.EMPTY);
		}

		if (this.getStack().isEmpty()) {
			this.discard();
		}
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		if (!this.getWorld().isClient) {
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
		return text != null ? text : this.getStack().getItemName();
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Nullable
	@Override
	public Entity teleportTo(TeleportTarget teleportTarget) {
		Entity entity = super.teleportTo(teleportTarget);
		if (!this.getWorld().isClient && entity instanceof ItemEntity itemEntity) {
			itemEntity.tryMerge();
		}

		return entity;
	}

	/**
	 * Returns the item stack contained in this item entity.
	 */
	public ItemStack getStack() {
		return this.getDataTracker().get(STACK);
	}

	/**
	 * Sets the item stack contained in this item entity to {@code stack}.
	 */
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

	public void setOwner(@Nullable UUID owner) {
		this.owner = owner;
	}

	/**
	 * Sets the thrower of this item entity to {@code thrower}.
	 */
	public void setThrower(Entity thrower) {
		this.throwerUuid = thrower.getUuid();
		this.thrower = thrower;
	}

	/**
	 * Returns the number of ticks since this item entity has been created.
	 * 
	 * <p>Increases every tick. When it equals to 6000 ticks (5 minutes),
	 * this item entity disappears.
	 * 
	 * <p>Unlike {@linkplain Entity#age}, it is persistent and not synchronized
	 * between the client and the server.
	 * 
	 * @see #tick()
	 */
	public int getItemAge() {
		return this.itemAge;
	}

	/**
	 * Sets the number of ticks before this item entity can be picked up
	 * to the default value of 10.
	 */
	public void setToDefaultPickupDelay() {
		this.pickupDelay = 10;
	}

	/**
	 * Sets the number of ticks before this item entity can be picked up
	 * to 0.
	 */
	public void resetPickupDelay() {
		this.pickupDelay = 0;
	}

	/**
	 * Makes this item entity impossible to be picked up by setting its
	 * pickup delay to 32767.
	 */
	public void setPickupDelayInfinite() {
		this.pickupDelay = 32767;
	}

	/**
	 * Sets the number of ticks before this item entity can be picked up
	 * to {@code pickupDelay}.
	 */
	public void setPickupDelay(int pickupDelay) {
		this.pickupDelay = pickupDelay;
	}

	/**
	 * Returns whether the pickup delay of this item entity is greater
	 * than 0.
	 */
	public boolean cannotPickup() {
		return this.pickupDelay > 0;
	}

	public void setNeverDespawn() {
		this.itemAge = -32768;
	}

	public void setCovetedItem() {
		this.itemAge = -6000;
	}

	public void setDespawnImmediately() {
		this.setPickupDelayInfinite();
		this.itemAge = 5999;
	}

	public static float getRotation(float f, float g) {
		return f / 20.0F + g;
	}

	public ItemEntity copy() {
		return new ItemEntity(this);
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.AMBIENT;
	}

	@Override
	public float getBodyYaw() {
		return 180.0F - getRotation((float)this.getItemAge() + 0.5F, this.uniqueOffset) / (float) (Math.PI * 2) * 360.0F;
	}

	@Override
	public StackReference getStackReference(int mappedIndex) {
		return mappedIndex == 0 ? StackReference.of(this::getStack, this::setStack) : super.getStackReference(mappedIndex);
	}
}
