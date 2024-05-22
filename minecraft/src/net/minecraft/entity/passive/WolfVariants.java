package net.minecraft.entity.passive;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.TagKey;
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
	public static final RegistryKey<WolfVariant> DEFAULT = PALE;

	private static RegistryKey<WolfVariant> of(String id) {
		return RegistryKey.of(RegistryKeys.WOLF_VARIANT, Identifier.ofVanilla(id));
	}

	static void register(Registerable<WolfVariant> registry, RegistryKey<WolfVariant> key, String textureName, RegistryKey<Biome> biome) {
		register(registry, key, textureName, RegistryEntryList.of(registry.getRegistryLookup(RegistryKeys.BIOME).getOrThrow(biome)));
	}

	static void register(Registerable<WolfVariant> registry, RegistryKey<WolfVariant> key, String textureName, TagKey<Biome> biomeTag) {
		register(registry, key, textureName, registry.getRegistryLookup(RegistryKeys.BIOME).getOrThrow(biomeTag));
	}

	static void register(Registerable<WolfVariant> registry, RegistryKey<WolfVariant> key, String textureName, RegistryEntryList<Biome> biomes) {
		Identifier identifier = Identifier.ofVanilla("entity/wolf/" + textureName);
		Identifier identifier2 = Identifier.ofVanilla("entity/wolf/" + textureName + "_tame");
		Identifier identifier3 = Identifier.ofVanilla("entity/wolf/" + textureName + "_angry");
		registry.register(key, new WolfVariant(identifier, identifier2, identifier3, biomes));
	}

	public static RegistryEntry<WolfVariant> fromBiome(DynamicRegistryManager dynamicRegistryManager, RegistryEntry<Biome> biome) {
		Registry<WolfVariant> registry = dynamicRegistryManager.get(RegistryKeys.WOLF_VARIANT);
		return (RegistryEntry<WolfVariant>)registry.streamEntries()
			.filter(entry -> ((WolfVariant)entry.value()).getBiomes().contains(biome))
			.findFirst()
			.or(() -> registry.getEntry(DEFAULT))
			.or(registry::getDefaultEntry)
			.orElseThrow();
	}

	public static void bootstrap(Registerable<WolfVariant> registry) {
		register(registry, PALE, "wolf", BiomeKeys.TAIGA);
		register(registry, SPOTTED, "wolf_spotted", BiomeTags.IS_SAVANNA);
		register(registry, SNOWY, "wolf_snowy", BiomeKeys.GROVE);
		register(registry, BLACK, "wolf_black", BiomeKeys.OLD_GROWTH_PINE_TAIGA);
		register(registry, ASHEN, "wolf_ashen", BiomeKeys.SNOWY_TAIGA);
		register(registry, RUSTY, "wolf_rusty", BiomeTags.IS_JUNGLE);
		register(registry, WOODS, "wolf_woods", BiomeKeys.FOREST);
		register(registry, CHESTNUT, "wolf_chestnut", BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA);
		register(registry, STRIPED, "wolf_striped", BiomeTags.IS_BADLANDS);
	}
}
