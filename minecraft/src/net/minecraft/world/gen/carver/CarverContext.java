package net.minecraft.world.gen.carver;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;

public class CarverContext extends HeightContext {
	private final NoiseChunkGenerator chunkGenerator;
	private final DynamicRegistryManager registryManager;

	public CarverContext(NoiseChunkGenerator chunkGenerator, DynamicRegistryManager registryManager, HeightLimitView heightLimitView) {
		super(chunkGenerator, heightLimitView);
		this.chunkGenerator = chunkGenerator;
		this.registryManager = registryManager;
	}

	@Deprecated
	public Optional<BlockState> method_39114(Biome biome, Chunk chunk, BlockPos pos, boolean bl) {
		return this.chunkGenerator.method_39041(this, biome, chunk, pos, bl);
	}

	@Deprecated
	public DynamicRegistryManager getRegistryManager() {
		return this.registryManager;
	}
}
