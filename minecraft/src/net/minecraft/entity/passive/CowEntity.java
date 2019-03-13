package net.minecraft.entity.passive;

import net.minecraft.class_1394;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
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
	public CowEntity(EntityType<? extends CowEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		this.field_6201.add(0, new SwimGoal(this));
		this.field_6201.add(1, new EscapeDangerGoal(this, 2.0));
		this.field_6201.add(2, new AnimalMateGoal(this, 1.0));
		this.field_6201.add(3, new TemptGoal(this, 1.25, Ingredient.method_8091(Items.field_8861), false));
		this.field_6201.add(4, new FollowParentGoal(this, 1.25));
		this.field_6201.add(5, new class_1394(this, 1.0));
		this.field_6201.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.field_6201.add(7, new LookAroundGoal(this));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.2F);
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_14780;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_14597;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_14857;
	}

	@Override
	protected void method_5712(BlockPos blockPos, BlockState blockState) {
		this.method_5783(SoundEvents.field_15110, 0.15F, 1.0F);
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	public boolean method_5992(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		if (itemStack.getItem() == Items.field_8550 && !playerEntity.abilities.creativeMode && !this.isChild()) {
			playerEntity.method_5783(SoundEvents.field_14691, 1.0F, 1.0F);
			itemStack.subtractAmount(1);
			if (itemStack.isEmpty()) {
				playerEntity.method_6122(hand, new ItemStack(Items.field_8103));
			} else if (!playerEntity.inventory.method_7394(new ItemStack(Items.field_8103))) {
				playerEntity.method_7328(new ItemStack(Items.field_8103), false);
			}

			return true;
		} else {
			return super.method_5992(playerEntity, hand);
		}
	}

	public CowEntity method_6483(PassiveEntity passiveEntity) {
		return EntityType.COW.method_5883(this.field_6002);
	}

	@Override
	protected float method_18394(EntityPose entityPose, EntitySize entitySize) {
		return this.isChild() ? entitySize.height : 1.3F;
	}
}
