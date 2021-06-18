package net.minecraft.world.gen.decorator;

import java.util.BitSet;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class DecoratorContext extends HeightContext {
	private final StructureWorldAccess world;

	public DecoratorContext(StructureWorldAccess world, ChunkGenerator generator) {
		super(generator, world);
		this.world = world;
	}

	public int getTopY(Heightmap.Type heightmap, int x, int z) {
		return this.world.getTopY(heightmap, x, z);
	}

	public BitSet getOrCreateCarvingMask(ChunkPos chunkPos, GenerationStep.Carver carver) {
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
}
