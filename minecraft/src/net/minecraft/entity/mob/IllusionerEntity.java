package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class IllusionerEntity extends SpellcastingIllagerEntity implements RangedAttackMob {
	private int field_7296;
	private final Vec3d[][] field_7297;

	public IllusionerEntity(EntityType<? extends IllusionerEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 5;
		this.field_7297 = new Vec3d[2][4];

		for (int i = 0; i < 4; i++) {
			this.field_7297[0][i] = new Vec3d(0.0, 0.0, 0.0);
			this.field_7297[1][i] = new Vec3d(0.0, 0.0, 0.0);
		}
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new SpellcastingIllagerEntity.LookAtTargetGoal());
		this.goalSelector.add(4, new IllusionerEntity.GiveInvisibilityGoal());
		this.goalSelector.add(5, new IllusionerEntity.BlindTargetGoal());
		this.goalSelector.add(6, new BowAttackGoal<>(this, 0.5, 20, 15.0F));
		this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
		this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
		this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge());
		this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true).setMaxTimeWithoutVisibility(300));
		this.targetSelector.add(3, new FollowTargetGoal(this, AbstractTraderEntity.class, false).setMaxTimeWithoutVisibility(300));
		this.targetSelector.add(3, new FollowTargetGoal(this, IronGolemEntity.class, false).setMaxTimeWithoutVisibility(300));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(18.0);
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(32.0);
	}

	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		this.setEquippedStack(EquipmentSlot.field_6173, new ItemStack(Items.field_8102));
		return super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Box method_5830() {
		return this.method_5829().expand(3.0, 0.0, 3.0);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.field_6002.isClient && this.isInvisible()) {
			this.field_7296--;
			if (this.field_7296 < 0) {
				this.field_7296 = 0;
			}

			if (this.hurtTime == 1 || this.age % 1200 == 0) {
				this.field_7296 = 3;
				float f = -6.0F;
				int i = 13;

				for (int j = 0; j < 4; j++) {
					this.field_7297[0][j] = this.field_7297[1][j];
					this.field_7297[1][j] = new Vec3d(
						(double)(-6.0F + (float)this.random.nextInt(13)) * 0.5,
						(double)Math.max(0, this.random.nextInt(6) - 4),
						(double)(-6.0F + (float)this.random.nextInt(13)) * 0.5
					);
				}

				for (int j = 0; j < 16; j++) {
					this.field_6002
						.addParticle(
							ParticleTypes.field_11204,
							this.x + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
							this.y + this.random.nextDouble() * (double)this.getHeight(),
							this.z + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
							0.0,
							0.0,
							0.0
						);
				}

				this.field_6002.playSound(this.x, this.y, this.z, SoundEvents.field_14941, this.getSoundCategory(), 1.0F, 1.0F, false);
			} else if (this.hurtTime == this.field_6254 - 1) {
				this.field_7296 = 3;

				for (int k = 0; k < 4; k++) {
					this.field_7297[0][k] = this.field_7297[1][k];
					this.field_7297[1][k] = new Vec3d(0.0, 0.0, 0.0);
				}
			}
		}
	}

	@Override
	public SoundEvent getCelebratingSound() {
		return SoundEvents.field_14644;
	}

	@Environment(EnvType.CLIENT)
	public Vec3d[] method_7065(float f) {
		if (this.field_7296 <= 0) {
			return this.field_7297[1];
		} else {
			double d = (double)(((float)this.field_7296 - f) / 3.0F);
			d = Math.pow(d, 0.25);
			Vec3d[] vec3ds = new Vec3d[4];

			for (int i = 0; i < 4; i++) {
				vec3ds[i] = this.field_7297[1][i].multiply(1.0 - d).add(this.field_7297[0][i].multiply(d));
			}

			return vec3ds;
		}
	}

	@Override
	public boolean isTeammate(Entity entity) {
		if (super.isTeammate(entity)) {
			return true;
		} else {
			return entity instanceof LivingEntity && ((LivingEntity)entity).getGroup() == EntityGroup.ILLAGER
				? this.method_5781() == null && entity.method_5781() == null
				: false;
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14644;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15153;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15223;
	}

	@Override
	protected SoundEvent getCastSpellSound() {
		return SoundEvents.field_14545;
	}

	@Override
	public void addBonusForWave(int i, boolean bl) {
	}

	@Override
	public void attack(LivingEntity livingEntity, float f) {
		ItemStack itemStack = this.getArrowType(this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.field_8102)));
		ProjectileEntity projectileEntity = ProjectileUtil.createArrowProjectile(this, itemStack, f);
		double d = livingEntity.x - this.x;
		double e = livingEntity.method_5829().minY + (double)(livingEntity.getHeight() / 3.0F) - projectileEntity.y;
		double g = livingEntity.z - this.z;
		double h = (double)MathHelper.sqrt(d * d + g * g);
		projectileEntity.setVelocity(d, e + h * 0.2F, g, 1.6F, (float)(14 - this.field_6002.getDifficulty().getId() * 4));
		this.playSound(SoundEvents.field_14633, 1.0F, 1.0F / (this.getRand().nextFloat() * 0.4F + 0.8F));
		this.field_6002.spawnEntity(projectileEntity);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public IllagerEntity.State getState() {
		if (this.isSpellcasting()) {
			return IllagerEntity.State.field_7212;
		} else {
			return this.isAttacking() ? IllagerEntity.State.field_7208 : IllagerEntity.State.field_7207;
		}
	}

	class BlindTargetGoal extends SpellcastingIllagerEntity.CastSpellGoal {
		private int targetId;

		private BlindTargetGoal() {
		}

		@Override
		public boolean canStart() {
			if (!super.canStart()) {
				return false;
			} else if (IllusionerEntity.this.getTarget() == null) {
				return false;
			} else {
				return IllusionerEntity.this.getTarget().getEntityId() == this.targetId
					? false
					: IllusionerEntity.this.field_6002.getLocalDifficulty(new BlockPos(IllusionerEntity.this)).method_5455((float)Difficulty.field_5802.ordinal());
			}
		}

		@Override
		public void start() {
			super.start();
			this.targetId = IllusionerEntity.this.getTarget().getEntityId();
		}

		@Override
		protected int getSpellTicks() {
			return 20;
		}

		@Override
		protected int startTimeDelay() {
			return 180;
		}

		@Override
		protected void castSpell() {
			IllusionerEntity.this.getTarget().addPotionEffect(new StatusEffectInstance(StatusEffects.field_5919, 400));
		}

		@Override
		protected SoundEvent getSoundPrepare() {
			return SoundEvents.field_15019;
		}

		@Override
		protected SpellcastingIllagerEntity.Spell getSpell() {
			return SpellcastingIllagerEntity.Spell.field_7378;
		}
	}

	class GiveInvisibilityGoal extends SpellcastingIllagerEntity.CastSpellGoal {
		private GiveInvisibilityGoal() {
		}

		@Override
		public boolean canStart() {
			return !super.canStart() ? false : !IllusionerEntity.this.hasStatusEffect(StatusEffects.field_5905);
		}

		@Override
		protected int getSpellTicks() {
			return 20;
		}

		@Override
		protected int startTimeDelay() {
			return 340;
		}

		@Override
		protected void castSpell() {
			IllusionerEntity.this.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5905, 1200));
		}

		@Nullable
		@Override
		protected SoundEvent getSoundPrepare() {
			return SoundEvents.field_14738;
		}

		@Override
		protected SpellcastingIllagerEntity.Spell getSpell() {
			return SpellcastingIllagerEntity.Spell.field_7382;
		}
	}
}
