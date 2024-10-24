package net.minecraft.entity.mob;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.EntityStatuses;
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
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class WitchEntity extends RaiderEntity implements RangedAttackMob {
	private static final Identifier DRINKING_SPEED_PENALTY_MODIFIER_ID = Identifier.ofVanilla("drinking");
	private static final EntityAttributeModifier DRINKING_SPEED_PENALTY_MODIFIER = new EntityAttributeModifier(
		DRINKING_SPEED_PENALTY_MODIFIER_ID, -0.25, EntityAttributeModifier.Operation.ADD_VALUE
	);
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
		this.raidGoal = new RaidGoal<>(this, RaiderEntity.class, true, (target, world) -> this.hasActiveRaid() && target.getType() != EntityType.WITCH);
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
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(DRINKING, false);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_WITCH_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_WITCH_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_WITCH_DEATH;
	}

	public void setDrinking(boolean drinking) {
		this.getDataTracker().set(DRINKING, drinking);
	}

	public boolean isDrinking() {
		return this.getDataTracker().get(DRINKING);
	}

	public static DefaultAttributeContainer.Builder createWitchAttributes() {
		return HostileEntity.createHostileAttributes().add(EntityAttributes.MAX_HEALTH, 26.0).add(EntityAttributes.MOVEMENT_SPEED, 0.25);
	}

	@Override
	public void tickMovement() {
		if (!this.getWorld().isClient && this.isAlive()) {
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
					this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
					PotionContentsComponent potionContentsComponent = itemStack.get(DataComponentTypes.POTION_CONTENTS);
					if (itemStack.isOf(Items.POTION) && potionContentsComponent != null) {
						potionContentsComponent.forEachEffect(this::addStatusEffect);
					}

					this.emitGameEvent(GameEvent.DRINK);
					this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).removeModifier(DRINKING_SPEED_PENALTY_MODIFIER.id());
				}
			} else {
				RegistryEntry<Potion> registryEntry = null;
				if (this.random.nextFloat() < 0.15F && this.isSubmergedIn(FluidTags.WATER) && !this.hasStatusEffect(StatusEffects.WATER_BREATHING)) {
					registryEntry = Potions.WATER_BREATHING;
				} else if (this.random.nextFloat() < 0.15F
					&& (this.isOnFire() || this.getRecentDamageSource() != null && this.getRecentDamageSource().isIn(DamageTypeTags.IS_FIRE))
					&& !this.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
					registryEntry = Potions.FIRE_RESISTANCE;
				} else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
					registryEntry = Potions.HEALING;
				} else if (this.random.nextFloat() < 0.5F
					&& this.getTarget() != null
					&& !this.hasStatusEffect(StatusEffects.SPEED)
					&& this.getTarget().squaredDistanceTo(this) > 121.0) {
					registryEntry = Potions.SWIFTNESS;
				}

				if (registryEntry != null) {
					this.equipStack(EquipmentSlot.MAINHAND, PotionContentsComponent.createStack(Items.POTION, registryEntry));
					this.drinkTimeLeft = this.getMainHandStack().getMaxUseTime(this);
					this.setDrinking(true);
					if (!this.isSilent()) {
						this.getWorld()
							.playSound(
								null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WITCH_DRINK, this.getSoundCategory(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F
							);
					}

					EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
					entityAttributeInstance.removeModifier(DRINKING_SPEED_PENALTY_MODIFIER_ID);
					entityAttributeInstance.addTemporaryModifier(DRINKING_SPEED_PENALTY_MODIFIER);
				}
			}

			if (this.random.nextFloat() < 7.5E-4F) {
				this.getWorld().sendEntityStatus(this, EntityStatuses.ADD_WITCH_PARTICLES);
			}
		}

		super.tickMovement();
	}

	@Override
	public SoundEvent getCelebratingSound() {
		return SoundEvents.ENTITY_WITCH_CELEBRATE;
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.ADD_WITCH_PARTICLES) {
			for (int i = 0; i < this.random.nextInt(35) + 10; i++) {
				this.getWorld()
					.addParticle(
						ParticleTypes.WITCH,
						this.getX() + this.random.nextGaussian() * 0.13F,
						this.getBoundingBox().maxY + 0.5 + this.random.nextGaussian() * 0.13F,
						this.getZ() + this.random.nextGaussian() * 0.13F,
						0.0,
						0.0,
						0.0
					);
			}
		} else {
			super.handleStatus(status);
		}
	}

	@Override
	protected float modifyAppliedDamage(DamageSource source, float amount) {
		amount = super.modifyAppliedDamage(source, amount);
		if (source.getAttacker() == this) {
			amount = 0.0F;
		}

		if (source.isIn(DamageTypeTags.WITCH_RESISTANT_TO)) {
			amount *= 0.15F;
		}

		return amount;
	}

	@Override
	public void shootAt(LivingEntity target, float pullProgress) {
		if (!this.isDrinking()) {
			Vec3d vec3d = target.getVelocity();
			double d = target.getX() + vec3d.x - this.getX();
			double e = target.getEyeY() - 1.1F - this.getY();
			double f = target.getZ() + vec3d.z - this.getZ();
			double g = Math.sqrt(d * d + f * f);
			RegistryEntry<Potion> registryEntry = Potions.HARMING;
			if (target instanceof RaiderEntity) {
				if (target.getHealth() <= 4.0F) {
					registryEntry = Potions.HEALING;
				} else {
					registryEntry = Potions.REGENERATION;
				}

				this.setTarget(null);
			} else if (g >= 8.0 && !target.hasStatusEffect(StatusEffects.SLOWNESS)) {
				registryEntry = Potions.SLOWNESS;
			} else if (target.getHealth() >= 8.0F && !target.hasStatusEffect(StatusEffects.POISON)) {
				registryEntry = Potions.POISON;
			} else if (g <= 3.0 && !target.hasStatusEffect(StatusEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
				registryEntry = Potions.WEAKNESS;
			}

			if (this.getWorld() instanceof ServerWorld serverWorld) {
				ItemStack itemStack = PotionContentsComponent.createStack(Items.SPLASH_POTION, registryEntry);
				ProjectileEntity.spawnWithVelocity(PotionEntity::new, serverWorld, itemStack, this, d, e + g * 0.2, f, 0.75F, 8.0F);
			}

			if (!this.isSilent()) {
				this.getWorld()
					.playSound(
						null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F
					);
			}
		}
	}

	@Override
	public void addBonusForWave(ServerWorld world, int wave, boolean unused) {
	}

	@Override
	public boolean canLead() {
		return false;
	}
}
