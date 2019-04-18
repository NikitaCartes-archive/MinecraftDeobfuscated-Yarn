package net.minecraft.entity.passive;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.WaterCreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
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
		this.goalSelector.add(0, new SquidEntity.SwimInOneDirectionGoal(this));
		this.goalSelector.add(1, new SquidEntity.EscapeAttackerGoal());
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntitySize entitySize) {
		return entitySize.height * 0.5F;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_15034;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15212;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15124;
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
	public void updateState() {
		super.updateState();
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
			this.field_6283 = this.field_6283 + (-((float)MathHelper.atan2(vec3d.x, vec3d.z)) * (180.0F / (float)Math.PI) - this.field_6283) * 0.1F;
			this.yaw = this.field_6283;
			this.field_6903 = (float)((double)this.field_6903 + Math.PI * (double)this.field_6913 * 1.5);
			this.field_6907 = this.field_6907 + (-((float)MathHelper.atan2((double)g, vec3d.y)) * (180.0F / (float)Math.PI) - this.field_6907) * 0.1F;
		} else {
			this.field_6904 = MathHelper.abs(MathHelper.sin(this.field_6908)) * (float) Math.PI * 0.25F;
			if (!this.world.isClient) {
				double d = this.getVelocity().y;
				if (this.hasStatusEffect(StatusEffects.field_5902)) {
					d = 0.05 * (double)(this.getStatusEffect(StatusEffects.field_5902).getAmplifier() + 1);
				} else if (!this.isUnaffectedByGravity()) {
					d -= 0.08;
				}

				this.setVelocity(0.0, d * 0.98F, 0.0);
			}

			this.field_6907 = (float)((double)this.field_6907 + (double)(-90.0F - this.field_6907) * 0.02);
		}
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (super.damage(damageSource, f) && this.getAttacker() != null) {
			this.squirt();
			return true;
		} else {
			return false;
		}
	}

	private Vec3d method_6671(Vec3d vec3d) {
		Vec3d vec3d2 = vec3d.rotateX(this.field_6905 * (float) (Math.PI / 180.0));
		return vec3d2.rotateY(-this.field_6220 * (float) (Math.PI / 180.0));
	}

	private void squirt() {
		this.playSound(SoundEvents.field_15121, this.getSoundVolume(), this.getSoundPitch());
		Vec3d vec3d = this.method_6671(new Vec3d(0.0, -1.0, 0.0)).add(this.x, this.y, this.z);

		for (int i = 0; i < 30; i++) {
			Vec3d vec3d2 = this.method_6671(new Vec3d((double)this.random.nextFloat() * 0.6 - 0.3, -1.0, (double)this.random.nextFloat() * 0.6 - 0.3));
			Vec3d vec3d3 = vec3d2.multiply(0.3 + (double)(this.random.nextFloat() * 2.0F));
			((ServerWorld)this.world).spawnParticles(ParticleTypes.field_11233, vec3d.x, vec3d.y + 0.5, vec3d.z, 0, vec3d3.x, vec3d3.y, vec3d3.z, 0.1F);
		}
	}

	@Override
	public void travel(Vec3d vec3d) {
		this.move(MovementType.field_6308, this.getVelocity());
	}

	@Override
	public boolean canSpawn(IWorld iWorld, SpawnType spawnType) {
		return this.y > 45.0 && this.y < (double)iWorld.getSeaLevel();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte b) {
		if (b == 19) {
			this.field_6908 = 0.0F;
		} else {
			super.handleStatus(b);
		}
	}

	public void setConstantVelocity(float f, float g, float h) {
		this.constantVelocityX = f;
		this.constantVelocityY = g;
		this.constantVelocityZ = h;
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
			return SquidEntity.this.isInsideWater() && livingEntity != null ? SquidEntity.this.squaredDistanceTo(livingEntity) < 100.0 : false;
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
				Vec3d vec3d = new Vec3d(SquidEntity.this.x - livingEntity.x, SquidEntity.this.y - livingEntity.y, SquidEntity.this.z - livingEntity.z);
				BlockState blockState = SquidEntity.this.world
					.getBlockState(new BlockPos(SquidEntity.this.x + vec3d.x, SquidEntity.this.y + vec3d.y, SquidEntity.this.z + vec3d.z));
				FluidState fluidState = SquidEntity.this.world
					.getFluidState(new BlockPos(SquidEntity.this.x + vec3d.x, SquidEntity.this.y + vec3d.y, SquidEntity.this.z + vec3d.z));
				if (fluidState.matches(FluidTags.field_15517) || blockState.isAir()) {
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
					SquidEntity.this.world.addParticle(ParticleTypes.field_11247, SquidEntity.this.x, SquidEntity.this.y, SquidEntity.this.z, 0.0, 0.0, 0.0);
				}
			}
		}
	}

	class SwimInOneDirectionGoal extends Goal {
		private final SquidEntity owner;

		public SwimInOneDirectionGoal(SquidEntity squidEntity2) {
			this.owner = squidEntity2;
		}

		@Override
		public boolean canStart() {
			return true;
		}

		@Override
		public void tick() {
			int i = this.owner.getDespawnCounter();
			if (i > 100) {
				this.owner.setConstantVelocity(0.0F, 0.0F, 0.0F);
			} else if (this.owner.getRand().nextInt(50) == 0 || !this.owner.insideWater || !this.owner.hasConstantVelocity()) {
				float f = this.owner.getRand().nextFloat() * (float) (Math.PI * 2);
				float g = MathHelper.cos(f) * 0.2F;
				float h = -0.1F + this.owner.getRand().nextFloat() * 0.2F;
				float j = MathHelper.sin(f) * 0.2F;
				this.owner.setConstantVelocity(g, h, j);
			}
		}
	}
}
