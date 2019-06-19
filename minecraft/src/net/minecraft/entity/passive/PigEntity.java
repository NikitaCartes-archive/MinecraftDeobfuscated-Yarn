package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
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
import net.minecraft.entity.mob.ZombiePigmanEntity;
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

public class PigEntity extends AnimalEntity {
	private static final TrackedData<Boolean> SADDLED = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> field_6815 = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.field_8179, Items.field_8567, Items.field_8186);
	private boolean field_6814;
	private int field_6812;
	private int field_6813;

	public PigEntity(EntityType<? extends PigEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new EscapeDangerGoal(this, 1.25));
		this.goalSelector.add(3, new AnimalMateGoal(this, 1.0));
		this.goalSelector.add(4, new TemptGoal(this, 1.2, Ingredient.ofItems(Items.field_8184), false));
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
			return playerEntity.getMainHandStack().getItem() == Items.field_8184 || playerEntity.getOffHandStack().getItem() == Items.field_8184;
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		if (field_6815.equals(trackedData) && this.world.isClient) {
			this.field_6814 = true;
			this.field_6812 = 0;
			this.field_6813 = this.dataTracker.get(field_6815);
		}

		super.onTrackedDataSet(trackedData);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(SADDLED, false);
		this.dataTracker.startTracking(field_6815, 0);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putBoolean("Saddle", this.isSaddled());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setSaddled(compoundTag.getBoolean("Saddle"));
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14615;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14750;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14689;
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSound(SoundEvents.field_14894, 0.15F, 1.0F);
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		if (!super.interactMob(playerEntity, hand)) {
			ItemStack itemStack = playerEntity.getStackInHand(hand);
			if (itemStack.getItem() == Items.field_8448) {
				itemStack.useOnEntity(playerEntity, this, hand);
				return true;
			} else if (this.isSaddled() && !this.hasPassengers()) {
				if (!this.world.isClient) {
					playerEntity.startRiding(this);
				}

				return true;
			} else if (itemStack.getItem() == Items.field_8175) {
				itemStack.useOnEntity(playerEntity, this, hand);
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	protected void dropInventory() {
		super.dropInventory();
		if (this.isSaddled()) {
			this.dropItem(Items.field_8175);
		}
	}

	public boolean isSaddled() {
		return this.dataTracker.get(SADDLED);
	}

	public void setSaddled(boolean bl) {
		if (bl) {
			this.dataTracker.set(SADDLED, true);
		} else {
			this.dataTracker.set(SADDLED, false);
		}
	}

	@Override
	public void onStruckByLightning(LightningEntity lightningEntity) {
		ZombiePigmanEntity zombiePigmanEntity = EntityType.field_6050.create(this.world);
		zombiePigmanEntity.setEquippedStack(EquipmentSlot.field_6173, new ItemStack(Items.field_8845));
		zombiePigmanEntity.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
		zombiePigmanEntity.setAiDisabled(this.isAiDisabled());
		if (this.hasCustomName()) {
			zombiePigmanEntity.setCustomName(this.getCustomName());
			zombiePigmanEntity.setCustomNameVisible(this.isCustomNameVisible());
		}

		this.world.spawnEntity(zombiePigmanEntity);
		this.remove();
	}

	@Override
	public void travel(Vec3d vec3d) {
		if (this.isAlive()) {
			Entity entity = this.getPassengerList().isEmpty() ? null : (Entity)this.getPassengerList().get(0);
			if (this.hasPassengers() && this.canBeControlledByRider()) {
				this.yaw = entity.yaw;
				this.prevYaw = this.yaw;
				this.pitch = entity.pitch * 0.5F;
				this.setRotation(this.yaw, this.pitch);
				this.field_6283 = this.yaw;
				this.headYaw = this.yaw;
				this.stepHeight = 1.0F;
				this.field_6281 = this.getMovementSpeed() * 0.1F;
				if (this.field_6814 && this.field_6812++ > this.field_6813) {
					this.field_6814 = false;
				}

				if (this.isLogicalSideForUpdatingMovement()) {
					float f = (float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue() * 0.225F;
					if (this.field_6814) {
						f += f * 1.15F * MathHelper.sin((float)this.field_6812 / (float)this.field_6813 * (float) Math.PI);
					}

					this.setMovementSpeed(f);
					super.travel(new Vec3d(0.0, 0.0, 1.0));
				} else {
					this.setVelocity(Vec3d.ZERO);
				}

				this.lastLimbDistance = this.limbDistance;
				double d = this.x - this.prevX;
				double e = this.z - this.prevZ;
				float g = MathHelper.sqrt(d * d + e * e) * 4.0F;
				if (g > 1.0F) {
					g = 1.0F;
				}

				this.limbDistance = this.limbDistance + (g - this.limbDistance) * 0.4F;
				this.limbAngle = this.limbAngle + this.limbDistance;
			} else {
				this.stepHeight = 0.5F;
				this.field_6281 = 0.02F;
				super.travel(vec3d);
			}
		}
	}

	public boolean method_6577() {
		if (this.field_6814) {
			return false;
		} else {
			this.field_6814 = true;
			this.field_6812 = 0;
			this.field_6813 = this.getRand().nextInt(841) + 140;
			this.getDataTracker().set(field_6815, this.field_6813);
			return true;
		}
	}

	public PigEntity method_6574(PassiveEntity passiveEntity) {
		return EntityType.field_6093.create(this.world);
	}

	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
		return BREEDING_INGREDIENT.method_8093(itemStack);
	}
}
