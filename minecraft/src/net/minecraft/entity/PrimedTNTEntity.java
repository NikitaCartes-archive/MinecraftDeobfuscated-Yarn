package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

public class PrimedTNTEntity extends Entity {
	private static final TrackedData<Integer> FUSE = DataTracker.registerData(PrimedTNTEntity.class, TrackedDataHandlerRegistry.INTEGER);
	@Nullable
	private LivingEntity causingEntity;
	private int fuseTimer = 80;

	public PrimedTNTEntity(World world) {
		super(EntityType.TNT, world);
		this.field_6033 = true;
		this.fireImmune = true;
		this.setSize(0.98F, 0.98F);
	}

	public PrimedTNTEntity(World world, double d, double e, double f, @Nullable LivingEntity livingEntity) {
		this(world);
		this.setPosition(d, e, f);
		float g = (float)(Math.random() * (float) (Math.PI * 2));
		this.velocityX = (double)(-((float)Math.sin((double)g)) * 0.02F);
		this.velocityY = 0.2F;
		this.velocityZ = (double)(-((float)Math.cos((double)g)) * 0.02F);
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
	public void update() {
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		if (!this.isUnaffectedByGravity()) {
			this.velocityY -= 0.04F;
		}

		this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
		this.velocityX *= 0.98F;
		this.velocityY *= 0.98F;
		this.velocityZ *= 0.98F;
		if (this.onGround) {
			this.velocityX *= 0.7F;
			this.velocityZ *= 0.7F;
			this.velocityY *= -0.5;
		}

		this.fuseTimer--;
		if (this.fuseTimer <= 0) {
			this.invalidate();
			if (!this.world.isRemote) {
				this.explode();
			}
		} else {
			this.method_5713();
			this.world.method_8406(ParticleTypes.field_11251, this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
		}
	}

	private void explode() {
		float f = 4.0F;
		this.world.createExplosion(this, this.x, this.y + (double)(this.height / 16.0F), this.z, 4.0F, true);
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
	public float getEyeHeight() {
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
}
