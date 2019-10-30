package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.item.ItemStack;

public interface CrossbowUser {
	void setCharging(boolean charging);

	void shoot(LivingEntity target, ItemStack crossbow, Projectile projectile, float multiShotSpray);

	@Nullable
	LivingEntity getTarget();
}
