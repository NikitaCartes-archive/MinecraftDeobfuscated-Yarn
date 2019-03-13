package net.minecraft.entity.projectile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1675;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public abstract class ExplosiveProjectileEntity extends Entity {
	public LivingEntity owner;
	private int field_7603;
	private int field_7602;
	public double field_7601;
	public double field_7600;
	public double field_7599;

	protected ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	public ExplosiveProjectileEntity(
		EntityType<? extends ExplosiveProjectileEntity> entityType, double d, double e, double f, double g, double h, double i, World world
	) {
		this(entityType, world);
		this.setPositionAndAngles(d, e, f, this.yaw, this.pitch);
		this.setPosition(d, e, f);
		double j = (double)MathHelper.sqrt(g * g + h * h + i * i);
		this.field_7601 = g / j * 0.1;
		this.field_7600 = h / j * 0.1;
		this.field_7599 = i / j * 0.1;
	}

	public ExplosiveProjectileEntity(
		EntityType<? extends ExplosiveProjectileEntity> entityType, LivingEntity livingEntity, double d, double e, double f, World world
	) {
		this(entityType, world);
		this.owner = livingEntity;
		this.setPositionAndAngles(livingEntity.x, livingEntity.y, livingEntity.z, livingEntity.yaw, livingEntity.pitch);
		this.setPosition(this.x, this.y, this.z);
		this.method_18799(Vec3d.ZERO);
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
		double e = this.method_5829().averageDimension() * 4.0;
		if (Double.isNaN(e)) {
			e = 4.0;
		}

		e *= 64.0;
		return d < e * e;
	}

	@Override
	public void update() {
		if (this.field_6002.isClient || (this.owner == null || !this.owner.invalid) && this.field_6002.method_8591(new BlockPos(this))) {
			super.update();
			if (this.method_7468()) {
				this.setOnFireFor(1);
			}

			this.field_7602++;
			HitResult hitResult = class_1675.method_18076(this, true, this.field_7602 >= 25, this.owner, RayTraceContext.ShapeType.field_17558);
			if (hitResult.getType() != HitResult.Type.NONE) {
				this.method_7469(hitResult);
			}

			Vec3d vec3d = this.method_18798();
			this.x = this.x + vec3d.x;
			this.y = this.y + vec3d.y;
			this.z = this.z + vec3d.z;
			class_1675.method_7484(this, 0.2F);
			float f = this.method_7466();
			if (this.isInsideWater()) {
				for (int i = 0; i < 4; i++) {
					float g = 0.25F;
					this.field_6002
						.method_8406(ParticleTypes.field_11247, this.x - vec3d.x * 0.25, this.y - vec3d.y * 0.25, this.z - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
				}

				f = 0.8F;
			}

			this.method_18799(vec3d.add(this.field_7601, this.field_7600, this.field_7599).multiply((double)f));
			this.field_6002.method_8406(this.method_7467(), this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
			this.setPosition(this.x, this.y, this.z);
		} else {
			this.invalidate();
		}
	}

	protected boolean method_7468() {
		return true;
	}

	protected ParticleParameters method_7467() {
		return ParticleTypes.field_11251;
	}

	protected float method_7466() {
		return 0.95F;
	}

	protected abstract void method_7469(HitResult hitResult);

	@Override
	public void method_5652(CompoundTag compoundTag) {
		Vec3d vec3d = this.method_18798();
		compoundTag.method_10566("direction", this.method_5846(new double[]{vec3d.x, vec3d.y, vec3d.z}));
		compoundTag.method_10566("power", this.method_5846(new double[]{this.field_7601, this.field_7600, this.field_7599}));
		compoundTag.putInt("life", this.field_7603);
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		if (compoundTag.containsKey("power", 9)) {
			ListTag listTag = compoundTag.method_10554("power", 6);
			if (listTag.size() == 3) {
				this.field_7601 = listTag.getDouble(0);
				this.field_7600 = listTag.getDouble(1);
				this.field_7599 = listTag.getDouble(2);
			}
		}

		this.field_7603 = compoundTag.getInt("life");
		if (compoundTag.containsKey("direction", 9) && compoundTag.method_10554("direction", 6).size() == 3) {
			ListTag listTag = compoundTag.method_10554("direction", 6);
			this.setVelocity(listTag.getDouble(0), listTag.getDouble(1), listTag.getDouble(2));
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
			if (damageSource.method_5529() != null) {
				Vec3d vec3d = damageSource.method_5529().method_5720();
				this.method_18799(vec3d);
				this.field_7601 = vec3d.x * 0.1;
				this.field_7600 = vec3d.y * 0.1;
				this.field_7599 = vec3d.z * 0.1;
				if (damageSource.method_5529() instanceof LivingEntity) {
					this.owner = (LivingEntity)damageSource.method_5529();
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

	@Override
	public Packet<?> method_18002() {
		int i = this.owner == null ? 0 : this.owner.getEntityId();
		return new EntitySpawnS2CPacket(
			this.getEntityId(),
			this.getUuid(),
			this.x,
			this.y,
			this.z,
			this.pitch,
			this.yaw,
			this.method_5864(),
			i,
			new Vec3d(this.field_7601, this.field_7600, this.field_7599)
		);
	}
}
