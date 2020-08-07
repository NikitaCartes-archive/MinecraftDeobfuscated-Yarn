package net.minecraft.village;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltInBiomes;

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
		hashMap.put(BuiltInBiomes.BADLANDS, DESERT);
		hashMap.put(BuiltInBiomes.BADLANDS_PLATEAU, DESERT);
		hashMap.put(BuiltInBiomes.DESERT, DESERT);
		hashMap.put(BuiltInBiomes.DESERT_HILLS, DESERT);
		hashMap.put(BuiltInBiomes.DESERT_LAKES, DESERT);
		hashMap.put(BuiltInBiomes.ERODED_BADLANDS, DESERT);
		hashMap.put(BuiltInBiomes.MODIFIED_BADLANDS_PLATEAU, DESERT);
		hashMap.put(BuiltInBiomes.MODIFIED_WOODED_BADLANDS_PLATEAU, DESERT);
		hashMap.put(BuiltInBiomes.WOODED_BADLANDS_PLATEAU, DESERT);
		hashMap.put(BuiltInBiomes.BAMBOO_JUNGLE, JUNGLE);
		hashMap.put(BuiltInBiomes.BAMBOO_JUNGLE_HILLS, JUNGLE);
		hashMap.put(BuiltInBiomes.JUNGLE, JUNGLE);
		hashMap.put(BuiltInBiomes.JUNGLE_EDGE, JUNGLE);
		hashMap.put(BuiltInBiomes.JUNGLE_HILLS, JUNGLE);
		hashMap.put(BuiltInBiomes.MODIFIED_JUNGLE, JUNGLE);
		hashMap.put(BuiltInBiomes.MODIFIED_JUNGLE_EDGE, JUNGLE);
		hashMap.put(BuiltInBiomes.SAVANNA_PLATEAU, SAVANNA);
		hashMap.put(BuiltInBiomes.SAVANNA, SAVANNA);
		hashMap.put(BuiltInBiomes.SHATTERED_SAVANNA, SAVANNA);
		hashMap.put(BuiltInBiomes.SHATTERED_SAVANNA_PLATEAU, SAVANNA);
		hashMap.put(BuiltInBiomes.DEEP_FROZEN_OCEAN, SNOW);
		hashMap.put(BuiltInBiomes.FROZEN_OCEAN, SNOW);
		hashMap.put(BuiltInBiomes.FROZEN_RIVER, SNOW);
		hashMap.put(BuiltInBiomes.ICE_SPIKES, SNOW);
		hashMap.put(BuiltInBiomes.SNOWY_BEACH, SNOW);
		hashMap.put(BuiltInBiomes.SNOWY_MOUNTAINS, SNOW);
		hashMap.put(BuiltInBiomes.SNOWY_TAIGA, SNOW);
		hashMap.put(BuiltInBiomes.SNOWY_TAIGA_HILLS, SNOW);
		hashMap.put(BuiltInBiomes.SNOWY_TAIGA_MOUNTAINS, SNOW);
		hashMap.put(BuiltInBiomes.SNOWY_TUNDRA, SNOW);
		hashMap.put(BuiltInBiomes.SWAMP, SWAMP);
		hashMap.put(BuiltInBiomes.SWAMP_HILLS, SWAMP);
		hashMap.put(BuiltInBiomes.GIANT_SPRUCE_TAIGA, TAIGA);
		hashMap.put(BuiltInBiomes.GIANT_SPRUCE_TAIGA_HILLS, TAIGA);
		hashMap.put(BuiltInBiomes.GIANT_TREE_TAIGA, TAIGA);
		hashMap.put(BuiltInBiomes.GIANT_TREE_TAIGA_HILLS, TAIGA);
		hashMap.put(BuiltInBiomes.GRAVELLY_MOUNTAINS, TAIGA);
		hashMap.put(BuiltInBiomes.MODIFIED_GRAVELLY_MOUNTAINS, TAIGA);
		hashMap.put(BuiltInBiomes.MOUNTAIN_EDGE, TAIGA);
		hashMap.put(BuiltInBiomes.MOUNTAINS, TAIGA);
		hashMap.put(BuiltInBiomes.TAIGA, TAIGA);
		hashMap.put(BuiltInBiomes.TAIGA_HILLS, TAIGA);
		hashMap.put(BuiltInBiomes.TAIGA_MOUNTAINS, TAIGA);
		hashMap.put(BuiltInBiomes.WOODED_MOUNTAINS, TAIGA);
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

	public static VillagerType forBiome(Optional<RegistryKey<Biome>> optional) {
		return (VillagerType)optional.flatMap(registryKey -> Optional.ofNullable(BIOME_TO_TYPE.get(registryKey))).orElse(PLAINS);
	}
}
