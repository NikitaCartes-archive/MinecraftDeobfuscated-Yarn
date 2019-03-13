package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.class_1394;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PigEntity extends AnimalEntity {
	private static final TrackedData<Boolean> field_6816 = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> field_6815 = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final Ingredient field_6817 = Ingredient.method_8091(Items.field_8179, Items.field_8567, Items.field_8186);
	private boolean field_6814;
	private int field_6812;
	private int field_6813;

	public PigEntity(EntityType<? extends PigEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		this.field_6201.add(0, new SwimGoal(this));
		this.field_6201.add(1, new EscapeDangerGoal(this, 1.25));
		this.field_6201.add(3, new AnimalMateGoal(this, 1.0));
		this.field_6201.add(4, new TemptGoal(this, 1.2, Ingredient.method_8091(Items.field_8184), false));
		this.field_6201.add(4, new TemptGoal(this, 1.2, false, field_6817));
		this.field_6201.add(5, new FollowParentGoal(this, 1.1));
		this.field_6201.add(6, new class_1394(this, 1.0));
		this.field_6201.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.field_6201.add(8, new LookAroundGoal(this));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
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
			return playerEntity.method_6047().getItem() == Items.field_8184 || playerEntity.method_6079().getItem() == Items.field_8184;
		}
	}

	@Override
	public void method_5674(TrackedData<?> trackedData) {
		if (field_6815.equals(trackedData) && this.field_6002.isClient) {
			this.field_6814 = true;
			this.field_6812 = 0;
			this.field_6813 = this.field_6011.get(field_6815);
		}

		super.method_5674(trackedData);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_6816, false);
		this.field_6011.startTracking(field_6815, 0);
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putBoolean("Saddle", this.isSaddled());
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.setSaddled(compoundTag.getBoolean("Saddle"));
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_14615;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_14750;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_14689;
	}

	@Override
	protected void method_5712(BlockPos blockPos, BlockState blockState) {
		this.method_5783(SoundEvents.field_14894, 0.15F, 1.0F);
	}

	@Override
	public boolean method_5992(PlayerEntity playerEntity, Hand hand) {
		if (!super.method_5992(playerEntity, hand)) {
			ItemStack itemStack = playerEntity.method_5998(hand);
			if (itemStack.getItem() == Items.field_8448) {
				itemStack.interactWithEntity(playerEntity, this, hand);
				return true;
			} else if (this.isSaddled() && !this.hasPassengers()) {
				if (!this.field_6002.isClient) {
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
			this.method_5706(Items.field_8175);
		}
	}

	public boolean isSaddled() {
		return this.field_6011.get(field_6816);
	}

	public void setSaddled(boolean bl) {
		if (bl) {
			this.field_6011.set(field_6816, true);
		} else {
			this.field_6011.set(field_6816, false);
		}
	}

	@Override
	public void method_5800(LightningEntity lightningEntity) {
		PigZombieEntity pigZombieEntity = EntityType.ZOMBIE_PIGMAN.method_5883(this.field_6002);
		pigZombieEntity.method_5673(EquipmentSlot.HAND_MAIN, new ItemStack(Items.field_8845));
		pigZombieEntity.setPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
		pigZombieEntity.setAiDisabled(this.isAiDisabled());
		if (this.hasCustomName()) {
			pigZombieEntity.method_5665(this.method_5797());
			pigZombieEntity.setCustomNameVisible(this.isCustomNameVisible());
		}

		this.field_6002.spawnEntity(pigZombieEntity);
		this.invalidate();
	}

	@Override
	public void method_6091(Vec3d vec3d) {
		if (this.isValid()) {
			Entity entity = this.getPassengerList().isEmpty() ? null : (Entity)this.getPassengerList().get(0);
			if (this.hasPassengers() && this.method_5956()) {
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

				if (this.method_5787()) {
					float f = (float)this.method_5996(EntityAttributes.MOVEMENT_SPEED).getValue() * 0.225F;
					if (this.field_6814) {
						f += f * 1.15F * MathHelper.sin((float)this.field_6812 / (float)this.field_6813 * (float) Math.PI);
					}

					this.setMovementSpeed(f);
					super.method_6091(new Vec3d(0.0, 0.0, 1.0));
				} else {
					this.method_18799(Vec3d.ZERO);
				}

				this.field_6211 = this.field_6225;
				double d = this.x - this.prevX;
				double e = this.z - this.prevZ;
				float g = MathHelper.sqrt(d * d + e * e) * 4.0F;
				if (g > 1.0F) {
					g = 1.0F;
				}

				this.field_6225 = this.field_6225 + (g - this.field_6225) * 0.4F;
				this.field_6249 = this.field_6249 + this.field_6225;
			} else {
				this.stepHeight = 0.5F;
				this.field_6281 = 0.02F;
				super.method_6091(vec3d);
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
			this.method_5841().set(field_6815, this.field_6813);
			return true;
		}
	}

	public PigEntity method_6574(PassiveEntity passiveEntity) {
		return EntityType.PIG.method_5883(this.field_6002);
	}

	@Override
	public boolean method_6481(ItemStack itemStack) {
		return field_6817.method_8093(itemStack);
	}
}
