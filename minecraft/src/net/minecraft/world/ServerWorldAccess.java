package net.minecraft.world;

import java.util.stream.Stream;
import net.minecraft.class_5425;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.gen.feature.StructureFeature;

public interface ServerWorldAccess extends class_5425 {
	long getSeed();

	Stream<? extends StructureStart<?>> method_30275(ChunkSectionPos chunkSectionPos, StructureFeature<?> structureFeature);
}
