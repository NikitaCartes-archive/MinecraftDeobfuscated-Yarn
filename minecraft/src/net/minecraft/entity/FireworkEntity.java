package net.minecraft.entity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = FlyingItemEntity.class
	)})
public class FireworkEntity extends Entity implements FlyingItemEntity {
	private static final TrackedData<ItemStack> ITEM_STACK = DataTracker.registerData(FireworkEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private static final TrackedData<Optional<UUID>> field_7611 = DataTracker.registerData(FireworkEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	private static final TrackedData<Boolean> field_7615 = DataTracker.registerData(FireworkEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final Predicate<Entity> field_16996 = EntityPredicates.EXCEPT_SPECTATOR.and(EntityPredicates.VALID_ENTITY.and(Entity::doesCollide));
	private int field_7613;
	private int field_7612;
	private LivingEntity field_7616;

	public FireworkEntity(World world) {
		super(EntityType.FIREWORK_ROCKET, world);
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(ITEM_STACK, ItemStack.EMPTY);
		this.dataTracker.startTracking(field_7611, Optional.empty());
		this.dataTracker.startTracking(field_7615, false);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderAtDistance(double d) {
		return d < 4096.0 && !this.method_7476();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderFrom(double d, double e, double f) {
		return super.shouldRenderFrom(d, e, f) && !this.method_7476();
	}

	public FireworkEntity(World world, double d, double e, double f, ItemStack itemStack) {
		super(EntityType.FIREWORK_ROCKET, world);
		this.field_7613 = 0;
		this.setPosition(d, e, f);
		int i = 1;
		if (!itemStack.isEmpty() && itemStack.hasTag()) {
			this.dataTracker.set(ITEM_STACK, itemStack.copy());
			i += itemStack.getOrCreateSubCompoundTag("Fireworks").getByte("Flight");
		}

		this.velocityX = this.random.nextGaussian() * 0.001;
		this.velocityZ = this.random.nextGaussian() * 0.001;
		this.velocityY = 0.05;
		this.field_7612 = 10 * i + this.random.nextInt(6) + this.random.nextInt(7);
	}

	public FireworkEntity(World world, ItemStack itemStack, LivingEntity livingEntity) {
		this(world, livingEntity.x, livingEntity.y, livingEntity.z, itemStack);
		this.dataTracker.set(field_7611, Optional.of(livingEntity.getUuid()));
		this.field_7616 = livingEntity;
	}

	public FireworkEntity(World world, ItemStack itemStack, double d, double e, double f, boolean bl) {
		this(world, d, e, f, itemStack);
		this.dataTracker.set(field_7615, bl);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setVelocityClient(double d, double e, double f) {
		this.velocityX = d;
		this.velocityY = e;
		this.velocityZ = f;
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float g = MathHelper.sqrt(d * d + f * f);
			this.yaw = (float)(MathHelper.atan2(d, f) * 180.0F / (float)Math.PI);
			this.pitch = (float)(MathHelper.atan2(e, (double)g) * 180.0F / (float)Math.PI);
			this.prevYaw = this.yaw;
			this.prevPitch = this.pitch;
		}
	}

	public void method_7474(Entity entity, float f, float g) {
		float h = -MathHelper.sin(f * (float) (Math.PI / 180.0)) * MathHelper.cos(entity.pitch * (float) (Math.PI / 180.0));
		float i = -MathHelper.sin(entity.pitch * (float) (Math.PI / 180.0));
		float j = MathHelper.cos(f * (float) (Math.PI / 180.0)) * MathHelper.cos(entity.pitch * (float) (Math.PI / 180.0));
		float k = MathHelper.sqrt(h * h + i * i + j * j);
		h /= k;
		i /= k;
		j /= k;
		h = (float)((double)h + this.random.nextGaussian() * 0.0075F * (double)g);
		i = (float)((double)i + this.random.nextGaussian() * 0.0075F * (double)g);
		j = (float)((double)j + this.random.nextGaussian() * 0.0075F * (double)g);
		this.velocityX = (double)h;
		this.velocityY = (double)i;
		this.velocityZ = (double)j;
		float l = MathHelper.sqrt(h * h + j * j);
		this.yaw = (float)(MathHelper.atan2((double)h, (double)j) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2((double)i, (double)l) * 180.0F / (float)Math.PI);
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
		this.field_7613 = 0;
		this.velocityX = this.velocityX + entity.velocityX;
		this.velocityY = this.velocityY + entity.velocityY;
		this.velocityZ = this.velocityZ + entity.velocityZ;
	}

	@Override
	public void update() {
		this.prevRenderX = this.x;
		this.prevRenderY = this.y;
		this.prevRenderZ = this.z;
		super.update();
		if (this.method_7476()) {
			if (this.field_7616 == null) {
				UUID uUID = (UUID)this.dataTracker.get(field_7611).orElse(null);
				Entity entity = this.world.getEntityByUuid(uUID);
				if (entity instanceof LivingEntity) {
					this.field_7616 = (LivingEntity)entity;
				}
			}

			if (this.field_7616 != null) {
				if (this.field_7616.isFallFlying()) {
					Vec3d vec3d = this.field_7616.method_5720();
					double d = 1.5;
					double e = 0.1;
					this.field_7616.velocityX = this.field_7616.velocityX + vec3d.x * 0.1 + (vec3d.x * 1.5 - this.field_7616.velocityX) * 0.5;
					this.field_7616.velocityY = this.field_7616.velocityY + vec3d.y * 0.1 + (vec3d.y * 1.5 - this.field_7616.velocityY) * 0.5;
					this.field_7616.velocityZ = this.field_7616.velocityZ + vec3d.z * 0.1 + (vec3d.z * 1.5 - this.field_7616.velocityZ) * 0.5;
				}

				this.setPosition(this.field_7616.x, this.field_7616.y, this.field_7616.z);
				this.velocityX = this.field_7616.velocityX;
				this.velocityY = this.field_7616.velocityY;
				this.velocityZ = this.field_7616.velocityZ;
			}
		} else {
			if (!this.method_7477()) {
				this.velocityX *= 1.15;
				this.velocityZ *= 1.15;
				this.velocityY += 0.04;
			}

			this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
		}

		Vec3d vec3d = new Vec3d(this.x, this.y, this.z);
		Vec3d vec3d2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
		HitResult hitResult = this.world
			.method_17742(new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.field_17558, RayTraceContext.FluidHandling.NONE, this));
		vec3d = new Vec3d(this.x, this.y, this.z);
		vec3d2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
		if (hitResult.getType() != HitResult.Type.NONE) {
			vec3d2 = hitResult.getPos();
		}

		Entity entity2 = this.method_16829(vec3d, vec3d2);
		if (entity2 != null) {
			hitResult = new EntityHitResult(entity2);
		}

		if (!this.noClip) {
			this.method_16828(hitResult);
			this.velocityDirty = true;
		}

		float f = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
		this.yaw = (float)(MathHelper.atan2(this.velocityX, this.velocityZ) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(this.velocityY, (double)f) * 180.0F / (float)Math.PI);

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
		if (this.field_7613 == 0 && !this.isSilent()) {
			this.world.playSound(null, this.x, this.y, this.z, SoundEvents.field_14702, SoundCategory.field_15256, 3.0F, 1.0F);
		}

		this.field_7613++;
		if (this.world.isClient && this.field_7613 % 2 < 2) {
			this.world
				.addParticle(
					ParticleTypes.field_11248, this.x, this.y - 0.3, this.z, this.random.nextGaussian() * 0.05, -this.velocityY * 0.5, this.random.nextGaussian() * 0.05
				);
		}

		if (!this.world.isClient && this.field_7613 > this.field_7612) {
			this.method_16830();
		}
	}

	private void method_16830() {
		this.world.summonParticle(this, (byte)17);
		this.method_7475();
		this.invalidate();
	}

	protected void method_16828(HitResult hitResult) {
		if (hitResult.getType() == HitResult.Type.ENTITY && !this.world.isClient) {
			this.method_16830();
		} else if (this.collided) {
			BlockPos blockPos;
			if (hitResult.getType() == HitResult.Type.BLOCK) {
				blockPos = new BlockPos(((BlockHitResult)hitResult).getBlockPos());
			} else {
				blockPos = new BlockPos(this);
			}

			this.world.getBlockState(blockPos).onEntityCollision(this.world, blockPos, this);
			this.method_16830();
		}
	}

	@Nullable
	protected Entity method_16829(Vec3d vec3d, Vec3d vec3d2) {
		Entity entity = null;
		List<Entity> list = this.world.getEntities(this, this.getBoundingBox().stretch(this.velocityX, this.velocityY, this.velocityZ).expand(1.0), field_16996);
		double d = 0.0;

		for (Entity entity2 : list) {
			BoundingBox boundingBox = entity2.getBoundingBox().expand(0.3F);
			Optional<Vec3d> optional = boundingBox.rayTrace(vec3d, vec3d2);
			if (optional.isPresent()) {
				double e = vec3d.squaredDistanceTo((Vec3d)optional.get());
				if (e < d || d == 0.0) {
					entity = entity2;
					d = e;
				}
			}
		}

		return entity;
	}

	private void method_7475() {
		float f = 0.0F;
		ItemStack itemStack = this.dataTracker.get(ITEM_STACK);
		CompoundTag compoundTag = itemStack.isEmpty() ? null : itemStack.getSubCompoundTag("Fireworks");
		ListTag listTag = compoundTag != null ? compoundTag.getList("Explosions", 10) : null;
		if (listTag != null && !listTag.isEmpty()) {
			f = 5.0F + (float)(listTag.size() * 2);
		}

		if (f > 0.0F) {
			if (this.field_7616 != null) {
				this.field_7616.damage(DamageSource.FIREWORKS, 5.0F + (float)(listTag.size() * 2));
			}

			double d = 5.0;
			Vec3d vec3d = new Vec3d(this.x, this.y, this.z);

			for (LivingEntity livingEntity : this.world.getVisibleEntities(LivingEntity.class, this.getBoundingBox().expand(5.0))) {
				if (livingEntity != this.field_7616 && !(this.squaredDistanceTo(livingEntity) > 25.0)) {
					boolean bl = false;

					for (int i = 0; i < 2; i++) {
						Vec3d vec3d2 = new Vec3d(livingEntity.x, livingEntity.y + (double)livingEntity.getHeight() * 0.5 * (double)i, livingEntity.z);
						HitResult hitResult = this.world
							.method_17742(new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.field_17558, RayTraceContext.FluidHandling.NONE, this));
						if (hitResult.getType() == HitResult.Type.NONE) {
							bl = true;
							break;
						}
					}

					if (bl) {
						float g = f * (float)Math.sqrt((5.0 - (double)this.distanceTo(livingEntity)) / 5.0);
						livingEntity.damage(DamageSource.FIREWORKS, g);
					}
				}
			}
		}
	}

	private boolean method_7476() {
		return this.dataTracker.get(field_7611).orElse(null) != null;
	}

	public boolean method_7477() {
		return this.dataTracker.get(field_7615);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 17 && this.world.isClient) {
			ItemStack itemStack = this.dataTracker.get(ITEM_STACK);
			CompoundTag compoundTag = itemStack.isEmpty() ? null : itemStack.getSubCompoundTag("Fireworks");
			this.world.addFireworkParticle(this.x, this.y, this.z, this.velocityX, this.velocityY, this.velocityZ, compoundTag);
		}

		super.method_5711(b);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		compoundTag.putInt("Life", this.field_7613);
		compoundTag.putInt("LifeTime", this.field_7612);
		ItemStack itemStack = this.dataTracker.get(ITEM_STACK);
		if (!itemStack.isEmpty()) {
			compoundTag.put("FireworksItem", itemStack.toTag(new CompoundTag()));
		}

		compoundTag.putBoolean("ShotAtAngle", this.dataTracker.get(field_7615));
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		this.field_7613 = compoundTag.getInt("Life");
		this.field_7612 = compoundTag.getInt("LifeTime");
		ItemStack itemStack = ItemStack.fromTag(compoundTag.getCompound("FireworksItem"));
		if (!itemStack.isEmpty()) {
			this.dataTracker.set(ITEM_STACK, itemStack);
		}

		if (compoundTag.containsKey("ShotAtAngle")) {
			this.dataTracker.set(field_7615, compoundTag.getBoolean("ShotAtAngle"));
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public ItemStack getItem() {
		ItemStack itemStack = this.dataTracker.get(ITEM_STACK);
		return itemStack.isEmpty() ? new ItemStack(Items.field_8639) : itemStack;
	}

	@Override
	public boolean method_5732() {
		return false;
	}
}
