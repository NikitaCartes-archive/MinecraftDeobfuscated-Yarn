package net.minecraft.entity.passive;

import net.minecraft.class_1361;
import net.minecraft.class_1374;
import net.minecraft.class_1376;
import net.minecraft.class_1394;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CowEntity extends AnimalEntity {
	protected CowEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.setSize(0.9F, 1.4F);
	}

	public CowEntity(World world) {
		this(EntityType.COW, world);
	}

	@Override
	protected void method_5959() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new class_1374(this, 2.0));
		this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
		this.goalSelector.add(3, new TemptGoal(this, 1.25, Ingredient.method_8091(Items.field_8861), false));
		this.goalSelector.add(4, new FollowParentGoal(this, 1.25));
		this.goalSelector.add(5, new class_1394(this, 1.0));
		this.goalSelector.add(6, new class_1361(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(7, new class_1376(this));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.2F);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14780;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14597;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14857;
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSoundAtEntity(SoundEvents.field_15110, 0.15F, 1.0F);
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.field_8550 && !playerEntity.abilities.creativeMode && !this.isChild()) {
			playerEntity.playSoundAtEntity(SoundEvents.field_14691, 1.0F, 1.0F);
			itemStack.subtractAmount(1);
			if (itemStack.isEmpty()) {
				playerEntity.setStackInHand(hand, new ItemStack(Items.field_8103));
			} else if (!playerEntity.inventory.insertStack(new ItemStack(Items.field_8103))) {
				playerEntity.dropItem(new ItemStack(Items.field_8103), false);
			}

			return true;
		} else {
			return super.interactMob(playerEntity, hand);
		}
	}

	public CowEntity createChild(PassiveEntity passiveEntity) {
		return new CowEntity(this.world);
	}

	@Override
	public float getEyeHeight() {
		return this.isChild() ? this.height : 1.3F;
	}
}
