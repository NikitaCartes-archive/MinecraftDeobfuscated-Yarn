package net.minecraft.entity.mob;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1361;
import net.minecraft.class_1370;
import net.minecraft.class_1376;
import net.minecraft.class_1394;
import net.minecraft.class_1399;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BlazeEntity extends HostileEntity {
	private float field_7214 = 0.5F;
	private int field_7215;
	private static final TrackedData<Byte> BLAZE_FLAGS = DataTracker.registerData(BlazeEntity.class, TrackedDataHandlerRegistry.BYTE);

	public BlazeEntity(World world) {
		super(EntityType.BLAZE, world);
		this.setPathNodeTypeWeight(PathNodeType.WATER, -1.0F);
		this.setPathNodeTypeWeight(PathNodeType.LAVA, 8.0F);
		this.setPathNodeTypeWeight(PathNodeType.FIRE_NEAR, 0.0F);
		this.setPathNodeTypeWeight(PathNodeType.FIRE, 0.0F);
		this.fireImmune = true;
		this.experiencePoints = 10;
	}

	@Override
	protected void method_5959() {
		this.goalSelector.add(4, new BlazeEntity.ShootFireballGoal(this));
		this.goalSelector.add(5, new class_1370(this, 1.0));
		this.goalSelector.add(7, new class_1394(this, 1.0, 0.0F));
		this.goalSelector.add(8, new class_1361(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(8, new class_1376(this));
		this.targetSelector.add(1, new class_1399(this).method_6318());
		this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.23F);
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(48.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(BLAZE_FLAGS, (byte)0);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14991;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14842;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14580;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getLightmapCoordinates() {
		return 15728880;
	}

	@Override
	public float method_5718() {
		return 1.0F;
	}

	@Override
	public void updateMovement() {
		if (!this.onGround && this.velocityY < 0.0) {
			this.velocityY *= 0.6;
		}

		if (this.world.isRemote) {
			if (this.random.nextInt(24) == 0 && !this.isSilent()) {
				this.world
					.playSound(
						this.x + 0.5,
						this.y + 0.5,
						this.z + 0.5,
						SoundEvents.field_14734,
						this.getSoundCategory(),
						1.0F + this.random.nextFloat(),
						this.random.nextFloat() * 0.7F + 0.3F,
						false
					);
			}

			for (int i = 0; i < 2; i++) {
				this.world
					.method_8406(
						ParticleTypes.field_11237,
						this.x + (this.random.nextDouble() - 0.5) * (double)this.width,
						this.y + this.random.nextDouble() * (double)this.height,
						this.z + (this.random.nextDouble() - 0.5) * (double)this.width,
						0.0,
						0.0,
						0.0
					);
			}
		}

		super.updateMovement();
	}

	@Override
	protected void mobTick() {
		if (this.method_5637()) {
			this.damage(DamageSource.DROWN, 1.0F);
		}

		this.field_7215--;
		if (this.field_7215 <= 0) {
			this.field_7215 = 100;
			this.field_7214 = 0.5F + (float)this.random.nextGaussian() * 3.0F;
		}

		LivingEntity livingEntity = this.getTarget();
		if (livingEntity != null && livingEntity.y + (double)livingEntity.getEyeHeight() > this.y + (double)this.getEyeHeight() + (double)this.field_7214) {
			this.velocityY = this.velocityY + (0.3F - this.velocityY) * 0.3F;
			this.velocityDirty = true;
		}

		super.mobTick();
	}

	@Override
	public void handleFallDamage(float f, float g) {
	}

	@Override
	public boolean isOnFire() {
		return this.isFireActive();
	}

	public boolean isFireActive() {
		return (this.dataTracker.get(BLAZE_FLAGS) & 1) != 0;
	}

	public void setFireActive(boolean bl) {
		byte b = this.dataTracker.get(BLAZE_FLAGS);
		if (bl) {
			b = (byte)(b | 1);
		} else {
			b = (byte)(b & -2);
		}

		this.dataTracker.set(BLAZE_FLAGS, b);
	}

	@Override
	protected boolean method_7075() {
		return true;
	}

	static class ShootFireballGoal extends Goal {
		private final BlazeEntity field_7219;
		private int field_7218;
		private int field_7217;

		public ShootFireballGoal(BlazeEntity blazeEntity) {
			this.field_7219 = blazeEntity;
			this.setControlBits(3);
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = this.field_7219.getTarget();
			return livingEntity != null && livingEntity.isValid();
		}

		@Override
		public void start() {
			this.field_7218 = 0;
		}

		@Override
		public void onRemove() {
			this.field_7219.setFireActive(false);
		}

		@Override
		public void tick() {
			this.field_7217--;
			LivingEntity livingEntity = this.field_7219.getTarget();
			double d = this.field_7219.squaredDistanceTo(livingEntity);
			if (d < 4.0) {
				if (this.field_7217 <= 0) {
					this.field_7217 = 20;
					this.field_7219.method_6121(livingEntity);
				}

				this.field_7219.getMoveControl().method_6239(livingEntity.x, livingEntity.y, livingEntity.z, 1.0);
			} else if (d < this.method_6995() * this.method_6995()) {
				double e = livingEntity.x - this.field_7219.x;
				double f = livingEntity.getBoundingBox().minY + (double)(livingEntity.height / 2.0F) - (this.field_7219.y + (double)(this.field_7219.height / 2.0F));
				double g = livingEntity.z - this.field_7219.z;
				if (this.field_7217 <= 0) {
					this.field_7218++;
					if (this.field_7218 == 1) {
						this.field_7217 = 60;
						this.field_7219.setFireActive(true);
					} else if (this.field_7218 <= 4) {
						this.field_7217 = 6;
					} else {
						this.field_7217 = 100;
						this.field_7218 = 0;
						this.field_7219.setFireActive(false);
					}

					if (this.field_7218 > 1) {
						float h = MathHelper.sqrt(MathHelper.sqrt(d)) * 0.5F;
						this.field_7219.world.fireWorldEvent(null, 1018, new BlockPos((int)this.field_7219.x, (int)this.field_7219.y, (int)this.field_7219.z), 0);

						for (int i = 0; i < 1; i++) {
							SmallFireballEntity smallFireballEntity = new SmallFireballEntity(
								this.field_7219.world,
								this.field_7219,
								e + this.field_7219.getRand().nextGaussian() * (double)h,
								f,
								g + this.field_7219.getRand().nextGaussian() * (double)h
							);
							smallFireballEntity.y = this.field_7219.y + (double)(this.field_7219.height / 2.0F) + 0.5;
							this.field_7219.world.spawnEntity(smallFireballEntity);
						}
					}
				}

				this.field_7219.getLookControl().lookAt(livingEntity, 10.0F, 10.0F);
			} else {
				this.field_7219.getNavigation().method_6340();
				this.field_7219.getMoveControl().method_6239(livingEntity.x, livingEntity.y, livingEntity.z, 1.0);
			}

			super.tick();
		}

		private double method_6995() {
			EntityAttributeInstance entityAttributeInstance = this.field_7219.getAttributeInstance(EntityAttributes.FOLLOW_RANGE);
			return entityAttributeInstance == null ? 16.0 : entityAttributeInstance.getValue();
		}
	}
}
