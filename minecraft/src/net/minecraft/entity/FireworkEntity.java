package net.minecraft.entity;

import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.class_1675;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = FlyingItemEntity.class
	)})
public class FireworkEntity extends Entity implements FlyingItemEntity, Projectile {
	private static final TrackedData<ItemStack> ITEM_STACK = DataTracker.registerData(FireworkEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private static final TrackedData<OptionalInt> field_7611 = DataTracker.registerData(FireworkEntity.class, TrackedDataHandlerRegistry.field_17910);
	private static final TrackedData<Boolean> field_7615 = DataTracker.registerData(FireworkEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int field_7613;
	private int field_7612;
	private LivingEntity field_7616;

	public FireworkEntity(EntityType<? extends FireworkEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(ITEM_STACK, ItemStack.EMPTY);
		this.dataTracker.startTracking(field_7611, OptionalInt.empty());
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

		this.setVelocity(this.random.nextGaussian() * 0.001, 0.05, this.random.nextGaussian() * 0.001);
		this.field_7612 = 10 * i + this.random.nextInt(6) + this.random.nextInt(7);
	}

	public FireworkEntity(World world, ItemStack itemStack, LivingEntity livingEntity) {
		this(world, livingEntity.x, livingEntity.y, livingEntity.z, itemStack);
		this.dataTracker.set(field_7611, OptionalInt.of(livingEntity.getEntityId()));
		this.field_7616 = livingEntity;
	}

	public FireworkEntity(World world, ItemStack itemStack, double d, double e, double f, boolean bl) {
		this(world, d, e, f, itemStack);
		this.dataTracker.set(field_7615, bl);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setVelocityClient(double d, double e, double f) {
		this.setVelocity(d, e, f);
		if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
			float g = MathHelper.sqrt(d * d + f * f);
			this.yaw = (float)(MathHelper.atan2(d, f) * 180.0F / (float)Math.PI);
			this.pitch = (float)(MathHelper.atan2(e, (double)g) * 180.0F / (float)Math.PI);
			this.prevYaw = this.yaw;
			this.prevPitch = this.pitch;
		}
	}

	@Override
	public void update() {
		this.prevRenderX = this.x;
		this.prevRenderY = this.y;
		this.prevRenderZ = this.z;
		super.update();
		if (this.method_7476()) {
			if (this.field_7616 == null) {
				this.dataTracker.get(field_7611).ifPresent(i -> {
					Entity entity = this.world.getEntityById(i);
					if (entity instanceof LivingEntity) {
						this.field_7616 = (LivingEntity)entity;
					}
				});
			}

			if (this.field_7616 != null) {
				if (this.field_7616.isFallFlying()) {
					Vec3d vec3d = this.field_7616.method_5720();
					double d = 1.5;
					double e = 0.1;
					Vec3d vec3d2 = this.field_7616.getVelocity();
					this.field_7616
						.setVelocity(
							vec3d2.add(
								vec3d.x * 0.1 + (vec3d.x * 1.5 - vec3d2.x) * 0.5, vec3d.y * 0.1 + (vec3d.y * 1.5 - vec3d2.y) * 0.5, vec3d.z * 0.1 + (vec3d.z * 1.5 - vec3d2.z) * 0.5
							)
						);
				}

				this.setPosition(this.field_7616.x, this.field_7616.y, this.field_7616.z);
				this.setVelocity(this.field_7616.getVelocity());
			}
		} else {
			if (!this.method_7477()) {
				this.setVelocity(this.getVelocity().multiply(1.15, 1.0, 1.15).add(0.0, 0.04, 0.0));
			}

			this.move(MovementType.field_6308, this.getVelocity());
		}

		Vec3d vec3d = this.getVelocity();
		HitResult hitResult = class_1675.method_18074(
			this,
			this.getBoundingBox().method_18804(vec3d).expand(1.0),
			entity -> !entity.isSpectator() && entity.isValid() && entity.doesCollide(),
			RayTraceContext.ShapeType.field_17558,
			true
		);
		if (!this.noClip) {
			this.method_16828(hitResult);
			this.velocityDirty = true;
		}

		float f = MathHelper.sqrt(squaredHorizontalLength(vec3d));
		this.yaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0F / (float)Math.PI);
		this.pitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * 180.0F / (float)Math.PI);

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
					ParticleTypes.field_11248, this.x, this.y - 0.3, this.z, this.random.nextGaussian() * 0.05, -this.getVelocity().y * 0.5, this.random.nextGaussian() * 0.05
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

			for (LivingEntity livingEntity : this.world.method_18467(LivingEntity.class, this.getBoundingBox().expand(5.0))) {
				if (livingEntity != this.field_7616 && !(this.squaredDistanceTo(livingEntity) > 25.0)) {
					boolean bl = false;

					for (int i = 0; i < 2; i++) {
						Vec3d vec3d2 = new Vec3d(livingEntity.x, livingEntity.y + (double)livingEntity.getHeight() * 0.5 * (double)i, livingEntity.z);
						HitResult hitResult = this.world
							.rayTrace(new RayTraceContext(vec3d, vec3d2, RayTraceContext.ShapeType.field_17558, RayTraceContext.FluidHandling.NONE, this));
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
		return this.dataTracker.get(field_7611).isPresent();
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
			Vec3d vec3d = this.getVelocity();
			this.world.addFireworkParticle(this.x, this.y, this.z, vec3d.x, vec3d.y, vec3d.z, compoundTag);
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

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
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
		this.setVelocity(d, e, f);
	}
}
