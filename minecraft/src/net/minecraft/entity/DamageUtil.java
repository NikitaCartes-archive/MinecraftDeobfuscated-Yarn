package net.minecraft.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

public class DamageUtil {
	public static final float field_29962 = 20.0F;
	public static final float field_29963 = 25.0F;
	public static final float field_29964 = 2.0F;
	public static final float field_29965 = 0.2F;
	private static final int field_29966 = 4;

	public static float getDamageLeft(LivingEntity armorWearer, float damageAmount, DamageSource damageSource, float armor, float armorToughness) {
		float f = 2.0F + armorToughness / 4.0F;
		float g = MathHelper.clamp(armor - damageAmount / f, armor * 0.2F, 20.0F);
		float h = g / 25.0F;
		ItemStack itemStack = damageSource.getWeaponStack();
		float i;
		if (itemStack != null && armorWearer.getWorld() instanceof ServerWorld serverWorld) {
			i = MathHelper.clamp(EnchantmentHelper.getArmorEffectiveness(serverWorld, itemStack, armorWearer, damageSource, h), 0.0F, 1.0F);
		} else {
			i = h;
		}

		float j = 1.0F - i;
		return damageAmount * j;
	}

	public static float getInflictedDamage(float damageDealt, float protection) {
		float f = MathHelper.clamp(protection, 0.0F, 20.0F);
		return damageDealt * (1.0F - f / 25.0F);
	}
}
