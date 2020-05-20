package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class CheckerboardBiomeSource extends BiomeSource {
	public static final Codec<CheckerboardBiomeSource> field_24715 = RecordCodecBuilder.create(
		instance -> instance.group(
					Registry.BIOME.listOf().fieldOf("biomes").forGetter(checkerboardBiomeSource -> checkerboardBiomeSource.biomeArray),
					Codec.INT.fieldOf("scale").withDefault(2).forGetter(checkerboardBiomeSource -> checkerboardBiomeSource.field_24716)
				)
				.apply(instance, CheckerboardBiomeSource::new)
	);
	private final List<Biome> biomeArray;
	private final int gridSize;
	private final int field_24716;

	public CheckerboardBiomeSource(List<Biome> list, int size) {
		super(ImmutableList.copyOf(list));
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
		return (Biome)this.biomeArray.get(Math.floorMod((biomeX >> this.gridSize) + (biomeZ >> this.gridSize), this.biomeArray.size()));
	}
}
