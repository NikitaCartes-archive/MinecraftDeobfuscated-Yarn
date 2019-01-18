package net.minecraft.entity.projectile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1675;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class ExplosiveProjectileEntity extends Entity {
	public LivingEntity owner;
	private int field_7603;
	private int field_7602;
	public double field_7601;
	public double field_7600;
	public double field_7599;

	protected ExplosiveProjectileEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
	}

	public ExplosiveProjectileEntity(EntityType<?> entityType, double d, double e, double f, double g, double h, double i, World world) {
		this(entityType, world);
		this.setPositionAndAngles(d, e, f, this.yaw, this.pitch);
		this.setPosition(d, e, f);
		double j = (double)MathHelper.sqrt(g * g + h * h + i * i);
		this.field_7601 = g / j * 0.1;
		this.field_7600 = h / j * 0.1;
		this.field_7599 = i / j * 0.1;
	}

	public ExplosiveProjectileEntity(EntityType<?> entityType, LivingEntity livingEntity, double d, double e, double f, World world) {
		this(entityType, world);
		this.owner = livingEntity;
		this.setPositionAndAngles(livingEntity.x, livingEntity.y, livingEntity.z, livingEntity.yaw, livingEntity.pitch);
		this.setPosition(this.x, this.y, this.z);
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
		d += this.random.nextGaussian() * 0.4;
		e += this.random.nextGaussian() * 0.4;
		f += this.random.nextGaussian() * 0.4;
		double g = (double)MathHelper.sqrt(d * d + e * e + f * f);
		this.field_7601 = d / g * 0.1;
		this.field_7600 = e / g * 0.1;
		this.field_7599 = f / g * 0.1;
	}

	@Override
	protected void initDataTracker() {
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderAtDistance(double d) {
		double e = this.getBoundingBox().averageDimension() * 4.0;
		if (Double.isNaN(e)) {
			e = 4.0;
		}

		e *= 64.0;
		return d < e * e;
	}

	@Override
	public void update() {
		if (this.world.isClient || (this.owner == null || !this.owner.invalid) && this.world.isBlockLoaded(new BlockPos(this))) {
			super.update();
			if (this.method_7468()) {
				this.setOnFireFor(1);
			}

			this.field_7602++;
			HitResult hitResult = class_1675.method_7482(this, true, this.field_7602 >= 25, this.owner);
			if (hitResult.getType() != HitResult.Type.NONE) {
				this.method_7469(hitResult);
			}

			this.x = this.x + this.velocityX;
			this.y = this.y + this.velocityY;
			this.z = this.z + this.velocityZ;
			class_1675.method_7484(this, 0.2F);
			float f = this.method_7466();
			if (this.isInsideWater()) {
				for (int i = 0; i < 4; i++) {
					float g = 0.25F;
					this.world
						.addParticle(
							ParticleTypes.field_11247,
							this.x - this.velocityX * 0.25,
							this.y - this.velocityY * 0.25,
							this.z - this.velocityZ * 0.25,
							this.velocityX,
							this.velocityY,
							this.velocityZ
						);
				}

				f = 0.8F;
			}

			this.velocityX = this.velocityX + this.field_7601;
			this.velocityY = this.velocityY + this.field_7600;
			this.velocityZ = this.velocityZ + this.field_7599;
			this.velocityX *= (double)f;
			this.velocityY *= (double)f;
			this.velocityZ *= (double)f;
			this.world.addParticle(this.getParticleType(), this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
			this.setPosition(this.x, this.y, this.z);
		} else {
			this.invalidate();
		}
	}

	protected boolean method_7468() {
		return true;
	}

	protected ParticleParameters getParticleType() {
		return ParticleTypes.field_11251;
	}

	protected float method_7466() {
		return 0.95F;
	}

	protected abstract void method_7469(HitResult hitResult);

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		compoundTag.put("direction", this.toListTag(new double[]{this.velocityX, this.velocityY, this.velocityZ}));
		compoundTag.put("power", this.toListTag(new double[]{this.field_7601, this.field_7600, this.field_7599}));
		compoundTag.putInt("life", this.field_7603);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		if (compoundTag.containsKey("power", 9)) {
			ListTag listTag = compoundTag.getList("power", 6);
			if (listTag.size() == 3) {
				this.field_7601 = listTag.getDouble(0);
				this.field_7600 = listTag.getDouble(1);
				this.field_7599 = listTag.getDouble(2);
			}
		}

		this.field_7603 = compoundTag.getInt("life");
		if (compoundTag.containsKey("direction", 9) && compoundTag.getList("direction", 6).size() == 3) {
			ListTag listTag = compoundTag.getList("direction", 6);
			this.velocityX = listTag.getDouble(0);
			this.velocityY = listTag.getDouble(1);
			this.velocityZ = listTag.getDouble(2);
		} else {
			this.invalidate();
		}
	}

	@Override
	public boolean doesCollide() {
		return true;
	}

	@Override
	public float getBoundingBoxMarginForTargeting() {
		return 1.0F;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else {
			this.scheduleVelocityUpdate();
			if (damageSource.getAttacker() != null) {
				Vec3d vec3d = damageSource.getAttacker().method_5720();
				if (vec3d != null) {
					this.velocityX = vec3d.x;
					this.velocityY = vec3d.y;
					this.velocityZ = vec3d.z;
					this.field_7601 = this.velocityX * 0.1;
					this.field_7600 = this.velocityY * 0.1;
					this.field_7599 = this.velocityZ * 0.1;
				}

				if (damageSource.getAttacker() instanceof LivingEntity) {
					this.owner = (LivingEntity)damageSource.getAttacker();
				}

				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public float method_5718() {
		return 1.0F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getLightmapCoordinates() {
		return 15728880;
	}
}
