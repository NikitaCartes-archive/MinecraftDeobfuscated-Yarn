package net.minecraft.world.gen.feature;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.carver.CarvingMask;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class FeaturePlacementContext extends HeightContext {
	private final StructureWorldAccess world;
	private final ChunkGenerator generator;
	private final Optional<PlacedFeature> placedFeature;

	public FeaturePlacementContext(StructureWorldAccess world, ChunkGenerator generator, Optional<PlacedFeature> placedFeature) {
		super(generator, world);
		this.world = world;
		this.generator = generator;
		this.placedFeature = placedFeature;
	}

	public int getTopY(Heightmap.Type heightmap, int x, int z) {
		return this.world.getTopY(heightmap, x, z);
	}

	public CarvingMask getOrCreateCarvingMask(ChunkPos chunkPos, GenerationStep.Carver carver) {
		return ((ProtoChunk)this.world.getChunk(chunkPos.x, chunkPos.z)).getOrCreateCarvingMask(carver);
	}

	public BlockState getBlockState(BlockPos pos) {
		return this.world.getBlockState(pos);
	}

	public int getBottomY() {
		return this.world.getBottomY();
	}

	public StructureWorldAccess getWorld() {
		return this.world;
	}

	public Optional<PlacedFeature> getPlacedFeature() {
		return this.placedFeature;
	}

	public ChunkGenerator getChunkGenerator() {
		return this.generator;
	}
}
