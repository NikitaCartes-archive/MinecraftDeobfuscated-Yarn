package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.AquiferSampler;

public record ConfiguredCarver<WC extends CarverConfig>(Carver<WC> carver, WC config) {
	public static final Codec<ConfiguredCarver<?>> CODEC = Registries.CARVER.getCodec().dispatch(configuredCarver -> configuredCarver.carver, Carver::getCodec);
	public static final Codec<RegistryEntry<ConfiguredCarver<?>>> REGISTRY_CODEC = RegistryElementCodec.of(RegistryKeys.CONFIGURED_CARVER, CODEC);
	public static final Codec<RegistryEntryList<ConfiguredCarver<?>>> LIST_CODEC = RegistryCodecs.entryList(RegistryKeys.CONFIGURED_CARVER, CODEC);

	public boolean shouldCarve(Random random) {
		return this.carver.shouldCarve(this.config, random);
	}

	public boolean carve(
		CarverContext context,
		Chunk chunk,
		Function<BlockPos, RegistryEntry<Biome>> posToBiome,
		Random random,
		AquiferSampler aquiferSampler,
		ChunkPos pos,
		CarvingMask mask
	) {
		return SharedConstants.isOutsideGenerationArea(chunk.getPos())
			? false
			: this.carver.carve(context, this.config, chunk, posToBiome, random, aquiferSampler, pos, mask);
	}
}
