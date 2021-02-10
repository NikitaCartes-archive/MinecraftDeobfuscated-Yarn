package net.minecraft.entity.projectile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class ExplosiveProjectileEntity extends ProjectileEntity {
	public double powerX;
	public double powerY;
	public double powerZ;

	protected ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	public ExplosiveProjectileEntity(
		EntityType<? extends ExplosiveProjectileEntity> type, double x, double y, double z, double directionX, double directionY, double directionZ, World world
	) {
		this(type, world);
		this.refreshPositionAndAngles(x, y, z, this.yaw, this.pitch);
		this.refreshPosition();
		double d = (double)MathHelper.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
		if (d != 0.0) {
			this.powerX = directionX / d * 0.1;
			this.powerY = directionY / d * 0.1;
			this.powerZ = directionZ / d * 0.1;
		}
	}

	public ExplosiveProjectileEntity(
		EntityType<? extends ExplosiveProjectileEntity> type, LivingEntity owner, double directionX, double directionY, double directionZ, World world
	) {
		this(type, owner.getX(), owner.getY(), owner.getZ(), directionX, directionY, directionZ, world);
		this.setOwner(owner);
		this.setRotation(owner.yaw, owner.pitch);
	}

	@Override
	protected void initDataTracker() {
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRender(double distance) {
		double d = this.getBoundingBox().getAverageSideLength() * 4.0;
		if (Double.isNaN(d)) {
			d = 4.0;
		}

		d *= 64.0;
		return distance < d * d;
	}

	@Override
	public void tick() {
		Entity entity = this.getOwner();
		if (this.world.isClient || (entity == null || !entity.isRemoved()) && this.world.isChunkLoaded(this.getBlockPos())) {
			super.tick();
			if (this.isBurning()) {
				this.setOnFireFor(1);
			}

			HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
			if (hitResult.getType() != HitResult.Type.MISS) {
				this.onCollision(hitResult);
			}

			this.checkBlockCollision();
			Vec3d vec3d = this.getVelocity();
			double d = this.getX() + vec3d.x;
			double e = this.getY() + vec3d.y;
			double f = this.getZ() + vec3d.z;
			ProjectileUtil.method_7484(this, 0.2F);
			float g = this.getDrag();
			if (this.isTouchingWater()) {
				for (int i = 0; i < 4; i++) {
					float h = 0.25F;
					this.world.addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
				}

				g = 0.8F;
			}

			this.setVelocity(vec3d.add(this.powerX, this.powerY, this.powerZ).multiply((double)g));
			this.world.addParticle(this.getParticleType(), d, e + 0.5, f, 0.0, 0.0, 0.0);
			this.setPosition(d, e, f);
		} else {
			this.discard();
		}
	}

	@Override
	protected boolean canHit(Entity entity) {
		return super.canHit(entity) && !entity.noClip;
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

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.put("power", this.toListTag(new double[]{this.powerX, this.powerY, this.powerZ}));
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		if (tag.contains("power", 9)) {
			ListTag listTag = tag.getList("power", 6);
			if (listTag.size() == 3) {
				this.powerX = listTag.getDouble(0);
				this.powerY = listTag.getDouble(1);
				this.powerZ = listTag.getDouble(2);
			}
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
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			this.scheduleVelocityUpdate();
			Entity entity = source.getAttacker();
			if (entity != null) {
				Vec3d vec3d = entity.getRotationVector();
				this.setVelocity(vec3d);
				this.powerX = vec3d.x * 0.1;
				this.powerY = vec3d.y * 0.1;
				this.powerZ = vec3d.z * 0.1;
				this.setOwner(entity);
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

	@Override
	public Packet<?> createSpawnPacket() {
		Entity entity = this.getOwner();
		int i = entity == null ? 0 : entity.getId();
		return new EntitySpawnS2CPacket(
			this.getId(),
			this.getUuid(),
			this.getX(),
			this.getY(),
			this.getZ(),
			this.pitch,
			this.yaw,
			this.getType(),
			i,
			new Vec3d(this.powerX, this.powerY, this.powerZ)
		);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet) {
		super.onSpawnPacket(packet);
		double d = packet.getVelocityX();
		double e = packet.getVelocityY();
		double f = packet.getVelocityZ();
		double g = (double)MathHelper.sqrt(d * d + e * e + f * f);
		if (g != 0.0) {
			this.powerX = d / g * 0.1;
			this.powerY = e / g * 0.1;
			this.powerZ = f / g * 0.1;
		}
	}
}
