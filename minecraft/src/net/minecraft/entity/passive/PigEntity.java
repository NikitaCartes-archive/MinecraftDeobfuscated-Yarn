package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.class_4980;
import net.minecraft.class_4981;
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

public class PigEntity extends AnimalEntity implements class_4981 {
	private static final TrackedData<Boolean> SADDLED = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> field_6815 = DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final Ingredient BREEDING_INGREDIENT = Ingredient.ofItems(Items.CARROT, Items.POTATO, Items.BEETROOT);
	private final class_4980 field_23230 = new class_4980(this.dataTracker, field_6815, SADDLED);

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
		if (field_6815.equals(data) && this.world.isClient) {
			this.field_23230.method_26307();
		}

		super.onTrackedDataSet(data);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(SADDLED, false);
		this.dataTracker.startTracking(field_6815, 0);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		this.field_23230.method_26309(tag);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.field_23230.method_26312(tag);
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
		return !super.interactMob(player, hand) ? this.method_26314(this, player, hand, true) : true;
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
		return this.field_23230.method_26311();
	}

	@Override
	public void setSaddled(boolean bl) {
		this.field_23230.method_26310(bl);
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
		if (this.method_26313(this, this.field_23230, movementInput)) {
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
	public float method_26316() {
		return (float)this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue() * 0.225F;
	}

	@Override
	public void method_26315(Vec3d vec3d) {
		super.travel(vec3d);
	}

	@Override
	public boolean method_6577() {
		return this.field_23230.method_26308(this.getRandom());
	}

	public PigEntity createChild(PassiveEntity passiveEntity) {
		return EntityType.PIG.create(this.world);
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return BREEDING_INGREDIENT.test(stack);
	}
}
