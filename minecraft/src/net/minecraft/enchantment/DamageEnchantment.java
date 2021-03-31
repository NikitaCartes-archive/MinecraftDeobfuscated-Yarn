package net.minecraft.enchantment;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;

public class DamageEnchantment extends Enchantment {
	public static final int ALL_INDEX = 0;
	public static final int UNDEAD_INDEX = 1;
	public static final int ARTHROPODS_INDEX = 2;
	private static final String[] typeNames = new String[]{"all", "undead", "arthropods"};
	private static final int[] basePowers = new int[]{1, 5, 5};
	private static final int[] powersPerLevel = new int[]{11, 8, 8};
	private static final int[] minMaxPowerDifferences = new int[]{20, 20, 20};
	public final int typeIndex;

	public DamageEnchantment(Enchantment.Rarity weight, int typeIndex, EquipmentSlot... slots) {
		super(weight, EnchantmentTarget.WEAPON, slots);
		this.typeIndex = typeIndex;
	}

	@Override
	public int getMinPower(int level) {
		return basePowers[this.typeIndex] + (level - 1) * powersPerLevel[this.typeIndex];
	}

	@Override
	public int getMaxPower(int level) {
		return this.getMinPower(level) + minMaxPowerDifferences[this.typeIndex];
	}

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	public float getAttackDamage(int level, EntityGroup group) {
		if (this.typeIndex == 0) {
			return 1.0F + (float)Math.max(0, level - 1) * 0.5F;
		} else if (this.typeIndex == 1 && group == EntityGroup.UNDEAD) {
			return (float)level * 2.5F;
		} else {
			return this.typeIndex == 2 && group == EntityGroup.ARTHROPOD ? (float)level * 2.5F : 0.0F;
		}
	}

	@Override
	public boolean canAccept(Enchantment other) {
		return !(other instanceof DamageEnchantment);
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		return stack.getItem() instanceof AxeItem ? true : super.isAcceptableItem(stack);
	}

	@Override
	public void onTargetDamaged(LivingEntity user, Entity target, int level) {
		if (target instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)target;
			if (this.typeIndex == 2 && level > 0 && livingEntity.getGroup() == EntityGroup.ARTHROPOD) {
				int i = 20 + user.getRandom().nextInt(10 * level);
				livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, i, 3));
			}
		}
	}
}
