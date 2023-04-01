package net.minecraft.world.dimension;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class DimensionTypes {
	public static final RegistryKey<DimensionType> OVERWORLD = of("overworld");
	public static final RegistryKey<DimensionType> THE_NETHER = of("the_nether");
	public static final RegistryKey<DimensionType> THE_END = of("the_end");
	public static final RegistryKey<DimensionType> THE_MOON = of("the_moon");
	public static final RegistryKey<DimensionType> OVERWORLD_CAVES = of("overworld_caves");
	public static final Identifier OVERWORLD_ID = new Identifier("overworld");
	public static final Identifier THE_NETHER_ID = new Identifier("the_nether");
	public static final Identifier THE_END_ID = new Identifier("the_end");
	public static final Identifier field_44249 = new Identifier("the_moon");

	private static RegistryKey<DimensionType> of(String id) {
		return RegistryKey.of(RegistryKeys.DIMENSION_TYPE, new Identifier(id));
	}
}
