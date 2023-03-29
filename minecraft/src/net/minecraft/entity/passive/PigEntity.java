package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Dismounting;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.SaddledComponent;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class PigEntity extends AnimalEntity implements ItemSteerable, Saddleable {
	private static final TrackedData<Boolean> SADDLED = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> BOOST_TIME = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.CARROT, Items.POTATO, Items.BEETROOT);
	private final SaddledComponent saddledComponent = new SaddledComponent(this.dataTracker, BOOST_TIME, SADDLED);

	public PigEntity(EntityType<? extends PigEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new EscapeDangerGoal(this, 1.25));
		this.goalSelector.add(3, new AnimalMateGoal(this, 1.0));
		this.goalSelector.add(4, new TemptGoal(this, 1.2, Ingredient.ofItems(Items.CARROT_ON_A_STICK), false));
		this.goalSelector.add(4, new TemptGoal(this, 1.2, BREEDING_INGREDIENT, false));
		this.goalSelector.add(5, new FollowParentGoal(this, 1.1));
		this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
	}

	public static DefaultAttributeContainer.Builder createPigAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
	}

	@Nullable
	@Override
	public LivingEntity getControllingPassenger() {
		return !this.isSaddled()
				|| !(this.getFirstPassenger() instanceof PlayerEntity playerEntity)
				|| !playerEntity.getMainHandStack().isOf(Items.CARROT_ON_A_STICK) && !playerEntity.getOffHandStack().isOf(Items.CARROT_ON_A_STICK)
			? null
			: playerEntity;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (BOOST_TIME.equals(data) && this.world.isClient) {
			this.saddledComponent.boost();
		}

		super.onTrackedDataSet(data);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(SADDLED, false);
		this.dataTracker.startTracking(BOOST_TIME, 0);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		this.saddledComponent.writeNbt(nbt);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.saddledComponent.readNbt(nbt);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_PIG_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_PIG_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_PIG_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_PIG_STEP, 0.15F, 1.0F);
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		boolean bl = this.isBreedingItem(player.getStackInHand(hand));
		if (!bl && this.isSaddled() && !this.hasPassengers() && !player.shouldCancelInteraction()) {
			if (!this.world.isClient) {
				player.startRiding(this);
			}

			return ActionResult.success(this.world.isClient);
		} else {
			ActionResult actionResult = super.interactMob(player, hand);
			if (!actionResult.isAccepted()) {
				ItemStack itemStack = player.getStackInHand(hand);
				return itemStack.isOf(Items.SADDLE) ? itemStack.useOnEntity(player, this, hand) : ActionResult.PASS;
			} else {
				return actionResult;
			}
		}
	}

	@Override
	public boolean canBeSaddled() {
		return this.isAlive() && !this.isBaby();
	}

	@Override
	protected void dropInventory() {
		super.dropInventory();
		if (this.isSaddled()) {
			this.dropItem(Items.SADDLE);
		}
	}

	@Override
	public boolean isSaddled() {
		return this.saddledComponent.isSaddled();
	}

	@Override
	public void saddle(@Nullable SoundCategory sound) {
		this.saddledComponent.setSaddled(true);
		if (sound != null) {
			this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_PIG_SADDLE, sound, 0.5F, 1.0F);
		}
	}

	@Override
	public Vec3d updatePassengerForDismount(LivingEntity passenger) {
		Direction direction = this.getMovementDirection();
		if (direction.getAxis() == Direction.Axis.Y) {
			return super.updatePassengerForDismount(passenger);
		} else {
			int[][] is = Dismounting.getDismountOffsets(direction);
			BlockPos blockPos = this.getBlockPos();
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (EntityPose entityPose : passenger.getPoses()) {
				Box box = passenger.getBoundingBox(entityPose);

				for (int[] js : is) {
					mutable.set(blockPos.getX() + js[0], blockPos.getY(), blockPos.getZ() + js[1]);
					double d = this.world.getDismountHeight(mutable);
					if (Dismounting.canDismountInBlock(d)) {
						Vec3d vec3d = Vec3d.ofCenter(mutable, d);
						if (Dismounting.canPlaceEntityAt(this.world, passenger, box.offset(vec3d))) {
							passenger.setPose(entityPose);
							return vec3d;
						}
					}
				}
			}

			return super.updatePassengerForDismount(passenger);
		}
	}

	@Override
	public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
		if (world.getDifficulty() != Difficulty.PEACEFUL) {
			ZombifiedPiglinEntity zombifiedPiglinEntity = EntityType.ZOMBIFIED_PIGLIN.create(world);
			if (zombifiedPiglinEntity != null) {
				zombifiedPiglinEntity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
				zombifiedPiglinEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
				zombifiedPiglinEntity.setAiDisabled(this.isAiDisabled());
				zombifiedPiglinEntity.setBaby(this.isBaby());
				if (this.hasCustomName()) {
					zombifiedPiglinEntity.setCustomName(this.getCustomName());
					zombifiedPiglinEntity.setCustomNameVisible(this.isCustomNameVisible());
				}

				zombifiedPiglinEntity.setPersistent();
				world.spawnEntity(zombifiedPiglinEntity);
				this.discard();
			} else {
				super.onStruckByLightning(world, lightning);
			}
		} else {
			super.onStruckByLightning(world, lightning);
		}
	}

	@Override
	protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput) {
		super.tickControlled(controllingPlayer, movementInput);
		this.setRotation(controllingPlayer.getYaw(), controllingPlayer.getPitch() * 0.5F);
		this.prevYaw = this.bodyYaw = this.headYaw = this.getYaw();
		this.saddledComponent.tickBoost();
	}

	@Override
	protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput) {
		return new Vec3d(0.0, 0.0, 1.0);
	}

	@Override
	protected float getSaddledSpeed(PlayerEntity controllingPlayer) {
		return (float)(this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 0.225 * (double)this.saddledComponent.getMovementSpeedMultiplier());
	}

	@Override
	public boolean consumeOnAStickItem() {
		return this.saddledComponent.boost(this.getRandom());
	}

	@Nullable
	public PigEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		return EntityType.PIG.create(serverWorld);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return BREEDING_INGREDIENT.test(stack);
	}

	@Override
	public Vec3d getLeashOffset() {
		return new Vec3d(0.0, (double)(0.6F * this.getStandingEyeHeight()), (double)(this.getWidth() * 0.4F));
	}
}
