package net.minecraft.entity.projectile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public abstract class ExplosiveProjectileEntity extends Entity {
	public LivingEntity owner;
	private int life;
	private int ticks;
	public double posX;
	public double posY;
	public double posZ;

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
		this.posX = g / j * 0.1;
		this.posY = h / j * 0.1;
		this.posZ = i / j * 0.1;
	}

	public ExplosiveProjectileEntity(
		EntityType<? extends ExplosiveProjectileEntity> entityType, LivingEntity livingEntity, double d, double e, double f, World world
	) {
		this(entityType, world);
		this.owner = livingEntity;
		this.setPositionAndAngles(livingEntity.x, livingEntity.y, livingEntity.z, livingEntity.yaw, livingEntity.pitch);
		this.setPosition(this.x, this.y, this.z);
		this.setVelocity(Vec3d.ZERO);
		d += this.random.nextGaussian() * 0.4;
		e += this.random.nextGaussian() * 0.4;
		f += this.random.nextGaussian() * 0.4;
		double g = (double)MathHelper.sqrt(d * d + e * e + f * f);
		this.posX = d / g * 0.1;
		this.posY = e / g * 0.1;
		this.posZ = f / g * 0.1;
	}

	@Override
	protected void initDataTracker() {
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRenderAtDistance(double d) {
		double e = this.getBoundingBox().getAverageSideLength() * 4.0;
		if (Double.isNaN(e)) {
			e = 4.0;
		}

		e *= 64.0;
		return d < e * e;
	}

	@Override
	public void tick() {
		if (this.world.isClient || (this.owner == null || !this.owner.removed) && this.world.isChunkLoaded(new BlockPos(this))) {
			super.tick();
			if (this.isBurning()) {
				this.setOnFireFor(1);
			}

			this.ticks++;
			HitResult hitResult = ProjectileUtil.getCollision(this, true, this.ticks >= 25, this.owner, RayTraceContext.ShapeType.COLLIDER);
			if (hitResult.getType() != HitResult.Type.MISS) {
				this.onCollision(hitResult);
			}

			Vec3d vec3d = this.getVelocity();
			this.x = this.x + vec3d.x;
			this.y = this.y + vec3d.y;
			this.z = this.z + vec3d.z;
			ProjectileUtil.method_7484(this, 0.2F);
			float f = this.getDrag();
			if (this.isInsideWater()) {
				for (int i = 0; i < 4; i++) {
					float g = 0.25F;
					this.world.addParticle(ParticleTypes.BUBBLE, this.x - vec3d.x * 0.25, this.y - vec3d.y * 0.25, this.z - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
				}

				f = 0.8F;
			}

			this.setVelocity(vec3d.add(this.posX, this.posY, this.posZ).multiply((double)f));
			this.world.addParticle(this.getParticleType(), this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
			this.setPosition(this.x, this.y, this.z);
		} else {
			this.remove();
		}
	}

	protected boolean isBurning() {
		return true;
	}

	protected ParticleEffect getParticleType() {
		return ParticleTypes.SMOKE;
	}

	protected float getDrag() {
		return 0.95F;
	}

	protected abstract void onCollision(HitResult hitResult);

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		Vec3d vec3d = this.getVelocity();
		compoundTag.put("direction", this.toListTag(new double[]{vec3d.x, vec3d.y, vec3d.z}));
		compoundTag.put("power", this.toListTag(new double[]{this.posX, this.posY, this.posZ}));
		compoundTag.putInt("life", this.life);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		if (compoundTag.containsKey("power", 9)) {
			ListTag listTag = compoundTag.getList("power", 6);
			if (listTag.size() == 3) {
				this.posX = listTag.getDouble(0);
				this.posY = listTag.getDouble(1);
				this.posZ = listTag.getDouble(2);
			}
		}

		this.life = compoundTag.getInt("life");
		if (compoundTag.containsKey("direction", 9) && compoundTag.getList("direction", 6).size() == 3) {
			ListTag listTag = compoundTag.getList("direction", 6);
			this.setVelocity(listTag.getDouble(0), listTag.getDouble(1), listTag.getDouble(2));
		} else {
			this.remove();
		}
	}

	@Override
	public boolean collides() {
		return true;
	}

	@Override
	public float getTargetingMargin() {
		return 1.0F;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else {
			this.scheduleVelocityUpdate();
			if (damageSource.getAttacker() != null) {
				Vec3d vec3d = damageSource.getAttacker().getRotationVector();
				this.setVelocity(vec3d);
				this.posX = vec3d.x * 0.1;
				this.posY = vec3d.y * 0.1;
				this.posZ = vec3d.z * 0.1;
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
	public float getBrightnessAtEyes() {
		return 1.0F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getLightmapCoordinates() {
		return 15728880;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		int i = this.owner == null ? 0 : this.owner.getEntityId();
		return new EntitySpawnS2CPacket(
			this.getEntityId(), this.getUuid(), this.x, this.y, this.z, this.pitch, this.yaw, this.getType(), i, new Vec3d(this.posX, this.posY, this.posZ)
		);
	}
}
