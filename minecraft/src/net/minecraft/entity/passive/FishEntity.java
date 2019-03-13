package net.minecraft.entity.passive;

import net.minecraft.class_1378;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.WaterCreatureEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class FishEntity extends WaterCreatureEntity {
	private static final TrackedData<Boolean> field_6730 = DataTracker.registerData(FishEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	public FishEntity(EntityType<? extends FishEntity> entityType, World world) {
		super(entityType, world);
		this.field_6207 = new FishEntity.FishMoveControl(this);
	}

	@Override
	protected float method_18394(EntityPose entityPose, EntitySize entitySize) {
		return entitySize.height * 0.65F;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(3.0);
	}

	@Override
	public boolean cannotDespawn() {
		return this.isFromBucket();
	}

	@Override
	public boolean method_5979(IWorld iWorld, SpawnType spawnType) {
		BlockPos blockPos = new BlockPos(this);
		return iWorld.method_8320(blockPos).getBlock() == Blocks.field_10382 && iWorld.method_8320(blockPos.up()).getBlock() == Blocks.field_10382
			? super.method_5979(iWorld, spawnType)
			: false;
	}

	@Override
	public boolean canImmediatelyDespawn(double d) {
		return !this.isFromBucket() && !this.hasCustomName();
	}

	@Override
	public int getLimitPerChunk() {
		return 8;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_6730, false);
	}

	private boolean isFromBucket() {
		return this.field_6011.get(field_6730);
	}

	public void setFromBucket(boolean bl) {
		this.field_6011.set(field_6730, bl);
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putBoolean("FromBucket", this.isFromBucket());
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.setFromBucket(compoundTag.getBoolean("FromBucket"));
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.field_6201.add(0, new EscapeDangerGoal(this, 1.25));
		this.field_6201.add(2, new FleeEntityGoal(this, PlayerEntity.class, 8.0F, 1.6, 1.4, EntityPredicates.EXCEPT_SPECTATOR::test));
		this.field_6201.add(4, new FishEntity.SwimToRandomPlaceGoal(this));
	}

	@Override
	protected EntityNavigation method_5965(World world) {
		return new SwimNavigation(this, world);
	}

	@Override
	public void method_6091(Vec3d vec3d) {
		if (this.method_6034() && this.isInsideWater()) {
			this.method_5724(0.01F, vec3d);
			this.method_5784(MovementType.field_6308, this.method_18798());
			this.method_18799(this.method_18798().multiply(0.9));
			if (this.getTarget() == null) {
				this.method_18799(this.method_18798().add(0.0, -0.005, 0.0));
			}
		} else {
			super.method_6091(vec3d);
		}
	}

	@Override
	public void updateMovement() {
		if (!this.isInsideWater() && this.onGround && this.verticalCollision) {
			this.method_18799(
				this.method_18798().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F), 0.4F, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F))
			);
			this.onGround = false;
			this.velocityDirty = true;
			this.method_5783(this.method_6457(), this.getSoundVolume(), this.getSoundPitch());
		}

		super.updateMovement();
	}

	@Override
	protected boolean method_5992(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		if (itemStack.getItem() == Items.field_8705 && this.isValid()) {
			this.method_5783(SoundEvents.field_14568, 1.0F, 1.0F);
			itemStack.subtractAmount(1);
			ItemStack itemStack2 = this.method_6452();
			this.method_6455(itemStack2);
			if (!this.field_6002.isClient) {
				Criterions.FILLED_BUCKET.method_8932((ServerPlayerEntity)playerEntity, itemStack2);
			}

			if (itemStack.isEmpty()) {
				playerEntity.method_6122(hand, itemStack2);
			} else if (!playerEntity.inventory.method_7394(itemStack2)) {
				playerEntity.method_7328(itemStack2, false);
			}

			this.invalidate();
			return true;
		} else {
			return super.method_5992(playerEntity, hand);
		}
	}

	protected void method_6455(ItemStack itemStack) {
		if (this.hasCustomName()) {
			itemStack.method_7977(this.method_5797());
		}
	}

	protected abstract ItemStack method_6452();

	protected boolean hasSelfControl() {
		return true;
	}

	protected abstract SoundEvent method_6457();

	@Override
	protected SoundEvent method_5737() {
		return SoundEvents.field_14591;
	}

	static class FishMoveControl extends MoveControl {
		private final FishEntity fish;

		FishMoveControl(FishEntity fishEntity) {
			super(fishEntity);
			this.fish = fishEntity;
		}

		@Override
		public void tick() {
			if (this.fish.method_5777(FluidTags.field_15517)) {
				this.fish.method_18799(this.fish.method_18798().add(0.0, 0.005, 0.0));
			}

			if (this.state == MoveControl.State.field_6378 && !this.fish.method_5942().isIdle()) {
				double d = this.targetX - this.fish.x;
				double e = this.targetY - this.fish.y;
				double f = this.targetZ - this.fish.z;
				double g = (double)MathHelper.sqrt(d * d + e * e + f * f);
				e /= g;
				float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
				this.fish.yaw = this.method_6238(this.fish.yaw, h, 90.0F);
				this.fish.field_6283 = this.fish.yaw;
				float i = (float)(this.speed * this.fish.method_5996(EntityAttributes.MOVEMENT_SPEED).getValue());
				this.fish.setMovementSpeed(MathHelper.lerp(0.125F, this.fish.getMovementSpeed(), i));
				this.fish.method_18799(this.fish.method_18798().add(0.0, (double)this.fish.getMovementSpeed() * e * 0.1, 0.0));
			} else {
				this.fish.setMovementSpeed(0.0F);
			}
		}
	}

	static class SwimToRandomPlaceGoal extends class_1378 {
		private final FishEntity field_6732;

		public SwimToRandomPlaceGoal(FishEntity fishEntity) {
			super(fishEntity, 1.0, 40);
			this.field_6732 = fishEntity;
		}

		@Override
		public boolean canStart() {
			return this.field_6732.hasSelfControl() && super.canStart();
		}
	}
}
