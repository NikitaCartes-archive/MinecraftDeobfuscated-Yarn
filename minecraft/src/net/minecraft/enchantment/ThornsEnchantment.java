package net.minecraft.enchantment;

import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;

public class ThornsEnchantment extends Enchantment {
	private static final float ATTACK_CHANCE_PER_LEVEL = 0.15F;

	public ThornsEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.ARMOR_CHEST, slotTypes);
	}

	@Override
	public int getMinPower(int level) {
		return 10 + 20 * (level - 1);
	}

	@Override
	public int getMaxPower(int level) {
		return super.getMinPower(level) + 50;
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		return stack.getItem() instanceof ArmorItem ? true : super.isAcceptableItem(stack);
	}

	@Override
	public void onUserDamaged(LivingEntity user, Entity attacker, int level) {
		Random random = user.getRandom();
		Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.chooseEquipmentWith(Enchantments.THORNS, user);
		if (shouldDamageAttacker(level, random)) {
			if (attacker != null) {
				attacker.damage(user.getDamageSources().thorns(user), (float)getDamageAmount(level, random));
			}

			if (entry != null) {
				((ItemStack)entry.getValue()).damage(2, user, entity -> entity.sendEquipmentBreakStatus((EquipmentSlot)entry.getKey()));
			}
		}
	}

	public static boolean shouldDamageAttacker(int level, Random random) {
		return level <= 0 ? false : random.nextFloat() < 0.15F * (float)level;
	}

	public static int getDamageAmount(int level, Random random) {
		return level > 10 ? level - 10 : 1 + random.nextInt(4);
	}
}
