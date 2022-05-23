package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public class FixedBiomeSource extends BiomeSource implements BiomeAccess.Storage {
	public static final Codec<FixedBiomeSource> CODEC = Biome.REGISTRY_CODEC
		.fieldOf("biome")
		.<FixedBiomeSource>xmap(FixedBiomeSource::new, fixedBiomeSource -> fixedBiomeSource.biome)
		.stable()
		.codec();
	private final RegistryEntry<Biome> biome;

	public FixedBiomeSource(RegistryEntry<Biome> biome) {
		super(ImmutableList.of(biome));
		this.biome = biome;
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() {
		return CODEC;
	}

	@Override
	public RegistryEntry<Biome> getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise) {
		return this.biome;
	}

	@Override
	public RegistryEntry<Biome> getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return this.biome;
	}

	@Nullable
	@Override
	public Pair<BlockPos, RegistryEntry<Biome>> locateBiome(
		int x,
		int y,
		int z,
		int radius,
		int blockCheckInterval,
		Predicate<RegistryEntry<Biome>> predicate,
		Random random,
		boolean bl,
		MultiNoiseUtil.MultiNoiseSampler noiseSampler
	) {
		if (predicate.test(this.biome)) {
			return bl
				? Pair.of(new BlockPos(x, y, z), this.biome)
				: Pair.of(new BlockPos(x - radius + random.nextInt(radius * 2 + 1), y, z - radius + random.nextInt(radius * 2 + 1)), this.biome);
		} else {
			return null;
		}
	}

	@Nullable
	@Override
	public Pair<BlockPos, RegistryEntry<Biome>> locateBiome(
		BlockPos origin,
		int radius,
		int horizontalBlockCheckInterval,
		int verticalBlockCheckInterval,
		Predicate<RegistryEntry<Biome>> predicate,
		MultiNoiseUtil.MultiNoiseSampler noiseSampler,
		WorldView world
	) {
		return predicate.test(this.biome) ? Pair.of(origin, this.biome) : null;
	}

	@Override
	public Set<RegistryEntry<Biome>> getBiomesInArea(int x, int y, int z, int radius, MultiNoiseUtil.MultiNoiseSampler sampler) {
		return Sets.<RegistryEntry<Biome>>newHashSet(Set.of(this.biome));
	}
}
