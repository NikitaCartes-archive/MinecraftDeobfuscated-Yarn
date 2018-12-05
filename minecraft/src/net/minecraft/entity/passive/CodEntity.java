package net.minecraft.entity.passive;

import net.minecraft.class_1425;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class CodEntity extends class_1425 {
	public CodEntity(World world) {
		super(EntityType.COD, world);
		this.setSize(0.5F, 0.3F);
	}

	@Override
	protected ItemStack method_6452() {
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
	protected SoundEvent method_6457() {
		return SoundEvents.field_14918;
	}
}
