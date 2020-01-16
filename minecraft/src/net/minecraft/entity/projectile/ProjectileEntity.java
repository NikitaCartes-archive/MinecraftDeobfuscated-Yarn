package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public abstract class ProjectileEntity extends Entity implements Projectile {
	private static final TrackedData<Byte> PROJECTILE_FLAGS = DataTracker.registerData(ProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Optional<UUID>> OPTIONAL_UUID = DataTracker.registerData(ProjectileEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	private static final TrackedData<Byte> PIERCE_LEVEL = DataTracker.registerData(ProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
	@Nullable
	private BlockState inBlockState;
	protected boolean inGround;
	protected int inGroundTime;
	public ProjectileEntity.PickupPermission pickupType = ProjectileEntity.PickupPermission.DISALLOWED;
	public int shake;
	public UUID ownerUuid;
	private int life;
	private int flyingTick;
	private double damage = 2.0;
	private int punch;
	private SoundEvent sound = this.getHitSound();
	private IntOpenHashSet piercedEntities;
	private List<Entity> piercingKilledEntities;

	protected ProjectileEntity(EntityType<? extends ProjectileEntity> type, World world) {
		super(type, world);
	}

	protected ProjectileEntity(EntityType<? extends ProjectileEntity> type, double x, double y, double z, World world) {
		this(type, world);
		this.updatePosition(x, y, z);
	}

	protected ProjectileEntity(EntityType<? extends ProjectileEntity> type, LivingEntity owner, World world) {
		this(type, owner.getX(), owner.getEyeY() - 0.1F, owner.getZ(), world);
		this.setOwner(owner);
		if (owner instanceof PlayerEntity) {
			this.pickupType = ProjectileEntity.PickupPermission.ALLOWED;
		}
	}

	public void setSound(SoundEvent sound) {
		this.sound = sound;
	}

	@Environment(EnvType.CLIENT)
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
	protected void initDataTracker() {
		this.dataTracker.startTracking(PROJECTILE_FLAGS, (byte)0);
		this.dataTracker.startTracking(OPTIONAL_UUID, Optional.empty());
		this.dataTracker.startTracking(PIERCE_LEVEL, (byte)0);
	}

	public void setProperties(Entity user, float pitch, float yaw, float roll, float modifierZ, float modifierXYZ) {
		float f = -MathHelper.sin(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
		float g = -MathHelper.sin(pitch * (float) (Math.PI / 180.0));
		float h = MathHelper.cos(yaw * (float) (Math.PI / 180.0)) * MathHelper.cos(pitch * (float) (Math.PI / 180.0));
		this.setVelocity((double)f, (double)g, (double)h, modifierZ, modifierXYZ);
		this.setVelocity(this.getVelocity().add(user.getVelocity().x, user.onGround ? 0.0 : user.getVelocity().y, user.getVelocity().z));
	}

	@Override
	public void setVelocity(double x, double y, double z, float speed, float divergence) {
		Vec3d vec3d = new Vec3d(x, y, z)
			.normalize()
			.add(
				this.random.nextGaussian() * 0.0075F * (double)divergence,
				this.random.nextGaussian() * 0.0075F * (double)divergence,
				this.random.nextGaussian() * 0.0075F * (double)divergence
			)
			.multiply((double)speed);
		this.setVelocity(vec3d);
		float f = MathHelper.sqrt(squaredHorizontalLength(vec3d));
		this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 180.0F / (float)Math.PI);
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
		this.life = 0;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		this.updatePosition(x, y, z);
		this.setRotation(yaw, pitch);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setVelocityClient(double x, double y, double z) {
		this.setVelocity(x, y, z);
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float f = MathHelper.sqrt(x * x + z * z);
			this.pitch = (float)(MathHelper.atan2(y, (double)f) * 180.0F / (float)Math.PI);
			this.yaw = (float)(MathHelper.atan2(x, z) * 180.0F / (float)Math.PI);
			this.prevPitch = this.pitch;
			this.prevYaw = this.yaw;
			this.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
			this.life = 0;
		}
	}

	@Override
	public void tick() {
		super.tick();
		boolean bl = this.isNoClip();
		Vec3d vec3d = this.getVelocity();
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float f = MathHelper.sqrt(squaredHorizontalLength(vec3d));
			this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI);
			this.pitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 180.0F / (float)Math.PI);
			this.prevYaw = this.yaw;
			this.prevPitch = this.pitch;
		}

		BlockPos blockPos = new BlockPos(this);
		BlockState blockState = this.world.getBlockState(blockPos);
		if (!blockState.isAir() && !bl) {
			VoxelShape voxelShape = blockState.getCollisionShape(this.world, blockPos);
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

		if (this.isTouchingWaterOrRain()) {
			this.extinguish();
		}

		if (this.inGround && !bl) {
			if (this.inBlockState != blockState && this.world.doesNotCollide(this.getBoundingBox().expand(0.06))) {
				this.inGround = false;
				this.setVelocity(
					vec3d.multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F))
				);
				this.life = 0;
				this.flyingTick = 0;
			} else if (!this.world.isClient) {
				this.age();
			}

			this.inGroundTime++;
		} else {
			this.inGroundTime = 0;
			this.flyingTick++;
			Vec3d vec3d3 = this.getPos();
			Vec3d vec3d2 = vec3d3.add(vec3d);
			HitResult hitResult = this.world.rayTrace(new RayTraceContext(vec3d3, vec3d2, RayTraceContext.ShapeType.COLLIDER, RayTraceContext.FluidHandling.NONE, this));
			if (hitResult.getType() != HitResult.Type.MISS) {
				vec3d2 = hitResult.getPos();
			}

			while (!this.removed) {
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
					this.onHit(hitResult);
					this.velocityDirty = true;
				}

				if (entityHitResult == null || this.getPierceLevel() <= 0) {
					break;
				}

				hitResult = null;
			}

			vec3d = this.getVelocity();
			double d = vec3d.x;
			double e = vec3d.y;
			double g = vec3d.z;
			if (this.isCritical()) {
				for (int i = 0; i < 4; i++) {
					this.world
						.addParticle(
							ParticleTypes.CRIT, this.getX() + d * (double)i / 4.0, this.getY() + e * (double)i / 4.0, this.getZ() + g * (double)i / 4.0, -d, -e + 0.2, -g
						);
				}
			}

			double h = this.getX() + d;
			double j = this.getY() + e;
			double k = this.getZ() + g;
			float l = MathHelper.sqrt(squaredHorizontalLength(vec3d));
			if (bl) {
				this.yaw = (float)(MathHelper.atan2(-d, -g) * 180.0F / (float)Math.PI);
			} else {
				this.yaw = (float)(MathHelper.atan2(d, g) * 180.0F / (float)Math.PI);
			}

			this.pitch = (float)(MathHelper.atan2(e, (double)l) * 180.0F / (float)Math.PI);

			while (this.pitch - this.prevPitch < -180.0F) {
				this.prevPitch -= 360.0F;
			}

			while (this.pitch - this.prevPitch >= 180.0F) {
				this.prevPitch += 360.0F;
			}

			while (this.yaw - this.prevYaw < -180.0F) {
				this.prevYaw -= 360.0F;
			}

			while (this.yaw - this.prevYaw >= 180.0F) {
				this.prevYaw += 360.0F;
			}

			this.pitch = MathHelper.lerp(0.2F, this.prevPitch, this.pitch);
			this.yaw = MathHelper.lerp(0.2F, this.prevYaw, this.yaw);
			float m = 0.99F;
			float n = 0.05F;
			if (this.isTouchingWater()) {
				for (int o = 0; o < 4; o++) {
					float p = 0.25F;
					this.world.addParticle(ParticleTypes.BUBBLE, h - d * 0.25, j - e * 0.25, k - g * 0.25, d, e, g);
				}

				m = this.getDragInWater();
			}

			this.setVelocity(vec3d.multiply((double)m));
			if (!this.hasNoGravity() && !bl) {
				Vec3d vec3d4 = this.getVelocity();
				this.setVelocity(vec3d4.x, vec3d4.y - 0.05F, vec3d4.z);
			}

			this.updatePosition(h, j, k);
			this.checkBlockCollision();
		}
	}

	protected void age() {
		this.life++;
		if (this.life >= 1200) {
			this.remove();
		}
	}

	protected void onHit(HitResult hitResult) {
		HitResult.Type type = hitResult.getType();
		if (type == HitResult.Type.ENTITY) {
			this.onEntityHit((EntityHitResult)hitResult);
		} else if (type == HitResult.Type.BLOCK) {
			BlockHitResult blockHitResult = (BlockHitResult)hitResult;
			BlockState blockState = this.world.getBlockState(blockHitResult.getBlockPos());
			this.inBlockState = blockState;
			Vec3d vec3d = blockHitResult.getPos().subtract(this.getX(), this.getY(), this.getZ());
			this.setVelocity(vec3d);
			Vec3d vec3d2 = vec3d.normalize().multiply(0.05F);
			this.setPos(this.getX() - vec3d2.x, this.getY() - vec3d2.y, this.getZ() - vec3d2.z);
			this.playSound(this.getSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
			this.inGround = true;
			this.shake = 7;
			this.setCritical(false);
			this.setPierceLevel((byte)0);
			this.setSound(SoundEvents.ENTITY_ARROW_HIT);
			this.setShotFromCrossbow(false);
			this.clearPiercingStatus();
			blockState.onProjectileHit(this.world, blockState, blockHitResult, this);
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

	protected void onEntityHit(EntityHitResult entityHitResult) {
		Entity entity = entityHitResult.getEntity();
		float f = (float)this.getVelocity().length();
		int i = MathHelper.ceil(Math.max((double)f * this.damage, 0.0));
		if (this.getPierceLevel() > 0) {
			if (this.piercedEntities == null) {
				this.piercedEntities = new IntOpenHashSet(5);
			}

			if (this.piercingKilledEntities == null) {
				this.piercingKilledEntities = Lists.<Entity>newArrayListWithCapacity(5);
			}

			if (this.piercedEntities.size() >= this.getPierceLevel() + 1) {
				this.remove();
				return;
			}

			this.piercedEntities.add(entity.getEntityId());
		}

		if (this.isCritical()) {
			i += this.random.nextInt(i / 2 + 2);
		}

		Entity entity2 = this.getOwner();
		DamageSource damageSource;
		if (entity2 == null) {
			damageSource = DamageSource.arrow(this, this);
		} else {
			damageSource = DamageSource.arrow(this, entity2);
			if (entity2 instanceof LivingEntity) {
				((LivingEntity)entity2).onAttacking(entity);
			}
		}

		boolean bl = entity.getType() == EntityType.ENDERMAN;
		int j = entity.getFireTicks();
		if (this.isOnFire() && !bl) {
			entity.setOnFireFor(5);
		}

		if (entity.damage(damageSource, (float)i)) {
			if (bl) {
				return;
			}

			if (entity instanceof LivingEntity) {
				LivingEntity livingEntity = (LivingEntity)entity;
				if (!this.world.isClient && this.getPierceLevel() <= 0) {
					livingEntity.setStuckArrowCount(livingEntity.getStuckArrowCount() + 1);
				}

				if (this.punch > 0) {
					Vec3d vec3d = this.getVelocity().multiply(1.0, 0.0, 1.0).normalize().multiply((double)this.punch * 0.6);
					if (vec3d.lengthSquared() > 0.0) {
						livingEntity.addVelocity(vec3d.x, 0.1, vec3d.z);
					}
				}

				if (!this.world.isClient && entity2 instanceof LivingEntity) {
					EnchantmentHelper.onUserDamaged(livingEntity, entity2);
					EnchantmentHelper.onTargetDamaged((LivingEntity)entity2, livingEntity);
				}

				this.onHit(livingEntity);
				if (entity2 != null && livingEntity != entity2 && livingEntity instanceof PlayerEntity && entity2 instanceof ServerPlayerEntity) {
					((ServerPlayerEntity)entity2).networkHandler.sendPacket(new GameStateChangeS2CPacket(6, 0.0F));
				}

				if (!entity.isAlive() && this.piercingKilledEntities != null) {
					this.piercingKilledEntities.add(livingEntity);
				}

				if (!this.world.isClient && entity2 instanceof ServerPlayerEntity) {
					ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity2;
					if (this.piercingKilledEntities != null && this.isShotFromCrossbow()) {
						Criterions.KILLED_BY_CROSSBOW.trigger(serverPlayerEntity, this.piercingKilledEntities, this.piercingKilledEntities.size());
					} else if (!entity.isAlive() && this.isShotFromCrossbow()) {
						Criterions.KILLED_BY_CROSSBOW.trigger(serverPlayerEntity, Arrays.asList(entity), 0);
					}
				}
			}

			this.playSound(this.sound, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
			if (this.getPierceLevel() <= 0) {
				this.remove();
			}
		} else {
			entity.setFireTicks(j);
			this.setVelocity(this.getVelocity().multiply(-0.1));
			this.yaw += 180.0F;
			this.prevYaw += 180.0F;
			this.flyingTick = 0;
			if (!this.world.isClient && this.getVelocity().lengthSquared() < 1.0E-7) {
				if (this.pickupType == ProjectileEntity.PickupPermission.ALLOWED) {
					this.dropStack(this.asItemStack(), 0.1F);
				}

				this.remove();
			}
		}
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
			this.world,
			this,
			currentPosition,
			nextPosition,
			this.getBoundingBox().stretch(this.getVelocity()).expand(1.0),
			entity -> !entity.isSpectator()
					&& entity.isAlive()
					&& entity.collides()
					&& (entity != this.getOwner() || this.flyingTick >= 5)
					&& (this.piercedEntities == null || !this.piercedEntities.contains(entity.getEntityId()))
		);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		tag.putShort("life", (short)this.life);
		if (this.inBlockState != null) {
			tag.put("inBlockState", NbtHelper.fromBlockState(this.inBlockState));
		}

		tag.putByte("shake", (byte)this.shake);
		tag.putBoolean("inGround", this.inGround);
		tag.putByte("pickup", (byte)this.pickupType.ordinal());
		tag.putDouble("damage", this.damage);
		tag.putBoolean("crit", this.isCritical());
		tag.putByte("PierceLevel", this.getPierceLevel());
		if (this.ownerUuid != null) {
			tag.putUuid("OwnerUUID", this.ownerUuid);
		}

		tag.putString("SoundEvent", Registry.SOUND_EVENT.getId(this.sound).toString());
		tag.putBoolean("ShotFromCrossbow", this.isShotFromCrossbow());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		this.life = tag.getShort("life");
		if (tag.contains("inBlockState", 10)) {
			this.inBlockState = NbtHelper.toBlockState(tag.getCompound("inBlockState"));
		}

		this.shake = tag.getByte("shake") & 255;
		this.inGround = tag.getBoolean("inGround");
		if (tag.contains("damage", 99)) {
			this.damage = tag.getDouble("damage");
		}

		if (tag.contains("pickup", 99)) {
			this.pickupType = ProjectileEntity.PickupPermission.fromOrdinal(tag.getByte("pickup"));
		} else if (tag.contains("player", 99)) {
			this.pickupType = tag.getBoolean("player") ? ProjectileEntity.PickupPermission.ALLOWED : ProjectileEntity.PickupPermission.DISALLOWED;
		}

		this.setCritical(tag.getBoolean("crit"));
		this.setPierceLevel(tag.getByte("PierceLevel"));
		if (tag.containsUuid("OwnerUUID")) {
			this.ownerUuid = tag.getUuid("OwnerUUID");
		}

		if (tag.contains("SoundEvent", 8)) {
			this.sound = (SoundEvent)Registry.SOUND_EVENT.getOrEmpty(new Identifier(tag.getString("SoundEvent"))).orElse(this.getHitSound());
		}

		this.setShotFromCrossbow(tag.getBoolean("ShotFromCrossbow"));
	}

	public void setOwner(@Nullable Entity entity) {
		this.ownerUuid = entity == null ? null : entity.getUuid();
		if (entity instanceof PlayerEntity) {
			this.pickupType = ((PlayerEntity)entity).abilities.creativeMode
				? ProjectileEntity.PickupPermission.CREATIVE_ONLY
				: ProjectileEntity.PickupPermission.ALLOWED;
		}
	}

	@Nullable
	public Entity getOwner() {
		return this.ownerUuid != null && this.world instanceof ServerWorld ? ((ServerWorld)this.world).getEntity(this.ownerUuid) : null;
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		if (!this.world.isClient && (this.inGround || this.isNoClip()) && this.shake <= 0) {
			boolean bl = this.pickupType == ProjectileEntity.PickupPermission.ALLOWED
				|| this.pickupType == ProjectileEntity.PickupPermission.CREATIVE_ONLY && player.abilities.creativeMode
				|| this.isNoClip() && this.getOwner().getUuid() == player.getUuid();
			if (this.pickupType == ProjectileEntity.PickupPermission.ALLOWED && !player.inventory.insertStack(this.asItemStack())) {
				bl = false;
			}

			if (bl) {
				player.sendPickup(this, 1);
				this.remove();
			}
		}
	}

	protected abstract ItemStack asItemStack();

	@Override
	protected boolean canClimb() {
		return false;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public double getDamage() {
		return this.damage;
	}

	public void setPunch(int punch) {
		this.punch = punch;
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return 0.0F;
	}

	public void setCritical(boolean critical) {
		this.setProjectileFlag(1, critical);
	}

	public void setPierceLevel(byte level) {
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

	public boolean isCritical() {
		byte b = this.dataTracker.get(PROJECTILE_FLAGS);
		return (b & 1) != 0;
	}

	public boolean isShotFromCrossbow() {
		byte b = this.dataTracker.get(PROJECTILE_FLAGS);
		return (b & 4) != 0;
	}

	public byte getPierceLevel() {
		return this.dataTracker.get(PIERCE_LEVEL);
	}

	public void applyEnchantmentEffects(LivingEntity livingEntity, float damageModifier) {
		int i = EnchantmentHelper.getEquipmentLevel(Enchantments.POWER, livingEntity);
		int j = EnchantmentHelper.getEquipmentLevel(Enchantments.PUNCH, livingEntity);
		this.setDamage((double)(damageModifier * 2.0F) + this.random.nextGaussian() * 0.25 + (double)((float)this.world.getDifficulty().getId() * 0.11F));
		if (i > 0) {
			this.setDamage(this.getDamage() + (double)i * 0.5 + 0.5);
		}

		if (j > 0) {
			this.setPunch(j);
		}

		if (EnchantmentHelper.getEquipmentLevel(Enchantments.FLAME, livingEntity) > 0) {
			this.setOnFireFor(100);
		}
	}

	protected float getDragInWater() {
		return 0.6F;
	}

	public void setNoClip(boolean noClip) {
		this.noClip = noClip;
		this.setProjectileFlag(2, noClip);
	}

	public boolean isNoClip() {
		return !this.world.isClient ? this.noClip : (this.dataTracker.get(PROJECTILE_FLAGS) & 2) != 0;
	}

	public void setShotFromCrossbow(boolean shotFromCrossbow) {
		this.setProjectileFlag(4, shotFromCrossbow);
	}

	@Override
	public Packet<?> createSpawnPacket() {
		Entity entity = this.getOwner();
		return new EntitySpawnS2CPacket(this, entity == null ? 0 : entity.getEntityId());
	}

	public static enum PickupPermission {
		DISALLOWED,
		ALLOWED,
		CREATIVE_ONLY;

		public static ProjectileEntity.PickupPermission fromOrdinal(int ordinal) {
			if (ordinal < 0 || ordinal > values().length) {
				ordinal = 0;
			}

			return values()[ordinal];
		}
	}
}
