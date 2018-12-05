package net.minecraft.entity.passive;

import net.minecraft.class_1425;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class SalmonEntity extends class_1425 {
	public SalmonEntity(World world) {
		super(EntityType.SALMON, world);
		this.setSize(0.7F, 0.4F);
	}

	@Override
	public int method_6465() {
		return 5;
	}

	@Override
	protected ItemStack method_6452() {
		return new ItemStack(Items.field_8714);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_15033;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15123;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14638;
	}

	@Override
	protected SoundEvent method_6457() {
		return SoundEvents.field_14563;
	}
}
