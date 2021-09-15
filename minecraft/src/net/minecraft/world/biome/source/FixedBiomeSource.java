package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public class FixedBiomeSource extends BiomeSource implements BiomeAccess.Storage {
	public static final Codec<FixedBiomeSource> CODEC = Biome.REGISTRY_CODEC
		.fieldOf("biome")
		.<FixedBiomeSource>xmap(FixedBiomeSource::new, fixedBiomeSource -> fixedBiomeSource.biome)
		.stable()
		.codec();
	private final Supplier<Biome> biome;

	public FixedBiomeSource(Biome biome) {
		this(() -> biome);
	}

	public FixedBiomeSource(Supplier<Biome> biome) {
		super(ImmutableList.of((Biome)biome.get()));
		this.biome = biome;
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() {
		return CODEC;
	}

	@Override
	public BiomeSource withSeed(long seed) {
		return this;
	}

	@Override
	public Biome method_38109(int i, int j, int k, MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler) {
		return (Biome)this.biome.get();
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return (Biome)this.biome.get();
	}

	@Nullable
	@Override
	public BlockPos locateBiome(
		int x, int y, int z, int radius, int i, Predicate<Biome> predicate, Random random, boolean bl, MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler
	) {
		if (predicate.test((Biome)this.biome.get())) {
			return bl ? new BlockPos(x, y, z) : new BlockPos(x - radius + random.nextInt(radius * 2 + 1), y, z - radius + random.nextInt(radius * 2 + 1));
		} else {
			return null;
		}
	}

	@Override
	public Set<Biome> getBiomesInArea(int x, int y, int z, int radius, MultiNoiseUtil.MultiNoiseSampler multiNoiseSampler) {
		return Sets.<Biome>newHashSet((Biome)this.biome.get());
	}
}
