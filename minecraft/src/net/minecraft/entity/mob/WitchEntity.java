package net.minecraft.entity.mob;

import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1361;
import net.minecraft.class_1376;
import net.minecraft.class_1394;
import net.minecraft.class_1399;
import net.minecraft.class_3760;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
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
import net.minecraft.world.World;

public class WitchEntity extends RaiderEntity implements RangedAttacker {
	private static final UUID field_7418 = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
	private static final EntityAttributeModifier field_7416 = new EntityAttributeModifier(
			field_7418, "Drinking speed penalty", -0.25, EntityAttributeModifier.Operation.field_6328
		)
		.setSerialize(false);
	private static final TrackedData<Boolean> DRINKING = DataTracker.registerData(WitchEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int field_7417;

	public WitchEntity(World world) {
		super(EntityType.WITCH, world);
		this.setSize(0.6F, 1.95F);
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(2, new ProjectileAttackGoal(this, 1.0, 60, 10.0F));
		this.goalSelector.add(2, new class_1394(this, 1.0));
		this.goalSelector.add(3, new class_1361(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(3, new class_1376(this));
		this.targetSelector.add(1, new class_1399(this, RaiderEntity.class));
		this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, 10, true, false, playerEntity -> playerEntity != null && this.hasActiveRaid()));
		this.targetSelector.add(2, new class_3760(this, RaiderEntity.class, true));
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
	public void updateMovement() {
		if (!this.world.isClient) {
			if (this.isDrinking()) {
				if (this.field_7417-- <= 0) {
					this.setDrinking(false);
					ItemStack itemStack = this.getMainHandStack();
					this.setEquippedStack(EquipmentSlot.HAND_MAIN, ItemStack.EMPTY);
					if (itemStack.getItem() == Items.field_8574) {
						List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
						if (list != null) {
							for (StatusEffectInstance statusEffectInstance : list) {
								this.addPotionEffect(new StatusEffectInstance(statusEffectInstance));
							}
						}
					}

					this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).removeModifier(field_7416);
				}
			} else {
				Potion potion = null;
				if (this.random.nextFloat() < 0.15F && this.method_5777(FluidTags.field_15517) && !this.hasPotionEffect(StatusEffects.field_5923)) {
					potion = Potions.field_8994;
				} else if (this.random.nextFloat() < 0.15F
					&& (this.isOnFire() || this.method_6081() != null && this.method_6081().isFire())
					&& !this.hasPotionEffect(StatusEffects.field_5918)) {
					potion = Potions.field_8987;
				} else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getHealthMaximum()) {
					potion = Potions.field_8963;
				} else if (this.random.nextFloat() < 0.5F
					&& this.getTarget() != null
					&& !this.hasPotionEffect(StatusEffects.field_5904)
					&& this.getTarget().squaredDistanceTo(this) > 121.0) {
					potion = Potions.field_9005;
				}

				if (potion != null) {
					this.setEquippedStack(EquipmentSlot.HAND_MAIN, PotionUtil.setPotion(new ItemStack(Items.field_8574), potion));
					this.field_7417 = this.getMainHandStack().getMaxUseTime();
					this.setDrinking(true);
					this.world.playSound(null, this.x, this.y, this.z, SoundEvents.field_14565, this.getSoundCategory(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
					EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
					entityAttributeInstance.removeModifier(field_7416);
					entityAttributeInstance.addModifier(field_7416);
				}
			}

			if (this.random.nextFloat() < 7.5E-4F) {
				this.world.summonParticle(this, (byte)15);
			}
		}

		super.updateMovement();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 15) {
			for (int i = 0; i < this.random.nextInt(35) + 10; i++) {
				this.world
					.method_8406(
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
			super.method_5711(b);
		}
	}

	@Override
	protected float method_6036(DamageSource damageSource, float f) {
		f = super.method_6036(damageSource, f);
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
			double d = livingEntity.y + (double)livingEntity.getEyeHeight() - 1.1F;
			double e = livingEntity.x + livingEntity.velocityX - this.x;
			double g = d - this.y;
			double h = livingEntity.z + livingEntity.velocityZ - this.z;
			float i = MathHelper.sqrt(e * e + h * h);
			Potion potion = Potions.field_9004;
			if (i >= 8.0F && !livingEntity.hasPotionEffect(StatusEffects.field_5909)) {
				potion = Potions.field_8996;
			} else if (livingEntity.getHealth() >= 8.0F && !livingEntity.hasPotionEffect(StatusEffects.field_5899)) {
				potion = Potions.field_8982;
			} else if (i <= 3.0F && !livingEntity.hasPotionEffect(StatusEffects.field_5911) && this.random.nextFloat() < 0.25F) {
				potion = Potions.field_8975;
			}

			ThrownPotionEntity thrownPotionEntity = new ThrownPotionEntity(this.world, this);
			thrownPotionEntity.setItemStack(PotionUtil.setPotion(new ItemStack(Items.field_8436), potion));
			thrownPotionEntity.pitch -= -20.0F;
			thrownPotionEntity.setVelocity(e, g + (double)(i * 0.2F), h, 0.75F, 8.0F);
			this.world.playSound(null, this.x, this.y, this.z, SoundEvents.field_15067, this.getSoundCategory(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
			this.world.spawnEntity(thrownPotionEntity);
		}
	}

	@Override
	public float getEyeHeight() {
		return 1.62F;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasArmsRaised() {
		return false;
	}

	@Override
	public void setArmsRaised(boolean bl) {
	}

	@Override
	public void addBonusForWave(int i, boolean bl) {
	}

	@Override
	public boolean canLead() {
		return false;
	}
}
