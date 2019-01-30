package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1394;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SnowmanEntity extends GolemEntity implements RangedAttacker {
	private static final TrackedData<Byte> SNOWMAN_FLAGS = DataTracker.registerData(SnowmanEntity.class, TrackedDataHandlerRegistry.BYTE);

	public SnowmanEntity(World world) {
		super(EntityType.SNOW_GOLEM, world);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new ProjectileAttackGoal(this, 1.25, 20, 10.0F));
		this.goalSelector.add(2, new class_1394(this, 1.0, 1.0000001E-5F));
		this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(4, new LookAroundGoal(this));
		this.targetSelector.add(1, new FollowTargetGoal(this, MobEntity.class, 10, true, false, Monster.field_7270));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(4.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.2F);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(SNOWMAN_FLAGS, (byte)16);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putBoolean("Pumpkin", this.hasPumpkin());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("Pumpkin")) {
			this.setHasPumpkin(compoundTag.getBoolean("Pumpkin"));
		}
	}

	@Override
	public void updateMovement() {
		super.updateMovement();
		if (!this.world.isClient) {
			int i = MathHelper.floor(this.x);
			int j = MathHelper.floor(this.y);
			int k = MathHelper.floor(this.z);
			if (this.isTouchingWater()) {
				this.damage(DamageSource.DROWN, 1.0F);
			}

			if (this.world.getBiome(new BlockPos(i, 0, k)).getTemperature(new BlockPos(i, j, k)) > 1.0F) {
				this.damage(DamageSource.ON_FIRE, 1.0F);
			}

			if (!this.world.getGameRules().getBoolean("mobGriefing")) {
				return;
			}

			BlockState blockState = Blocks.field_10477.getDefaultState();

			for (int l = 0; l < 4; l++) {
				i = MathHelper.floor(this.x + (double)((float)(l % 2 * 2 - 1) * 0.25F));
				j = MathHelper.floor(this.y);
				k = MathHelper.floor(this.z + (double)((float)(l / 2 % 2 * 2 - 1) * 0.25F));
				BlockPos blockPos = new BlockPos(i, j, k);
				if (this.world.getBlockState(blockPos).isAir()
					&& this.world.getBiome(blockPos).getTemperature(blockPos) < 0.8F
					&& blockState.canPlaceAt(this.world, blockPos)) {
					this.world.setBlockState(blockPos, blockState);
				}
			}
		}
	}

	@Override
	public void attack(LivingEntity livingEntity, float f) {
		SnowballEntity snowballEntity = new SnowballEntity(this.world, this);
		double d = livingEntity.y + (double)livingEntity.getEyeHeight() - 1.1F;
		double e = livingEntity.x - this.x;
		double g = d - snowballEntity.y;
		double h = livingEntity.z - this.z;
		float i = MathHelper.sqrt(e * e + h * h) * 0.2F;
		snowballEntity.setVelocity(e, g + (double)i, h, 1.6F, 12.0F);
		this.playSound(SoundEvents.field_14745, 1.0F, 1.0F / (this.getRand().nextFloat() * 0.4F + 0.8F));
		this.world.spawnEntity(snowballEntity);
	}

	@Override
	public float getEyeHeight() {
		return 1.7F;
	}

	@Override
	protected boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.field_8868 && this.hasPumpkin() && !this.world.isClient) {
			this.setHasPumpkin(false);
			itemStack.applyDamage(1, playerEntity);
		}

		return super.interactMob(playerEntity, hand);
	}

	public boolean hasPumpkin() {
		return (this.dataTracker.get(SNOWMAN_FLAGS) & 16) != 0;
	}

	public void setHasPumpkin(boolean bl) {
		byte b = this.dataTracker.get(SNOWMAN_FLAGS);
		if (bl) {
			this.dataTracker.set(SNOWMAN_FLAGS, (byte)(b | 16));
		} else {
			this.dataTracker.set(SNOWMAN_FLAGS, (byte)(b & -17));
		}
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14655;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14830;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14594;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasArmsRaised() {
		return false;
	}

	@Override
	public void setArmsRaised(boolean bl) {
	}
}
