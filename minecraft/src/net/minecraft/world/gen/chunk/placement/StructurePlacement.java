package net.minecraft.world.gen.chunk.placement;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public interface StructurePlacement {
	Codec<StructurePlacement> TYPE_CODEC = Registry.STRUCTURE_PLACEMENT.getCodec().dispatch(StructurePlacement::getType, StructurePlacementType::codec);

	boolean isStartChunk(ChunkGenerator chunkGenerator, long l, int i, int j);

	StructurePlacementType<?> getType();
}
