package net.minecraft.enchantment;

import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class ThornsEnchantment extends Enchantment {
	public ThornsEnchantment(Enchantment.Weight weight, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.CHEST, equipmentSlots);
	}

	@Override
	public int getMinimumPower(int i) {
		return 10 + 20 * (i - 1);
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
		ItemStack itemStack = EnchantmentHelper.getRandomEnchantedEquipment(Enchantments.field_9097, livingEntity);
		if (method_8243(i, random)) {
			if (entity != null) {
				entity.damage(DamageSource.method_5513(livingEntity), (float)method_8242(i, random));
			}

			if (!itemStack.isEmpty()) {
				itemStack.applyDamage(3, livingEntity);
			}
		} else if (!itemStack.isEmpty()) {
			itemStack.applyDamage(1, livingEntity);
		}
	}

	public static boolean method_8243(int i, Random random) {
		return i <= 0 ? false : random.nextFloat() < 0.15F * (float)i;
	}

	public static int method_8242(int i, Random random) {
		return i > 10 ? i - 10 : 1 + random.nextInt(4);
	}
}
