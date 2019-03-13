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
import net.minecraft.class_1675;
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
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.sortme.Projectile;
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
	private static final TrackedData<Byte> field_7573 = DataTracker.registerData(ProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final TrackedData<Optional<UUID>> field_7580 = DataTracker.registerData(ProjectileEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	private static final TrackedData<Byte> field_7589 = DataTracker.registerData(ProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);
	private int xTile = -1;
	private int yTile = -1;
	private int zTIle = -1;
	@Nullable
	private BlockState field_7586;
	protected boolean inGround;
	protected int field_7576;
	public ProjectileEntity.PickupType pickupType = ProjectileEntity.PickupType.NO_PICKUP;
	public int shake;
	public UUID ownerUuid;
	private int life;
	private int field_7577;
	private double damage = 2.0;
	private int field_7575;
	private SoundEvent field_7584 = SoundEvents.field_15151;
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

	public void method_7444(SoundEvent soundEvent) {
		this.field_7584 = soundEvent;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderAtDistance(double d) {
		double e = this.method_5829().averageDimension() * 10.0;
		if (Double.isNaN(e)) {
			e = 1.0;
		}

		e *= 64.0 * getRenderDistanceMultiplier();
		return d < e * e;
	}

	@Override
	protected void initDataTracker() {
		this.field_6011.startTracking(field_7573, (byte)0);
		this.field_6011.startTracking(field_7580, Optional.empty());
		this.field_6011.startTracking(field_7589, (byte)0);
	}

	public void method_7474(Entity entity, float f, float g, float h, float i, float j) {
		float k = -MathHelper.sin(g * (float) (Math.PI / 180.0)) * MathHelper.cos(f * (float) (Math.PI / 180.0));
		float l = -MathHelper.sin(f * (float) (Math.PI / 180.0));
		float m = MathHelper.cos(g * (float) (Math.PI / 180.0)) * MathHelper.cos(f * (float) (Math.PI / 180.0));
		this.setVelocity((double)k, (double)l, (double)m, i, j);
		this.method_18799(this.method_18798().add(entity.method_18798().x, entity.onGround ? 0.0 : entity.method_18798().y, entity.method_18798().z));
	}

	@Override
	public void setVelocity(double d, double e, double f, float g, float h) {
		Vec3d vec3d = new Vec3d(d, e, f)
			.normalize()
			.add(this.random.nextGaussian() * 0.0075F * (double)h, this.random.nextGaussian() * 0.0075F * (double)h, this.random.nextGaussian() * 0.0075F * (double)h)
			.multiply((double)g);
		this.method_18799(vec3d);
		float i = MathHelper.sqrt(method_17996(vec3d));
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
	public void update() {
		super.update();
		boolean bl = this.isNoClip();
		Vec3d vec3d = this.method_18798();
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float f = MathHelper.sqrt(method_17996(vec3d));
			this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI);
			this.pitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 180.0F / (float)Math.PI);
			this.prevYaw = this.yaw;
			this.prevPitch = this.pitch;
		}

		BlockPos blockPos = new BlockPos(this.xTile, this.yTile, this.zTIle);
		BlockState blockState = this.field_6002.method_8320(blockPos);
		if (!blockState.isAir() && !bl) {
			VoxelShape voxelShape = blockState.method_11628(this.field_6002, blockPos);
			if (!voxelShape.isEmpty()) {
				for (BoundingBox boundingBox : voxelShape.getBoundingBoxList()) {
					if (boundingBox.method_996(blockPos).method_1006(new Vec3d(this.x, this.y, this.z))) {
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
			if (this.field_7586 != blockState && this.field_6002.method_18026(this.method_5829().expand(0.05))) {
				this.inGround = false;
				this.method_18799(
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
			HitResult hitResult = this.field_6002
				.method_17742(new RayTraceContext(vec3d2, vec3d3, RayTraceContext.ShapeType.field_17558, RayTraceContext.FluidHandling.NONE, this));
			if (hitResult.getType() != HitResult.Type.NONE) {
				vec3d3 = hitResult.method_17784();
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

			vec3d = this.method_18798();
			double d = vec3d.x;
			double e = vec3d.y;
			double g = vec3d.z;
			if (this.method_7443()) {
				for (int i = 0; i < 4; i++) {
					this.field_6002
						.method_8406(ParticleTypes.field_11205, this.x + d * (double)i / 4.0, this.y + e * (double)i / 4.0, this.z + g * (double)i / 4.0, -d, -e + 0.2, -g);
				}
			}

			this.x += d;
			this.y += e;
			this.z += g;
			float h = MathHelper.sqrt(method_17996(vec3d));
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
					this.field_6002.method_8406(ParticleTypes.field_11247, this.x - d * 0.25, this.y - e * 0.25, this.z - g * 0.25, d, e, g);
				}

				j = this.method_7436();
			}

			this.method_18799(vec3d.multiply((double)j));
			if (!this.isUnaffectedByGravity() && !bl) {
				Vec3d vec3d4 = this.method_18798();
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
			BlockPos blockPos = blockHitResult.method_17777();
			this.xTile = blockPos.getX();
			this.yTile = blockPos.getY();
			this.zTIle = blockPos.getZ();
			BlockState blockState = this.field_6002.method_8320(blockPos);
			this.field_7586 = blockState;
			Vec3d vec3d = blockHitResult.method_17784().subtract(this.x, this.y, this.z);
			this.method_18799(vec3d);
			Vec3d vec3d2 = vec3d.normalize().multiply(0.05F);
			this.x = this.x - vec3d2.x;
			this.y = this.y - vec3d2.y;
			this.z = this.z - vec3d2.z;
			this.method_5783(this.method_7440(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
			this.inGround = true;
			this.shake = 7;
			this.setCritical(false);
			this.setFlagByte((byte)0);
			this.method_7444(SoundEvents.field_15151);
			this.setShotFromCrossbow(false);
			this.method_7453();
			blockState.method_19287(this.field_6002, blockState, blockHitResult, this);
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
		float f = (float)this.method_18798().length();
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
			damageSource = DamageSource.method_5522(this, this);
		} else {
			damageSource = DamageSource.method_5522(this, entity2);
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
				if (!this.field_6002.isClient && this.getPierceLevel() <= 0) {
					livingEntity.setStuckArrows(livingEntity.getStuckArrows() + 1);
				}

				if (this.field_7575 > 0) {
					Vec3d vec3d = this.method_18798().multiply(1.0, 0.0, 1.0).normalize().multiply((double)this.field_7575 * 0.6);
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
					((ServerPlayerEntity)entity2).field_13987.sendPacket(new GameStateChangeS2CPacket(6, 0.0F));
				}

				if (!entity.isValid() && this.field_7579 != null) {
					this.field_7579.add(livingEntity);
				}

				if (!this.field_6002.isClient && entity2 instanceof ServerPlayerEntity) {
					ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity2;
					if (this.field_7579 != null && this.isShotFromCrossbow()) {
						Criterions.KILLED_BY_CROSSBOW.method_8980(serverPlayerEntity, this.field_7579, this.field_7579.size());
					} else if (!entity.isValid() && this.isShotFromCrossbow()) {
						Criterions.KILLED_BY_CROSSBOW.method_8980(serverPlayerEntity, Arrays.asList(entity), 0);
					}
				}
			}

			this.method_5783(this.field_7584, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
			if (this.getPierceLevel() <= 0 && !(entity instanceof EndermanEntity)) {
				this.invalidate();
			}
		} else {
			this.method_18799(this.method_18798().multiply(-0.1));
			this.yaw += 180.0F;
			this.prevYaw += 180.0F;
			this.field_7577 = 0;
			if (!this.field_6002.isClient && this.method_18798().lengthSquared() < 1.0E-7) {
				if (this.pickupType == ProjectileEntity.PickupType.PICKUP) {
					this.method_5699(this.method_7445(), 0.1F);
				}

				this.invalidate();
			}
		}
	}

	protected SoundEvent method_7440() {
		return this.field_7584;
	}

	@Override
	public void method_5784(MovementType movementType, Vec3d vec3d) {
		super.method_5784(movementType, vec3d);
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
		return class_1675.method_18077(
			this.field_6002,
			this,
			vec3d,
			vec3d2,
			this.method_5829().method_18804(this.method_18798()).expand(1.0),
			entity -> !entity.isSpectator()
					&& entity.isValid()
					&& entity.doesCollide()
					&& (entity != this.getOwner() || this.field_7577 >= 5)
					&& (this.field_7590 == null || !this.field_7590.contains(entity.getEntityId()))
		);
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		compoundTag.putInt("xTile", this.xTile);
		compoundTag.putInt("yTile", this.yTile);
		compoundTag.putInt("zTile", this.zTIle);
		compoundTag.putShort("life", (short)this.life);
		if (this.field_7586 != null) {
			compoundTag.method_10566("inBlockState", TagHelper.serializeBlockState(this.field_7586));
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

		compoundTag.putString("SoundEvent", Registry.SOUND_EVENT.method_10221(this.field_7584).toString());
		compoundTag.putBoolean("ShotFromCrossbow", this.isShotFromCrossbow());
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		this.xTile = compoundTag.getInt("xTile");
		this.yTile = compoundTag.getInt("yTile");
		this.zTIle = compoundTag.getInt("zTile");
		this.life = compoundTag.getShort("life");
		if (compoundTag.containsKey("inBlockState", 10)) {
			this.field_7586 = TagHelper.deserializeBlockState(compoundTag.getCompound("inBlockState"));
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
			this.field_7584 = (SoundEvent)Registry.SOUND_EVENT.method_17966(new Identifier(compoundTag.getString("SoundEvent"))).orElse(SoundEvents.field_15151);
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
		return this.ownerUuid != null && this.field_6002 instanceof ServerWorld ? ((ServerWorld)this.field_6002).getEntity(this.ownerUuid) : null;
	}

	@Override
	public void method_5694(PlayerEntity playerEntity) {
		if (!this.field_6002.isClient && (this.inGround || this.isNoClip()) && this.shake <= 0) {
			boolean bl = this.pickupType == ProjectileEntity.PickupType.PICKUP
				|| this.pickupType == ProjectileEntity.PickupType.CREATIVE_PICKUP && playerEntity.abilities.creativeMode
				|| this.isNoClip() && this.getOwner().getUuid() == playerEntity.getUuid();
			if (this.pickupType == ProjectileEntity.PickupType.PICKUP && !playerEntity.inventory.method_7394(this.method_7445())) {
				bl = false;
			}

			if (bl) {
				playerEntity.pickUpEntity(this, 1);
				this.invalidate();
			}
		}
	}

	protected abstract ItemStack method_7445();

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
	protected float method_18378(EntityPose entityPose, EntitySize entitySize) {
		return 0.0F;
	}

	public void setCritical(boolean bl) {
		this.setFlag(1, bl);
	}

	public void setFlagByte(byte b) {
		this.field_6011.set(field_7589, b);
	}

	private void setFlag(int i, boolean bl) {
		byte b = this.field_6011.get(field_7573);
		if (bl) {
			this.field_6011.set(field_7573, (byte)(b | i));
		} else {
			this.field_6011.set(field_7573, (byte)(b & ~i));
		}
	}

	public boolean method_7443() {
		byte b = this.field_6011.get(field_7573);
		return (b & 1) != 0;
	}

	public boolean isShotFromCrossbow() {
		byte b = this.field_6011.get(field_7573);
		return (b & 4) != 0;
	}

	public byte getPierceLevel() {
		return this.field_6011.get(field_7589);
	}

	public void method_7435(LivingEntity livingEntity, float f) {
		int i = EnchantmentHelper.getEquipmentLevel(Enchantments.field_9103, livingEntity);
		int j = EnchantmentHelper.getEquipmentLevel(Enchantments.field_9116, livingEntity);
		this.setDamage((double)(f * 2.0F) + this.random.nextGaussian() * 0.25 + (double)((float)this.field_6002.getDifficulty().getId() * 0.11F));
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
		return !this.field_6002.isClient ? this.noClip : (this.field_6011.get(field_7573) & 2) != 0;
	}

	public void setShotFromCrossbow(boolean bl) {
		this.setFlag(4, bl);
	}

	@Override
	public Packet<?> method_18002() {
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
