package net.minecraft.village;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public final class VillagerType {
	public static final VillagerType DESERT = create("desert");
	public static final VillagerType JUNGLE = create("jungle");
	public static final VillagerType PLAINS = create("plains");
	public static final VillagerType SAVANNA = create("savanna");
	public static final VillagerType SNOW = create("snow");
	public static final VillagerType SWAMP = create("swamp");
	public static final VillagerType TAIGA = create("taiga");
	private final String field_26690;
	private static final Map<RegistryKey<Biome>, VillagerType> BIOME_TO_TYPE = Util.make(Maps.<RegistryKey<Biome>, VillagerType>newHashMap(), hashMap -> {
		hashMap.put(BiomeKeys.BADLANDS, DESERT);
		hashMap.put(BiomeKeys.BADLANDS_PLATEAU, DESERT);
		hashMap.put(BiomeKeys.DESERT, DESERT);
		hashMap.put(BiomeKeys.DESERT_HILLS, DESERT);
		hashMap.put(BiomeKeys.DESERT_LAKES, DESERT);
		hashMap.put(BiomeKeys.ERODED_BADLANDS, DESERT);
		hashMap.put(BiomeKeys.MODIFIED_BADLANDS_PLATEAU, DESERT);
		hashMap.put(BiomeKeys.MODIFIED_WOODED_BADLANDS_PLATEAU, DESERT);
		hashMap.put(BiomeKeys.WOODED_BADLANDS_PLATEAU, DESERT);
		hashMap.put(BiomeKeys.BAMBOO_JUNGLE, JUNGLE);
		hashMap.put(BiomeKeys.BAMBOO_JUNGLE_HILLS, JUNGLE);
		hashMap.put(BiomeKeys.JUNGLE, JUNGLE);
		hashMap.put(BiomeKeys.JUNGLE_EDGE, JUNGLE);
		hashMap.put(BiomeKeys.JUNGLE_HILLS, JUNGLE);
		hashMap.put(BiomeKeys.MODIFIED_JUNGLE, JUNGLE);
		hashMap.put(BiomeKeys.MODIFIED_JUNGLE_EDGE, JUNGLE);
		hashMap.put(BiomeKeys.SAVANNA_PLATEAU, SAVANNA);
		hashMap.put(BiomeKeys.SAVANNA, SAVANNA);
		hashMap.put(BiomeKeys.SHATTERED_SAVANNA, SAVANNA);
		hashMap.put(BiomeKeys.SHATTERED_SAVANNA_PLATEAU, SAVANNA);
		hashMap.put(BiomeKeys.DEEP_FROZEN_OCEAN, SNOW);
		hashMap.put(BiomeKeys.FROZEN_OCEAN, SNOW);
		hashMap.put(BiomeKeys.FROZEN_RIVER, SNOW);
		hashMap.put(BiomeKeys.ICE_SPIKES, SNOW);
		hashMap.put(BiomeKeys.SNOWY_BEACH, SNOW);
		hashMap.put(BiomeKeys.SNOWY_MOUNTAINS, SNOW);
		hashMap.put(BiomeKeys.SNOWY_TAIGA, SNOW);
		hashMap.put(BiomeKeys.SNOWY_TAIGA_HILLS, SNOW);
		hashMap.put(BiomeKeys.SNOWY_TAIGA_MOUNTAINS, SNOW);
		hashMap.put(BiomeKeys.SNOWY_TUNDRA, SNOW);
		hashMap.put(BiomeKeys.SWAMP, SWAMP);
		hashMap.put(BiomeKeys.SWAMP_HILLS, SWAMP);
		hashMap.put(BiomeKeys.GIANT_SPRUCE_TAIGA, TAIGA);
		hashMap.put(BiomeKeys.GIANT_SPRUCE_TAIGA_HILLS, TAIGA);
		hashMap.put(BiomeKeys.GIANT_TREE_TAIGA, TAIGA);
		hashMap.put(BiomeKeys.GIANT_TREE_TAIGA_HILLS, TAIGA);
		hashMap.put(BiomeKeys.GRAVELLY_MOUNTAINS, TAIGA);
		hashMap.put(BiomeKeys.MODIFIED_GRAVELLY_MOUNTAINS, TAIGA);
		hashMap.put(BiomeKeys.MOUNTAIN_EDGE, TAIGA);
		hashMap.put(BiomeKeys.MOUNTAINS, TAIGA);
		hashMap.put(BiomeKeys.TAIGA, TAIGA);
		hashMap.put(BiomeKeys.TAIGA_HILLS, TAIGA);
		hashMap.put(BiomeKeys.TAIGA_MOUNTAINS, TAIGA);
		hashMap.put(BiomeKeys.WOODED_MOUNTAINS, TAIGA);
	});

	private VillagerType(String string) {
		this.field_26690 = string;
	}

	public String toString() {
		return this.field_26690;
	}

	private static VillagerType create(String id) {
		return Registry.register(Registry.VILLAGER_TYPE, new Identifier(id), new VillagerType(id));
	}

	public static VillagerType forBiome(Optional<RegistryKey<Biome>> biomeKey) {
		return (VillagerType)biomeKey.flatMap(registryKey -> Optional.ofNullable(BIOME_TO_TYPE.get(registryKey))).orElse(PLAINS);
	}
}
