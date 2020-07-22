package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.biome.Biome;

public class CheckerboardBiomeSource extends BiomeSource {
	public static final Codec<CheckerboardBiomeSource> field_24715 = RecordCodecBuilder.create(
		instance -> instance.group(
					Biome.REGISTRY_CODEC.listOf().fieldOf("biomes").forGetter(checkerboardBiomeSource -> checkerboardBiomeSource.biomeArray),
					Codec.intRange(0, 62).fieldOf("scale").orElse(2).forGetter(checkerboardBiomeSource -> checkerboardBiomeSource.field_24716)
				)
				.apply(instance, CheckerboardBiomeSource::new)
	);
	private final List<Supplier<Biome>> biomeArray;
	private final int gridSize;
	private final int field_24716;

	public CheckerboardBiomeSource(List<Supplier<Biome>> list, int size) {
		super((List<Biome>)list.stream().map(Supplier::get).collect(ImmutableList.toImmutableList()));
		this.biomeArray = list;
		this.gridSize = size + 2;
		this.field_24716 = size;
	}

	@Override
	protected Codec<? extends BiomeSource> method_28442() {
		return field_24715;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public BiomeSource withSeed(long seed) {
		return this;
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return (Biome)((Supplier)this.biomeArray.get(Math.floorMod((biomeX >> this.gridSize) + (biomeZ >> this.gridSize), this.biomeArray.size()))).get();
	}
}
