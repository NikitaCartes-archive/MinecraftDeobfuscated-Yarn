package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class IllusionerEntity extends SpellcastingIllagerEntity implements RangedAttackMob {
	private static final int field_30473 = 4;
	private static final int field_30471 = 3;
	private static final int field_30472 = 3;
	private int field_7296;
	private final Vec3d[][] field_7297;

	public IllusionerEntity(EntityType<? extends IllusionerEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 5;
		this.field_7297 = new Vec3d[2][4];

		for (int i = 0; i < 4; i++) {
			this.field_7297[0][i] = Vec3d.ZERO;
			this.field_7297[1][i] = Vec3d.ZERO;
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
		this.targetSelector.add(3, new FollowTargetGoal(this, MerchantEntity.class, false).setMaxTimeWithoutVisibility(300));
		this.targetSelector.add(3, new FollowTargetGoal(this, IronGolemEntity.class, false).setMaxTimeWithoutVisibility(300));
	}

	public static DefaultAttributeContainer.Builder createIllusionerAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5)
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 18.0)
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 32.0);
	}

	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
	}

	@Override
	public Box getVisibilityBoundingBox() {
		return this.getBoundingBox().expand(3.0, 0.0, 3.0);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.world.isClient && this.isInvisible()) {
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
					this.world.addParticle(ParticleTypes.CLOUD, this.getParticleX(0.5), this.getRandomBodyY(), this.offsetZ(0.5), 0.0, 0.0, 0.0);
				}

				this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, this.getSoundCategory(), 1.0F, 1.0F, false);
			} else if (this.hurtTime == this.maxHurtTime - 1) {
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
		return SoundEvents.ENTITY_ILLUSIONER_AMBIENT;
	}

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
	public boolean isTeammate(Entity other) {
		if (super.isTeammate(other)) {
			return true;
		} else {
			return other instanceof LivingEntity && ((LivingEntity)other).getGroup() == EntityGroup.ILLAGER
				? this.getScoreboardTeam() == null && other.getScoreboardTeam() == null
				: false;
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ILLUSIONER_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ILLUSIONER_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_ILLUSIONER_HURT;
	}

	@Override
	protected SoundEvent getCastSpellSound() {
		return SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL;
	}

	@Override
	public void addBonusForWave(int wave, boolean unused) {
	}

	@Override
	public void attack(LivingEntity target, float pullProgress) {
		ItemStack itemStack = this.getArrowType(this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW)));
		PersistentProjectileEntity persistentProjectileEntity = ProjectileUtil.createArrowProjectile(this, itemStack, pullProgress);
		double d = target.getX() - this.getX();
		double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
		double f = target.getZ() - this.getZ();
		double g = Math.sqrt(d * d + f * f);
		persistentProjectileEntity.setVelocity(d, e + g * 0.2F, f, 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));
		this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
		this.world.spawnEntity(persistentProjectileEntity);
	}

	@Override
	public IllagerEntity.State getState() {
		if (this.isSpellcasting()) {
			return IllagerEntity.State.SPELLCASTING;
		} else {
			return this.isAttacking() ? IllagerEntity.State.BOW_AND_ARROW : IllagerEntity.State.CROSSED;
		}
	}

	class BlindTargetGoal extends SpellcastingIllagerEntity.CastSpellGoal {
		private int targetId;

		@Override
		public boolean canStart() {
			if (!super.canStart()) {
				return false;
			} else if (IllusionerEntity.this.getTarget() == null) {
				return false;
			} else {
				return IllusionerEntity.this.getTarget().getId() == this.targetId
					? false
					: IllusionerEntity.this.world.getLocalDifficulty(IllusionerEntity.this.getBlockPos()).isHarderThan((float)Difficulty.NORMAL.ordinal());
			}
		}

		@Override
		public void start() {
			super.start();
			this.targetId = IllusionerEntity.this.getTarget().getId();
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
			IllusionerEntity.this.getTarget().addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 400), IllusionerEntity.this);
		}

		@Override
		protected SoundEvent getSoundPrepare() {
			return SoundEvents.ENTITY_ILLUSIONER_PREPARE_BLINDNESS;
		}

		@Override
		protected SpellcastingIllagerEntity.Spell getSpell() {
			return SpellcastingIllagerEntity.Spell.BLINDNESS;
		}
	}

	class GiveInvisibilityGoal extends SpellcastingIllagerEntity.CastSpellGoal {
		@Override
		public boolean canStart() {
			return !super.canStart() ? false : !IllusionerEntity.this.hasStatusEffect(StatusEffects.INVISIBILITY);
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
			IllusionerEntity.this.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 1200));
		}

		@Nullable
		@Override
		protected SoundEvent getSoundPrepare() {
			return SoundEvents.ENTITY_ILLUSIONER_PREPARE_MIRROR;
		}

		@Override
		protected SpellcastingIllagerEntity.Spell getSpell() {
			return SpellcastingIllagerEntity.Spell.DISAPPEAR;
		}
	}
}
