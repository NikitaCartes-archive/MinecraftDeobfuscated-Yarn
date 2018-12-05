package net.minecraft.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.registry.Registry;

public class Enchantments {
	private static final EquipmentSlot[] ALL_ARMOR = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
	public static final Enchantment field_9111 = register(
		"protection", new ProtectionEnchantment(Enchantment.Weight.COMMON, ProtectionEnchantment.Type.ALL, ALL_ARMOR)
	);
	public static final Enchantment field_9095 = register(
		"fire_protection", new ProtectionEnchantment(Enchantment.Weight.UNCOMMON, ProtectionEnchantment.Type.FIRE, ALL_ARMOR)
	);
	public static final Enchantment field_9129 = register(
		"feather_falling", new ProtectionEnchantment(Enchantment.Weight.UNCOMMON, ProtectionEnchantment.Type.FALL, ALL_ARMOR)
	);
	public static final Enchantment field_9107 = register(
		"blast_protection", new ProtectionEnchantment(Enchantment.Weight.RARE, ProtectionEnchantment.Type.EXPLOSION, ALL_ARMOR)
	);
	public static final Enchantment field_9096 = register(
		"projectile_protection", new ProtectionEnchantment(Enchantment.Weight.UNCOMMON, ProtectionEnchantment.Type.PROJECTILE, ALL_ARMOR)
	);
	public static final Enchantment field_9127 = register("respiration", new RespirationEnchantment(Enchantment.Weight.RARE, ALL_ARMOR));
	public static final Enchantment field_9105 = register("aqua_affinity", new AquaAffinityEnchantment(Enchantment.Weight.RARE, ALL_ARMOR));
	public static final Enchantment field_9097 = register("thorns", new ThornsEnchantment(Enchantment.Weight.LEGENDARY, ALL_ARMOR));
	public static final Enchantment field_9128 = register("depth_strider", new DepthStriderEnchantment(Enchantment.Weight.RARE, ALL_ARMOR));
	public static final Enchantment field_9122 = register("frost_walker", new FrostWalkerEnchantment(Enchantment.Weight.RARE, EquipmentSlot.FEET));
	public static final Enchantment field_9113 = register("binding_curse", new BindingCurseEnchantment(Enchantment.Weight.LEGENDARY, ALL_ARMOR));
	public static final Enchantment field_9118 = register("sharpness", new DamageEnchantment(Enchantment.Weight.COMMON, 0, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9123 = register("smite", new DamageEnchantment(Enchantment.Weight.UNCOMMON, 1, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9112 = register("bane_of_arthropods", new DamageEnchantment(Enchantment.Weight.UNCOMMON, 2, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9121 = register("knockback", new KnockbackEnchantment(Enchantment.Weight.UNCOMMON, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9124 = register("fire_aspect", new FireAspectEnchantment(Enchantment.Weight.RARE, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9110 = register(
		"looting", new LuckEnchantment(Enchantment.Weight.RARE, EnchantmentTarget.WEAPON, EquipmentSlot.HAND_MAIN)
	);
	public static final Enchantment field_9115 = register("sweeping", new SweepingEnchantment(Enchantment.Weight.RARE, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9131 = register("efficiency", new EfficiencyEnchantment(Enchantment.Weight.COMMON, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9099 = register("silk_touch", new SilkTouchEnchantment(Enchantment.Weight.LEGENDARY, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9119 = register("unbreaking", new UnbreakingEnchantment(Enchantment.Weight.UNCOMMON, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9130 = register(
		"fortune", new LuckEnchantment(Enchantment.Weight.RARE, EnchantmentTarget.BREAKER, EquipmentSlot.HAND_MAIN)
	);
	public static final Enchantment field_9103 = register("power", new PowerEnchantment(Enchantment.Weight.COMMON, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9116 = register("punch", new PunchEnchantment(Enchantment.Weight.RARE, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9126 = register("flame", new FlameEnchantment(Enchantment.Weight.RARE, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9125 = register("infinity", new InfinityEnchantment(Enchantment.Weight.LEGENDARY, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9114 = register(
		"luck_of_the_sea", new LuckEnchantment(Enchantment.Weight.RARE, EnchantmentTarget.FISHING, EquipmentSlot.HAND_MAIN)
	);
	public static final Enchantment field_9100 = register("lure", new LureEnchantment(Enchantment.Weight.RARE, EnchantmentTarget.FISHING, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9120 = register("loyalty", new LoyaltyEnchantment(Enchantment.Weight.UNCOMMON, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9106 = register("impaling", new ImpalingEnchantment(Enchantment.Weight.RARE, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9104 = register("riptide", new RiptideEnchantment(Enchantment.Weight.RARE, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9117 = register("channeling", new ChannelingEnchantment(Enchantment.Weight.LEGENDARY, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9108 = register("multishot", new MultishotEnchantment(Enchantment.Weight.RARE, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9098 = register("quick_charge", new QuickChargeEnchantment(Enchantment.Weight.UNCOMMON, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9132 = register("piercing", new PiercingEnchantment(Enchantment.Weight.COMMON, EquipmentSlot.HAND_MAIN));
	public static final Enchantment field_9101 = register("mending", new MendingEnchantment(Enchantment.Weight.RARE, EquipmentSlot.values()));
	public static final Enchantment field_9109 = register("vanishing_curse", new VanishingCurseEnchantment(Enchantment.Weight.LEGENDARY, EquipmentSlot.values()));

	private static Enchantment register(String string, Enchantment enchantment) {
		return Registry.register(Registry.ENCHANTMENT, string, enchantment);
	}
}
