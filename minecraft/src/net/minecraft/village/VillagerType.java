package net.minecraft.village;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public interface VillagerType {
	VillagerType DESERT = create("desert");
	VillagerType JUNGLE = create("jungle");
	VillagerType PLAINS = create("plains");
	VillagerType SAVANNA = create("savanna");
	VillagerType SNOW = create("snow");
	VillagerType SWAMP = create("swamp");
	VillagerType TAIGA = create("taiga");
	Map<Biome, VillagerType> BIOME_TO_TYPE = SystemUtil.consume(Maps.<Biome, VillagerType>newHashMap(), hashMap -> {
		hashMap.put(Biomes.field_9415, DESERT);
		hashMap.put(Biomes.field_9433, DESERT);
		hashMap.put(Biomes.field_9424, DESERT);
		hashMap.put(Biomes.field_9466, DESERT);
		hashMap.put(Biomes.field_9427, DESERT);
		hashMap.put(Biomes.field_9443, DESERT);
		hashMap.put(Biomes.field_9406, DESERT);
		hashMap.put(Biomes.field_9413, DESERT);
		hashMap.put(Biomes.field_9410, DESERT);
		hashMap.put(Biomes.field_9440, JUNGLE);
		hashMap.put(Biomes.field_9468, JUNGLE);
		hashMap.put(Biomes.field_9417, JUNGLE);
		hashMap.put(Biomes.field_9474, JUNGLE);
		hashMap.put(Biomes.field_9432, JUNGLE);
		hashMap.put(Biomes.field_9426, JUNGLE);
		hashMap.put(Biomes.field_9405, JUNGLE);
		hashMap.put(Biomes.field_9430, SAVANNA);
		hashMap.put(Biomes.field_9449, SAVANNA);
		hashMap.put(Biomes.field_9456, SAVANNA);
		hashMap.put(Biomes.field_9445, SAVANNA);
		hashMap.put(Biomes.field_9418, SNOW);
		hashMap.put(Biomes.field_9435, SNOW);
		hashMap.put(Biomes.field_9463, SNOW);
		hashMap.put(Biomes.field_9453, SNOW);
		hashMap.put(Biomes.field_9478, SNOW);
		hashMap.put(Biomes.field_9444, SNOW);
		hashMap.put(Biomes.field_9454, SNOW);
		hashMap.put(Biomes.field_9425, SNOW);
		hashMap.put(Biomes.field_9437, SNOW);
		hashMap.put(Biomes.field_9452, SNOW);
		hashMap.put(Biomes.field_9471, SWAMP);
		hashMap.put(Biomes.field_9479, SWAMP);
		hashMap.put(Biomes.field_9416, TAIGA);
		hashMap.put(Biomes.field_9404, TAIGA);
		hashMap.put(Biomes.field_9477, TAIGA);
		hashMap.put(Biomes.field_9429, TAIGA);
		hashMap.put(Biomes.field_9476, TAIGA);
		hashMap.put(Biomes.field_9436, TAIGA);
		hashMap.put(Biomes.field_9464, TAIGA);
		hashMap.put(Biomes.field_9472, TAIGA);
		hashMap.put(Biomes.field_9420, TAIGA);
		hashMap.put(Biomes.field_9428, TAIGA);
		hashMap.put(Biomes.field_9422, TAIGA);
		hashMap.put(Biomes.field_9460, TAIGA);
	});

	static VillagerType create(String string) {
		return Registry.register(Registry.VILLAGER_TYPE, new Identifier(string), new VillagerType() {
			public String toString() {
				return string;
			}
		});
	}

	static VillagerType forBiome(Biome biome) {
		return (VillagerType)BIOME_TO_TYPE.getOrDefault(biome, PLAINS);
	}
}
