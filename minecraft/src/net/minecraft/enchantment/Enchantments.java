package net.minecraft.enchantment;

import net.minecraft.class_5509;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.registry.Registry;

public class Enchantments {
	private static final EquipmentSlot[] ALL_ARMOR = new EquipmentSlot[]{
		EquipmentSlot.field_6169, EquipmentSlot.field_6174, EquipmentSlot.field_6172, EquipmentSlot.field_6166
	};
	public static final Enchantment field_9111 = register(
		"protection", new ProtectionEnchantment(Enchantment.Rarity.field_9087, ProtectionEnchantment.Type.field_9138, ALL_ARMOR)
	);
	public static final Enchantment field_9095 = register(
		"fire_protection", new ProtectionEnchantment(Enchantment.Rarity.field_9090, ProtectionEnchantment.Type.field_9139, ALL_ARMOR)
	);
	public static final Enchantment field_9129 = register(
		"feather_falling", new ProtectionEnchantment(Enchantment.Rarity.field_9090, ProtectionEnchantment.Type.field_9140, ALL_ARMOR)
	);
	public static final Enchantment field_9107 = register(
		"blast_protection", new ProtectionEnchantment(Enchantment.Rarity.field_9088, ProtectionEnchantment.Type.field_9141, ALL_ARMOR)
	);
	public static final Enchantment field_9096 = register(
		"projectile_protection", new ProtectionEnchantment(Enchantment.Rarity.field_9090, ProtectionEnchantment.Type.field_9142, ALL_ARMOR)
	);
	public static final Enchantment field_9127 = register("respiration", new RespirationEnchantment(Enchantment.Rarity.field_9088, ALL_ARMOR));
	public static final Enchantment field_9105 = register("aqua_affinity", new AquaAffinityEnchantment(Enchantment.Rarity.field_9088, ALL_ARMOR));
	public static final Enchantment field_9097 = register("thorns", new ThornsEnchantment(Enchantment.Rarity.field_9091, ALL_ARMOR));
	public static final Enchantment field_9128 = register("depth_strider", new DepthStriderEnchantment(Enchantment.Rarity.field_9088, ALL_ARMOR));
	public static final Enchantment field_9122 = register("frost_walker", new FrostWalkerEnchantment(Enchantment.Rarity.field_9088, EquipmentSlot.field_6166));
	public static final Enchantment field_9113 = register("binding_curse", new BindingCurseEnchantment(Enchantment.Rarity.field_9091, ALL_ARMOR));
	public static final Enchantment field_23071 = register("soul_speed", new SoulSpeedEnchantment(Enchantment.Rarity.field_9091, EquipmentSlot.field_6166));
	public static final Enchantment field_9118 = register("sharpness", new DamageEnchantment(Enchantment.Rarity.field_9087, 0, EquipmentSlot.field_6173));
	public static final Enchantment field_9123 = register("smite", new DamageEnchantment(Enchantment.Rarity.field_9090, 1, EquipmentSlot.field_6173));
	public static final Enchantment field_9112 = register("bane_of_arthropods", new DamageEnchantment(Enchantment.Rarity.field_9090, 2, EquipmentSlot.field_6173));
	public static final Enchantment field_9121 = register("knockback", new KnockbackEnchantment(Enchantment.Rarity.field_9090, EquipmentSlot.field_6173));
	public static final Enchantment field_9124 = register("fire_aspect", new FireAspectEnchantment(Enchantment.Rarity.field_9088, EquipmentSlot.field_6173));
	public static final Enchantment field_9110 = register(
		"looting", new LuckEnchantment(Enchantment.Rarity.field_9088, EnchantmentTarget.field_9074, EquipmentSlot.field_6173)
	);
	public static final Enchantment field_9115 = register("sweeping", new SweepingEnchantment(Enchantment.Rarity.field_9088, EquipmentSlot.field_6173));
	public static final Enchantment field_26775 = register("cleaving", new class_5509(Enchantment.Rarity.field_9088, EquipmentSlot.field_6173));
	public static final Enchantment field_9131 = register("efficiency", new EfficiencyEnchantment(Enchantment.Rarity.field_9087, EquipmentSlot.field_6173));
	public static final Enchantment field_9099 = register("silk_touch", new SilkTouchEnchantment(Enchantment.Rarity.field_9091, EquipmentSlot.field_6173));
	public static final Enchantment field_9119 = register("unbreaking", new UnbreakingEnchantment(Enchantment.Rarity.field_9090, EquipmentSlot.field_6173));
	public static final Enchantment field_9130 = register(
		"fortune", new LuckEnchantment(Enchantment.Rarity.field_9088, EnchantmentTarget.field_9069, EquipmentSlot.field_6173)
	);
	public static final Enchantment field_9103 = register("power", new PowerEnchantment(Enchantment.Rarity.field_9087, EquipmentSlot.field_6173));
	public static final Enchantment field_9116 = register("punch", new PunchEnchantment(Enchantment.Rarity.field_9088, EquipmentSlot.field_6173));
	public static final Enchantment field_9126 = register("flame", new FlameEnchantment(Enchantment.Rarity.field_9088, EquipmentSlot.field_6173));
	public static final Enchantment field_9125 = register("infinity", new InfinityEnchantment(Enchantment.Rarity.field_9091, EquipmentSlot.field_6173));
	public static final Enchantment field_9114 = register(
		"luck_of_the_sea", new LuckEnchantment(Enchantment.Rarity.field_9088, EnchantmentTarget.field_9072, EquipmentSlot.field_6173)
	);
	public static final Enchantment field_9100 = register(
		"lure", new LureEnchantment(Enchantment.Rarity.field_9088, EnchantmentTarget.field_9072, EquipmentSlot.field_6173)
	);
	public static final Enchantment field_9120 = register("loyalty", new LoyaltyEnchantment(Enchantment.Rarity.field_9090, EquipmentSlot.field_6173));
	public static final Enchantment field_9106 = register("impaling", new ImpalingEnchantment(Enchantment.Rarity.field_9088, EquipmentSlot.field_6173));
	public static final Enchantment field_9104 = register("riptide", new RiptideEnchantment(Enchantment.Rarity.field_9088, EquipmentSlot.field_6173));
	public static final Enchantment field_9117 = register("channeling", new ChannelingEnchantment(Enchantment.Rarity.field_9091, EquipmentSlot.field_6173));
	public static final Enchantment field_9108 = register("multishot", new MultishotEnchantment(Enchantment.Rarity.field_9088, EquipmentSlot.field_6173));
	public static final Enchantment field_9098 = register("quick_charge", new QuickChargeEnchantment(Enchantment.Rarity.field_9090, EquipmentSlot.field_6173));
	public static final Enchantment field_9132 = register("piercing", new PiercingEnchantment(Enchantment.Rarity.field_9087, EquipmentSlot.field_6173));
	public static final Enchantment field_9101 = register("mending", new MendingEnchantment(Enchantment.Rarity.field_9088, EquipmentSlot.values()));
	public static final Enchantment field_9109 = register("vanishing_curse", new VanishingCurseEnchantment(Enchantment.Rarity.field_9091, EquipmentSlot.values()));

	private static Enchantment register(String name, Enchantment enchantment) {
		return Registry.register(Registry.ENCHANTMENT, name, enchantment);
	}
}
