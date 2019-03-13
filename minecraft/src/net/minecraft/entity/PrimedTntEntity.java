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
	private static final TrackedData<Integer> field_7197 = DataTracker.registerData(PrimedTntEntity.class, TrackedDataHandlerRegistry.INTEGER);
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
		this.field_6011.startTracking(field_7197, 80);
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
	public void update() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		if (!this.isUnaffectedByGravity()) {
			this.method_18799(this.method_18798().add(0.0, -0.04, 0.0));
		}

		this.method_5784(MovementType.field_6308, this.method_18798());
		this.method_18799(this.method_18798().multiply(0.98));
		if (this.onGround) {
			this.method_18799(this.method_18798().multiply(0.7, -0.5, 0.7));
		}

		this.fuseTimer--;
		if (this.fuseTimer <= 0) {
			this.invalidate();
			if (!this.field_6002.isClient) {
				this.explode();
			}
		} else {
			this.method_5713();
			this.field_6002.method_8406(ParticleTypes.field_11251, this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
		}
	}

	private void explode() {
		float f = 4.0F;
		this.field_6002.createExplosion(this, this.x, this.y + (double)(this.getHeight() / 16.0F), this.z, 4.0F, Explosion.class_4179.field_18686);
	}

	@Override
	protected void method_5652(CompoundTag compoundTag) {
		compoundTag.putShort("Fuse", (short)this.getFuseTimer());
	}

	@Override
	protected void method_5749(CompoundTag compoundTag) {
		this.setFuse(compoundTag.getShort("Fuse"));
	}

	@Nullable
	public LivingEntity getCausingEntity() {
		return this.causingEntity;
	}

	@Override
	protected float method_18378(EntityPose entityPose, EntitySize entitySize) {
		return 0.0F;
	}

	public void setFuse(int i) {
		this.field_6011.set(field_7197, i);
		this.fuseTimer = i;
	}

	@Override
	public void method_5674(TrackedData<?> trackedData) {
		if (field_7197.equals(trackedData)) {
			this.fuseTimer = this.getFuse();
		}
	}

	public int getFuse() {
		return this.field_6011.get(field_7197);
	}

	public int getFuseTimer() {
		return this.fuseTimer;
	}

	@Override
	public Packet<?> method_18002() {
		return new EntitySpawnS2CPacket(this);
	}
}
