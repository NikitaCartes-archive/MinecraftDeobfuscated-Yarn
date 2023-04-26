package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
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
		this.refreshPositionAndAngles(x, y, z, this.getYaw(), this.getPitch());
		this.refreshPosition();
		double d = Math.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
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
		this.setRotation(owner.getYaw(), owner.getPitch());
	}

	@Override
	protected void initDataTracker() {
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

	@Override
	public void tick() {
		Entity entity = this.getOwner();
		if (this.getWorld().isClient || (entity == null || !entity.isRemoved()) && this.getWorld().isChunkLoaded(this.getBlockPos())) {
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
			ProjectileUtil.setRotationFromVelocity(this, 0.2F);
			float g = this.getDrag();
			if (this.isTouchingWater()) {
				for (int i = 0; i < 4; i++) {
					float h = 0.25F;
					this.getWorld().addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
				}

				g = 0.8F;
			}

			this.setVelocity(vec3d.add(this.powerX, this.powerY, this.powerZ).multiply((double)g));
			this.getWorld().addParticle(this.getParticleType(), d, e + 0.5, f, 0.0, 0.0, 0.0);
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
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.put("power", this.toNbtList(new double[]{this.powerX, this.powerY, this.powerZ}));
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("power", NbtElement.LIST_TYPE)) {
			NbtList nbtList = nbt.getList("power", NbtElement.DOUBLE_TYPE);
			if (nbtList.size() == 3) {
				this.powerX = nbtList.getDouble(0);
				this.powerY = nbtList.getDouble(1);
				this.powerZ = nbtList.getDouble(2);
			}
		}
	}

	@Override
	public boolean canHit() {
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
				if (!this.getWorld().isClient) {
					Vec3d vec3d = entity.getRotationVector();
					this.setVelocity(vec3d);
					this.powerX = vec3d.x * 0.1;
					this.powerY = vec3d.y * 0.1;
					this.powerZ = vec3d.z * 0.1;
					this.setOwner(entity);
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

	@Override
	public Packet<ClientPlayPacketListener> createSpawnPacket() {
		Entity entity = this.getOwner();
		int i = entity == null ? 0 : entity.getId();
		return new EntitySpawnS2CPacket(
			this.getId(),
			this.getUuid(),
			this.getX(),
			this.getY(),
			this.getZ(),
			this.getPitch(),
			this.getYaw(),
			this.getType(),
			i,
			new Vec3d(this.powerX, this.powerY, this.powerZ),
			0.0
		);
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet) {
		super.onSpawnPacket(packet);
		double d = packet.getVelocityX();
		double e = packet.getVelocityY();
		double f = packet.getVelocityZ();
		double g = Math.sqrt(d * d + e * e + f * f);
		if (g != 0.0) {
			this.powerX = d / g * 0.1;
			this.powerY = e / g * 0.1;
			this.powerZ = f / g * 0.1;
		}
	}
}
