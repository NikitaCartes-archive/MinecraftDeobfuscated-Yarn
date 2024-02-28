package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public interface CrossbowUser extends RangedAttackMob {
	void setCharging(boolean charging);

	@Nullable
	LivingEntity getTarget();

	void postShoot();

	default void shoot(LivingEntity entity, float speed) {
		Hand hand = ProjectileUtil.getHandPossiblyHolding(entity, Items.CROSSBOW);
		ItemStack itemStack = entity.getStackInHand(hand);
		if (itemStack.getItem() instanceof CrossbowItem crossbowItem) {
			crossbowItem.shootAll(entity.getWorld(), entity, hand, itemStack, speed, (float)(14 - entity.getWorld().getDifficulty().getId() * 4), this.getTarget());
		}

		this.postShoot();
	}
}
