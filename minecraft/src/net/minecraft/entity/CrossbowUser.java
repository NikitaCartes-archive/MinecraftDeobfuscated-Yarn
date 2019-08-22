package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.item.ItemStack;

public interface CrossbowUser {
	void setCharging(boolean bl);

	void shoot(LivingEntity livingEntity, ItemStack itemStack, Projectile projectile, float f);

	@Nullable
	LivingEntity getTarget();
}
