package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class CodEntity extends SchoolingFishEntity {
	public CodEntity(EntityType<? extends CodEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected ItemStack getFishBucketItem() {
		return new ItemStack(Items.field_8666);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_15083;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15003;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14851;
	}

	@Override
	protected SoundEvent getFlopSound() {
		return SoundEvents.field_14918;
	}
}
