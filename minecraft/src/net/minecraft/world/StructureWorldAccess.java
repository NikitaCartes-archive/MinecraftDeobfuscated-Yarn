package net.minecraft.world;

import java.util.stream.Stream;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.gen.feature.StructureFeature;

public interface StructureWorldAccess extends ServerWorldAccess {
	long getSeed();

	Stream<? extends StructureStart<?>> getStructures(ChunkSectionPos pos, StructureFeature<?> feature);
}
