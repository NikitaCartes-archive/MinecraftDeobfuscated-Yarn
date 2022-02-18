package net.minecraft.world.gen.chunk.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public record ConcentricRingsStructurePlacement(int distance, int spread, int count) implements StructurePlacement {
	public static final Codec<ConcentricRingsStructurePlacement> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.intRange(0, 1023).fieldOf("distance").forGetter(ConcentricRingsStructurePlacement::distance),
					Codec.intRange(0, 1023).fieldOf("spread").forGetter(ConcentricRingsStructurePlacement::spread),
					Codec.intRange(1, 4095).fieldOf("count").forGetter(ConcentricRingsStructurePlacement::count)
				)
				.apply(instance, ConcentricRingsStructurePlacement::new)
	);

	@Override
	public boolean isStartChunk(ChunkGenerator chunkGenerator, int x, int z) {
		List<ChunkPos> list = chunkGenerator.getConcentricRingsStartChunks(this);
		return list == null ? false : list.contains(new ChunkPos(x, z));
	}

	@Override
	public StructurePlacementType<?> getType() {
		return StructurePlacementType.CONCENTRIC_RINGS;
	}
}
