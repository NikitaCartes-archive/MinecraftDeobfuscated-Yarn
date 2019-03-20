package net.minecraft.entity.passive;

import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.AvoidGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.FollowTargetIfTamedGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TrackAttackerGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.WolfBegGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WolfEntity extends TameableEntity {
	private static final TrackedData<Float> WOLF_HEALTH = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Boolean> field_6946 = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> COLLAR_COLOR = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final Predicate<LivingEntity> field_18004 = livingEntity -> {
		EntityType<?> entityType = livingEntity.getType();
		return entityType == EntityType.SHEEP || entityType == EntityType.RABBIT || entityType == EntityType.field_17943;
	};
	private float field_6952;
	private float field_6949;
	private boolean field_6944;
	private boolean field_6951;
	private float field_6947;
	private float field_6945;

	public WolfEntity(EntityType<? extends WolfEntity> entityType, World world) {
		super(entityType, world);
		this.setTamed(false);
	}

	@Override
	protected void initGoals() {
		this.sitGoal = new SitGoal(this);
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(2, this.sitGoal);
		this.goalSelector.add(3, new WolfEntity.WolfFleeGoal(this, LlamaEntity.class, 24.0F, 1.5, 1.5));
		this.goalSelector.add(4, new PounceAtTargetGoal(this, 0.4F));
		this.goalSelector.add(5, new MeleeAttackGoal(this, 1.0, true));
		this.goalSelector.add(6, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F));
		this.goalSelector.add(7, new AnimalMateGoal(this, 1.0));
		this.goalSelector.add(8, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(9, new WolfBegGoal(this, 8.0F));
		this.goalSelector.add(10, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(10, new LookAroundGoal(this));
		this.targetSelector.add(1, new TrackAttackerGoal(this));
		this.targetSelector.add(2, new AttackWithOwnerGoal(this));
		this.targetSelector.add(3, new AvoidGoal(this).method_6318());
		this.targetSelector.add(4, new FollowTargetIfTamedGoal(this, AnimalEntity.class, false, field_18004));
		this.targetSelector.add(4, new FollowTargetIfTamedGoal(this, TurtleEntity.class, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
		this.targetSelector.add(5, new FollowTargetGoal(this, AbstractSkeletonEntity.class, false));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3F);
		if (this.isTamed()) {
			this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(20.0);
		} else {
			this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(8.0);
		}

		this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
	}

	@Override
	public void setTarget(@Nullable LivingEntity livingEntity) {
		super.setTarget(livingEntity);
		if (livingEntity == null) {
			this.setAngry(false);
		} else if (!this.isTamed()) {
			this.setAngry(true);
		}
	}

	@Override
	protected void mobTick() {
		this.dataTracker.set(WOLF_HEALTH, this.getHealth());
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(WOLF_HEALTH, this.getHealth());
		this.dataTracker.startTracking(field_6946, false);
		this.dataTracker.startTracking(COLLAR_COLOR, DyeColor.field_7964.getId());
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSound(SoundEvents.field_14772, 0.15F, 1.0F);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putBoolean("Angry", this.isAngry());
		compoundTag.putByte("CollarColor", (byte)this.method_6713().getId());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setAngry(compoundTag.getBoolean("Angry"));
		if (compoundTag.containsKey("CollarColor", 99)) {
			this.method_6708(DyeColor.byId(compoundTag.getInt("CollarColor")));
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		if (this.isAngry()) {
			return SoundEvents.field_14575;
		} else if (this.random.nextInt(3) == 0) {
			return this.isTamed() && this.dataTracker.get(WOLF_HEALTH) < 10.0F ? SoundEvents.field_14807 : SoundEvents.field_14922;
		} else {
			return SoundEvents.field_14724;
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15218;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14659;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	public void updateMovement() {
		super.updateMovement();
		if (!this.world.isClient && this.field_6944 && !this.field_6951 && !this.isNavigating() && this.onGround) {
			this.field_6951 = true;
			this.field_6947 = 0.0F;
			this.field_6945 = 0.0F;
			this.world.summonParticle(this, (byte)8);
		}

		if (!this.world.isClient && this.getTarget() == null && this.isAngry()) {
			this.setAngry(false);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.isValid()) {
			this.field_6949 = this.field_6952;
			if (this.method_6710()) {
				this.field_6952 = this.field_6952 + (1.0F - this.field_6952) * 0.4F;
			} else {
				this.field_6952 = this.field_6952 + (0.0F - this.field_6952) * 0.4F;
			}

			if (this.isTouchingWater()) {
				this.field_6944 = true;
				this.field_6951 = false;
				this.field_6947 = 0.0F;
				this.field_6945 = 0.0F;
			} else if ((this.field_6944 || this.field_6951) && this.field_6951) {
				if (this.field_6947 == 0.0F) {
					this.playSound(SoundEvents.field_15042, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
				}

				this.field_6945 = this.field_6947;
				this.field_6947 += 0.05F;
				if (this.field_6945 >= 2.0F) {
					this.field_6944 = false;
					this.field_6951 = false;
					this.field_6945 = 0.0F;
					this.field_6947 = 0.0F;
				}

				if (this.field_6947 > 0.4F) {
					float f = (float)this.getBoundingBox().minY;
					int i = (int)(MathHelper.sin((this.field_6947 - 0.4F) * (float) Math.PI) * 7.0F);
					Vec3d vec3d = this.getVelocity();

					for (int j = 0; j < i; j++) {
						float g = (this.random.nextFloat() * 2.0F - 1.0F) * this.getWidth() * 0.5F;
						float h = (this.random.nextFloat() * 2.0F - 1.0F) * this.getWidth() * 0.5F;
						this.world.addParticle(ParticleTypes.field_11202, this.x + (double)g, (double)(f + 0.8F), this.z + (double)h, vec3d.x, vec3d.y, vec3d.z);
					}
				}
			}
		}
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		this.field_6944 = false;
		this.field_6951 = false;
		this.field_6945 = 0.0F;
		this.field_6947 = 0.0F;
		super.onDeath(damageSource);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_6711() {
		return this.field_6944;
	}

	@Environment(EnvType.CLIENT)
	public float method_6707(float f) {
		return 0.75F + MathHelper.lerp(f, this.field_6945, this.field_6947) / 2.0F * 0.25F;
	}

	@Environment(EnvType.CLIENT)
	public float method_6715(float f, float g) {
		float h = (MathHelper.lerp(f, this.field_6945, this.field_6947) + g) / 1.8F;
		if (h < 0.0F) {
			h = 0.0F;
		} else if (h > 1.0F) {
			h = 1.0F;
		}

		return MathHelper.sin(h * (float) Math.PI) * MathHelper.sin(h * (float) Math.PI * 11.0F) * 0.15F * (float) Math.PI;
	}

	@Environment(EnvType.CLIENT)
	public float method_6719(float f) {
		return MathHelper.lerp(f, this.field_6949, this.field_6952) * 0.15F * (float) Math.PI;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntitySize entitySize) {
		return entitySize.height * 0.8F;
	}

	@Override
	public int method_5978() {
		return this.isSitting() ? 20 : super.method_5978();
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else {
			Entity entity = damageSource.getAttacker();
			if (this.sitGoal != null) {
				this.sitGoal.setEnabledWithOwner(false);
			}

			if (entity != null && !(entity instanceof PlayerEntity) && !(entity instanceof ProjectileEntity)) {
				f = (f + 1.0F) / 2.0F;
			}

			return super.damage(damageSource, f);
		}
	}

	@Override
	public boolean attack(Entity entity) {
		boolean bl = entity.damage(DamageSource.mob(this), (float)((int)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue()));
		if (bl) {
			this.dealDamage(this, entity);
		}

		return bl;
	}

	@Override
	public void setTamed(boolean bl) {
		super.setTamed(bl);
		if (bl) {
			this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(20.0);
		} else {
			this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(8.0);
		}

		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		Item item = itemStack.getItem();
		if (this.isTamed()) {
			if (!itemStack.isEmpty()) {
				if (item.isFood()) {
					if (item.getFoodSetting().isWolfFood() && this.dataTracker.get(WOLF_HEALTH) < 20.0F) {
						if (!playerEntity.abilities.creativeMode) {
							itemStack.subtractAmount(1);
						}

						this.heal((float)item.getFoodSetting().getHunger());
						return true;
					}
				} else if (item instanceof DyeItem) {
					DyeColor dyeColor = ((DyeItem)item).getColor();
					if (dyeColor != this.method_6713()) {
						this.method_6708(dyeColor);
						if (!playerEntity.abilities.creativeMode) {
							itemStack.subtractAmount(1);
						}

						return true;
					}
				}
			}

			if (this.isOwner(playerEntity) && !this.world.isClient && !this.isBreedingItem(itemStack)) {
				this.sitGoal.setEnabledWithOwner(!this.isSitting());
				this.field_6282 = false;
				this.navigation.stop();
				this.setTarget(null);
			}
		} else if (item == Items.field_8606 && !this.isAngry()) {
			if (!playerEntity.abilities.creativeMode) {
				itemStack.subtractAmount(1);
			}

			if (!this.world.isClient) {
				if (this.random.nextInt(3) == 0) {
					this.method_6170(playerEntity);
					this.navigation.stop();
					this.setTarget(null);
					this.sitGoal.setEnabledWithOwner(true);
					this.setHealth(20.0F);
					this.method_6180(true);
					this.world.summonParticle(this, (byte)7);
				} else {
					this.method_6180(false);
					this.world.summonParticle(this, (byte)6);
				}
			}

			return true;
		}

		return super.interactMob(playerEntity, hand);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 8) {
			this.field_6951 = true;
			this.field_6947 = 0.0F;
			this.field_6945 = 0.0F;
		} else {
			super.method_5711(b);
		}
	}

	@Environment(EnvType.CLIENT)
	public float method_6714() {
		if (this.isAngry()) {
			return 1.5393804F;
		} else {
			return this.isTamed() ? (0.55F - (this.getHealthMaximum() - this.dataTracker.get(WOLF_HEALTH)) * 0.02F) * (float) Math.PI : (float) (Math.PI / 5);
		}
	}

	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
		Item item = itemStack.getItem();
		return item.isFood() && item.getFoodSetting().isWolfFood();
	}

	@Override
	public int getLimitPerChunk() {
		return 8;
	}

	public boolean isAngry() {
		return (this.dataTracker.get(TAMEABLE_FLAGS) & 2) != 0;
	}

	public void setAngry(boolean bl) {
		byte b = this.dataTracker.get(TAMEABLE_FLAGS);
		if (bl) {
			this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b | 2));
		} else {
			this.dataTracker.set(TAMEABLE_FLAGS, (byte)(b & -3));
		}
	}

	public DyeColor method_6713() {
		return DyeColor.byId(this.dataTracker.get(COLLAR_COLOR));
	}

	public void method_6708(DyeColor dyeColor) {
		this.dataTracker.set(COLLAR_COLOR, dyeColor.getId());
	}

	public WolfEntity method_6717(PassiveEntity passiveEntity) {
		WolfEntity wolfEntity = EntityType.WOLF.create(this.world);
		UUID uUID = this.method_6139();
		if (uUID != null) {
			wolfEntity.setOwnerUuid(uUID);
			wolfEntity.setTamed(true);
		}

		return wolfEntity;
	}

	public void method_6712(boolean bl) {
		this.dataTracker.set(field_6946, bl);
	}

	@Override
	public boolean canBreedWith(AnimalEntity animalEntity) {
		if (animalEntity == this) {
			return false;
		} else if (!this.isTamed()) {
			return false;
		} else if (!(animalEntity instanceof WolfEntity)) {
			return false;
		} else {
			WolfEntity wolfEntity = (WolfEntity)animalEntity;
			if (!wolfEntity.isTamed()) {
				return false;
			} else {
				return wolfEntity.isSitting() ? false : this.isInLove() && wolfEntity.isInLove();
			}
		}
	}

	public boolean method_6710() {
		return this.dataTracker.get(field_6946);
	}

	@Override
	public boolean method_6178(LivingEntity livingEntity, LivingEntity livingEntity2) {
		if (!(livingEntity instanceof CreeperEntity) && !(livingEntity instanceof GhastEntity)) {
			if (livingEntity instanceof WolfEntity) {
				WolfEntity wolfEntity = (WolfEntity)livingEntity;
				if (wolfEntity.isTamed() && wolfEntity.getOwner() == livingEntity2) {
					return false;
				}
			}

			if (livingEntity instanceof PlayerEntity
				&& livingEntity2 instanceof PlayerEntity
				&& !((PlayerEntity)livingEntity2).shouldDamagePlayer((PlayerEntity)livingEntity)) {
				return false;
			} else {
				return livingEntity instanceof HorseBaseEntity && ((HorseBaseEntity)livingEntity).isTame()
					? false
					: !(livingEntity instanceof CatEntity) || !((CatEntity)livingEntity).isTamed();
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity playerEntity) {
		return !this.isAngry() && super.canBeLeashedBy(playerEntity);
	}

	class WolfFleeGoal<T extends LivingEntity> extends FleeEntityGoal<T> {
		private final WolfEntity field_6954;

		public WolfFleeGoal(WolfEntity wolfEntity2, Class<T> class_, float f, double d, double e) {
			super(wolfEntity2, class_, f, d, e);
			this.field_6954 = wolfEntity2;
		}

		@Override
		public boolean canStart() {
			return super.canStart() && this.field_6390 instanceof LlamaEntity ? !this.field_6954.isTamed() && this.method_6720((LlamaEntity)this.field_6390) : false;
		}

		private boolean method_6720(LlamaEntity llamaEntity) {
			return llamaEntity.getStrength() >= WolfEntity.this.random.nextInt(5);
		}

		@Override
		public void start() {
			WolfEntity.this.setTarget(null);
			super.start();
		}

		@Override
		public void tick() {
			WolfEntity.this.setTarget(null);
			super.tick();
		}
	}
}
