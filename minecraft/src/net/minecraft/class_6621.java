package net.minecraft;

import java.util.Random;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

@FunctionalInterface
public interface class_6621 {
	class_6621 field_34938 = (structureWorldAccess, structureAccessor, chunkGenerator, random, blockBox, chunkPos, arg) -> {
	};

	void afterPlace(
		StructureWorldAccess structureWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		Random random,
		BlockBox blockBox,
		ChunkPos chunkPos,
		class_6624 arg
	);
}
