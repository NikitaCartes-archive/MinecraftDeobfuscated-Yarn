package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.MathHelper;

public class ProtectionEnchantment extends Enchantment {
	public final ProtectionEnchantment.Type type;

	public ProtectionEnchantment(Enchantment.Weight weight, ProtectionEnchantment.Type type, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.field_9068, equipmentSlots);
		this.type = type;
		if (type == ProtectionEnchantment.Type.field_9140) {
			this.type = EnchantmentTarget.field_9079;
		}
	}

	@Override
	public int getMinimumPower(int i) {
		return this.type.getBasePower() + (i - 1) * this.type.getPowerPerLevel();
	}

	@Override
	public int getMaximumPower(int i) {
		return this.getMinimumPower(i) + this.type.getPowerPerLevel();
	}

	@Override
	public int getMaximumLevel() {
		return 4;
	}

	@Override
	public int getProtectionAmount(int i, DamageSource damageSource) {
		if (damageSource.doesDamageToCreative()) {
			return 0;
		} else if (this.type == ProtectionEnchantment.Type.field_9138) {
			return i;
		} else if (this.type == ProtectionEnchantment.Type.field_9139 && damageSource.isFire()) {
			return i * 2;
		} else if (this.type == ProtectionEnchantment.Type.field_9140 && damageSource == DamageSource.FALL) {
			return i * 3;
		} else if (this.type == ProtectionEnchantment.Type.field_9141 && damageSource.isExplosive()) {
			return i * 2;
		} else {
			return this.type == ProtectionEnchantment.Type.field_9142 && damageSource.isProjectile() ? i * 2 : 0;
		}
	}

	@Override
	public boolean differs(Enchantment enchantment) {
		if (enchantment instanceof ProtectionEnchantment) {
			ProtectionEnchantment protectionEnchantment = (ProtectionEnchantment)enchantment;
			return this.type == protectionEnchantment.type
				? false
				: this.type == ProtectionEnchantment.Type.field_9140 || protectionEnchantment.type == ProtectionEnchantment.Type.field_9140;
		} else {
			return super.differs(enchantment);
		}
	}

	public static int transformFireDuration(LivingEntity livingEntity, int i) {
		int j = EnchantmentHelper.getEquipmentLevel(Enchantments.field_9095, livingEntity);
		if (j > 0) {
			i -= MathHelper.floor((float)i * (float)j * 0.15F);
		}

		return i;
	}

	public static double transformExplosionKnockback(LivingEntity livingEntity, double d) {
		int i = EnchantmentHelper.getEquipmentLevel(Enchantments.field_9107, livingEntity);
		if (i > 0) {
			d -= (double)MathHelper.floor(d * (double)((float)i * 0.15F));
		}

		return d;
	}

	public static enum Type {
		field_9138("all", 1, 11),
		field_9139("fire", 10, 8),
		field_9140("fall", 5, 6),
		field_9141("explosion", 5, 8),
		field_9142("projectile", 3, 6);

		private final String name;
		private final int basePower;
		private final int powerPerLevel;

		private Type(String string2, int j, int k) {
			this.name = string2;
			this.basePower = j;
			this.powerPerLevel = k;
		}

		public int getBasePower() {
			return this.basePower;
		}

		public int getPowerPerLevel() {
			return this.powerPerLevel;
		}
	}
}
