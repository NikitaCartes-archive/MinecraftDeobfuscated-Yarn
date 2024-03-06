package net.minecraft.entity.passive;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public class WolfVariants {
	public static final RegistryKey<WolfVariant> PALE = of("pale");
	public static final RegistryKey<WolfVariant> SPOTTED = of("spotted");
	public static final RegistryKey<WolfVariant> SNOWY = of("snowy");
	public static final RegistryKey<WolfVariant> BLACK = of("black");
	public static final RegistryKey<WolfVariant> ASHEN = of("ashen");
	public static final RegistryKey<WolfVariant> RUSTY = of("rusty");
	public static final RegistryKey<WolfVariant> WOODS = of("woods");
	public static final RegistryKey<WolfVariant> CHESTNUT = of("chestnut");
	public static final RegistryKey<WolfVariant> STRIPED = of("striped");

	private static RegistryKey<WolfVariant> of(String id) {
		return RegistryKey.of(RegistryKeys.WOLF_VARIANT, new Identifier(id));
	}

	static void register(Registerable<WolfVariant> registry, RegistryKey<WolfVariant> key, String textureName, RegistryKey<Biome> biome) {
		Identifier identifier = new Identifier("textures/entity/wolf/" + textureName + ".png");
		Identifier identifier2 = new Identifier("textures/entity/wolf/" + textureName + "_tame.png");
		Identifier identifier3 = new Identifier("textures/entity/wolf/" + textureName + "_angry.png");
		registry.register(
			key, new WolfVariant(identifier, identifier2, identifier3, RegistryEntryList.of(registry.getRegistryLookup(RegistryKeys.BIOME).getOrThrow(biome)))
		);
	}

	public static RegistryEntry<WolfVariant> fromBiome(DynamicRegistryManager dynamicRegistryManager, RegistryEntry<Biome> biome) {
		Registry<WolfVariant> registry = dynamicRegistryManager.get(RegistryKeys.WOLF_VARIANT);
		return (RegistryEntry<WolfVariant>)registry.streamEntries()
			.filter(entry -> ((WolfVariant)entry.value()).biomes().contains(biome))
			.findFirst()
			.orElse(registry.entryOf(PALE));
	}

	public static void bootstrap(Registerable<WolfVariant> registry) {
		register(registry, PALE, "wolf", BiomeKeys.TAIGA);
		register(registry, SPOTTED, "wolf_spotted", BiomeKeys.SAVANNA_PLATEAU);
		register(registry, SNOWY, "wolf_snowy", BiomeKeys.GROVE);
		register(registry, BLACK, "wolf_black", BiomeKeys.OLD_GROWTH_PINE_TAIGA);
		register(registry, ASHEN, "wolf_ashen", BiomeKeys.SNOWY_TAIGA);
		register(registry, RUSTY, "wolf_rusty", BiomeKeys.SPARSE_JUNGLE);
		register(registry, WOODS, "wolf_woods", BiomeKeys.FOREST);
		register(registry, CHESTNUT, "wolf_chestnut", BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA);
		register(registry, STRIPED, "wolf_striped", BiomeKeys.WOODED_BADLANDS);
	}
}
