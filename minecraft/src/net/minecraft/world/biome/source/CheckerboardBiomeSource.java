package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Objects;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class CheckerboardBiomeSource extends BiomeSource {
	private final Biome[] biomeArray;
	private final int gridSize;

	public CheckerboardBiomeSource(CheckerboardBiomeSourceConfig config) {
		super(ImmutableSet.copyOf(config.getBiomes()));
		this.biomeArray = config.getBiomes();
		this.gridSize = config.getSize() + 2;
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return this.biomeArray[Math.abs(((biomeX >> this.gridSize) + (biomeZ >> this.gridSize)) % this.biomeArray.length)];
	}

	@Override
	public BiomeSourceType<?, ?> method_26467() {
		return BiomeSourceType.CHECKERBOARD;
	}

	@Override
	public <T> Dynamic<T> method_26466(DynamicOps<T> dynamicOps) {
		T object = dynamicOps.createList(this.biomes.stream().map(Registry.BIOME::getId).map(Objects::toString).map(dynamicOps::createString));
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("biomes"), object, dynamicOps.createString("bitShift"), dynamicOps.createInt(this.gridSize)))
		);
	}
}
