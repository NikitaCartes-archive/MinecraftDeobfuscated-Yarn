package net.minecraft.world.gen;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.class_5268;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.StructureHolder;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.feature.StructureFeature;

public class StructureAccessor {
	private final ServerWorld field_24404;
	private final class_5268 field_24405;

	public StructureAccessor(ServerWorld serverWorld, class_5268 arg) {
		this.field_24404 = serverWorld;
		this.field_24405 = arg;
	}

	public Stream<StructureStart> getStructuresWithChildren(ChunkSectionPos pos, StructureFeature<?> feature, IWorld world) {
		return world.getChunk(pos.getSectionX(), pos.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES)
			.getStructureReferences(feature.getName())
			.stream()
			.map(long_ -> ChunkSectionPos.from(new ChunkPos(long_), 0))
			.map(
				chunkSectionPos -> this.getStructureStart(
						chunkSectionPos, feature, world.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.STRUCTURE_STARTS)
					)
			)
			.filter(structureStart -> structureStart != null && structureStart.hasChildren());
	}

	@Nullable
	public StructureStart getStructureStart(ChunkSectionPos pos, StructureFeature<?> feature, StructureHolder holder) {
		return holder.getStructureStart(feature.getName());
	}

	public void setStructureStart(ChunkSectionPos pos, StructureFeature<?> feature, StructureStart structureStart, StructureHolder holder) {
		holder.setStructureStart(feature.getName(), structureStart);
	}

	public void addStructureReference(ChunkSectionPos pos, StructureFeature<?> feature, long reference, StructureHolder holder) {
		holder.addStructureReference(feature.getName(), reference);
	}

	public boolean method_27834() {
		return this.field_24405.hasStructures();
	}
}
