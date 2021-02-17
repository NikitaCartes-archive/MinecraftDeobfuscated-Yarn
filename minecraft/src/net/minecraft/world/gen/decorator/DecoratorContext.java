package net.minecraft.world.gen.decorator;

import java.util.BitSet;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class DecoratorContext {
	private final StructureWorldAccess world;
	private final ChunkGenerator generator;

	public DecoratorContext(StructureWorldAccess world, ChunkGenerator generator) {
		this.world = world;
		this.generator = generator;
	}

	public int getTopY(Heightmap.Type heightmap, int x, int z) {
		return this.world.getTopY(heightmap, x, z);
	}

	public int getMinY() {
		return this.generator.getMinimumY();
	}

	public int getMaxY() {
		return this.generator.getWorldHeight();
	}

	public BitSet getOrCreateCarvingMask(ChunkPos chunkPos, GenerationStep.Carver carver) {
		return ((ProtoChunk)this.world.getChunk(chunkPos.x, chunkPos.z)).getOrCreateCarvingMask(carver);
	}

	public BlockState getBlockState(BlockPos pos) {
		return this.world.getBlockState(pos);
	}

	public int method_33868() {
		return this.world.getBottomY();
	}
}
