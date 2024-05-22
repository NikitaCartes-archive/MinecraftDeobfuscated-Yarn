package net.minecraft.block;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class BlockKeys {
	public static final RegistryKey<Block> PUMPKIN = of("pumpkin");
	public static final RegistryKey<Block> PUMPKIN_STEM = of("pumpkin_stem");
	public static final RegistryKey<Block> ATTACHED_PUMPKIN_STEM = of("attached_pumpkin_stem");
	public static final RegistryKey<Block> MELON = of("melon");
	public static final RegistryKey<Block> MELON_STEM = of("melon_stem");
	public static final RegistryKey<Block> ATTACHED_MELON_STEM = of("attached_melon_stem");

	private static RegistryKey<Block> of(String id) {
		return RegistryKey.of(RegistryKeys.BLOCK, Identifier.ofVanilla(id));
	}
}
