package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1370;
import net.minecraft.class_1394;
import net.minecraft.class_1399;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class BlazeEntity extends HostileEntity {
	private float field_7214 = 0.5F;
	private int field_7215;
	private static final TrackedData<Byte> field_7216 = DataTracker.registerData(BlazeEntity.class, TrackedDataHandlerRegistry.BYTE);

	public BlazeEntity(EntityType<? extends BlazeEntity> entityType, World world) {
		super(entityType, world);
		this.method_5941(PathNodeType.field_18, -1.0F);
		this.method_5941(PathNodeType.field_14, 8.0F);
		this.method_5941(PathNodeType.field_9, 0.0F);
		this.method_5941(PathNodeType.field_3, 0.0F);
		this.fireImmune = true;
		this.experiencePoints = 10;
	}

	@Override
	protected void initGoals() {
		this.field_6201.add(4, new BlazeEntity.ShootFireballGoal(this));
		this.field_6201.add(5, new class_1370(this, 1.0));
		this.field_6201.add(7, new class_1394(this, 1.0, 0.0F));
		this.field_6201.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.field_6201.add(8, new LookAroundGoal(this));
		this.field_6185.add(1, new class_1399(this).method_6318());
		this.field_6185.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.23F);
		this.method_5996(EntityAttributes.FOLLOW_RANGE).setBaseValue(48.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_7216, (byte)0);
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_14991;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_14842;
	}

	@Override
	protected SoundEvent method_6002() {
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
		if (!this.onGround && this.method_18798().y < 0.0) {
			this.method_18799(this.method_18798().multiply(1.0, 0.6, 1.0));
		}

		if (this.field_6002.isClient) {
			if (this.random.nextInt(24) == 0 && !this.isSilent()) {
				this.field_6002
					.method_8486(
						this.x + 0.5,
						this.y + 0.5,
						this.z + 0.5,
						SoundEvents.field_14734,
						this.method_5634(),
						1.0F + this.random.nextFloat(),
						this.random.nextFloat() * 0.7F + 0.3F,
						false
					);
			}

			for (int i = 0; i < 2; i++) {
				this.field_6002
					.method_8406(
						ParticleTypes.field_11237,
						this.x + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
						this.y + this.random.nextDouble() * (double)this.getHeight(),
						this.z + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
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
		if (this.isTouchingWater()) {
			this.damage(DamageSource.DROWN, 1.0F);
		}

		this.field_7215--;
		if (this.field_7215 <= 0) {
			this.field_7215 = 100;
			this.field_7214 = 0.5F + (float)this.random.nextGaussian() * 3.0F;
		}

		LivingEntity livingEntity = this.getTarget();
		if (livingEntity != null
			&& livingEntity.y + (double)livingEntity.getStandingEyeHeight() > this.y + (double)this.getStandingEyeHeight() + (double)this.field_7214) {
			Vec3d vec3d = this.method_18798();
			this.method_18799(this.method_18798().add(0.0, (0.3F - vec3d.y) * 0.3F, 0.0));
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
		return (this.field_6011.get(field_7216) & 1) != 0;
	}

	public void setFireActive(boolean bl) {
		byte b = this.field_6011.get(field_7216);
		if (bl) {
			b = (byte)(b | 1);
		} else {
			b = (byte)(b & -2);
		}

		this.field_6011.set(field_7216, b);
	}

	@Override
	protected boolean checkLightLevelForSpawn() {
		return true;
	}

	static class ShootFireballGoal extends Goal {
		private final BlazeEntity field_7219;
		private int field_7218;
		private int field_7217;

		public ShootFireballGoal(BlazeEntity blazeEntity) {
			this.field_7219 = blazeEntity;
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18405, Goal.class_4134.field_18406));
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
					this.field_7219.attack(livingEntity);
				}

				this.field_7219.method_5962().moveTo(livingEntity.x, livingEntity.y, livingEntity.z, 1.0);
			} else if (d < this.method_6995() * this.method_6995()) {
				double e = livingEntity.x - this.field_7219.x;
				double f = livingEntity.method_5829().minY + (double)(livingEntity.getHeight() / 2.0F) - (this.field_7219.y + (double)(this.field_7219.getHeight() / 2.0F));
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
						this.field_7219.field_6002.method_8444(null, 1018, new BlockPos((int)this.field_7219.x, (int)this.field_7219.y, (int)this.field_7219.z), 0);

						for (int i = 0; i < 1; i++) {
							SmallFireballEntity smallFireballEntity = new SmallFireballEntity(
								this.field_7219.field_6002,
								this.field_7219,
								e + this.field_7219.getRand().nextGaussian() * (double)h,
								f,
								g + this.field_7219.getRand().nextGaussian() * (double)h
							);
							smallFireballEntity.y = this.field_7219.y + (double)(this.field_7219.getHeight() / 2.0F) + 0.5;
							this.field_7219.field_6002.spawnEntity(smallFireballEntity);
						}
					}
				}

				this.field_7219.method_5988().lookAt(livingEntity, 10.0F, 10.0F);
			} else {
				this.field_7219.method_5942().stop();
				this.field_7219.method_5962().moveTo(livingEntity.x, livingEntity.y, livingEntity.z, 1.0);
			}

			super.tick();
		}

		private double method_6995() {
			EntityAttributeInstance entityAttributeInstance = this.field_7219.method_5996(EntityAttributes.FOLLOW_RANGE);
			return entityAttributeInstance == null ? 16.0 : entityAttributeInstance.getValue();
		}
	}
}
