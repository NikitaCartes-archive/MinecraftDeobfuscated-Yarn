package net.minecraft.village;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public interface VillagerType {
	VillagerType field_17071 = create("desert");
	VillagerType field_17072 = create("jungle");
	VillagerType field_17073 = create("plains");
	VillagerType field_17074 = create("savanna");
	VillagerType field_17075 = create("snow");
	VillagerType field_17076 = create("swamp");
	VillagerType field_17077 = create("taiga");
	Map<Biome, VillagerType> biomeToType = SystemUtil.consume(Maps.<Biome, VillagerType>newHashMap(), hashMap -> {
		hashMap.put(Biomes.field_9415, field_17071);
		hashMap.put(Biomes.field_9433, field_17071);
		hashMap.put(Biomes.field_9424, field_17071);
		hashMap.put(Biomes.field_9466, field_17071);
		hashMap.put(Biomes.field_9427, field_17071);
		hashMap.put(Biomes.field_9443, field_17071);
		hashMap.put(Biomes.field_9406, field_17071);
		hashMap.put(Biomes.field_9413, field_17071);
		hashMap.put(Biomes.field_9410, field_17071);
		hashMap.put(Biomes.field_9440, field_17072);
		hashMap.put(Biomes.field_9468, field_17072);
		hashMap.put(Biomes.field_9417, field_17072);
		hashMap.put(Biomes.field_9474, field_17072);
		hashMap.put(Biomes.field_9432, field_17072);
		hashMap.put(Biomes.field_9426, field_17072);
		hashMap.put(Biomes.field_9405, field_17072);
		hashMap.put(Biomes.field_9430, field_17074);
		hashMap.put(Biomes.field_9449, field_17074);
		hashMap.put(Biomes.field_9456, field_17074);
		hashMap.put(Biomes.field_9445, field_17074);
		hashMap.put(Biomes.field_9418, field_17075);
		hashMap.put(Biomes.field_9435, field_17075);
		hashMap.put(Biomes.field_9463, field_17075);
		hashMap.put(Biomes.field_9453, field_17075);
		hashMap.put(Biomes.field_9478, field_17075);
		hashMap.put(Biomes.field_9444, field_17075);
		hashMap.put(Biomes.field_9454, field_17075);
		hashMap.put(Biomes.field_9425, field_17075);
		hashMap.put(Biomes.field_9437, field_17075);
		hashMap.put(Biomes.field_9452, field_17075);
		hashMap.put(Biomes.field_9471, field_17076);
		hashMap.put(Biomes.field_9479, field_17076);
		hashMap.put(Biomes.field_9416, field_17077);
		hashMap.put(Biomes.field_9404, field_17077);
		hashMap.put(Biomes.field_9477, field_17077);
		hashMap.put(Biomes.field_9429, field_17077);
		hashMap.put(Biomes.field_9476, field_17077);
		hashMap.put(Biomes.field_9436, field_17077);
		hashMap.put(Biomes.field_9464, field_17077);
		hashMap.put(Biomes.field_9472, field_17077);
		hashMap.put(Biomes.field_9420, field_17077);
		hashMap.put(Biomes.field_9428, field_17077);
		hashMap.put(Biomes.field_9422, field_17077);
		hashMap.put(Biomes.field_9460, field_17077);
	});

	static VillagerType create(String string) {
		return Registry.VILLAGER_TYPE.register(new Identifier(string), new VillagerType() {
			public String toString() {
				return string;
			}
		});
	}

	static VillagerType forBiome(Biome biome) {
		return (VillagerType)biomeToType.getOrDefault(biome, field_17073);
	}
}
