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
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ChickenEntity extends AnimalEntity {
	private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
	public float flapProgress;
	public float maxWingDeviation;
	public float prevMaxWingDeviation;
	public float prevFlapProgress;
	public float flapSpeed = 1.0F;
	private float field_28639 = 1.0F;
	public int eggLayTime = this.random.nextInt(6000) + 6000;
	public boolean jockey;

	public ChickenEntity(EntityType<? extends ChickenEntity> entityType, World world) {
		super(entityType, world);
		this.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new EscapeDangerGoal(this, 1.4));
		this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
		this.goalSelector.add(3, new TemptGoal(this, 1.0, BREEDING_INGREDIENT, false));
		this.goalSelector.add(4, new FollowParentGoal(this, 1.1));
		this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(7, new LookAroundGoal(this));
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return this.isBaby() ? dimensions.height * 0.85F : dimensions.height * 0.92F;
	}

	public static DefaultAttributeContainer.Builder createChickenAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 4.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		this.prevFlapProgress = this.flapProgress;
		this.prevMaxWingDeviation = this.maxWingDeviation;
		this.maxWingDeviation = (float)((double)this.maxWingDeviation + (double)(this.onGround ? -1 : 4) * 0.3);
		this.maxWingDeviation = MathHelper.clamp(this.maxWingDeviation, 0.0F, 1.0F);
		if (!this.onGround && this.flapSpeed < 1.0F) {
			this.flapSpeed = 1.0F;
		}

		this.flapSpeed = (float)((double)this.flapSpeed * 0.9);
		Vec3d vec3d = this.getVelocity();
		if (!this.onGround && vec3d.y < 0.0) {
			this.setVelocity(vec3d.multiply(1.0, 0.6, 1.0));
		}

		this.flapProgress = this.flapProgress + this.flapSpeed * 2.0F;
		if (!this.world.isClient && this.isAlive() && !this.isBaby() && !this.hasJockey() && --this.eggLayTime <= 0) {
			this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			this.dropItem(Items.EGG);
			this.eggLayTime = this.random.nextInt(6000) + 6000;
		}
	}

	@Override
	protected boolean hasWings() {
		return this.field_28627 > this.field_28639;
	}

	@Override
	protected void addFlapEffects() {
		this.field_28639 = this.field_28627 + this.maxWingDeviation / 2.0F;
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_CHICKEN_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_CHICKEN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_CHICKEN_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_CHICKEN_STEP, 0.15F, 1.0F);
	}

	public ChickenEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		return EntityType.CHICKEN.create(serverWorld);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return BREEDING_INGREDIENT.test(stack);
	}

	@Override
	protected int getXpToDrop(PlayerEntity player) {
		return this.hasJockey() ? 10 : super.getXpToDrop(player);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.jockey = nbt.getBoolean("IsChickenJockey");
		if (nbt.contains("EggLayTime")) {
			this.eggLayTime = nbt.getInt("EggLayTime");
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("IsChickenJockey", this.jockey);
		nbt.putInt("EggLayTime", this.eggLayTime);
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return this.hasJockey();
	}

	@Override
	public void updatePassengerPosition(Entity passenger) {
		super.updatePassengerPosition(passenger);
		float f = MathHelper.sin(this.bodyYaw * (float) (Math.PI / 180.0));
		float g = MathHelper.cos(this.bodyYaw * (float) (Math.PI / 180.0));
		float h = 0.1F;
		float i = 0.0F;
		passenger.setPosition(this.getX() + (double)(0.1F * f), this.getBodyY(0.5) + passenger.getHeightOffset() + 0.0, this.getZ() - (double)(0.1F * g));
		if (passenger instanceof LivingEntity) {
			((LivingEntity)passenger).bodyYaw = this.bodyYaw;
		}
	}

	public boolean hasJockey() {
		return this.jockey;
	}

	public void setHasJockey(boolean hasJockey) {
		this.jockey = hasJockey;
	}
}
