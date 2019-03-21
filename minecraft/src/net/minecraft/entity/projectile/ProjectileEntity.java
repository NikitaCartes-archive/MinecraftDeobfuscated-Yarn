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
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.TagHelper;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public abstract class ProjectileEntity extends Entity implements Projectile {
	private static final TrackedData<Byte> FLAGS = DataTracker.registerData(ProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Optional<UUID>> field_7580 = DataTracker.registerData(ProjectileEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	private static final TrackedData<Byte> PIERCE_LEVEL = DataTracker.registerData(ProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
	private int xTile = -1;
	private int yTile = -1;
	private int zTIle = -1;
	@Nullable
	private BlockState inBlockState;
	protected boolean inGround;
	protected int field_7576;
	public ProjectileEntity.PickupType pickupType = ProjectileEntity.PickupType.NO_PICKUP;
	public int shake;
	public UUID ownerUuid;
	private int life;
	private int field_7577;
	private double damage = 2.0;
	private int field_7575;
	private SoundEvent sound = SoundEvents.field_15151;
	private IntOpenHashSet field_7590;
	private List<Entity> field_7579;

	protected ProjectileEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	protected ProjectileEntity(EntityType<? extends ProjectileEntity> entityType, double d, double e, double f, World world) {
		this(entityType, world);
		this.setPosition(d, e, f);
	}

	protected ProjectileEntity(EntityType<? extends ProjectileEntity> entityType, LivingEntity livingEntity, World world) {
		this(entityType, livingEntity.x, livingEntity.y + (double)livingEntity.getStandingEyeHeight() - 0.1F, livingEntity.z, world);
		this.setOwner(livingEntity);
		if (livingEntity instanceof PlayerEntity) {
			this.pickupType = ProjectileEntity.PickupType.PICKUP;
		}
	}

	public void setSound(SoundEvent soundEvent) {
		this.sound = soundEvent;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderAtDistance(double d) {
		double e = this.getBoundingBox().averageDimension() * 10.0;
		if (Double.isNaN(e)) {
			e = 1.0;
		}

		e *= 64.0 * getRenderDistanceMultiplier();
		return d < e * e;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(FLAGS, (byte)0);
		this.dataTracker.startTracking(field_7580, Optional.empty());
		this.dataTracker.startTracking(PIERCE_LEVEL, (byte)0);
	}

	public void method_7474(Entity entity, float f, float g, float h, float i, float j) {
		float k = -MathHelper.sin(g * (float) (Math.PI / 180.0)) * MathHelper.cos(f * (float) (Math.PI / 180.0));
		float l = -MathHelper.sin(f * (float) (Math.PI / 180.0));
		float m = MathHelper.cos(g * (float) (Math.PI / 180.0)) * MathHelper.cos(f * (float) (Math.PI / 180.0));
		this.setVelocity((double)k, (double)l, (double)m, i, j);
		this.setVelocity(this.getVelocity().add(entity.getVelocity().x, entity.onGround ? 0.0 : entity.getVelocity().y, entity.getVelocity().z));
	}

	@Override
	public void setVelocity(double d, double e, double f, float g, float h) {
		Vec3d vec3d = new Vec3d(d, e, f)
			.normalize()
			.add(this.random.nextGaussian() * 0.0075F * (double)h, this.random.nextGaussian() * 0.0075F * (double)h, this.random.nextGaussian() * 0.0075F * (double)h)
			.multiply((double)g);
		this.setVelocity(vec3d);
		float i = MathHelper.sqrt(squaredHorizontalLength(vec3d));
		this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(vec3d.y, (double)i) * 180.0F / (float)Math.PI);
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
		this.life = 0;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setPositionAndRotations(double d, double e, double f, float g, float h, int i, boolean bl) {
		this.setPosition(d, e, f);
		this.setRotation(g, h);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setVelocityClient(double d, double e, double f) {
		this.setVelocity(d, e, f);
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float g = MathHelper.sqrt(d * d + f * f);
			this.pitch = (float)(MathHelper.atan2(e, (double)g) * 180.0F / (float)Math.PI);
			this.yaw = (float)(MathHelper.atan2(d, f) * 180.0F / (float)Math.PI);
			this.prevPitch = this.pitch;
			this.prevYaw = this.yaw;
			this.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
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

		BlockPos blockPos = new BlockPos(this.xTile, this.yTile, this.zTIle);
		BlockState blockState = this.world.getBlockState(blockPos);
		if (!blockState.isAir() && !bl) {
			VoxelShape voxelShape = blockState.getCollisionShape(this.world, blockPos);
			if (!voxelShape.isEmpty()) {
				for (BoundingBox boundingBox : voxelShape.getBoundingBoxes()) {
					if (boundingBox.offset(blockPos).contains(new Vec3d(this.x, this.y, this.z))) {
						this.inGround = true;
						break;
					}
				}
			}
		}

		if (this.shake > 0) {
			this.shake--;
		}

		if (this.isInsideWaterOrRain()) {
			this.extinguish();
		}

		if (this.inGround && !bl) {
			if (this.inBlockState != blockState && this.world.method_18026(this.getBoundingBox().expand(0.05))) {
				this.inGround = false;
				this.setVelocity(
					vec3d.multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F))
				);
				this.life = 0;
				this.field_7577 = 0;
			} else {
				this.method_7446();
			}

			this.field_7576++;
		} else {
			this.field_7576 = 0;
			this.field_7577++;
			Vec3d vec3d2 = new Vec3d(this.x, this.y, this.z);
			Vec3d vec3d3 = vec3d2.add(vec3d);
			HitResult hitResult = this.world
				.rayTrace(new RayTraceContext(vec3d2, vec3d3, RayTraceContext.ShapeType.field_17558, RayTraceContext.FluidHandling.NONE, this));
			if (hitResult.getType() != HitResult.Type.NONE) {
				vec3d3 = hitResult.getPos();
			}

			while (!this.invalid) {
				EntityHitResult entityHitResult = this.method_7434(vec3d2, vec3d3);
				if (entityHitResult != null) {
					hitResult = entityHitResult;
				}

				if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
					Entity entity = ((EntityHitResult)hitResult).getEntity();
					Entity entity2 = this.getOwner();
					if (entity instanceof PlayerEntity && entity2 instanceof PlayerEntity && !((PlayerEntity)entity2).shouldDamagePlayer((PlayerEntity)entity)) {
						hitResult = null;
					}
				}

				if (hitResult != null && !bl) {
					this.method_7457(hitResult);
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
			if (this.method_7443()) {
				for (int i = 0; i < 4; i++) {
					this.world
						.addParticle(ParticleTypes.field_11205, this.x + d * (double)i / 4.0, this.y + e * (double)i / 4.0, this.z + g * (double)i / 4.0, -d, -e + 0.2, -g);
				}
			}

			this.x += d;
			this.y += e;
			this.z += g;
			float h = MathHelper.sqrt(squaredHorizontalLength(vec3d));
			if (bl) {
				this.yaw = (float)(MathHelper.atan2(-d, -g) * 180.0F / (float)Math.PI);
			} else {
				this.yaw = (float)(MathHelper.atan2(d, g) * 180.0F / (float)Math.PI);
			}

			this.pitch = (float)(MathHelper.atan2(e, (double)h) * 180.0F / (float)Math.PI);

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
			float j = 0.99F;
			float k = 0.05F;
			if (this.isInsideWater()) {
				for (int l = 0; l < 4; l++) {
					float m = 0.25F;
					this.world.addParticle(ParticleTypes.field_11247, this.x - d * 0.25, this.y - e * 0.25, this.z - g * 0.25, d, e, g);
				}

				j = this.method_7436();
			}

			this.setVelocity(vec3d.multiply((double)j));
			if (!this.isUnaffectedByGravity() && !bl) {
				Vec3d vec3d4 = this.getVelocity();
				this.setVelocity(vec3d4.x, vec3d4.y - 0.05F, vec3d4.z);
			}

			this.setPosition(this.x, this.y, this.z);
			this.checkBlockCollision();
		}
	}

	protected void method_7446() {
		this.life++;
		if (this.life >= 1200) {
			this.invalidate();
		}
	}

	protected void method_7457(HitResult hitResult) {
		HitResult.Type type = hitResult.getType();
		if (type == HitResult.Type.ENTITY) {
			this.method_7454((EntityHitResult)hitResult);
		} else if (type == HitResult.Type.BLOCK) {
			BlockHitResult blockHitResult = (BlockHitResult)hitResult;
			BlockPos blockPos = blockHitResult.getBlockPos();
			this.xTile = blockPos.getX();
			this.yTile = blockPos.getY();
			this.zTIle = blockPos.getZ();
			BlockState blockState = this.world.getBlockState(blockPos);
			this.inBlockState = blockState;
			Vec3d vec3d = blockHitResult.getPos().subtract(this.x, this.y, this.z);
			this.setVelocity(vec3d);
			Vec3d vec3d2 = vec3d.normalize().multiply(0.05F);
			this.x = this.x - vec3d2.x;
			this.y = this.y - vec3d2.y;
			this.z = this.z - vec3d2.z;
			this.playSound(this.getSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
			this.inGround = true;
			this.shake = 7;
			this.setCritical(false);
			this.setFlagByte((byte)0);
			this.setSound(SoundEvents.field_15151);
			this.setShotFromCrossbow(false);
			this.method_7453();
			blockState.method_19287(this.world, blockState, blockHitResult, this);
		}
	}

	private void method_7453() {
		if (this.field_7579 != null) {
			this.field_7579.clear();
		}

		if (this.field_7590 != null) {
			this.field_7590.clear();
		}
	}

	protected void method_7454(EntityHitResult entityHitResult) {
		Entity entity = entityHitResult.getEntity();
		float f = (float)this.getVelocity().length();
		int i = MathHelper.ceil((double)f * this.damage);
		if (this.getPierceLevel() > 0) {
			if (this.field_7590 == null) {
				this.field_7590 = new IntOpenHashSet(5);
			}

			if (this.field_7579 == null) {
				this.field_7579 = Lists.<Entity>newArrayListWithCapacity(5);
			}

			if (this.field_7590.size() >= this.getPierceLevel() + 1) {
				this.invalidate();
				return;
			}

			this.field_7590.add(entity.getEntityId());
		}

		if (this.method_7443()) {
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

		if (this.isOnFire() && !(entity instanceof EndermanEntity)) {
			entity.setOnFireFor(5);
		}

		if (entity.damage(damageSource, (float)i)) {
			if (entity instanceof LivingEntity) {
				LivingEntity livingEntity = (LivingEntity)entity;
				if (!this.world.isClient && this.getPierceLevel() <= 0) {
					livingEntity.setStuckArrows(livingEntity.getStuckArrows() + 1);
				}

				if (this.field_7575 > 0) {
					Vec3d vec3d = this.getVelocity().multiply(1.0, 0.0, 1.0).normalize().multiply((double)this.field_7575 * 0.6);
					if (vec3d.lengthSquared() > 0.0) {
						livingEntity.addVelocity(vec3d.x, 0.1, vec3d.z);
					}
				}

				if (entity2 instanceof LivingEntity) {
					EnchantmentHelper.onUserDamaged(livingEntity, entity2);
					EnchantmentHelper.onTargetDamaged((LivingEntity)entity2, livingEntity);
				}

				this.onHit(livingEntity);
				if (entity2 != null && livingEntity != entity2 && livingEntity instanceof PlayerEntity && entity2 instanceof ServerPlayerEntity) {
					((ServerPlayerEntity)entity2).networkHandler.sendPacket(new GameStateChangeS2CPacket(6, 0.0F));
				}

				if (!entity.isValid() && this.field_7579 != null) {
					this.field_7579.add(livingEntity);
				}

				if (!this.world.isClient && entity2 instanceof ServerPlayerEntity) {
					ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity2;
					if (this.field_7579 != null && this.isShotFromCrossbow()) {
						Criterions.KILLED_BY_CROSSBOW.method_8980(serverPlayerEntity, this.field_7579, this.field_7579.size());
					} else if (!entity.isValid() && this.isShotFromCrossbow()) {
						Criterions.KILLED_BY_CROSSBOW.method_8980(serverPlayerEntity, Arrays.asList(entity), 0);
					}
				}
			}

			this.playSound(this.sound, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
			if (this.getPierceLevel() <= 0 && !(entity instanceof EndermanEntity)) {
				this.invalidate();
			}
		} else {
			this.setVelocity(this.getVelocity().multiply(-0.1));
			this.yaw += 180.0F;
			this.prevYaw += 180.0F;
			this.field_7577 = 0;
			if (!this.world.isClient && this.getVelocity().lengthSquared() < 1.0E-7) {
				if (this.pickupType == ProjectileEntity.PickupType.PICKUP) {
					this.dropStack(this.asItemStack(), 0.1F);
				}

				this.invalidate();
			}
		}
	}

	protected SoundEvent getSound() {
		return this.sound;
	}

	@Override
	public void move(MovementType movementType, Vec3d vec3d) {
		super.move(movementType, vec3d);
		if (this.inGround) {
			this.xTile = MathHelper.floor(this.x);
			this.yTile = MathHelper.floor(this.y);
			this.zTIle = MathHelper.floor(this.z);
		}
	}

	protected void onHit(LivingEntity livingEntity) {
	}

	@Nullable
	protected EntityHitResult method_7434(Vec3d vec3d, Vec3d vec3d2) {
		return ProjectileUtil.method_18077(
			this.world,
			this,
			vec3d,
			vec3d2,
			this.getBoundingBox().method_18804(this.getVelocity()).expand(1.0),
			entity -> !entity.isSpectator()
					&& entity.isValid()
					&& entity.doesCollide()
					&& (entity != this.getOwner() || this.field_7577 >= 5)
					&& (this.field_7590 == null || !this.field_7590.contains(entity.getEntityId()))
		);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		compoundTag.putInt("xTile", this.xTile);
		compoundTag.putInt("yTile", this.yTile);
		compoundTag.putInt("zTile", this.zTIle);
		compoundTag.putShort("life", (short)this.life);
		if (this.inBlockState != null) {
			compoundTag.put("inBlockState", TagHelper.serializeBlockState(this.inBlockState));
		}

		compoundTag.putByte("shake", (byte)this.shake);
		compoundTag.putByte("inGround", (byte)(this.inGround ? 1 : 0));
		compoundTag.putByte("pickup", (byte)this.pickupType.ordinal());
		compoundTag.putDouble("damage", this.damage);
		compoundTag.putBoolean("crit", this.method_7443());
		compoundTag.putByte("PierceLevel", this.getPierceLevel());
		if (this.ownerUuid != null) {
			compoundTag.putUuid("OwnerUUID", this.ownerUuid);
		}

		compoundTag.putString("SoundEvent", Registry.SOUND_EVENT.getId(this.sound).toString());
		compoundTag.putBoolean("ShotFromCrossbow", this.isShotFromCrossbow());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		this.xTile = compoundTag.getInt("xTile");
		this.yTile = compoundTag.getInt("yTile");
		this.zTIle = compoundTag.getInt("zTile");
		this.life = compoundTag.getShort("life");
		if (compoundTag.containsKey("inBlockState", 10)) {
			this.inBlockState = TagHelper.deserializeBlockState(compoundTag.getCompound("inBlockState"));
		}

		this.shake = compoundTag.getByte("shake") & 255;
		this.inGround = compoundTag.getByte("inGround") == 1;
		if (compoundTag.containsKey("damage", 99)) {
			this.damage = compoundTag.getDouble("damage");
		}

		if (compoundTag.containsKey("pickup", 99)) {
			this.pickupType = ProjectileEntity.PickupType.fromOrdinal(compoundTag.getByte("pickup"));
		} else if (compoundTag.containsKey("player", 99)) {
			this.pickupType = compoundTag.getBoolean("player") ? ProjectileEntity.PickupType.PICKUP : ProjectileEntity.PickupType.NO_PICKUP;
		}

		this.setCritical(compoundTag.getBoolean("crit"));
		this.setFlagByte(compoundTag.getByte("PierceLevel"));
		if (compoundTag.hasUuid("OwnerUUID")) {
			this.ownerUuid = compoundTag.getUuid("OwnerUUID");
		}

		if (compoundTag.containsKey("SoundEvent", 8)) {
			this.sound = (SoundEvent)Registry.SOUND_EVENT.getOrEmpty(new Identifier(compoundTag.getString("SoundEvent"))).orElse(SoundEvents.field_15151);
		}

		this.setShotFromCrossbow(compoundTag.getBoolean("ShotFromCrossbow"));
	}

	public void setOwner(@Nullable Entity entity) {
		this.ownerUuid = entity == null ? null : entity.getUuid();
		if (entity instanceof PlayerEntity) {
			this.pickupType = ((PlayerEntity)entity).abilities.creativeMode ? ProjectileEntity.PickupType.CREATIVE_PICKUP : ProjectileEntity.PickupType.PICKUP;
		}
	}

	@Nullable
	public Entity getOwner() {
		return this.ownerUuid != null && this.world instanceof ServerWorld ? ((ServerWorld)this.world).getEntity(this.ownerUuid) : null;
	}

	@Override
	public void onPlayerCollision(PlayerEntity playerEntity) {
		if (!this.world.isClient && (this.inGround || this.isNoClip()) && this.shake <= 0) {
			boolean bl = this.pickupType == ProjectileEntity.PickupType.PICKUP
				|| this.pickupType == ProjectileEntity.PickupType.CREATIVE_PICKUP && playerEntity.abilities.creativeMode
				|| this.isNoClip() && this.getOwner().getUuid() == playerEntity.getUuid();
			if (this.pickupType == ProjectileEntity.PickupType.PICKUP && !playerEntity.inventory.insertStack(this.asItemStack())) {
				bl = false;
			}

			if (bl) {
				playerEntity.pickUpEntity(this, 1);
				this.invalidate();
			}
		}
	}

	protected abstract ItemStack asItemStack();

	@Override
	protected boolean method_5658() {
		return false;
	}

	public void setDamage(double d) {
		this.damage = d;
	}

	public double getDamage() {
		return this.damage;
	}

	public void method_7449(int i) {
		this.field_7575 = i;
	}

	@Override
	public boolean method_5732() {
		return false;
	}

	@Override
	protected float getEyeHeight(EntityPose entityPose, EntitySize entitySize) {
		return 0.0F;
	}

	public void setCritical(boolean bl) {
		this.setFlag(1, bl);
	}

	public void setFlagByte(byte b) {
		this.dataTracker.set(PIERCE_LEVEL, b);
	}

	private void setFlag(int i, boolean bl) {
		byte b = this.dataTracker.get(FLAGS);
		if (bl) {
			this.dataTracker.set(FLAGS, (byte)(b | i));
		} else {
			this.dataTracker.set(FLAGS, (byte)(b & ~i));
		}
	}

	public boolean method_7443() {
		byte b = this.dataTracker.get(FLAGS);
		return (b & 1) != 0;
	}

	public boolean isShotFromCrossbow() {
		byte b = this.dataTracker.get(FLAGS);
		return (b & 4) != 0;
	}

	public byte getPierceLevel() {
		return this.dataTracker.get(PIERCE_LEVEL);
	}

	public void method_7435(LivingEntity livingEntity, float f) {
		int i = EnchantmentHelper.getEquipmentLevel(Enchantments.field_9103, livingEntity);
		int j = EnchantmentHelper.getEquipmentLevel(Enchantments.field_9116, livingEntity);
		this.setDamage((double)(f * 2.0F) + this.random.nextGaussian() * 0.25 + (double)((float)this.world.getDifficulty().getId() * 0.11F));
		if (i > 0) {
			this.setDamage(this.getDamage() + (double)i * 0.5 + 0.5);
		}

		if (j > 0) {
			this.method_7449(j);
		}

		if (EnchantmentHelper.getEquipmentLevel(Enchantments.field_9126, livingEntity) > 0) {
			this.setOnFireFor(100);
		}
	}

	protected float method_7436() {
		return 0.6F;
	}

	public void setNoClip(boolean bl) {
		this.noClip = bl;
		this.setFlag(2, bl);
	}

	public boolean isNoClip() {
		return !this.world.isClient ? this.noClip : (this.dataTracker.get(FLAGS) & 2) != 0;
	}

	public void setShotFromCrossbow(boolean bl) {
		this.setFlag(4, bl);
	}

	@Override
	public Packet<?> createSpawnPacket() {
		Entity entity = this.getOwner();
		return new EntitySpawnS2CPacket(this, entity == null ? 0 : entity.getEntityId());
	}

	public static enum PickupType {
		NO_PICKUP,
		PICKUP,
		CREATIVE_PICKUP;

		public static ProjectileEntity.PickupType fromOrdinal(int i) {
			if (i < 0 || i > values().length) {
				i = 0;
			}

			return values()[i];
		}
	}
}
