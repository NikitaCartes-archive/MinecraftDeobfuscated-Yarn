package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class FixedBiomeSource extends BiomeSource {
	public static final Codec<FixedBiomeSource> field_24717 = Registry.BIOME
		.fieldOf("biome")
		.<FixedBiomeSource>xmap(FixedBiomeSource::new, fixedBiomeSource -> fixedBiomeSource.biome)
		.stable()
		.codec();
	private final Biome biome;

	public FixedBiomeSource(Biome biome) {
		super(ImmutableList.of(biome));
		this.biome = biome;
	}

	@Override
	protected Codec<? extends BiomeSource> method_28442() {
		return field_24717;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public BiomeSource withSeed(long seed) {
		return this;
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return this.biome;
	}

	@Nullable
	@Override
	public BlockPos locateBiome(int x, int y, int z, int radius, int i, List<Biome> biomes, Random random, boolean bl) {
		if (biomes.contains(this.biome)) {
			return bl ? new BlockPos(x, y, z) : new BlockPos(x - radius + random.nextInt(radius * 2 + 1), y, z - radius + random.nextInt(radius * 2 + 1));
		} else {
			return null;
		}
	}

	@Override
	public Set<Biome> getBiomesInArea(int x, int y, int z, int radius) {
		return Sets.<Biome>newHashSet(this.biome);
	}
}
