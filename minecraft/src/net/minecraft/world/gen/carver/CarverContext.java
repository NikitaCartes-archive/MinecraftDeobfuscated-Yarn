package net.minecraft.world.gen.carver;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.chunk.ChunkNoiseSampler;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

public class CarverContext extends HeightContext {
	private final NoiseChunkGenerator chunkGenerator;
	private final DynamicRegistryManager registryManager;
	private final ChunkNoiseSampler chunkNoiseSampler;

	public CarverContext(
		NoiseChunkGenerator chunkGenerator, DynamicRegistryManager registryManager, HeightLimitView heightLimitView, ChunkNoiseSampler chunkNoiseSampler
	) {
		super(chunkGenerator, heightLimitView);
		this.chunkGenerator = chunkGenerator;
		this.registryManager = registryManager;
		this.chunkNoiseSampler = chunkNoiseSampler;
	}

	@Deprecated
	public Optional<BlockState> applyMaterialRule(Function<BlockPos, Biome> posToBiome, Chunk chunk, BlockPos pos, boolean bl) {
		return this.chunkGenerator.applyMaterialRule(this, posToBiome, chunk, this.chunkNoiseSampler, pos, bl);
	}

	@Deprecated
	public DynamicRegistryManager getRegistryManager() {
		return this.registryManager;
	}
}
