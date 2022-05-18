package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.chunk.AquiferSampler;

public record ConfiguredCarver<WC extends CarverConfig>(Carver<WC> carver, WC config) {
	public static final Codec<ConfiguredCarver<?>> CODEC = Registry.CARVER.getCodec().dispatch(configuredCarver -> configuredCarver.carver, Carver::getCodec);
	public static final Codec<RegistryEntry<ConfiguredCarver<?>>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.CONFIGURED_CARVER_KEY, CODEC);
	public static final Codec<RegistryEntryList<ConfiguredCarver<?>>> LIST_CODEC = RegistryCodecs.entryList(Registry.CONFIGURED_CARVER_KEY, CODEC);

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
		return SharedConstants.method_37896(chunk.getPos()) ? false : this.carver.carve(context, this.config, chunk, posToBiome, random, aquiferSampler, pos, mask);
	}
}
