package net.minecraft.enchantment;

import net.minecraft.class_1310;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;

public class DamageEnchantment extends Enchantment {
	private static final String[] field_9065 = new String[]{"all", "undead", "arthropods"};
	private static final int[] field_9063 = new int[]{1, 5, 5};
	private static final int[] field_9066 = new int[]{11, 8, 8};
	private static final int[] field_9064 = new int[]{20, 20, 20};
	public final int field_9067;

	public DamageEnchantment(Enchantment.Weight weight, int i, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.WEAPON, equipmentSlots);
		this.field_9067 = i;
	}

	@Override
	public int getMinimumPower(int i) {
		return field_9063[this.field_9067] + (i - 1) * field_9066[this.field_9067];
	}

	@Override
	public int getMaximumPower(int i) {
		return this.getMinimumPower(i) + field_9064[this.field_9067];
	}

	@Override
	public int getMaximumLevel() {
		return 5;
	}

	@Override
	public float getAttackDamage(int i, class_1310 arg) {
		if (this.field_9067 == 0) {
			return 1.0F + (float)Math.max(0, i - 1) * 0.5F;
		} else if (this.field_9067 == 1 && arg == class_1310.field_6289) {
			return (float)i * 2.5F;
		} else {
			return this.field_9067 == 2 && arg == class_1310.field_6293 ? (float)i * 2.5F : 0.0F;
		}
	}

	@Override
	public boolean differs(Enchantment enchantment) {
		return !(enchantment instanceof DamageEnchantment);
	}

	@Override
	public boolean isAcceptableItem(ItemStack itemStack) {
		return itemStack.getItem() instanceof AxeItem ? true : super.isAcceptableItem(itemStack);
	}

	@Override
	public void onTargetDamaged(LivingEntity livingEntity, Entity entity, int i) {
		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity2 = (LivingEntity)entity;
			if (this.field_9067 == 2 && livingEntity2.method_6046() == class_1310.field_6293) {
				int j = 20 + livingEntity.getRand().nextInt(10 * i);
				livingEntity2.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5909, j, 3));
			}
		}
	}
}
