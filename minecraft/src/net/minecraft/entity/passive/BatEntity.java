package net.minecraft.entity.passive;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;
import net.minecraft.class_4051;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BatEntity extends AmbientEntity {
	private static final TrackedData<Byte> BAT_FLAGS = DataTracker.registerData(BatEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final class_4051 field_18100 = new class_4051().method_18418(4.0).method_18421();
	private BlockPos field_6729;

	public BatEntity(EntityType<? extends BatEntity> entityType, World world) {
		super(entityType, world);
		this.setRoosting(true);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(BAT_FLAGS, (byte)0);
	}

	@Override
	protected float getSoundVolume() {
		return 0.1F;
	}

	@Override
	protected float getSoundPitch() {
		return super.getSoundPitch() * 0.95F;
	}

	@Nullable
	@Override
	public SoundEvent getAmbientSound() {
		return this.isRoosting() && this.random.nextInt(4) != 0 ? null : SoundEvents.field_15009;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14746;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14911;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected void pushAway(Entity entity) {
	}

	@Override
	protected void doPushLogic() {
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(6.0);
	}

	public boolean isRoosting() {
		return (this.dataTracker.get(BAT_FLAGS) & 1) != 0;
	}

	public void setRoosting(boolean bl) {
		byte b = this.dataTracker.get(BAT_FLAGS);
		if (bl) {
			this.dataTracker.set(BAT_FLAGS, (byte)(b | 1));
		} else {
			this.dataTracker.set(BAT_FLAGS, (byte)(b & -2));
		}
	}

	@Override
	public void update() {
		super.update();
		if (this.isRoosting()) {
			this.setVelocity(Vec3d.ZERO);
			this.y = (double)MathHelper.floor(this.y) + 1.0 - (double)this.getHeight();
		} else {
			this.setVelocity(this.getVelocity().multiply(1.0, 0.6, 1.0));
		}
	}

	@Override
	protected void mobTick() {
		super.mobTick();
		BlockPos blockPos = new BlockPos(this);
		BlockPos blockPos2 = blockPos.up();
		if (this.isRoosting()) {
			if (this.world.getBlockState(blockPos2).isSimpleFullBlock(this.world, blockPos)) {
				if (this.random.nextInt(200) == 0) {
					this.headYaw = (float)this.random.nextInt(360);
				}

				if (this.world.method_18462(field_18100, this) != null) {
					this.setRoosting(false);
					this.world.playEvent(null, 1025, blockPos, 0);
				}
			} else {
				this.setRoosting(false);
				this.world.playEvent(null, 1025, blockPos, 0);
			}
		} else {
			if (this.field_6729 != null && (!this.world.isAir(this.field_6729) || this.field_6729.getY() < 1)) {
				this.field_6729 = null;
			}

			if (this.field_6729 == null
				|| this.random.nextInt(30) == 0
				|| this.field_6729.squaredDistanceTo((double)((int)this.x), (double)((int)this.y), (double)((int)this.z)) < 4.0) {
				this.field_6729 = new BlockPos(
					(int)this.x + this.random.nextInt(7) - this.random.nextInt(7),
					(int)this.y + this.random.nextInt(6) - 2,
					(int)this.z + this.random.nextInt(7) - this.random.nextInt(7)
				);
			}

			double d = (double)this.field_6729.getX() + 0.5 - this.x;
			double e = (double)this.field_6729.getY() + 0.1 - this.y;
			double f = (double)this.field_6729.getZ() + 0.5 - this.z;
			Vec3d vec3d = this.getVelocity();
			Vec3d vec3d2 = vec3d.add((Math.signum(d) * 0.5 - vec3d.x) * 0.1F, (Math.signum(e) * 0.7F - vec3d.y) * 0.1F, (Math.signum(f) * 0.5 - vec3d.z) * 0.1F);
			this.setVelocity(vec3d2);
			float g = (float)(MathHelper.atan2(vec3d2.z, vec3d2.x) * 180.0F / (float)Math.PI) - 90.0F;
			float h = MathHelper.wrapDegrees(g - this.yaw);
			this.movementInputForward = 0.5F;
			this.yaw += h;
			if (this.random.nextInt(100) == 0 && this.world.getBlockState(blockPos2).isSimpleFullBlock(this.world, blockPos2)) {
				this.setRoosting(true);
			}
		}
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	public void handleFallDamage(float f, float g) {
	}

	@Override
	protected void method_5623(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
	}

	@Override
	public boolean canAvoidTraps() {
		return true;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else {
			if (!this.world.isClient && this.isRoosting()) {
				this.setRoosting(false);
			}

			return super.damage(damageSource, f);
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.dataTracker.set(BAT_FLAGS, compoundTag.getByte("BatFlags"));
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putByte("BatFlags", this.dataTracker.get(BAT_FLAGS));
	}

	@Override
	public boolean canSpawn(IWorld iWorld, SpawnType spawnType) {
		BlockPos blockPos = new BlockPos(this.x, this.getBoundingBox().minY, this.z);
		if (blockPos.getY() >= iWorld.getSeaLevel()) {
			return false;
		} else {
			int i = iWorld.getLightLevel(blockPos);
			int j = 4;
			if (this.method_6451()) {
				j = 7;
			} else if (this.random.nextBoolean()) {
				return false;
			}

			return i > this.random.nextInt(j) ? false : super.canSpawn(iWorld, spawnType);
		}
	}

	private boolean method_6451() {
		LocalDate localDate = LocalDate.now();
		int i = localDate.get(ChronoField.DAY_OF_MONTH);
		int j = localDate.get(ChronoField.MONTH_OF_YEAR);
		return j == 10 && i >= 20 || j == 11 && i <= 3;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntitySize entitySize) {
		return entitySize.height / 2.0F;
	}
}
