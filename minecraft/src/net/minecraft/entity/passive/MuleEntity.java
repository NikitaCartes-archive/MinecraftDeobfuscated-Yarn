package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class MuleEntity extends AbstractDonkeyEntity {
	public MuleEntity(EntityType<? extends MuleEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected SoundEvent method_5994() {
		super.method_5994();
		return SoundEvents.field_14614;
	}

	@Override
	protected SoundEvent method_6002() {
		super.method_6002();
		return SoundEvents.field_15158;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		super.method_6011(damageSource);
		return SoundEvents.field_14900;
	}

	@Override
	protected void method_6705() {
		this.method_5783(SoundEvents.field_15063, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
	}
}
