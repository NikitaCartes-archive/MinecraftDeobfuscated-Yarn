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
	protected ItemStack method_6452() {
		return new ItemStack(Items.field_8666);
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_15083;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_15003;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_14851;
	}

	@Override
	protected SoundEvent method_6457() {
		return SoundEvents.field_14918;
	}
}
