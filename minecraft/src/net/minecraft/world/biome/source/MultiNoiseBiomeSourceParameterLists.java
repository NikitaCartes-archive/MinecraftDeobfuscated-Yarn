package net.minecraft.world.biome.source;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class MultiNoiseBiomeSourceParameterLists {
	public static final RegistryKey<MultiNoiseBiomeSourceParameterList> NETHER = of("nether");
	public static final RegistryKey<MultiNoiseBiomeSourceParameterList> OVERWORLD = of("overworld");

	public static void bootstrap(Registerable<MultiNoiseBiomeSourceParameterList> registry) {
		RegistryEntryLookup<Biome> registryEntryLookup = registry.getRegistryLookup(RegistryKeys.BIOME);
		registry.register(NETHER, new MultiNoiseBiomeSourceParameterList(MultiNoiseBiomeSourceParameterList.Preset.NETHER, registryEntryLookup));
		registry.register(OVERWORLD, new MultiNoiseBiomeSourceParameterList(MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD, registryEntryLookup));
	}

	private static RegistryKey<MultiNoiseBiomeSourceParameterList> of(String id) {
		return RegistryKey.of(RegistryKeys.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, new Identifier(id));
	}
}
