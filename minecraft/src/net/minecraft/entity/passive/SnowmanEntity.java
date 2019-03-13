package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1394;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
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
	private static final TrackedData<Byte> field_6873 = DataTracker.registerData(SnowmanEntity.class, TrackedDataHandlerRegistry.BYTE);

	public SnowmanEntity(EntityType<? extends SnowmanEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		this.field_6201.add(1, new ProjectileAttackGoal(this, 1.25, 20, 10.0F));
		this.field_6201.add(2, new class_1394(this, 1.0, 1.0000001E-5F));
		this.field_6201.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.field_6201.add(4, new LookAroundGoal(this));
		this.field_6185.add(1, new FollowTargetGoal(this, MobEntity.class, 10, true, false, livingEntity -> livingEntity instanceof Monster));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(4.0);
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.2F);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_6873, (byte)16);
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putBoolean("Pumpkin", this.hasPumpkin());
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		if (compoundTag.containsKey("Pumpkin")) {
			this.setHasPumpkin(compoundTag.getBoolean("Pumpkin"));
		}
	}

	@Override
	public void updateMovement() {
		super.updateMovement();
		if (!this.field_6002.isClient) {
			int i = MathHelper.floor(this.x);
			int j = MathHelper.floor(this.y);
			int k = MathHelper.floor(this.z);
			if (this.isTouchingWater()) {
				this.damage(DamageSource.DROWN, 1.0F);
			}

			if (this.field_6002.method_8310(new BlockPos(i, 0, k)).method_8707(new BlockPos(i, j, k)) > 1.0F) {
				this.damage(DamageSource.ON_FIRE, 1.0F);
			}

			if (!this.field_6002.getGameRules().getBoolean("mobGriefing")) {
				return;
			}

			BlockState blockState = Blocks.field_10477.method_9564();

			for (int l = 0; l < 4; l++) {
				i = MathHelper.floor(this.x + (double)((float)(l % 2 * 2 - 1) * 0.25F));
				j = MathHelper.floor(this.y);
				k = MathHelper.floor(this.z + (double)((float)(l / 2 % 2 * 2 - 1) * 0.25F));
				BlockPos blockPos = new BlockPos(i, j, k);
				if (this.field_6002.method_8320(blockPos).isAir()
					&& this.field_6002.method_8310(blockPos).method_8707(blockPos) < 0.8F
					&& blockState.method_11591(this.field_6002, blockPos)) {
					this.field_6002.method_8501(blockPos, blockState);
				}
			}
		}
	}

	@Override
	public void attack(LivingEntity livingEntity, float f) {
		SnowballEntity snowballEntity = new SnowballEntity(this.field_6002, this);
		double d = livingEntity.y + (double)livingEntity.getStandingEyeHeight() - 1.1F;
		double e = livingEntity.x - this.x;
		double g = d - snowballEntity.y;
		double h = livingEntity.z - this.z;
		float i = MathHelper.sqrt(e * e + h * h) * 0.2F;
		snowballEntity.setVelocity(e, g + (double)i, h, 1.6F, 12.0F);
		this.method_5783(SoundEvents.field_14745, 1.0F, 1.0F / (this.getRand().nextFloat() * 0.4F + 0.8F));
		this.field_6002.spawnEntity(snowballEntity);
	}

	@Override
	protected float method_18394(EntityPose entityPose, EntitySize entitySize) {
		return 1.7F;
	}

	@Override
	protected boolean method_5992(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		if (itemStack.getItem() == Items.field_8868 && this.hasPumpkin() && !this.field_6002.isClient) {
			this.setHasPumpkin(false);
			itemStack.applyDamage(1, playerEntity);
		}

		return super.method_5992(playerEntity, hand);
	}

	public boolean hasPumpkin() {
		return (this.field_6011.get(field_6873) & 16) != 0;
	}

	public void setHasPumpkin(boolean bl) {
		byte b = this.field_6011.get(field_6873);
		if (bl) {
			this.field_6011.set(field_6873, (byte)(b | 16));
		} else {
			this.field_6011.set(field_6873, (byte)(b & -17));
		}
	}

	@Nullable
	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_14655;
	}

	@Nullable
	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_14830;
	}

	@Nullable
	@Override
	protected SoundEvent method_6002() {
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
