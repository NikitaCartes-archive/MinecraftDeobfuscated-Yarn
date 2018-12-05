package net.minecraft.entity.passive;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3730;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
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
	private float field_6901;
	private float field_6912;
	private float field_6913;
	private float field_6910;
	private float field_6911;
	private float field_6909;

	public SquidEntity(World world) {
		super(EntityType.SQUID, world);
		this.setSize(0.8F, 0.8F);
		this.random.setSeed((long)(1 + this.getEntityId()));
		this.field_6912 = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
	}

	@Override
	protected void method_5959() {
		this.goalSelector.add(0, new SquidEntity.class_1479(this));
		this.goalSelector.add(1, new SquidEntity.class_1478());
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
	}

	@Override
	public float getEyeHeight() {
		return this.height * 0.5F;
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
	protected boolean method_5658() {
		return false;
	}

	@Override
	public void updateMovement() {
		super.updateMovement();
		this.field_6905 = this.field_6907;
		this.field_6906 = this.field_6903;
		this.field_6902 = this.field_6908;
		this.field_6900 = this.field_6904;
		this.field_6908 = this.field_6908 + this.field_6912;
		if ((double)this.field_6908 > Math.PI * 2) {
			if (this.world.isRemote) {
				this.field_6908 = (float) (Math.PI * 2);
			} else {
				this.field_6908 = (float)((double)this.field_6908 - (Math.PI * 2));
				if (this.random.nextInt(10) == 0) {
					this.field_6912 = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
				}

				this.world.method_8421(this, (byte)19);
			}
		}

		if (this.method_5816()) {
			if (this.field_6908 < (float) Math.PI) {
				float f = this.field_6908 / (float) Math.PI;
				this.field_6904 = MathHelper.sin(f * f * (float) Math.PI) * (float) Math.PI * 0.25F;
				if ((double)f > 0.75) {
					this.field_6901 = 1.0F;
					this.field_6913 = 1.0F;
				} else {
					this.field_6913 *= 0.8F;
				}
			} else {
				this.field_6904 = 0.0F;
				this.field_6901 *= 0.9F;
				this.field_6913 *= 0.99F;
			}

			if (!this.world.isRemote) {
				this.velocityX = (double)(this.field_6910 * this.field_6901);
				this.velocityY = (double)(this.field_6911 * this.field_6901);
				this.velocityZ = (double)(this.field_6909 * this.field_6901);
			}

			float f = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
			this.field_6283 = this.field_6283 + (-((float)MathHelper.atan2(this.velocityX, this.velocityZ)) * (180.0F / (float)Math.PI) - this.field_6283) * 0.1F;
			this.yaw = this.field_6283;
			this.field_6903 = (float)((double)this.field_6903 + Math.PI * (double)this.field_6913 * 1.5);
			this.field_6907 = this.field_6907 + (-((float)MathHelper.atan2((double)f, this.velocityY)) * (180.0F / (float)Math.PI) - this.field_6907) * 0.1F;
		} else {
			this.field_6904 = MathHelper.abs(MathHelper.sin(this.field_6908)) * (float) Math.PI * 0.25F;
			if (!this.world.isRemote) {
				this.velocityX = 0.0;
				this.velocityZ = 0.0;
				if (this.hasPotionEffect(StatusEffects.field_5902)) {
					this.velocityY = this.velocityY + (0.05 * (double)(this.getPotionEffect(StatusEffects.field_5902).getAmplifier() + 1) - this.velocityY);
				} else if (!this.isUnaffectedByGravity()) {
					this.velocityY -= 0.08;
				}

				this.velocityY *= 0.98F;
			}

			this.field_6907 = (float)((double)this.field_6907 + (double)(-90.0F - this.field_6907) * 0.02);
		}
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (super.damage(damageSource, f) && this.getAttacker() != null) {
			this.method_6669();
			return true;
		} else {
			return false;
		}
	}

	private Vec3d method_6671(Vec3d vec3d) {
		Vec3d vec3d2 = vec3d.rotateX(this.field_6905 * (float) (Math.PI / 180.0));
		return vec3d2.rotateY(-this.field_6220 * (float) (Math.PI / 180.0));
	}

	private void method_6669() {
		this.playSoundAtEntity(SoundEvents.field_15121, this.getSoundVolume(), this.getSoundPitch());
		Vec3d vec3d = this.method_6671(new Vec3d(0.0, -1.0, 0.0)).add(this.x, this.y, this.z);

		for (int i = 0; i < 30; i++) {
			Vec3d vec3d2 = this.method_6671(new Vec3d((double)this.random.nextFloat() * 0.6 - 0.3, -1.0, (double)this.random.nextFloat() * 0.6 - 0.3));
			Vec3d vec3d3 = vec3d2.multiply(0.3 + (double)(this.random.nextFloat() * 2.0F));
			((ServerWorld)this.world).method_14199(ParticleTypes.field_11233, vec3d.x, vec3d.y + 0.5, vec3d.z, 0, vec3d3.x, vec3d3.y, vec3d3.z, 0.1F);
		}
	}

	@Override
	public void method_6091(float f, float g, float h) {
		this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
	}

	@Override
	public boolean method_5979(IWorld iWorld, class_3730 arg) {
		return this.y > 45.0 && this.y < (double)iWorld.getSeaLevel();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 19) {
			this.field_6908 = 0.0F;
		} else {
			super.method_5711(b);
		}
	}

	public void method_6670(float f, float g, float h) {
		this.field_6910 = f;
		this.field_6911 = g;
		this.field_6909 = h;
	}

	public boolean method_6672() {
		return this.field_6910 != 0.0F || this.field_6911 != 0.0F || this.field_6909 != 0.0F;
	}

	class class_1478 extends Goal {
		private int field_6915;

		private class_1478() {
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = SquidEntity.this.getAttacker();
			return SquidEntity.this.isInsideWater() && livingEntity != null ? SquidEntity.this.squaredDistanceTo(livingEntity) < 100.0 : false;
		}

		@Override
		public void start() {
			this.field_6915 = 0;
		}

		@Override
		public void tick() {
			this.field_6915++;
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

					SquidEntity.this.method_6670((float)vec3d.x / 20.0F, (float)vec3d.y / 20.0F, (float)vec3d.z / 20.0F);
				}

				if (this.field_6915 % 10 == 5) {
					SquidEntity.this.world.method_8406(ParticleTypes.field_11247, SquidEntity.this.x, SquidEntity.this.y, SquidEntity.this.z, 0.0, 0.0, 0.0);
				}
			}
		}
	}

	class class_1479 extends Goal {
		private final SquidEntity field_6917;

		public class_1479(SquidEntity squidEntity2) {
			this.field_6917 = squidEntity2;
		}

		@Override
		public boolean canStart() {
			return true;
		}

		@Override
		public void tick() {
			int i = this.field_6917.method_6131();
			if (i > 100) {
				this.field_6917.method_6670(0.0F, 0.0F, 0.0F);
			} else if (this.field_6917.getRand().nextInt(50) == 0 || !this.field_6917.insideWater || !this.field_6917.method_6672()) {
				float f = this.field_6917.getRand().nextFloat() * (float) (Math.PI * 2);
				float g = MathHelper.cos(f) * 0.2F;
				float h = -0.1F + this.field_6917.getRand().nextFloat() * 0.2F;
				float j = MathHelper.sin(f) * 0.2F;
				this.field_6917.method_6670(g, h, j);
			}
		}
	}
}
