package net.minecraft.world.gen.carver;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryEntry;
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
	private final NoiseConfig field_37706;
	private final MaterialRules.MaterialRule field_37707;

	public CarverContext(
		NoiseChunkGenerator noiseChunkGenerator,
		DynamicRegistryManager dynamicRegistryManager,
		HeightLimitView heightLimitView,
		ChunkNoiseSampler chunkNoiseSampler,
		NoiseConfig noiseConfig,
		MaterialRules.MaterialRule materialRule
	) {
		super(noiseChunkGenerator, heightLimitView);
		this.registryManager = dynamicRegistryManager;
		this.chunkNoiseSampler = chunkNoiseSampler;
		this.field_37706 = noiseConfig;
		this.field_37707 = materialRule;
	}

	@Deprecated
	public Optional<BlockState> applyMaterialRule(Function<BlockPos, RegistryEntry<Biome>> posToBiome, Chunk chunk, BlockPos pos, boolean hasFluid) {
		return this.field_37706.surfaceBuilder().applyMaterialRule(this.field_37707, this, posToBiome, chunk, this.chunkNoiseSampler, pos, hasFluid);
	}

	@Deprecated
	public DynamicRegistryManager getRegistryManager() {
		return this.registryManager;
	}

	public NoiseConfig method_41570() {
		return this.field_37706;
	}
}
