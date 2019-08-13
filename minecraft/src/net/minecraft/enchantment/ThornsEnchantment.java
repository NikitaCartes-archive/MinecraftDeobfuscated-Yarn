package net.minecraft.enchantment;

import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class ThornsEnchantment extends Enchantment {
	public ThornsEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.field_9071, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 10 + 20 * (i - 1);
	}

	@Override
	public int getMaximumPower(int i) {
		return super.getMinimumPower(i) + 50;
	}

	@Override
	public int getMaximumLevel() {
		return 3;
	}

	@Override
	public boolean isAcceptableItem(ItemStack itemStack) {
		return itemStack.getItem() instanceof ArmorItem ? true : super.isAcceptableItem(itemStack);
	}

	@Override
	public void onUserDamaged(LivingEntity livingEntity, Entity entity, int i) {
		Random random = livingEntity.getRand();
		Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.getRandomEnchantedEquipment(Enchantments.field_9097, livingEntity);
		if (shouldDamageAttacker(i, random)) {
			if (entity != null) {
				entity.damage(DamageSource.thorns(livingEntity), (float)getDamageAmount(i, random));
			}

			if (entry != null) {
				((ItemStack)entry.getValue()).damage(3, livingEntity, livingEntityx -> livingEntityx.sendEquipmentBreakStatus((EquipmentSlot)entry.getKey()));
			}
		} else if (entry != null) {
			((ItemStack)entry.getValue()).damage(1, livingEntity, livingEntityx -> livingEntityx.sendEquipmentBreakStatus((EquipmentSlot)entry.getKey()));
		}
	}

	public static boolean shouldDamageAttacker(int i, Random random) {
		return i <= 0 ? false : random.nextFloat() < 0.15F * (float)i;
	}

	public static int getDamageAmount(int i, Random random) {
		return i > 10 ? i - 10 : 1 + random.nextInt(4);
	}
}
