package net.minecraft.entity.mob;

import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.DisableableFollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RaidGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WitchEntity extends RaiderEntity implements RangedAttackMob {
	private static final UUID DRINKING_SPEED_PENALTY_MODIFIER_ID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
	private static final EntityAttributeModifier DRINKING_SPEED_PENALTY_MODIFIER = new EntityAttributeModifier(
			DRINKING_SPEED_PENALTY_MODIFIER_ID, "Drinking speed penalty", -0.25, EntityAttributeModifier.Operation.field_6328
		)
		.setSerialize(false);
	private static final TrackedData<Boolean> DRINKING = DataTracker.registerData(WitchEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int drinkTimeLeft;
	private RaidGoal<RaiderEntity> raidGoal;
	private DisableableFollowTargetGoal<PlayerEntity> attackPlayerGoal;

	public WitchEntity(EntityType<? extends WitchEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.raidGoal = new RaidGoal<>(
			this, RaiderEntity.class, true, livingEntity -> livingEntity != null && this.hasActiveRaid() && livingEntity.getType() != EntityType.field_6145
		);
		this.attackPlayerGoal = new DisableableFollowTargetGoal<>(this, PlayerEntity.class, 10, true, false, null);
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(2, new ProjectileAttackGoal(this, 1.0, 60, 10.0F));
		this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(3, new LookAroundGoal(this));
		this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class));
		this.targetSelector.add(2, this.raidGoal);
		this.targetSelector.add(3, this.attackPlayerGoal);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.getDataTracker().startTracking(DRINKING, false);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14736;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14645;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14820;
	}

	public void setDrinking(boolean bl) {
		this.getDataTracker().set(DRINKING, bl);
	}

	public boolean isDrinking() {
		return this.getDataTracker().get(DRINKING);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(26.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Override
	public void tickMovement() {
		if (!this.world.isClient && this.isAlive()) {
			this.raidGoal.decreaseCooldown();
			if (this.raidGoal.getCooldown() <= 0) {
				this.attackPlayerGoal.setEnabled(true);
			} else {
				this.attackPlayerGoal.setEnabled(false);
			}

			if (this.isDrinking()) {
				if (this.drinkTimeLeft-- <= 0) {
					this.setDrinking(false);
					ItemStack itemStack = this.getMainHandStack();
					this.setEquippedStack(EquipmentSlot.field_6173, ItemStack.EMPTY);
					if (itemStack.getItem() == Items.field_8574) {
						List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
						if (list != null) {
							for (StatusEffectInstance statusEffectInstance : list) {
								this.addPotionEffect(new StatusEffectInstance(statusEffectInstance));
							}
						}
					}

					this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).removeModifier(DRINKING_SPEED_PENALTY_MODIFIER);
				}
			} else {
				Potion potion = null;
				if (this.random.nextFloat() < 0.15F && this.isInFluid(FluidTags.field_15517) && !this.hasStatusEffect(StatusEffects.field_5923)) {
					potion = Potions.field_8994;
				} else if (this.random.nextFloat() < 0.15F
					&& (this.isOnFire() || this.getRecentDamageSource() != null && this.getRecentDamageSource().isFire())
					&& !this.hasStatusEffect(StatusEffects.field_5918)) {
					potion = Potions.field_8987;
				} else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getHealthMaximum()) {
					potion = Potions.field_8963;
				} else if (this.random.nextFloat() < 0.5F
					&& this.getTarget() != null
					&& !this.hasStatusEffect(StatusEffects.field_5904)
					&& this.getTarget().squaredDistanceTo(this) > 121.0) {
					potion = Potions.field_9005;
				}

				if (potion != null) {
					this.setEquippedStack(EquipmentSlot.field_6173, PotionUtil.setPotion(new ItemStack(Items.field_8574), potion));
					this.drinkTimeLeft = this.getMainHandStack().getMaxUseTime();
					this.setDrinking(true);
					this.world.playSound(null, this.x, this.y, this.z, SoundEvents.field_14565, this.getSoundCategory(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
					EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
					entityAttributeInstance.removeModifier(DRINKING_SPEED_PENALTY_MODIFIER);
					entityAttributeInstance.addModifier(DRINKING_SPEED_PENALTY_MODIFIER);
				}
			}

			if (this.random.nextFloat() < 7.5E-4F) {
				this.world.sendEntityStatus(this, (byte)15);
			}
		}

		super.tickMovement();
	}

	@Override
	public SoundEvent getCelebratingSound() {
		return SoundEvents.field_19153;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte b) {
		if (b == 15) {
			for (int i = 0; i < this.random.nextInt(35) + 10; i++) {
				this.world
					.addParticle(
						ParticleTypes.field_11249,
						this.x + this.random.nextGaussian() * 0.13F,
						this.getBoundingBox().maxY + 0.5 + this.random.nextGaussian() * 0.13F,
						this.z + this.random.nextGaussian() * 0.13F,
						0.0,
						0.0,
						0.0
					);
			}
		} else {
			super.handleStatus(b);
		}
	}

	@Override
	protected float applyEnchantmentsToDamage(DamageSource damageSource, float f) {
		f = super.applyEnchantmentsToDamage(damageSource, f);
		if (damageSource.getAttacker() == this) {
			f = 0.0F;
		}

		if (damageSource.getMagic()) {
			f = (float)((double)f * 0.15);
		}

		return f;
	}

	@Override
	public void attack(LivingEntity livingEntity, float f) {
		if (!this.isDrinking()) {
			Vec3d vec3d = livingEntity.getVelocity();
			double d = livingEntity.x + vec3d.x - this.x;
			double e = livingEntity.y + (double)livingEntity.getStandingEyeHeight() - 1.1F - this.y;
			double g = livingEntity.z + vec3d.z - this.z;
			float h = MathHelper.sqrt(d * d + g * g);
			Potion potion = Potions.field_9004;
			if (livingEntity instanceof RaiderEntity) {
				if (livingEntity.getHealth() <= 4.0F) {
					potion = Potions.field_8963;
				} else {
					potion = Potions.field_8986;
				}

				this.setTarget(null);
			} else if (h >= 8.0F && !livingEntity.hasStatusEffect(StatusEffects.field_5909)) {
				potion = Potions.field_8996;
			} else if (livingEntity.getHealth() >= 8.0F && !livingEntity.hasStatusEffect(StatusEffects.field_5899)) {
				potion = Potions.field_8982;
			} else if (h <= 3.0F && !livingEntity.hasStatusEffect(StatusEffects.field_5911) && this.random.nextFloat() < 0.25F) {
				potion = Potions.field_8975;
			}

			ThrownPotionEntity thrownPotionEntity = new ThrownPotionEntity(this.world, this);
			thrownPotionEntity.setItemStack(PotionUtil.setPotion(new ItemStack(Items.field_8436), potion));
			thrownPotionEntity.pitch -= -20.0F;
			thrownPotionEntity.setVelocity(d, e + (double)(h * 0.2F), g, 0.75F, 8.0F);
			this.world.playSound(null, this.x, this.y, this.z, SoundEvents.field_15067, this.getSoundCategory(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
			this.world.spawnEntity(thrownPotionEntity);
		}
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return 1.62F;
	}

	@Override
	public void addBonusForWave(int i, boolean bl) {
	}

	@Override
	public boolean canLead() {
		return false;
	}
}
