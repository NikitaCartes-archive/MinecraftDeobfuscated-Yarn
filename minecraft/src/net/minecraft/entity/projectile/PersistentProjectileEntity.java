package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.OminousItemSpawnerEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public abstract class PersistentProjectileEntity extends ProjectileEntity {
	private static final double field_30657 = 2.0;
	private static final TrackedData<Byte> PROJECTILE_FLAGS = DataTracker.registerData(PersistentProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Byte> PIERCE_LEVEL = DataTracker.registerData(PersistentProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final int CRITICAL_FLAG = 1;
	private static final int NO_CLIP_FLAG = 2;
	@Nullable
	private BlockState inBlockState;
	protected boolean inGround;
	protected int inGroundTime;
	public PersistentProjectileEntity.PickupPermission pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
	public int shake;
	private int life;
	private double damage = 2.0;
	private SoundEvent sound = this.getHitSound();
	@Nullable
	private IntOpenHashSet piercedEntities;
	@Nullable
	private List<Entity> piercingKilledEntities;
	private ItemStack stack = this.getDefaultItemStack();
	@Nullable
	private ItemStack weapon = null;

	protected PersistentProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	protected PersistentProjectileEntity(
		EntityType<? extends PersistentProjectileEntity> type, double x, double y, double z, World world, ItemStack stack, @Nullable ItemStack weapon
	) {
		this(type, world);
		this.stack = stack.copy();
		this.setCustomName(stack.get(DataComponentTypes.CUSTOM_NAME));
		Unit unit = stack.remove(DataComponentTypes.INTANGIBLE_PROJECTILE);
		if (unit != null) {
			this.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
		}

		this.setPosition(x, y, z);
		if (weapon != null && world instanceof ServerWorld serverWorld) {
			this.weapon = weapon.copy();
			int i = EnchantmentHelper.getProjectilePiercing(serverWorld, weapon, this.stack);
			if (i > 0) {
				this.setPierceLevel((byte)i);
			}

			EnchantmentHelper.onProjectileSpawned(serverWorld, weapon, this, item -> this.weapon = null);
		}
	}

	protected PersistentProjectileEntity(
		EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world, ItemStack stack, @Nullable ItemStack shotFrom
	) {
		this(type, owner.getX(), owner.getEyeY() - 0.1F, owner.getZ(), world, stack, shotFrom);
		this.setOwner(owner);
	}

	public void setSound(SoundEvent sound) {
		this.sound = sound;
	}

	@Override
	public boolean shouldRender(double distance) {
		double d = this.getBoundingBox().getAverageSideLength() * 10.0;
		if (Double.isNaN(d)) {
			d = 1.0;
		}

		d *= 64.0 * getRenderDistanceMultiplier();
		return distance < d * d;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		builder.add(PROJECTILE_FLAGS, (byte)0);
		builder.add(PIERCE_LEVEL, (byte)0);
	}

	@Override
	public void setVelocity(double x, double y, double z, float power, float uncertainty) {
		super.setVelocity(x, y, z, power, uncertainty);
		this.life = 0;
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
		this.setPosition(x, y, z);
		this.setRotation(yaw, pitch);
	}

	@Override
	public void setVelocityClient(double x, double y, double z) {
		super.setVelocityClient(x, y, z);
		this.life = 0;
	}

	@Override
	public void tick() {
		super.tick();
		boolean bl = this.isNoClip();
		Vec3d vec3d = this.getVelocity();
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			double d = vec3d.horizontalLength();
			this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI));
			this.setPitch((float)(MathHelper.atan2(vec3d.y, d) * 180.0F / (float)Math.PI));
			this.prevYaw = this.getYaw();
			this.prevPitch = this.getPitch();
		}

		BlockPos blockPos = this.getBlockPos();
		BlockState blockState = this.getWorld().getBlockState(blockPos);
		if (!blockState.isAir() && !bl) {
			VoxelShape voxelShape = blockState.getCollisionShape(this.getWorld(), blockPos);
			if (!voxelShape.isEmpty()) {
				Vec3d vec3d2 = this.getPos();

				for (Box box : voxelShape.getBoundingBoxes()) {
					if (box.offset(blockPos).contains(vec3d2)) {
						this.inGround = true;
						break;
					}
				}
			}
		}

		if (this.shake > 0) {
			this.shake--;
		}

		if (this.isTouchingWaterOrRain() || blockState.isOf(Blocks.POWDER_SNOW)) {
			this.extinguish();
		}

		if (this.inGround && !bl) {
			if (this.inBlockState != blockState && this.shouldFall()) {
				this.fall();
			} else if (!this.getWorld().isClient) {
				this.age();
			}

			this.inGroundTime++;
		} else {
			this.inGroundTime = 0;
			Vec3d vec3d3 = this.getPos();
			Vec3d vec3d2 = vec3d3.add(vec3d);
			HitResult hitResult = this.getWorld()
				.raycast(new RaycastContext(vec3d3, vec3d2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));
			if (hitResult.getType() != HitResult.Type.MISS) {
				vec3d2 = hitResult.getPos();
			}

			while (!this.isRemoved()) {
				EntityHitResult entityHitResult = this.getEntityCollision(vec3d3, vec3d2);
				if (entityHitResult != null) {
					hitResult = entityHitResult;
				}

				if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
					Entity entity = ((EntityHitResult)hitResult).getEntity();
					Entity entity2 = this.getOwner();
					if (entity instanceof PlayerEntity && entity2 instanceof PlayerEntity && !((PlayerEntity)entity2).shouldDamagePlayer((PlayerEntity)entity)) {
						hitResult = null;
						entityHitResult = null;
					}
				}

				if (hitResult != null && !bl) {
					ProjectileDeflection projectileDeflection = this.hitOrDeflect(hitResult);
					this.velocityDirty = true;
					if (projectileDeflection != ProjectileDeflection.NONE) {
						break;
					}
				}

				if (entityHitResult == null || this.getPierceLevel() <= 0) {
					break;
				}

				hitResult = null;
			}

			vec3d = this.getVelocity();
			double e = vec3d.x;
			double f = vec3d.y;
			double g = vec3d.z;
			if (this.isCritical()) {
				for (int i = 0; i < 4; i++) {
					this.getWorld()
						.addParticle(
							ParticleTypes.CRIT, this.getX() + e * (double)i / 4.0, this.getY() + f * (double)i / 4.0, this.getZ() + g * (double)i / 4.0, -e, -f + 0.2, -g
						);
				}
			}

			double h = this.getX() + e;
			double j = this.getY() + f;
			double k = this.getZ() + g;
			double l = vec3d.horizontalLength();
			if (bl) {
				this.setYaw((float)(MathHelper.atan2(-e, -g) * 180.0F / (float)Math.PI));
			} else {
				this.setYaw((float)(MathHelper.atan2(e, g) * 180.0F / (float)Math.PI));
			}

			this.setPitch((float)(MathHelper.atan2(f, l) * 180.0F / (float)Math.PI));
			this.setPitch(updateRotation(this.prevPitch, this.getPitch()));
			this.setYaw(updateRotation(this.prevYaw, this.getYaw()));
			float m = 0.99F;
			if (this.isTouchingWater()) {
				for (int n = 0; n < 4; n++) {
					float o = 0.25F;
					this.getWorld().addParticle(ParticleTypes.BUBBLE, h - e * 0.25, j - f * 0.25, k - g * 0.25, e, f, g);
				}

				m = this.getDragInWater();
			}

			this.setVelocity(vec3d.multiply((double)m));
			if (!bl) {
				this.applyGravity();
			}

			this.setPosition(h, j, k);
			this.checkBlockCollision();
		}
	}

	@Override
	protected double getGravity() {
		return 0.05;
	}

	private boolean shouldFall() {
		return this.inGround && this.getWorld().isSpaceEmpty(new Box(this.getPos(), this.getPos()).expand(0.06));
	}

	private void fall() {
		this.inGround = false;
		Vec3d vec3d = this.getVelocity();
		this.setVelocity(vec3d.multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F)));
		this.life = 0;
	}

	@Override
	public void move(MovementType movementType, Vec3d movement) {
		super.move(movementType, movement);
		if (movementType != MovementType.SELF && this.shouldFall()) {
			this.fall();
		}
	}

	protected void age() {
		this.life++;
		if (this.life >= 1200) {
			this.discard();
		}
	}

	private void clearPiercingStatus() {
		if (this.piercingKilledEntities != null) {
			this.piercingKilledEntities.clear();
		}

		if (this.piercedEntities != null) {
			this.piercedEntities.clear();
		}
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		Entity entity = entityHitResult.getEntity();
		float f = (float)this.getVelocity().length();
		double d = this.damage;
		Entity entity2 = this.getOwner();
		DamageSource damageSource = this.getDamageSources().arrow(this, (Entity)(entity2 != null ? entity2 : this));
		if (this.getWeaponStack() != null && this.getWorld() instanceof ServerWorld serverWorld) {
			d = (double)EnchantmentHelper.getDamage(serverWorld, this.getWeaponStack(), entity, damageSource, (float)d);
		}

		int i = MathHelper.ceil(MathHelper.clamp((double)f * d, 0.0, 2.147483647E9));
		if (this.getPierceLevel() > 0) {
			if (this.piercedEntities == null) {
				this.piercedEntities = new IntOpenHashSet(5);
			}

			if (this.piercingKilledEntities == null) {
				this.piercingKilledEntities = Lists.<Entity>newArrayListWithCapacity(5);
			}

			if (this.piercedEntities.size() >= this.getPierceLevel() + 1) {
				this.discard();
				return;
			}

			this.piercedEntities.add(entity.getId());
		}

		if (this.isCritical()) {
			long l = (long)this.random.nextInt(i / 2 + 2);
			i = (int)Math.min(l + (long)i, 2147483647L);
		}

		if (entity2 instanceof LivingEntity livingEntity) {
			livingEntity.onAttacking(entity);
		}

		boolean bl = entity.getType() == EntityType.ENDERMAN;
		int j = entity.getFireTicks();
		if (this.isOnFire() && !bl) {
			entity.setOnFireFor(5.0F);
		}

		if (entity.damage(damageSource, (float)i)) {
			if (bl) {
				return;
			}

			if (entity instanceof LivingEntity livingEntity2) {
				if (!this.getWorld().isClient && this.getPierceLevel() <= 0) {
					livingEntity2.setStuckArrowCount(livingEntity2.getStuckArrowCount() + 1);
				}

				this.knockback(livingEntity2, damageSource);
				if (this.getWorld() instanceof ServerWorld serverWorld2) {
					EnchantmentHelper.onTargetDamaged(serverWorld2, livingEntity2, damageSource, this.getWeaponStack());
				}

				this.onHit(livingEntity2);
				if (livingEntity2 != entity2 && livingEntity2 instanceof PlayerEntity && entity2 instanceof ServerPlayerEntity && !this.isSilent()) {
					((ServerPlayerEntity)entity2)
						.networkHandler
						.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER, GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
				}

				if (!entity.isAlive() && this.piercingKilledEntities != null) {
					this.piercingKilledEntities.add(livingEntity2);
				}

				if (!this.getWorld().isClient && entity2 instanceof ServerPlayerEntity serverPlayerEntity) {
					if (this.piercingKilledEntities != null && this.isShotFromCrossbow()) {
						Criteria.KILLED_BY_CROSSBOW.trigger(serverPlayerEntity, this.piercingKilledEntities);
					} else if (!entity.isAlive() && this.isShotFromCrossbow()) {
						Criteria.KILLED_BY_CROSSBOW.trigger(serverPlayerEntity, Arrays.asList(entity));
					}
				}
			}

			this.playSound(this.sound, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
			if (this.getPierceLevel() <= 0) {
				this.discard();
			}
		} else {
			entity.setFireTicks(j);
			this.deflect(ProjectileDeflection.SIMPLE, entity, this.getOwner(), false);
			this.setVelocity(this.getVelocity().multiply(0.2));
			if (!this.getWorld().isClient && this.getVelocity().lengthSquared() < 1.0E-7) {
				if (this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
					this.dropStack(this.asItemStack(), 0.1F);
				}

				this.discard();
			}
		}
	}

	protected void knockback(LivingEntity target, DamageSource source) {
		double d = (double)(
			this.weapon != null && this.getWorld() instanceof ServerWorld serverWorld
				? EnchantmentHelper.modifyKnockback(serverWorld, this.weapon, target, source, 0.0F)
				: 0.0F
		);
		if (d > 0.0) {
			double e = Math.max(0.0, 1.0 - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
			Vec3d vec3d = this.getVelocity().multiply(1.0, 0.0, 1.0).normalize().multiply(d * 0.6 * e);
			if (vec3d.lengthSquared() > 0.0) {
				target.addVelocity(vec3d.x, 0.1, vec3d.z);
			}
		}
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		this.inBlockState = this.getWorld().getBlockState(blockHitResult.getBlockPos());
		super.onBlockHit(blockHitResult);
		Vec3d vec3d = blockHitResult.getPos().subtract(this.getX(), this.getY(), this.getZ());
		this.setVelocity(vec3d);
		ItemStack itemStack = this.getWeaponStack();
		if (this.getWorld() instanceof ServerWorld serverWorld && itemStack != null) {
			this.onBlockHitEnchantmentEffects(serverWorld, blockHitResult, itemStack);
		}

		Vec3d vec3d2 = vec3d.normalize().multiply(0.05F);
		this.setPos(this.getX() - vec3d2.x, this.getY() - vec3d2.y, this.getZ() - vec3d2.z);
		this.playSound(this.getSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
		this.inGround = true;
		this.shake = 7;
		this.setCritical(false);
		this.setPierceLevel((byte)0);
		this.setSound(SoundEvents.ENTITY_ARROW_HIT);
		this.clearPiercingStatus();
	}

	protected void onBlockHitEnchantmentEffects(ServerWorld world, BlockHitResult blockHitResult, ItemStack weaponStack) {
		Vec3d vec3d = blockHitResult.getBlockPos().clampToWithin(blockHitResult.getPos());
		EnchantmentHelper.onHitBlock(
			world,
			weaponStack,
			this.getOwner() instanceof LivingEntity livingEntity ? livingEntity : null,
			this,
			null,
			vec3d,
			world.getBlockState(blockHitResult.getBlockPos()),
			item -> this.weapon = null
		);
	}

	@Override
	public ItemStack getWeaponStack() {
		return this.weapon;
	}

	protected SoundEvent getHitSound() {
		return SoundEvents.ENTITY_ARROW_HIT;
	}

	protected final SoundEvent getSound() {
		return this.sound;
	}

	protected void onHit(LivingEntity target) {
	}

	@Nullable
	protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
		return ProjectileUtil.getEntityCollision(
			this.getWorld(), this, currentPosition, nextPosition, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), this::canHit
		);
	}

	@Override
	protected boolean canHit(Entity entity) {
		return super.canHit(entity) && (this.piercedEntities == null || !this.piercedEntities.contains(entity.getId()));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putShort("life", (short)this.life);
		if (this.inBlockState != null) {
			nbt.put("inBlockState", NbtHelper.fromBlockState(this.inBlockState));
		}

		nbt.putByte("shake", (byte)this.shake);
		nbt.putBoolean("inGround", this.inGround);
		nbt.putByte("pickup", (byte)this.pickupType.ordinal());
		nbt.putDouble("damage", this.damage);
		nbt.putBoolean("crit", this.isCritical());
		nbt.putByte("PierceLevel", this.getPierceLevel());
		nbt.putString("SoundEvent", Registries.SOUND_EVENT.getId(this.sound).toString());
		nbt.put("item", this.stack.encode(this.getRegistryManager()));
		if (this.weapon != null) {
			nbt.put("weapon", this.weapon.encode(this.getRegistryManager(), new NbtCompound()));
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.life = nbt.getShort("life");
		if (nbt.contains("inBlockState", NbtElement.COMPOUND_TYPE)) {
			this.inBlockState = NbtHelper.toBlockState(this.getWorld().createCommandRegistryWrapper(RegistryKeys.BLOCK), nbt.getCompound("inBlockState"));
		}

		this.shake = nbt.getByte("shake") & 255;
		this.inGround = nbt.getBoolean("inGround");
		if (nbt.contains("damage", NbtElement.NUMBER_TYPE)) {
			this.damage = nbt.getDouble("damage");
		}

		this.pickupType = PersistentProjectileEntity.PickupPermission.fromOrdinal(nbt.getByte("pickup"));
		this.setCritical(nbt.getBoolean("crit"));
		this.setPierceLevel(nbt.getByte("PierceLevel"));
		if (nbt.contains("SoundEvent", NbtElement.STRING_TYPE)) {
			this.sound = (SoundEvent)Registries.SOUND_EVENT.getOrEmpty(Identifier.of(nbt.getString("SoundEvent"))).orElse(this.getHitSound());
		}

		if (nbt.contains("item", NbtElement.COMPOUND_TYPE)) {
			this.setStack((ItemStack)ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("item")).orElse(this.getDefaultItemStack()));
		} else {
			this.setStack(this.getDefaultItemStack());
		}

		if (nbt.contains("weapon", NbtElement.COMPOUND_TYPE)) {
			this.weapon = (ItemStack)ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("weapon")).orElse(null);
		} else {
			this.weapon = null;
		}
	}

	@Override
	public void setOwner(@Nullable Entity entity) {
		super.setOwner(entity);

		this.pickupType = switch (entity) {
			case null, default -> this.pickupType;
			case PlayerEntity playerEntity when this.pickupType == PersistentProjectileEntity.PickupPermission.DISALLOWED -> PersistentProjectileEntity.PickupPermission.ALLOWED;
			case OminousItemSpawnerEntity ominousItemSpawnerEntity -> PersistentProjectileEntity.PickupPermission.DISALLOWED;
		};
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		if (!this.getWorld().isClient && (this.inGround || this.isNoClip()) && this.shake <= 0) {
			if (this.tryPickup(player)) {
				player.sendPickup(this, 1);
				this.discard();
			}
		}
	}

	protected boolean tryPickup(PlayerEntity player) {
		return switch (this.pickupType) {
			case DISALLOWED -> false;
			case ALLOWED -> player.getInventory().insertStack(this.asItemStack());
			case CREATIVE_ONLY -> player.isInCreativeMode();
		};
	}

	protected ItemStack asItemStack() {
		return this.stack.copy();
	}

	protected abstract ItemStack getDefaultItemStack();

	@Override
	protected Entity.MoveEffect getMoveEffect() {
		return Entity.MoveEffect.NONE;
	}

	/**
	 * {@return the read-only item stack representing the projectile}
	 * 
	 * <p>This is the original stack used to spawn the projectile. {@link #asItemStack}
	 * returns a copy of that stack which can be safely changed. Additionally,
	 * {@link #asItemStack} reflects changes to the entity data, such as custom potion ID.
	 */
	public ItemStack getItemStack() {
		return this.stack;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public double getDamage() {
		return this.damage;
	}

	@Override
	public boolean isAttackable() {
		return this.getType().isIn(EntityTypeTags.REDIRECTABLE_PROJECTILE);
	}

	public void setCritical(boolean critical) {
		this.setProjectileFlag(CRITICAL_FLAG, critical);
	}

	private void setPierceLevel(byte level) {
		this.dataTracker.set(PIERCE_LEVEL, level);
	}

	private void setProjectileFlag(int index, boolean flag) {
		byte b = this.dataTracker.get(PROJECTILE_FLAGS);
		if (flag) {
			this.dataTracker.set(PROJECTILE_FLAGS, (byte)(b | index));
		} else {
			this.dataTracker.set(PROJECTILE_FLAGS, (byte)(b & ~index));
		}
	}

	protected void setStack(ItemStack stack) {
		if (!stack.isEmpty()) {
			this.stack = stack;
		} else {
			this.stack = this.getDefaultItemStack();
		}
	}

	public boolean isCritical() {
		byte b = this.dataTracker.get(PROJECTILE_FLAGS);
		return (b & 1) != 0;
	}

	public boolean isShotFromCrossbow() {
		return this.weapon != null && this.weapon.isOf(Items.CROSSBOW);
	}

	public byte getPierceLevel() {
		return this.dataTracker.get(PIERCE_LEVEL);
	}

	public void applyDamageModifier(float damageModifier) {
		this.setDamage((double)(damageModifier * 2.0F) + this.random.nextTriangular((double)this.getWorld().getDifficulty().getId() * 0.11, 0.57425));
	}

	protected float getDragInWater() {
		return 0.6F;
	}

	public void setNoClip(boolean noClip) {
		this.noClip = noClip;
		this.setProjectileFlag(NO_CLIP_FLAG, noClip);
	}

	public boolean isNoClip() {
		return !this.getWorld().isClient ? this.noClip : (this.dataTracker.get(PROJECTILE_FLAGS) & 2) != 0;
	}

	@Override
	public boolean canHit() {
		return super.canHit() && !this.inGround;
	}

	@Override
	public StackReference getStackReference(int mappedIndex) {
		return mappedIndex == 0 ? StackReference.of(this::getItemStack, this::setStack) : super.getStackReference(mappedIndex);
	}

	public static enum PickupPermission {
		DISALLOWED,
		ALLOWED,
		CREATIVE_ONLY;

		public static PersistentProjectileEntity.PickupPermission fromOrdinal(int ordinal) {
			if (ordinal < 0 || ordinal > values().length) {
				ordinal = 0;
			}

			return values()[ordinal];
		}
	}
}
