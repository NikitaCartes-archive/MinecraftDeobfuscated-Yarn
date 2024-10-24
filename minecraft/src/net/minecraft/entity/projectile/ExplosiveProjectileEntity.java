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
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public abstract class ExplosiveProjectileEntity extends ProjectileEntity {
	public static final double field_51891 = 0.1;
	public static final double field_51892 = 0.5;
	public double accelerationPower = 0.1;

	protected ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	protected ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> type, double x, double y, double z, World world) {
		this(type, world);
		this.setPosition(x, y, z);
	}

	public ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> type, double x, double y, double z, Vec3d velocity, World world) {
		this(type, world);
		this.refreshPositionAndAngles(x, y, z, this.getYaw(), this.getPitch());
		this.refreshPosition();
		this.setVelocityWithAcceleration(velocity, this.accelerationPower);
	}

	public ExplosiveProjectileEntity(EntityType<? extends ExplosiveProjectileEntity> type, LivingEntity owner, Vec3d velocity, World world) {
		this(type, owner.getX(), owner.getY(), owner.getZ(), velocity, world);
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
		this.applyDrag();
		if (this.getWorld().isClient || (entity == null || !entity.isRemoved()) && this.getWorld().isChunkLoaded(this.getBlockPos())) {
			HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit, this.getRaycastShapeType());
			Vec3d vec3d;
			if (hitResult.getType() != HitResult.Type.MISS) {
				vec3d = hitResult.getPos();
			} else {
				vec3d = this.getPos().add(this.getVelocity());
			}

			ProjectileUtil.setRotationFromVelocity(this, 0.2F);
			this.setPosition(vec3d);
			this.tickBlockCollision();
			super.tick();
			if (this.isBurning()) {
				this.setOnFireFor(1.0F);
			}

			if (hitResult.getType() != HitResult.Type.MISS && this.isAlive()) {
				this.hitOrDeflect(hitResult);
			}

			this.addParticles();
		} else {
			this.discard();
		}
	}

	private void applyDrag() {
		Vec3d vec3d = this.getVelocity();
		Vec3d vec3d2 = this.getPos();
		float g;
		if (this.isTouchingWater()) {
			for (int i = 0; i < 4; i++) {
				float f = 0.25F;
				this.getWorld()
					.addParticle(ParticleTypes.BUBBLE, vec3d2.x - vec3d.x * 0.25, vec3d2.y - vec3d.y * 0.25, vec3d2.z - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
			}

			g = this.getDragInWater();
		} else {
			g = this.getDrag();
		}

		this.setVelocity(vec3d.add(vec3d.normalize().multiply(this.accelerationPower)).multiply((double)g));
	}

	private void addParticles() {
		ParticleEffect particleEffect = this.getParticleType();
		Vec3d vec3d = this.getPos();
		if (particleEffect != null) {
			this.getWorld().addParticle(particleEffect, vec3d.x, vec3d.y + 0.5, vec3d.z, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount) {
		return false;
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
		nbt.putDouble("acceleration_power", this.accelerationPower);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("acceleration_power", NbtElement.DOUBLE_TYPE)) {
			this.accelerationPower = nbt.getDouble("acceleration_power");
		}
	}

	@Override
	public float getBrightnessAtEyes() {
		return 1.0F;
	}

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket(EntityTrackerEntry entityTrackerEntry) {
		Entity entity = this.getOwner();
		int i = entity == null ? 0 : entity.getId();
		Vec3d vec3d = entityTrackerEntry.getPos();
		return new EntitySpawnS2CPacket(
			this.getId(),
			this.getUuid(),
			vec3d.getX(),
			vec3d.getY(),
			vec3d.getZ(),
			entityTrackerEntry.getPitch(),
			entityTrackerEntry.getYaw(),
			this.getType(),
			i,
			entityTrackerEntry.getVelocity(),
			0.0
		);
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet) {
		super.onSpawnPacket(packet);
		Vec3d vec3d = new Vec3d(packet.getVelocityX(), packet.getVelocityY(), packet.getVelocityZ());
		this.setVelocity(vec3d);
	}

	private void setVelocityWithAcceleration(Vec3d velocity, double accelerationPower) {
		this.setVelocity(velocity.normalize().multiply(accelerationPower));
		this.velocityDirty = true;
	}

	@Override
	protected void onDeflected(@Nullable Entity deflector, boolean fromAttack) {
		super.onDeflected(deflector, fromAttack);
		if (fromAttack) {
			this.accelerationPower = 0.1;
		} else {
			this.accelerationPower *= 0.5;
		}
	}
}
