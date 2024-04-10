package net.minecraft.registry.tag;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public interface EnchantmentTags {
	TagKey<Enchantment> TOOLTIP_ORDER = of("tooltip_order");

	private static TagKey<Enchantment> of(String id) {
		return TagKey.of(RegistryKeys.ENCHANTMENT, new Identifier("minecraft", id));
	}
}
