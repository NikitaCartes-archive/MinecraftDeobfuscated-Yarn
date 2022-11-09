package net.minecraft.world.gen.carver;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;

public class CarverContext extends HeightContext {
	private final DynamicRegistryManager registryManager;
	private final ChunkNoiseSampler chunkNoiseSampler;
	private final NoiseConfig noiseConfig;
	private final MaterialRules.MaterialRule materialRule;

	public CarverContext(
		NoiseChunkGenerator noiseChunkGenerator,
		DynamicRegistryManager registryManager,
		HeightLimitView heightLimitView,
		ChunkNoiseSampler chunkNoiseSampler,
		NoiseConfig noiseConfig,
		MaterialRules.MaterialRule materialRule
	) {
		super(noiseChunkGenerator, heightLimitView);
		this.registryManager = registryManager;
		this.chunkNoiseSampler = chunkNoiseSampler;
		this.noiseConfig = noiseConfig;
		this.materialRule = materialRule;
	}

	@Deprecated
	public Optional<BlockState> applyMaterialRule(Function<BlockPos, RegistryEntry<Biome>> posToBiome, Chunk chunk, BlockPos pos, boolean hasFluid) {
		return this.noiseConfig.getSurfaceBuilder().applyMaterialRule(this.materialRule, this, posToBiome, chunk, this.chunkNoiseSampler, pos, hasFluid);
	}

	@Deprecated
	public DynamicRegistryManager getRegistryManager() {
		return this.registryManager;
	}

	public NoiseConfig getNoiseConfig() {
		return this.noiseConfig;
	}
}
