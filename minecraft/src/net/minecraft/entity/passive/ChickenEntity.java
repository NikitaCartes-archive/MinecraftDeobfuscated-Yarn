package net.minecraft.entity.passive;

import javax.annotation.Nullable;
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
import net.minecraft.world.event.GameEvent;
import org.joml.Vector3f;

public class ChickenEntity extends AnimalEntity {
	private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(
		Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD
	);
	public float flapProgress;
	public float maxWingDeviation;
	public float prevMaxWingDeviation;
	public float prevFlapProgress;
	public float flapSpeed = 1.0F;
	private float field_28639 = 1.0F;
	public int eggLayTime = this.random.nextInt(6000) + 6000;
	public boolean hasJockey;

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
		this.maxWingDeviation = this.maxWingDeviation + (this.isOnGround() ? -1.0F : 4.0F) * 0.3F;
		this.maxWingDeviation = MathHelper.clamp(this.maxWingDeviation, 0.0F, 1.0F);
		if (!this.isOnGround() && this.flapSpeed < 1.0F) {
			this.flapSpeed = 1.0F;
		}

		this.flapSpeed *= 0.9F;
		Vec3d vec3d = this.getVelocity();
		if (!this.isOnGround() && vec3d.y < 0.0) {
			this.setVelocity(vec3d.multiply(1.0, 0.6, 1.0));
		}

		this.flapProgress = this.flapProgress + this.flapSpeed * 2.0F;
		if (!this.getWorld().isClient && this.isAlive() && !this.isBaby() && !this.hasJockey() && --this.eggLayTime <= 0) {
			this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
			this.dropItem(Items.EGG);
			this.emitGameEvent(GameEvent.ENTITY_PLACE);
			this.eggLayTime = this.random.nextInt(6000) + 6000;
		}
	}

	@Override
	protected boolean isFlappingWings() {
		return this.speed > this.field_28639;
	}

	@Override
	protected void addFlapEffects() {
		this.field_28639 = this.speed + this.maxWingDeviation / 2.0F;
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

	@Nullable
	public ChickenEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		return EntityType.CHICKEN.create(serverWorld);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return BREEDING_INGREDIENT.test(stack);
	}

	@Override
	public int getXpToDrop() {
		return this.hasJockey() ? 10 : super.getXpToDrop();
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.hasJockey = nbt.getBoolean("IsChickenJockey");
		if (nbt.contains("EggLayTime")) {
			this.eggLayTime = nbt.getInt("EggLayTime");
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("IsChickenJockey", this.hasJockey);
		nbt.putInt("EggLayTime", this.eggLayTime);
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return this.hasJockey();
	}

	@Override
	protected void updatePassengerPosition(Entity passenger, Entity.PositionUpdater positionUpdater) {
		super.updatePassengerPosition(passenger, positionUpdater);
		if (passenger instanceof LivingEntity) {
			((LivingEntity)passenger).bodyYaw = this.bodyYaw;
		}
	}

	@Override
	protected Vector3f getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
		return new Vector3f(0.0F, dimensions.height, -0.1F * scaleFactor);
	}

	public boolean hasJockey() {
		return this.hasJockey;
	}

	public void setHasJockey(boolean hasJockey) {
		this.hasJockey = hasJockey;
	}
}
