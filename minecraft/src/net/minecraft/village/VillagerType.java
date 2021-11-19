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
	private final String name;
	private static final Map<RegistryKey<Biome>, VillagerType> BIOME_TO_TYPE = Util.make(Maps.<RegistryKey<Biome>, VillagerType>newHashMap(), map -> {
		map.put(BiomeKeys.BADLANDS, DESERT);
		map.put(BiomeKeys.DESERT, DESERT);
		map.put(BiomeKeys.ERODED_BADLANDS, DESERT);
		map.put(BiomeKeys.WOODED_BADLANDS, DESERT);
		map.put(BiomeKeys.BAMBOO_JUNGLE, JUNGLE);
		map.put(BiomeKeys.JUNGLE, JUNGLE);
		map.put(BiomeKeys.SPARSE_JUNGLE, JUNGLE);
		map.put(BiomeKeys.SAVANNA_PLATEAU, SAVANNA);
		map.put(BiomeKeys.SAVANNA, SAVANNA);
		map.put(BiomeKeys.WINDSWEPT_SAVANNA, SAVANNA);
		map.put(BiomeKeys.DEEP_FROZEN_OCEAN, SNOW);
		map.put(BiomeKeys.FROZEN_OCEAN, SNOW);
		map.put(BiomeKeys.FROZEN_RIVER, SNOW);
		map.put(BiomeKeys.ICE_SPIKES, SNOW);
		map.put(BiomeKeys.SNOWY_BEACH, SNOW);
		map.put(BiomeKeys.SNOWY_TAIGA, SNOW);
		map.put(BiomeKeys.SNOWY_PLAINS, SNOW);
		map.put(BiomeKeys.GROVE, SNOW);
		map.put(BiomeKeys.SNOWY_SLOPES, SNOW);
		map.put(BiomeKeys.FROZEN_PEAKS, SNOW);
		map.put(BiomeKeys.JAGGED_PEAKS, SNOW);
		map.put(BiomeKeys.SWAMP, SWAMP);
		map.put(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, TAIGA);
		map.put(BiomeKeys.OLD_GROWTH_PINE_TAIGA, TAIGA);
		map.put(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, TAIGA);
		map.put(BiomeKeys.WINDSWEPT_HILLS, TAIGA);
		map.put(BiomeKeys.TAIGA, TAIGA);
		map.put(BiomeKeys.WINDSWEPT_FOREST, TAIGA);
	});

	private VillagerType(String name) {
		this.name = name;
	}

	public String toString() {
		return this.name;
	}

	private static VillagerType create(String id) {
		return Registry.register(Registry.VILLAGER_TYPE, new Identifier(id), new VillagerType(id));
	}

	public static VillagerType forBiome(Optional<RegistryKey<Biome>> biomeKey) {
		return (VillagerType)biomeKey.flatMap(biomeKeyx -> Optional.ofNullable((VillagerType)BIOME_TO_TYPE.get(biomeKeyx))).orElse(PLAINS);
	}
}
