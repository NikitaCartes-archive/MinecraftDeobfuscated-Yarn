package net.minecraft.entity;

import net.minecraft.entity.projectile.Projectile;
import net.minecraft.item.ItemStack;

public interface CrossbowUser {
	void setCharging(boolean bl);

	void method_18811(LivingEntity livingEntity, ItemStack itemStack, Projectile projectile, float f);

	LivingEntity getTarget();
}
