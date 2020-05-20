package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class CarvingMaskDecorator extends Decorator<CarvingMaskDecoratorConfig> {
	public CarvingMaskDecorator(Codec<CarvingMaskDecoratorConfig> codec) {
		super(codec);
	}

	public Stream<BlockPos> getPositions(
		WorldAccess worldAccess, ChunkGenerator chunkGenerator, Random random, CarvingMaskDecoratorConfig carvingMaskDecoratorConfig, BlockPos blockPos
	) {
		Chunk chunk = worldAccess.getChunk(blockPos);
		ChunkPos chunkPos = chunk.getPos();
		BitSet bitSet = ((ProtoChunk)chunk).getCarvingMask(carvingMaskDecoratorConfig.step);
		return bitSet == null
			? Stream.empty()
			: IntStream.range(0, bitSet.length()).filter(i -> bitSet.get(i) && random.nextFloat() < carvingMaskDecoratorConfig.probability).mapToObj(i -> {
				int j = i & 15;
				int k = i >> 4 & 15;
				int l = i >> 8;
				return new BlockPos(chunkPos.getStartX() + j, l, chunkPos.getStartZ() + k);
			});
	}
}
