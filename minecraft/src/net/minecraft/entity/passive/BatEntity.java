package net.minecraft.entity.passive;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;

public class BatEntity extends AmbientEntity {
	public static final float field_30268 = 74.48451F;
	public static final int field_28637 = MathHelper.ceil(2.4166098F);
	/**
	 * The tracked flags of bats. Only has the {@code 1} bit for {@linkplain
	 * #isRoosting() roosting}.
	 */
	private static final TrackedData<Byte> BAT_FLAGS = DataTracker.registerData(BatEntity.class, TrackedDataHandlerRegistry.BYTE);
	private static final int ROOSTING_FLAG = 1;
	private static final TargetPredicate CLOSE_PLAYER_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(4.0);
	@Nullable
	private BlockPos hangingPosition;

	public BatEntity(EntityType<? extends BatEntity> entityType, World world) {
		super(entityType, world);
		if (!world.isClient) {
			this.setRoosting(true);
		}
	}

	@Override
	public boolean canFly() {
		return true;
	}

	@Override
	public boolean isFlappingWings() {
		return !this.isRoosting() && this.age % field_28637 == 0;
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
	public float getSoundPitch() {
		return super.getSoundPitch() * 0.95F;
	}

	@Nullable
	@Override
	public SoundEvent getAmbientSound() {
		return this.isRoosting() && this.random.nextInt(4) != 0 ? null : SoundEvents.ENTITY_BAT_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_BAT_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_BAT_DEATH;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected void pushAway(Entity entity) {
	}

	@Override
	protected void tickCramming() {
	}

	public static DefaultAttributeContainer.Builder createBatAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 6.0);
	}

	/**
	 * Returns whether this bat is hanging upside-down under a block.
	 */
	public boolean isRoosting() {
		return (this.dataTracker.get(BAT_FLAGS) & 1) != 0;
	}

	public void setRoosting(boolean roosting) {
		byte b = this.dataTracker.get(BAT_FLAGS);
		if (roosting) {
			this.dataTracker.set(BAT_FLAGS, (byte)(b | 1));
		} else {
			this.dataTracker.set(BAT_FLAGS, (byte)(b & -2));
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.isRoosting()) {
			this.setVelocity(Vec3d.ZERO);
			this.setPos(this.getX(), (double)MathHelper.floor(this.getY()) + 1.0 - (double)this.getHeight(), this.getZ());
		} else {
			this.setVelocity(this.getVelocity().multiply(1.0, 0.6, 1.0));
		}
	}

	@Override
	protected void mobTick() {
		super.mobTick();
		BlockPos blockPos = this.getBlockPos();
		BlockPos blockPos2 = blockPos.up();
		if (this.isRoosting()) {
			boolean bl = this.isSilent();
			if (this.world.getBlockState(blockPos2).isSolidBlock(this.world, blockPos)) {
				if (this.random.nextInt(200) == 0) {
					this.headYaw = (float)this.random.nextInt(360);
				}

				if (this.world.getClosestPlayer(CLOSE_PLAYER_PREDICATE, this) != null) {
					this.setRoosting(false);
					if (!bl) {
						this.world.syncWorldEvent(null, WorldEvents.BAT_TAKES_OFF, blockPos, 0);
					}
				}
			} else {
				this.setRoosting(false);
				if (!bl) {
					this.world.syncWorldEvent(null, WorldEvents.BAT_TAKES_OFF, blockPos, 0);
				}
			}
		} else {
			if (this.hangingPosition != null && (!this.world.isAir(this.hangingPosition) || this.hangingPosition.getY() <= this.world.getBottomY())) {
				this.hangingPosition = null;
			}

			if (this.hangingPosition == null || this.random.nextInt(30) == 0 || this.hangingPosition.isWithinDistance(this.getPos(), 2.0)) {
				this.hangingPosition = BlockPos.ofFloored(
					this.getX() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7),
					this.getY() + (double)this.random.nextInt(6) - 2.0,
					this.getZ() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7)
				);
			}

			double d = (double)this.hangingPosition.getX() + 0.5 - this.getX();
			double e = (double)this.hangingPosition.getY() + 0.1 - this.getY();
			double f = (double)this.hangingPosition.getZ() + 0.5 - this.getZ();
			Vec3d vec3d = this.getVelocity();
			Vec3d vec3d2 = vec3d.add((Math.signum(d) * 0.5 - vec3d.x) * 0.1F, (Math.signum(e) * 0.7F - vec3d.y) * 0.1F, (Math.signum(f) * 0.5 - vec3d.z) * 0.1F);
			this.setVelocity(vec3d2);
			float g = (float)(MathHelper.atan2(vec3d2.z, vec3d2.x) * 180.0F / (float)Math.PI) - 90.0F;
			float h = MathHelper.wrapDegrees(g - this.getYaw());
			this.forwardSpeed = 0.5F;
			this.setYaw(this.getYaw() + h);
			if (this.random.nextInt(100) == 0 && this.world.getBlockState(blockPos2).isSolidBlock(this.world, blockPos2)) {
				this.setRoosting(true);
			}
		}
	}

	@Override
	protected Entity.MoveEffect getMoveEffect() {
		return Entity.MoveEffect.EVENTS;
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
	}

	@Override
	public boolean canAvoidTraps() {
		return true;
	}

	@Override
	protected boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			if (!this.world.isClient && this.isRoosting()) {
				this.setRoosting(false);
			}

			return super.damage(source, amount);
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.dataTracker.set(BAT_FLAGS, nbt.getByte("BatFlags"));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putByte("BatFlags", this.dataTracker.get(BAT_FLAGS));
	}

	public static boolean canSpawn(EntityType<BatEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		if (pos.getY() >= world.getSeaLevel()) {
			return false;
		} else {
			int i = world.getLightLevel(pos);
			int j = 4;
			if (isTodayAroundHalloween()) {
				j = 7;
			} else if (random.nextBoolean()) {
				return false;
			}

			return i > random.nextInt(j) ? false : canMobSpawn(type, world, spawnReason, pos, random);
		}
	}

	private static boolean isTodayAroundHalloween() {
		LocalDate localDate = LocalDate.now();
		int i = localDate.get(ChronoField.DAY_OF_MONTH);
		int j = localDate.get(ChronoField.MONTH_OF_YEAR);
		return j == 10 && i >= 20 || j == 11 && i <= 3;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return dimensions.height / 2.0F;
	}
}
