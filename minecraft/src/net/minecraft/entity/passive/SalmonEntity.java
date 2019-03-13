package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class SalmonEntity extends SchoolingFishEntity {
	public SalmonEntity(EntityType<? extends SalmonEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public int getMaxGroupSize() {
		return 5;
	}

	@Override
	protected ItemStack method_6452() {
		return new ItemStack(Items.field_8714);
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_15033;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_15123;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_14638;
	}

	@Override
	protected SoundEvent method_6457() {
		return SoundEvents.field_14563;
	}
}
