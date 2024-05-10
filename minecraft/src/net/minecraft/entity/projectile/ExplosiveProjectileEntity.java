package net.minecraft.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public abstract class ExplosiveProjectileEntity extends ProjectileEntity {
	public static final double field_51891 = 0.1;
	public static final double field_51892 = 0.5;
	public double field_51893 = 0.1;

	protected ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	protected ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> type, double x, double y, double z, World world) {
		this(type, world);
		this.setPosition(x, y, z);
	}

	public ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> type, double x, double y, double z, Vec3d vec3d, World world) {
		this(type, world);
		this.refreshPositionAndAngles(x, y, z, this.getYaw(), this.getPitch());
		this.refreshPosition();
		this.method_60499(vec3d, this.field_51893);
	}

	public ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> type, LivingEntity owner, Vec3d vec3d, World world) {
		this(type, owner.getX(), owner.getY(), owner.getZ(), vec3d, world);
		this.setOwner(owner);
		this.setRotation(owner.getYaw(), owner.getPitch());
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
	}

	@Override
	public boolean shouldRender(double distance) {
		double d = this.getBoundingBox().getAverageSideLength() * 4.0;
		if (Double.isNaN(d)) {
			d = 4.0;
		}

		d *= 64.0;
		return distance < d * d;
	}

	protected RaycastContext.ShapeType getRaycastShapeType() {
		return RaycastContext.ShapeType.COLLIDER;
	}

	@Override
	public void tick() {
		Entity entity = this.getOwner();
		if (this.getWorld().isClient || (entity == null || !entity.isRemoved()) && this.getWorld().isChunkLoaded(this.getBlockPos())) {
			super.tick();
			if (this.isBurning()) {
				this.setOnFireFor(1.0F);
			}

			HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit, this.getRaycastShapeType());
			if (hitResult.getType() != HitResult.Type.MISS) {
				this.hitOrDeflect(hitResult);
			}

			this.checkBlockCollision();
			Vec3d vec3d = this.getVelocity();
			double d = this.getX() + vec3d.x;
			double e = this.getY() + vec3d.y;
			double f = this.getZ() + vec3d.z;
			ProjectileUtil.setRotationFromVelocity(this, 0.2F);
			float h;
			if (this.isTouchingWater()) {
				for (int i = 0; i < 4; i++) {
					float g = 0.25F;
					this.getWorld().addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
				}

				h = this.getDragInWater();
			} else {
				h = this.getDrag();
			}

			this.setVelocity(vec3d.add(vec3d.normalize().multiply(this.field_51893)).multiply((double)h));
			ParticleEffect particleEffect = this.getParticleType();
			if (particleEffect != null) {
				this.getWorld().addParticle(particleEffect, d, e + 0.5, f, 0.0, 0.0, 0.0);
			}

			this.setPosition(d, e, f);
		} else {
			this.discard();
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return !this.isInvulnerableTo(source);
	}

	@Override
	protected boolean canHit(Entity entity) {
		return super.canHit(entity) && !entity.noClip;
	}

	protected boolean isBurning() {
		return true;
	}

	@Nullable
	protected ParticleEffect getParticleType() {
		return ParticleTypes.SMOKE;
	}

	protected float getDrag() {
		return 0.95F;
	}

	protected float getDragInWater() {
		return 0.8F;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putDouble("acceleration_power", this.field_51893);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("acceleration_power", NbtElement.DOUBLE_TYPE)) {
			this.field_51893 = nbt.getDouble("acceleration_power");
		}
	}

	@Override
	public float getBrightnessAtEyes() {
		return 1.0F;
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket() {
		Entity entity = this.getOwner();
		int i = entity == null ? 0 : entity.getId();
		return new EntitySpawnS2CPacket(
			this.getId(), this.getUuid(), this.getX(), this.getY(), this.getZ(), this.getPitch(), this.getYaw(), this.getType(), i, this.getVelocity(), 0.0
		);
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet) {
		super.onSpawnPacket(packet);
		Vec3d vec3d = new Vec3d(packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityZ());
		this.setVelocity(vec3d);
	}

	private void method_60499(Vec3d vec3d, double d) {
		this.setVelocity(vec3d.normalize().multiply(d));
		this.velocityDirty = true;
	}

	@Override
	protected void onDeflected(@Nullable Entity deflector, boolean fromAttack) {
		super.onDeflected(deflector, fromAttack);
		if (fromAttack) {
			this.field_51893 = 0.1;
		} else {
			this.field_51893 *= 0.5;
		}
	}
}
