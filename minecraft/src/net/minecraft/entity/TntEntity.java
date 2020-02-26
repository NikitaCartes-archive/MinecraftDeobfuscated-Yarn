package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class TntEntity extends Entity {
	private static final TrackedData<Integer> FUSE = DataTracker.registerData(TntEntity.class, TrackedDataHandlerRegistry.INTEGER);
	@Nullable
	private LivingEntity causingEntity;
	private int fuseTimer = 80;

	public TntEntity(EntityType<? extends TntEntity> entityType, World world) {
		super(entityType, world);
		this.inanimate = true;
	}

	public TntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter) {
		this(EntityType.TNT, world);
		this.updatePosition(x, y, z);
		double d = world.random.nextDouble() * (float) (Math.PI * 2);
		this.setVelocity(-Math.sin(d) * 0.02, 0.2F, -Math.cos(d) * 0.02);
		this.setFuse(80);
		this.prevX = x;
		this.prevY = y;
		this.prevZ = z;
		this.causingEntity = igniter;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(FUSE, 80);
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	public boolean collides() {
		return !this.removed;
	}

	@Override
	public void tick() {
		if (!this.hasNoGravity()) {
			this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
		}

		this.move(MovementType.SELF, this.getVelocity());
		this.setVelocity(this.getVelocity().multiply(0.98));
		if (this.onGround) {
			this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
		}

		this.fuseTimer--;
		if (this.fuseTimer <= 0) {
			this.remove();
			if (!this.world.isClient) {
				this.explode();
			}
		} else {
			this.updateWaterState();
			if (this.world.isClient) {
				this.world.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
			}
		}
	}

	private void explode() {
		float f = 4.0F;
		this.world.createExplosion(this, this.getX(), this.getBodyY(0.0625), this.getZ(), 4.0F, Explosion.DestructionType.BREAK);
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag tag) {
		tag.putShort("Fuse", (short)this.getFuseTimer());
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag tag) {
		this.setFuse(tag.getShort("Fuse"));
	}

	@Nullable
	public LivingEntity getCausingEntity() {
		return this.causingEntity;
	}

	@Override
	protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return 0.15F;
	}

	public void setFuse(int fuse) {
		this.dataTracker.set(FUSE, fuse);
		this.fuseTimer = fuse;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (FUSE.equals(data)) {
			this.fuseTimer = this.getFuse();
		}
	}

	public int getFuse() {
		return this.dataTracker.get(FUSE);
	}

	public int getFuseTimer() {
		return this.fuseTimer;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}
}
