package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemSteerable;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SaddledComponent;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PigEntity extends AnimalEntity implements ItemSteerable {
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
		this.goalSelector.add(4, new TemptGoal(this, 1.2, false, BREEDING_INGREDIENT));
		this.goalSelector.add(5, new FollowParentGoal(this, 1.1));
		this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Nullable
	@Override
	public Entity getPrimaryPassenger() {
		return this.getPassengerList().isEmpty() ? null : (Entity)this.getPassengerList().get(0);
	}

	@Override
	public boolean canBeControlledByRider() {
		Entity entity = this.getPrimaryPassenger();
		if (!(entity instanceof PlayerEntity)) {
			return false;
		} else {
			PlayerEntity playerEntity = (PlayerEntity)entity;
			return playerEntity.getMainHandStack().getItem() == Items.CARROT_ON_A_STICK || playerEntity.getOffHandStack().getItem() == Items.CARROT_ON_A_STICK;
		}
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
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		this.saddledComponent.toTag(tag);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.saddledComponent.fromTag(tag);
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
	public boolean interactMob(PlayerEntity player, Hand hand) {
		return !super.interactMob(player, hand) ? this.interactMob(this, player, hand, true) : true;
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
	public void setSaddled(boolean saddled) {
		this.saddledComponent.setSaddled(saddled);
	}

	@Override
	public void onStruckByLightning(LightningEntity lightning) {
		ZombifiedPiglinEntity zombifiedPiglinEntity = EntityType.ZOMBIFIED_PIGLIN.create(this.world);
		zombifiedPiglinEntity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
		zombifiedPiglinEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
		zombifiedPiglinEntity.setAiDisabled(this.isAiDisabled());
		zombifiedPiglinEntity.setBaby(this.isBaby());
		if (this.hasCustomName()) {
			zombifiedPiglinEntity.setCustomName(this.getCustomName());
			zombifiedPiglinEntity.setCustomNameVisible(this.isCustomNameVisible());
		}

		this.world.spawnEntity(zombifiedPiglinEntity);
		this.remove();
	}

	@Override
	public void travel(Vec3d movementInput) {
		if (this.travel(this, this.saddledComponent, movementInput)) {
			this.lastLimbDistance = this.limbDistance;
			double d = this.getX() - this.prevX;
			double e = this.getZ() - this.prevZ;
			float f = MathHelper.sqrt(d * d + e * e) * 4.0F;
			if (f > 1.0F) {
				f = 1.0F;
			}

			this.limbDistance = this.limbDistance + (f - this.limbDistance) * 0.4F;
			this.limbAngle = this.limbAngle + this.limbDistance;
		}
	}

	@Override
	public float getSaddledSpeed() {
		return (float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue() * 0.225F;
	}

	@Override
	public void setMovementInput(Vec3d movementInput) {
		super.travel(movementInput);
	}

	@Override
	public boolean consumeOnAStickItem() {
		return this.saddledComponent.boost(this.getRandom());
	}

	public PigEntity createChild(PassiveEntity passiveEntity) {
		return EntityType.PIG.create(this.world);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return BREEDING_INGREDIENT.test(stack);
	}
}
