package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.MathHelper;

public class ProtectionEnchantment extends Enchantment {
	public final ProtectionEnchantment.Type protectionType;

	public ProtectionEnchantment(Enchantment.Rarity weight, ProtectionEnchantment.Type type, EquipmentSlot... equipmentSlots) {
		super(weight, type == ProtectionEnchantment.Type.field_9140 ? EnchantmentTarget.field_9079 : EnchantmentTarget.field_9068, equipmentSlots);
		this.protectionType = type;
	}

	@Override
	public int getMinPower(int level) {
		return this.protectionType.getBasePower() + (level - 1) * this.protectionType.getPowerPerLevel();
	}

	@Override
	public int getMaxPower(int level) {
		return this.getMinPower(level) + this.protectionType.getPowerPerLevel();
	}

	@Override
	public int getMaxLevel() {
		return 4;
	}

	@Override
	public int getProtectionAmount(int level, DamageSource source) {
		if (source.isOutOfWorld()) {
			return 0;
		} else if (this.protectionType == ProtectionEnchantment.Type.field_9138) {
			return level;
		} else if (this.protectionType == ProtectionEnchantment.Type.field_9139 && source.isFire()) {
			return level * 2;
		} else if (this.protectionType == ProtectionEnchantment.Type.field_9140 && source == DamageSource.FALL) {
			return level * 3;
		} else if (this.protectionType == ProtectionEnchantment.Type.field_9141 && source.isExplosive()) {
			return level * 2;
		} else {
			return this.protectionType == ProtectionEnchantment.Type.field_9142 && source.isProjectile() ? level * 2 : 0;
		}
	}

	@Override
	public boolean canAccept(Enchantment other) {
		if (other instanceof ProtectionEnchantment) {
			ProtectionEnchantment protectionEnchantment = (ProtectionEnchantment)other;
			return this.protectionType == protectionEnchantment.protectionType
				? false
				: this.protectionType == ProtectionEnchantment.Type.field_9140 || protectionEnchantment.protectionType == ProtectionEnchantment.Type.field_9140;
		} else {
			return super.canAccept(other);
		}
	}

	public static int transformFireDuration(LivingEntity entity, int duration) {
		int i = EnchantmentHelper.getEquipmentLevel(Enchantments.field_9095, entity);
		if (i > 0) {
			duration -= MathHelper.floor((float)duration * (float)i * 0.15F);
		}

		return duration;
	}

	public static double transformExplosionKnockback(LivingEntity entity, double velocity) {
		int i = EnchantmentHelper.getEquipmentLevel(Enchantments.field_9107, entity);
		if (i > 0) {
			velocity -= (double)MathHelper.floor(velocity * (double)((float)i * 0.15F));
		}

		return velocity;
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

		private Type(String name, int basePower, int powerPerLevel) {
			this.name = name;
			this.basePower = basePower;
			this.powerPerLevel = powerPerLevel;
		}

		public int getBasePower() {
			return this.basePower;
		}

		public int getPowerPerLevel() {
			return this.powerPerLevel;
		}
	}
}
