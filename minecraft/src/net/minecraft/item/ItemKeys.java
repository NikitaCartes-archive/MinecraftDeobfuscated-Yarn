package net.minecraft.item;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ItemKeys {
	public static final RegistryKey<Item> PUMPKIN_SEEDS = of("pumpkin_seeds");
	public static final RegistryKey<Item> MELON_SEEDS = of("melon_seeds");

	private static RegistryKey<Item> of(String id) {
		return RegistryKey.of(RegistryKeys.ITEM, Identifier.ofVanilla(id));
	}
}
