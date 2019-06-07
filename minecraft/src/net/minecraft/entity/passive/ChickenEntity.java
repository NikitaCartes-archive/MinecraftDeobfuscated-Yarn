package net.minecraft.entity.passive;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ChickenEntity extends AnimalEntity {
	private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.field_8317, Items.field_8188, Items.field_8706, Items.field_8309);
	public float field_6741;
	public float field_6743;
	public float field_6738;
	public float field_6736;
	public float field_6737 = 1.0F;
	public int eggLayTime = this.random.nextInt(6000) + 6000;
	public boolean jockey;

	public ChickenEntity(EntityType<? extends ChickenEntity> entityType, World world) {
		super(entityType, world);
		this.setPathNodeTypeWeight(PathNodeType.field_18, 0.0F);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new EscapeDangerGoal(this, 1.4));
		this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
		this.goalSelector.add(3, new TemptGoal(this, 1.0, false, BREEDING_INGREDIENT));
		this.goalSelector.add(4, new FollowParentGoal(this, 1.1));
		this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(7, new LookAroundGoal(this));
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return this.isBaby() ? entityDimensions.height * 0.85F : entityDimensions.height * 0.92F;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(4.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		this.field_6736 = this.field_6741;
		this.field_6738 = this.field_6743;
		this.field_6743 = (float)((double)this.field_6743 + (double)(this.onGround ? -1 : 4) * 0.3);
		this.field_6743 = MathHelper.clamp(this.field_6743, 0.0F, 1.0F);
		if (!this.onGround && this.field_6737 < 1.0F) {
			this.field_6737 = 1.0F;
		}

		this.field_6737 = (float)((double)this.field_6737 * 0.9);
		Vec3d vec3d = this.getVelocity();
		if (!this.onGround && vec3d.y < 0.0) {
			this.setVelocity(vec3d.multiply(1.0, 0.6, 1.0));
		}

		this.field_6741 = this.field_6741 + this.field_6737 * 2.0F;
		if (!this.world.isClient && this.isAlive() && !this.isBaby() && !this.hasJockey() && --this.eggLayTime <= 0) {
			this.playSound(SoundEvents.field_15219, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			this.dropItem(Items.field_8803);
			this.eggLayTime = this.random.nextInt(6000) + 6000;
		}
	}

	@Override
	public void handleFallDamage(float f, float g) {
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14871;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14601;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15140;
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSound(SoundEvents.field_14685, 0.15F, 1.0F);
	}

	public ChickenEntity method_6471(PassiveEntity passiveEntity) {
		return EntityType.field_6132.create(this.world);
	}

	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
		return BREEDING_INGREDIENT.method_8093(itemStack);
	}

	@Override
	protected int getCurrentExperience(PlayerEntity playerEntity) {
		return this.hasJockey() ? 10 : super.getCurrentExperience(playerEntity);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.jockey = compoundTag.getBoolean("IsChickenJockey");
		if (compoundTag.containsKey("EggLayTime")) {
			this.eggLayTime = compoundTag.getInt("EggLayTime");
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putBoolean("IsChickenJockey", this.jockey);
		compoundTag.putInt("EggLayTime", this.eggLayTime);
	}

	@Override
	public boolean canImmediatelyDespawn(double d) {
		return this.hasJockey() && !this.hasPassengers();
	}

	@Override
	public void updatePassengerPosition(Entity entity) {
		super.updatePassengerPosition(entity);
		float f = MathHelper.sin(this.field_6283 * (float) (Math.PI / 180.0));
		float g = MathHelper.cos(this.field_6283 * (float) (Math.PI / 180.0));
		float h = 0.1F;
		float i = 0.0F;
		entity.setPosition(this.x + (double)(0.1F * f), this.y + (double)(this.getHeight() * 0.5F) + entity.getHeightOffset() + 0.0, this.z - (double)(0.1F * g));
		if (entity instanceof LivingEntity) {
			((LivingEntity)entity).field_6283 = this.field_6283;
		}
	}

	public boolean hasJockey() {
		return this.jockey;
	}

	public void setHasJockey(boolean bl) {
		this.jockey = bl;
	}
}
