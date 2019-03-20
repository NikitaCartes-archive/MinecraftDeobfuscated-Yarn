package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class PrimedTntEntity extends Entity {
	private static final TrackedData<Integer> FUSE = DataTracker.registerData(PrimedTntEntity.class, TrackedDataHandlerRegistry.INTEGER);
	@Nullable
	private LivingEntity causingEntity;
	private int fuseTimer = 80;

	public PrimedTntEntity(EntityType<? extends PrimedTntEntity> entityType, World world) {
		super(entityType, world);
		this.field_6033 = true;
		this.fireImmune = true;
	}

	public PrimedTntEntity(World world, double d, double e, double f, @Nullable LivingEntity livingEntity) {
		this(EntityType.TNT, world);
		this.setPosition(d, e, f);
		double g = world.random.nextDouble() * (float) (Math.PI * 2);
		this.setVelocity(-Math.sin(g) * 0.02, 0.2F, -Math.cos(g) * 0.02);
		this.setFuse(80);
		this.prevX = d;
		this.prevY = e;
		this.prevZ = f;
		this.causingEntity = livingEntity;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(FUSE, 80);
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	public boolean doesCollide() {
		return !this.invalid;
	}

	@Override
	public void tick() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		if (!this.isUnaffectedByGravity()) {
			this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
		}

		this.move(MovementType.field_6308, this.getVelocity());
		this.setVelocity(this.getVelocity().multiply(0.98));
		if (this.onGround) {
			this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
		}

		this.fuseTimer--;
		if (this.fuseTimer <= 0) {
			this.invalidate();
			if (!this.world.isClient) {
				this.explode();
			}
		} else {
			this.method_5713();
			this.world.addParticle(ParticleTypes.field_11251, this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
		}
	}

	private void explode() {
		float f = 4.0F;
		this.world.createExplosion(this, this.x, this.y + (double)(this.getHeight() / 16.0F), this.z, 4.0F, Explosion.class_4179.field_18686);
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
		compoundTag.putShort("Fuse", (short)this.getFuseTimer());
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag compoundTag) {
		this.setFuse(compoundTag.getShort("Fuse"));
	}

	@Nullable
	public LivingEntity getCausingEntity() {
		return this.causingEntity;
	}

	@Override
	protected float getEyeHeight(EntityPose entityPose, EntitySize entitySize) {
		return 0.0F;
	}

	public void setFuse(int i) {
		this.dataTracker.set(FUSE, i);
		this.fuseTimer = i;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		if (FUSE.equals(trackedData)) {
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
