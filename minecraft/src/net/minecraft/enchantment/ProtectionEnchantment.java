package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.MathHelper;

public class ProtectionEnchantment extends Enchantment {
	public final ProtectionEnchantment.Type type;

	public ProtectionEnchantment(Enchantment.Weight weight, ProtectionEnchantment.Type type, EquipmentSlot... equipmentSlots) {
		super(weight, EnchantmentTarget.ARMOR, equipmentSlots);
		this.type = type;
		if (type == ProtectionEnchantment.Type.FALL) {
			this.type = EnchantmentTarget.ARMOR_FEET;
		}
	}

	@Override
	public int getMinimumPower(int i) {
		return this.type.getBasePower() + (i - 1) * this.type.getPowerPerLevel();
	}

	@Override
	public int method_20742(int i) {
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
		} else if (this.type == ProtectionEnchantment.Type.ALL) {
			return i;
		} else if (this.type == ProtectionEnchantment.Type.FIRE && damageSource.isFire()) {
			return i * 2;
		} else if (this.type == ProtectionEnchantment.Type.FALL && damageSource == DamageSource.FALL) {
			return i * 3;
		} else if (this.type == ProtectionEnchantment.Type.EXPLOSION && damageSource.isExplosive()) {
			return i * 2;
		} else {
			return this.type == ProtectionEnchantment.Type.PROJECTILE && damageSource.isProjectile() ? i * 2 : 0;
		}
	}

	@Override
	public boolean differs(Enchantment enchantment) {
		if (enchantment instanceof ProtectionEnchantment) {
			ProtectionEnchantment protectionEnchantment = (ProtectionEnchantment)enchantment;
			return this.type == protectionEnchantment.type
				? false
				: this.type == ProtectionEnchantment.Type.FALL || protectionEnchantment.type == ProtectionEnchantment.Type.FALL;
		} else {
			return super.differs(enchantment);
		}
	}

	public static int transformFireDuration(LivingEntity livingEntity, int i) {
		int j = EnchantmentHelper.getEquipmentLevel(Enchantments.FIRE_PROTECTION, livingEntity);
		if (j > 0) {
			i -= MathHelper.floor((float)i * (float)j * 0.15F);
		}

		return i;
	}

	public static double transformExplosionKnockback(LivingEntity livingEntity, double d) {
		int i = EnchantmentHelper.getEquipmentLevel(Enchantments.BLAST_PROTECTION, livingEntity);
		if (i > 0) {
			d -= (double)MathHelper.floor(d * (double)((float)i * 0.15F));
		}

		return d;
	}

	public static enum Type {
		ALL("all", 1, 11),
		FIRE("fire", 10, 8),
		FALL("fall", 5, 6),
		EXPLOSION("explosion", 5, 8),
		PROJECTILE("projectile", 3, 6);

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
