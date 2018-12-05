package net.minecraft.entity.passive;

import net.minecraft.class_1374;
import net.minecraft.class_1378;
import net.minecraft.class_3730;
import net.minecraft.advancement.criterion.CriterionCriterions;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.WaterCreatureEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.sortme.Living;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public abstract class FishEntity extends WaterCreatureEntity implements Living {
	private static final TrackedData<Boolean> field_6730 = DataTracker.registerData(FishEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	public FishEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.moveControl = new FishEntity.FishMoveControl(this);
	}

	@Override
	public float getEyeHeight() {
		return this.height * 0.65F;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(3.0);
	}

	@Override
	public boolean isPersistent() {
		return this.method_6453() || super.isPersistent();
	}

	@Override
	public boolean method_5979(IWorld iWorld, class_3730 arg) {
		BlockPos blockPos = new BlockPos(this);
		return iWorld.getBlockState(blockPos).getBlock() == Blocks.field_10382 && iWorld.getBlockState(blockPos.up()).getBlock() == Blocks.field_10382
			? super.method_5979(iWorld, arg)
			: false;
	}

	@Override
	public boolean method_5974(double d) {
		return !this.method_6453() && !this.hasCustomName();
	}

	@Override
	public int getLimitPerChunk() {
		return 8;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_6730, false);
	}

	private boolean method_6453() {
		return this.dataTracker.get(field_6730);
	}

	public void method_6454(boolean bl) {
		this.dataTracker.set(field_6730, bl);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putBoolean("FromBucket", this.method_6453());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.method_6454(compoundTag.getBoolean("FromBucket"));
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.goalSelector.add(0, new class_1374(this, 1.25));
		this.goalSelector.add(2, new FleeEntityGoal(this, PlayerEntity.class, 8.0F, 1.6, 1.4, EntityPredicates.EXCEPT_SPECTATOR));
		this.goalSelector.add(4, new FishEntity.class_1424(this));
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new SwimNavigation(this, world);
	}

	@Override
	public void method_6091(float f, float g, float h) {
		if (this.method_6034() && this.isInsideWater()) {
			this.method_5724(f, g, h, 0.01F);
			this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.9F;
			this.velocityY *= 0.9F;
			this.velocityZ *= 0.9F;
			if (this.getTarget() == null) {
				this.velocityY -= 0.005;
			}
		} else {
			super.method_6091(f, g, h);
		}
	}

	@Override
	public void updateMovement() {
		if (!this.isInsideWater() && this.onGround && this.field_5992) {
			this.velocityY += 0.4F;
			this.velocityX = this.velocityX + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F);
			this.velocityZ = this.velocityZ + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.05F);
			this.onGround = false;
			this.velocityDirty = true;
			this.playSoundAtEntity(this.method_6457(), this.getSoundVolume(), this.getSoundPitch());
		}

		super.updateMovement();
	}

	@Override
	protected boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.field_8705 && this.isValid()) {
			this.playSoundAtEntity(SoundEvents.field_14568, 1.0F, 1.0F);
			itemStack.subtractAmount(1);
			ItemStack itemStack2 = this.method_6452();
			this.method_6455(itemStack2);
			if (!this.world.isRemote) {
				CriterionCriterions.FILLED_BUCKET.method_8932((ServerPlayerEntity)playerEntity, itemStack2);
			}

			if (itemStack.isEmpty()) {
				playerEntity.setStackInHand(hand, itemStack2);
			} else if (!playerEntity.inventory.insertStack(itemStack2)) {
				playerEntity.dropItem(itemStack2, false);
			}

			this.invalidate();
			return true;
		} else {
			return super.interactMob(playerEntity, hand);
		}
	}

	protected void method_6455(ItemStack itemStack) {
		if (this.hasCustomName()) {
			itemStack.setDisplayName(this.getCustomName());
		}
	}

	protected abstract ItemStack method_6452();

	protected boolean method_6456() {
		return true;
	}

	protected abstract SoundEvent method_6457();

	@Override
	protected SoundEvent getSoundSwim() {
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
				this.fish.velocityY += 0.005;
			}

			if (this.field_6374 == MoveControl.class_1336.field_6378 && !this.fish.getNavigation().method_6357()) {
				double d = this.field_6370 - this.fish.x;
				double e = this.field_6369 - this.fish.y;
				double f = this.field_6367 - this.fish.z;
				double g = (double)MathHelper.sqrt(d * d + e * e + f * f);
				e /= g;
				float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
				this.fish.yaw = this.method_6238(this.fish.yaw, h, 90.0F);
				this.fish.field_6283 = this.fish.yaw;
				float i = (float)(this.field_6372 * this.fish.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
				this.fish.method_6125(MathHelper.lerp(0.125F, this.fish.method_6029(), i));
				this.fish.velocityY = this.fish.velocityY + (double)this.fish.method_6029() * e * 0.1;
			} else {
				this.fish.method_6125(0.0F);
			}
		}
	}

	static class class_1424 extends class_1378 {
		private final FishEntity field_6732;

		public class_1424(FishEntity fishEntity) {
			super(fishEntity, 1.0, 40);
			this.field_6732 = fishEntity;
		}

		@Override
		public boolean canStart() {
			return this.field_6732.method_6456() && super.canStart();
		}
	}
}
