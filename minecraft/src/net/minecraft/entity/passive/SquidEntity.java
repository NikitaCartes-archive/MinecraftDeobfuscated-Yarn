package net.minecraft.entity.passive;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class SquidEntity extends WaterCreatureEntity {
	public float field_6907;
	public float field_6905;
	public float field_6903;
	public float field_6906;
	public float field_6908;
	public float field_6902;
	public float field_6904;
	public float field_6900;
	private float constantVelocityRate;
	private float field_6912;
	private float field_6913;
	private float constantVelocityX;
	private float constantVelocityY;
	private float constantVelocityZ;

	public SquidEntity(EntityType<? extends SquidEntity> entityType, World world) {
		super(entityType, world);
		this.random.setSeed((long)this.getEntityId());
		this.field_6912 = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SquidEntity.SwimGoal(this));
		this.goalSelector.add(1, new SquidEntity.EscapeAttackerGoal());
	}

	public static DefaultAttributeContainer.Builder createSquidAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return dimensions.height * 0.5F;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_SQUID_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_SQUID_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SQUID_DEATH;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		this.field_6905 = this.field_6907;
		this.field_6906 = this.field_6903;
		this.field_6902 = this.field_6908;
		this.field_6900 = this.field_6904;
		this.field_6908 = this.field_6908 + this.field_6912;
		if ((double)this.field_6908 > Math.PI * 2) {
			if (this.world.isClient) {
				this.field_6908 = (float) (Math.PI * 2);
			} else {
				this.field_6908 = (float)((double)this.field_6908 - (Math.PI * 2));
				if (this.random.nextInt(10) == 0) {
					this.field_6912 = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
				}

				this.world.sendEntityStatus(this, (byte)19);
			}
		}

		if (this.isInsideWaterOrBubbleColumn()) {
			if (this.field_6908 < (float) Math.PI) {
				float f = this.field_6908 / (float) Math.PI;
				this.field_6904 = MathHelper.sin(f * f * (float) Math.PI) * (float) Math.PI * 0.25F;
				if ((double)f > 0.75) {
					this.constantVelocityRate = 1.0F;
					this.field_6913 = 1.0F;
				} else {
					this.field_6913 *= 0.8F;
				}
			} else {
				this.field_6904 = 0.0F;
				this.constantVelocityRate *= 0.9F;
				this.field_6913 *= 0.99F;
			}

			if (!this.world.isClient) {
				this.setVelocity(
					(double)(this.constantVelocityX * this.constantVelocityRate),
					(double)(this.constantVelocityY * this.constantVelocityRate),
					(double)(this.constantVelocityZ * this.constantVelocityRate)
				);
			}

			Vec3d vec3d = this.getVelocity();
			float g = MathHelper.sqrt(squaredHorizontalLength(vec3d));
			this.bodyYaw = this.bodyYaw + (-((float)MathHelper.atan2(vec3d.x, vec3d.z)) * (180.0F / (float)Math.PI) - this.bodyYaw) * 0.1F;
			this.yaw = this.bodyYaw;
			this.field_6903 = (float)((double)this.field_6903 + Math.PI * (double)this.field_6913 * 1.5);
			this.field_6907 = this.field_6907 + (-((float)MathHelper.atan2((double)g, vec3d.y)) * (180.0F / (float)Math.PI) - this.field_6907) * 0.1F;
		} else {
			this.field_6904 = MathHelper.abs(MathHelper.sin(this.field_6908)) * (float) Math.PI * 0.25F;
			if (!this.world.isClient) {
				double d = this.getVelocity().y;
				if (this.hasStatusEffect(StatusEffects.LEVITATION)) {
					d = 0.05 * (double)(this.getStatusEffect(StatusEffects.LEVITATION).getAmplifier() + 1);
				} else if (!this.hasNoGravity()) {
					d -= 0.08;
				}

				this.setVelocity(0.0, d * 0.98F, 0.0);
			}

			this.field_6907 = (float)((double)this.field_6907 + (double)(-90.0F - this.field_6907) * 0.02);
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (super.damage(source, amount) && this.getAttacker() != null) {
			this.squirt();
			return true;
		} else {
			return false;
		}
	}

	private Vec3d method_6671(Vec3d vec3d) {
		Vec3d vec3d2 = vec3d.rotateX(this.field_6905 * (float) (Math.PI / 180.0));
		return vec3d2.rotateY(-this.prevBodyYaw * (float) (Math.PI / 180.0));
	}

	private void squirt() {
		this.playSound(SoundEvents.ENTITY_SQUID_SQUIRT, this.getSoundVolume(), this.getSoundPitch());
		Vec3d vec3d = this.method_6671(new Vec3d(0.0, -1.0, 0.0)).add(this.getX(), this.getY(), this.getZ());

		for (int i = 0; i < 30; i++) {
			Vec3d vec3d2 = this.method_6671(new Vec3d((double)this.random.nextFloat() * 0.6 - 0.3, -1.0, (double)this.random.nextFloat() * 0.6 - 0.3));
			Vec3d vec3d3 = vec3d2.multiply(0.3 + (double)(this.random.nextFloat() * 2.0F));
			((ServerWorld)this.world).spawnParticles(ParticleTypes.SQUID_INK, vec3d.x, vec3d.y + 0.5, vec3d.z, 0, vec3d3.x, vec3d3.y, vec3d3.z, 0.1F);
		}
	}

	@Override
	public void travel(Vec3d movementInput) {
		this.move(MovementType.SELF, this.getVelocity());
	}

	public static boolean canSpawn(EntityType<SquidEntity> type, IWorld world, SpawnType spawnType, BlockPos pos, Random random) {
		return pos.getY() > 45 && pos.getY() < world.getSeaLevel();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte status) {
		if (status == 19) {
			this.field_6908 = 0.0F;
		} else {
			super.handleStatus(status);
		}
	}

	public void setConstantVelocity(float x, float y, float z) {
		this.constantVelocityX = x;
		this.constantVelocityY = y;
		this.constantVelocityZ = z;
	}

	public boolean hasConstantVelocity() {
		return this.constantVelocityX != 0.0F || this.constantVelocityY != 0.0F || this.constantVelocityZ != 0.0F;
	}

	class EscapeAttackerGoal extends Goal {
		private int timer;

		private EscapeAttackerGoal() {
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = SquidEntity.this.getAttacker();
			return SquidEntity.this.isTouchingWater() && livingEntity != null ? SquidEntity.this.squaredDistanceTo(livingEntity) < 100.0 : false;
		}

		@Override
		public void start() {
			this.timer = 0;
		}

		@Override
		public void tick() {
			this.timer++;
			LivingEntity livingEntity = SquidEntity.this.getAttacker();
			if (livingEntity != null) {
				Vec3d vec3d = new Vec3d(
					SquidEntity.this.getX() - livingEntity.getX(), SquidEntity.this.getY() - livingEntity.getY(), SquidEntity.this.getZ() - livingEntity.getZ()
				);
				BlockState blockState = SquidEntity.this.world
					.getBlockState(new BlockPos(SquidEntity.this.getX() + vec3d.x, SquidEntity.this.getY() + vec3d.y, SquidEntity.this.getZ() + vec3d.z));
				FluidState fluidState = SquidEntity.this.world
					.getFluidState(new BlockPos(SquidEntity.this.getX() + vec3d.x, SquidEntity.this.getY() + vec3d.y, SquidEntity.this.getZ() + vec3d.z));
				if (fluidState.matches(FluidTags.WATER) || blockState.isAir()) {
					double d = vec3d.length();
					if (d > 0.0) {
						vec3d.normalize();
						float f = 3.0F;
						if (d > 5.0) {
							f = (float)((double)f - (d - 5.0) / 5.0);
						}

						if (f > 0.0F) {
							vec3d = vec3d.multiply((double)f);
						}
					}

					if (blockState.isAir()) {
						vec3d = vec3d.subtract(0.0, vec3d.y, 0.0);
					}

					SquidEntity.this.setConstantVelocity((float)vec3d.x / 20.0F, (float)vec3d.y / 20.0F, (float)vec3d.z / 20.0F);
				}

				if (this.timer % 10 == 5) {
					SquidEntity.this.world.addParticle(ParticleTypes.BUBBLE, SquidEntity.this.getX(), SquidEntity.this.getY(), SquidEntity.this.getZ(), 0.0, 0.0, 0.0);
				}
			}
		}
	}

	class SwimGoal extends Goal {
		private final SquidEntity squid;

		public SwimGoal(SquidEntity squid) {
			this.squid = squid;
		}

		@Override
		public boolean canStart() {
			return true;
		}

		@Override
		public void tick() {
			int i = this.squid.getDespawnCounter();
			if (i > 100) {
				this.squid.setConstantVelocity(0.0F, 0.0F, 0.0F);
			} else if (this.squid.getRandom().nextInt(50) == 0 || !this.squid.touchingWater || !this.squid.hasConstantVelocity()) {
				float f = this.squid.getRandom().nextFloat() * (float) (Math.PI * 2);
				float g = MathHelper.cos(f) * 0.2F;
				float h = -0.1F + this.squid.getRandom().nextFloat() * 0.2F;
				float j = MathHelper.sin(f) * 0.2F;
				this.squid.setConstantVelocity(g, h, j);
			}
		}
	}
}
