package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.class_1361;
import net.minecraft.class_1374;
import net.minecraft.class_1376;
import net.minecraft.class_1394;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PigZombieEntity;
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
import net.minecraft.world.World;

public class PigEntity extends AnimalEntity {
	private static final TrackedData<Boolean> SADDLED = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> field_6815 = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.field_8179, Items.field_8567, Items.field_8186);
	private boolean field_6814;
	private int field_6812;
	private int field_6813;

	public PigEntity(World world) {
		super(EntityType.PIG, world);
	}

	@Override
	protected void method_5959() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new class_1374(this, 1.25));
		this.goalSelector.add(3, new AnimalMateGoal(this, 1.0));
		this.goalSelector.add(4, new TemptGoal(this, 1.2, Ingredient.ofItems(Items.field_8184), false));
		this.goalSelector.add(4, new TemptGoal(this, 1.2, false, BREEDING_INGREDIENT));
		this.goalSelector.add(5, new FollowParentGoal(this, 1.1));
		this.goalSelector.add(6, new class_1394(this, 1.0));
		this.goalSelector.add(7, new class_1361(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(8, new class_1376(this));
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
	public boolean method_5956() {
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
				itemStack.interactWithEntity(playerEntity, this, hand);
				return true;
			} else if (this.isSaddled() && !this.hasPassengers()) {
				if (!this.world.isClient) {
					playerEntity.startRiding(this);
				}

				return true;
			} else if (itemStack.getItem() == Items.field_8175) {
				itemStack.interactWithEntity(playerEntity, this, hand);
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
		if (!this.world.isClient && !this.invalid) {
			PigZombieEntity pigZombieEntity = new PigZombieEntity(this.world);
			pigZombieEntity.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(Items.field_8845));
			pigZombieEntity.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
			pigZombieEntity.setAiDisabled(this.isAiDisabled());
			if (this.hasCustomName()) {
				pigZombieEntity.setCustomName(this.getCustomName());
				pigZombieEntity.setCustomNameVisible(this.isCustomNameVisible());
			}

			this.world.spawnEntity(pigZombieEntity);
			this.invalidate();
		}
	}

	@Override
	public void method_6091(float f, float g, float h) {
		Entity entity = this.getPassengerList().isEmpty() ? null : (Entity)this.getPassengerList().get(0);
		if (this.hasPassengers() && this.method_5956()) {
			this.yaw = entity.yaw;
			this.prevYaw = this.yaw;
			this.pitch = entity.pitch * 0.5F;
			this.setRotation(this.yaw, this.pitch);
			this.field_6283 = this.yaw;
			this.headYaw = this.yaw;
			this.stepHeight = 1.0F;
			this.field_6281 = this.method_6029() * 0.1F;
			if (this.field_6814 && this.field_6812++ > this.field_6813) {
				this.field_6814 = false;
			}

			if (this.method_5787()) {
				float i = (float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue() * 0.225F;
				if (this.field_6814) {
					i += i * 1.15F * MathHelper.sin((float)this.field_6812 / (float)this.field_6813 * (float) Math.PI);
				}

				this.method_6125(i);
				super.method_6091(0.0F, 0.0F, 1.0F);
			} else {
				this.velocityX = 0.0;
				this.velocityY = 0.0;
				this.velocityZ = 0.0;
			}

			this.field_6211 = this.field_6225;
			double d = this.x - this.prevX;
			double e = this.z - this.prevZ;
			float j = MathHelper.sqrt(d * d + e * e) * 4.0F;
			if (j > 1.0F) {
				j = 1.0F;
			}

			this.field_6225 = this.field_6225 + (j - this.field_6225) * 0.4F;
			this.field_6249 = this.field_6249 + this.field_6225;
		} else {
			this.stepHeight = 0.5F;
			this.field_6281 = 0.02F;
			super.method_6091(f, g, h);
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

	public PigEntity createChild(PassiveEntity passiveEntity) {
		return new PigEntity(this.world);
	}

	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
		return BREEDING_INGREDIENT.matches(itemStack);
	}
}
