package net.minecraft.entity.projectile;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.packet.GameStateChangeClientPacket;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.sortme.Projectile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.HitResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.FluidRayTraceMode;
import net.minecraft.world.World;

public abstract class ProjectileEntity extends Entity implements Projectile {
	private static final Predicate<Entity> COLLIDABLE_ENTITIES = EntityPredicates.EXCEPT_SPECTATOR.and(EntityPredicates.VALID_ENTITY.and(Entity::doesCollide));
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
	private SoundEvent sound;
	private IntOpenHashSet field_7590;
	private List<Entity> field_7579;

	protected ProjectileEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.setSize(0.5F, 0.5F);
		this.sound = SoundEvents.field_15151;
	}

	protected ProjectileEntity(EntityType<?> entityType, double d, double e, double f, World world) {
		this(entityType, world);
		this.setPosition(d, e, f);
	}

	protected ProjectileEntity(EntityType<?> entityType, LivingEntity livingEntity, World world) {
		this(entityType, livingEntity.x, livingEntity.y + (double)livingEntity.getEyeHeight() - 0.1F, livingEntity.z, world);
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

	public void method_7437(Entity entity, float f, float g, float h, float i, float j) {
		float k = -MathHelper.sin(g * (float) (Math.PI / 180.0)) * MathHelper.cos(f * (float) (Math.PI / 180.0));
		float l = -MathHelper.sin(f * (float) (Math.PI / 180.0));
		float m = MathHelper.cos(g * (float) (Math.PI / 180.0)) * MathHelper.cos(f * (float) (Math.PI / 180.0));
		this.setVelocity((double)k, (double)l, (double)m, i, j);
		this.velocityX = this.velocityX + entity.velocityX;
		this.velocityZ = this.velocityZ + entity.velocityZ;
		if (!entity.onGround) {
			this.velocityY = this.velocityY + entity.velocityY;
		}
	}

	@Override
	public void setVelocity(double d, double e, double f, float g, float h) {
		float i = MathHelper.sqrt(d * d + e * e + f * f);
		d /= (double)i;
		e /= (double)i;
		f /= (double)i;
		d += this.random.nextGaussian() * 0.0075F * (double)h;
		e += this.random.nextGaussian() * 0.0075F * (double)h;
		f += this.random.nextGaussian() * 0.0075F * (double)h;
		d *= (double)g;
		e *= (double)g;
		f *= (double)g;
		this.velocityX = d;
		this.velocityY = e;
		this.velocityZ = f;
		float j = MathHelper.sqrt(d * d + f * f);
		this.yaw = (float)(MathHelper.atan2(d, f) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(e, (double)j) * 180.0F / (float)Math.PI);
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
		this.velocityX = d;
		this.velocityY = e;
		this.velocityZ = f;
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
	public void update() {
		super.update();
		boolean bl = this.isNoClip();
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float f = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
			this.yaw = (float)(MathHelper.atan2(this.velocityX, this.velocityZ) * 180.0F / (float)Math.PI);
			this.pitch = (float)(MathHelper.atan2(this.velocityY, (double)f) * 180.0F / (float)Math.PI);
			this.prevYaw = this.yaw;
			this.prevPitch = this.pitch;
		}

		BlockPos blockPos = new BlockPos(this.xTile, this.yTile, this.zTIle);
		BlockState blockState = this.world.getBlockState(blockPos);
		if (!blockState.isAir() && !bl) {
			VoxelShape voxelShape = blockState.getCollisionShape(this.world, blockPos);
			if (!voxelShape.isEmpty()) {
				for (BoundingBox boundingBox : voxelShape.getBoundingBoxList()) {
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
			if (this.inBlockState != blockState && this.world.method_8587(null, this.getBoundingBox().expand(0.05))) {
				this.inGround = false;
				this.velocityX = this.velocityX * (double)(this.random.nextFloat() * 0.2F);
				this.velocityY = this.velocityY * (double)(this.random.nextFloat() * 0.2F);
				this.velocityZ = this.velocityZ * (double)(this.random.nextFloat() * 0.2F);
				this.life = 0;
				this.field_7577 = 0;
			} else {
				this.method_7446();
			}

			this.field_7576++;
		} else {
			this.field_7576 = 0;
			this.field_7577++;
			Vec3d vec3d = new Vec3d(this.x, this.y, this.z);
			Vec3d vec3d2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
			HitResult hitResult = this.world.rayTrace(vec3d, vec3d2, FluidRayTraceMode.NONE, true, false);
			vec3d = new Vec3d(this.x, this.y, this.z);
			vec3d2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
			if (hitResult != null) {
				vec3d2 = new Vec3d(hitResult.pos.x, hitResult.pos.y, hitResult.pos.z);
			}

			while (!this.invalid) {
				Entity entity = this.method_7434(vec3d, vec3d2);
				if (entity != null) {
					hitResult = new HitResult(entity);
				}

				if (hitResult != null && hitResult.entity instanceof PlayerEntity) {
					PlayerEntity playerEntity = (PlayerEntity)hitResult.entity;
					Entity entity2 = this.getOwner();
					if (entity2 instanceof PlayerEntity && !((PlayerEntity)entity2).shouldDamagePlayer(playerEntity)) {
						hitResult = null;
					}
				}

				if (hitResult != null && !bl) {
					this.method_7457(hitResult);
					this.velocityDirty = true;
				}

				if (entity == null || this.getPierceLevel() <= 0) {
					break;
				}

				hitResult = null;
			}

			if (this.method_7443()) {
				for (int i = 0; i < 4; i++) {
					this.world
						.method_8406(
							ParticleTypes.field_11205,
							this.x + this.velocityX * (double)i / 4.0,
							this.y + this.velocityY * (double)i / 4.0,
							this.z + this.velocityZ * (double)i / 4.0,
							-this.velocityX,
							-this.velocityY + 0.2,
							-this.velocityZ
						);
				}
			}

			this.x = this.x + this.velocityX;
			this.y = this.y + this.velocityY;
			this.z = this.z + this.velocityZ;
			float g = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
			if (bl) {
				this.yaw = (float)(MathHelper.atan2(-this.velocityX, -this.velocityZ) * 180.0F / (float)Math.PI);
			} else {
				this.yaw = (float)(MathHelper.atan2(this.velocityX, this.velocityZ) * 180.0F / (float)Math.PI);
			}

			this.pitch = (float)(MathHelper.atan2(this.velocityY, (double)g) * 180.0F / (float)Math.PI);

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
			float h = 0.99F;
			float j = 0.05F;
			if (this.isInsideWater()) {
				for (int k = 0; k < 4; k++) {
					float l = 0.25F;
					this.world
						.method_8406(
							ParticleTypes.field_11247,
							this.x - this.velocityX * 0.25,
							this.y - this.velocityY * 0.25,
							this.z - this.velocityZ * 0.25,
							this.velocityX,
							this.velocityY,
							this.velocityZ
						);
				}

				h = this.method_7436();
			}

			this.velocityX *= (double)h;
			this.velocityY *= (double)h;
			this.velocityZ *= (double)h;
			if (!this.isUnaffectedByGravity() && !bl) {
				this.velocityY -= 0.05F;
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
		if (hitResult.entity != null) {
			this.method_7454(hitResult);
		} else {
			BlockPos blockPos = hitResult.getBlockPos();
			this.xTile = blockPos.getX();
			this.yTile = blockPos.getY();
			this.zTIle = blockPos.getZ();
			BlockState blockState = this.world.getBlockState(blockPos);
			this.inBlockState = blockState;
			this.velocityX = (double)((float)(hitResult.pos.x - this.x));
			this.velocityY = (double)((float)(hitResult.pos.y - this.y));
			this.velocityZ = (double)((float)(hitResult.pos.z - this.z));
			float f = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ) * 20.0F;
			this.x = this.x - this.velocityX / (double)f;
			this.y = this.y - this.velocityY / (double)f;
			this.z = this.z - this.velocityZ / (double)f;
			this.playSoundAtEntity(this.getSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
			this.inGround = true;
			this.shake = 7;
			this.setCritical(false);
			this.setFlagByte((byte)0);
			this.setSound(SoundEvents.field_15151);
			this.setShotFromCrossbow(false);
			this.method_7453();
			if (!blockState.isAir()) {
				this.inBlockState.onEntityCollision(this.world, blockPos, this);
			}
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

	protected void method_7454(HitResult hitResult) {
		Entity entity = hitResult.entity;
		float f = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
		int i = MathHelper.ceil((double)f * this.damage);
		if (this.getPierceLevel() > 0) {
			if (this.field_7590 == null) {
				this.field_7590 = new IntOpenHashSet(5);
			}

			if (this.field_7579 == null) {
				this.field_7579 = new ArrayList(5);
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
					float g = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
					if (g > 0.0F) {
						livingEntity.addVelocity(this.velocityX * (double)this.field_7575 * 0.6F / (double)g, 0.1, this.velocityZ * (double)this.field_7575 * 0.6F / (double)g);
					}
				}

				if (entity2 instanceof LivingEntity) {
					EnchantmentHelper.onUserDamaged(livingEntity, entity2);
					EnchantmentHelper.onTargetDamaged((LivingEntity)entity2, livingEntity);
				}

				this.onHit(livingEntity);
				if (entity2 != null && livingEntity != entity2 && livingEntity instanceof PlayerEntity && entity2 instanceof ServerPlayerEntity) {
					((ServerPlayerEntity)entity2).networkHandler.sendPacket(new GameStateChangeClientPacket(6, 0.0F));
				}

				if (!entity.isValid() && this.field_7579 != null) {
					this.field_7579.add(livingEntity);
				}

				if (!this.world.isClient && this.field_7579 != null && entity2 instanceof ServerPlayerEntity) {
					int j = this.field_7579.size();
					ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity2;
					Criterions.KILLED_BY_CROSSBOW.method_8980(serverPlayerEntity, this.field_7579, j);
				}
			}

			this.playSoundAtEntity(this.sound, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
			if (this.getPierceLevel() <= 0 && !(entity instanceof EndermanEntity)) {
				this.invalidate();
			}
		} else {
			this.velocityX *= -0.1F;
			this.velocityY *= -0.1F;
			this.velocityZ *= -0.1F;
			this.yaw += 180.0F;
			this.prevYaw += 180.0F;
			this.field_7577 = 0;
			if (!this.world.isClient && this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ < 0.001F) {
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
	public void move(MovementType movementType, double d, double e, double f) {
		super.move(movementType, d, e, f);
		if (this.inGround) {
			this.xTile = MathHelper.floor(this.x);
			this.yTile = MathHelper.floor(this.y);
			this.zTIle = MathHelper.floor(this.z);
		}
	}

	protected void onHit(LivingEntity livingEntity) {
	}

	@Nullable
	protected Entity method_7434(Vec3d vec3d, Vec3d vec3d2) {
		Entity entity = null;
		List<Entity> list = this.world
			.getEntities(this, this.getBoundingBox().stretch(this.velocityX, this.velocityY, this.velocityZ).expand(1.0), COLLIDABLE_ENTITIES);
		double d = 0.0;

		for (int i = 0; i < list.size(); i++) {
			Entity entity2 = (Entity)list.get(i);
			if ((entity2 != this.getOwner() || this.field_7577 >= 5) && (this.field_7590 == null || !this.field_7590.contains(entity2.getEntityId()))) {
				BoundingBox boundingBox = entity2.getBoundingBox().expand(0.3F);
				HitResult hitResult = boundingBox.rayTrace(vec3d, vec3d2);
				if (hitResult != null) {
					double e = vec3d.squaredDistanceTo(hitResult.pos);
					if (e < d || d == 0.0) {
						entity = entity2;
						d = e;
					}
				}
			}
		}

		return entity;
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
			this.sound = Registry.SOUND_EVENT.get(new Identifier(compoundTag.getString("SoundEvent")));
		}

		this.setShotFromCrossbow(compoundTag.getBoolean("ShotFromCrossbow"));
	}

	public void setOwner(@Nullable Entity entity) {
		this.ownerUuid = entity == null ? null : entity.getUuid();
	}

	@Nullable
	public Entity getOwner() {
		return this.ownerUuid != null && this.world instanceof ServerWorld ? this.world.getEntityByUuid(this.ownerUuid) : null;
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
				playerEntity.method_6103(this, 1);
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
	public float getEyeHeight() {
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
