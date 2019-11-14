package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
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
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class SnowGolemEntity extends GolemEntity implements RangedAttackMob {
	private static final TrackedData<Byte> SNOW_GOLEM_FLAGS = DataTracker.registerData(SnowGolemEntity.class, TrackedDataHandlerRegistry.BYTE);

	public SnowGolemEntity(EntityType<? extends SnowGolemEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new ProjectileAttackGoal(this, 1.25, 20, 10.0F));
		this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0, 1.0000001E-5F));
		this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(4, new LookAroundGoal(this));
		this.targetSelector.add(1, new FollowTargetGoal(this, MobEntity.class, 10, true, false, livingEntity -> livingEntity instanceof Monster));
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
		this.dataTracker.startTracking(SNOW_GOLEM_FLAGS, (byte)16);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putBoolean("Pumpkin", this.hasPumpkin());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		if (tag.contains("Pumpkin")) {
			this.setHasPumpkin(tag.getBoolean("Pumpkin"));
		}
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (!this.world.isClient) {
			int i = MathHelper.floor(this.getX());
			int j = MathHelper.floor(this.getY());
			int k = MathHelper.floor(this.getZ());
			if (this.isTouchingWater()) {
				this.damage(DamageSource.DROWN, 1.0F);
			}

			if (this.world.method_23753(new BlockPos(i, 0, k)).getTemperature(new BlockPos(i, j, k)) > 1.0F) {
				this.damage(DamageSource.ON_FIRE, 1.0F);
			}

			if (!this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
				return;
			}

			BlockState blockState = Blocks.SNOW.getDefaultState();

			for (int l = 0; l < 4; l++) {
				i = MathHelper.floor(this.getX() + (double)((float)(l % 2 * 2 - 1) * 0.25F));
				j = MathHelper.floor(this.getY());
				k = MathHelper.floor(this.getZ() + (double)((float)(l / 2 % 2 * 2 - 1) * 0.25F));
				BlockPos blockPos = new BlockPos(i, j, k);
				if (this.world.getBlockState(blockPos).isAir()
					&& this.world.method_23753(blockPos).getTemperature(blockPos) < 0.8F
					&& blockState.canPlaceAt(this.world, blockPos)) {
					this.world.setBlockState(blockPos, blockState);
				}
			}
		}
	}

	@Override
	public void attack(LivingEntity target, float f) {
		SnowballEntity snowballEntity = new SnowballEntity(this.world, this);
		double d = target.getEyeY() - 1.1F;
		double e = target.getX() - this.getX();
		double g = d - snowballEntity.getY();
		double h = target.getZ() - this.getZ();
		float i = MathHelper.sqrt(e * e + h * h) * 0.2F;
		snowballEntity.setVelocity(e, g + (double)i, h, 1.6F, 12.0F);
		this.playSound(SoundEvents.ENTITY_SNOW_GOLEM_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
		this.world.spawnEntity(snowballEntity);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return 1.7F;
	}

	@Override
	protected boolean interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.getItem() == Items.SHEARS && this.hasPumpkin()) {
			if (!this.world.isClient) {
				this.setHasPumpkin(false);
				itemStack.damage(1, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean hasPumpkin() {
		return (this.dataTracker.get(SNOW_GOLEM_FLAGS) & 16) != 0;
	}

	public void setHasPumpkin(boolean hasPumpkin) {
		byte b = this.dataTracker.get(SNOW_GOLEM_FLAGS);
		if (hasPumpkin) {
			this.dataTracker.set(SNOW_GOLEM_FLAGS, (byte)(b | 16));
		} else {
			this.dataTracker.set(SNOW_GOLEM_FLAGS, (byte)(b & -17));
		}
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_SNOW_GOLEM_AMBIENT;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_SNOW_GOLEM_HURT;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SNOW_GOLEM_DEATH;
	}
}
