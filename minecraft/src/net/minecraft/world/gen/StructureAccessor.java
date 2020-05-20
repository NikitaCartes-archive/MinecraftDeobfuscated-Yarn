package net.minecraft.world.gen;

import com.mojang.datafixers.DataFixUtils;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.StructureHolder;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.feature.StructureFeature;

public class StructureAccessor {
	private final ServerWorld field_24404;
	private final GeneratorOptions field_24497;

	public StructureAccessor(ServerWorld serverWorld, GeneratorOptions generatorOptions) {
		this.field_24404 = serverWorld;
		this.field_24497 = generatorOptions;
	}

	public Stream<? extends StructureStart<?>> getStructuresWithChildren(ChunkSectionPos pos, StructureFeature<?> feature) {
		return this.field_24404
			.getChunk(pos.getSectionX(), pos.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES)
			.getStructureReferences(feature.getName())
			.stream()
			.map(long_ -> ChunkSectionPos.from(new ChunkPos(long_), 0))
			.map(
				chunkSectionPos -> this.getStructureStart(
						chunkSectionPos, feature, this.field_24404.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.STRUCTURE_STARTS)
					)
			)
			.filter(structureStart -> structureStart != null && structureStart.hasChildren());
	}

	@Nullable
	public StructureStart<?> getStructureStart(ChunkSectionPos pos, StructureFeature<?> feature, StructureHolder holder) {
		return holder.getStructureStart(feature.getName());
	}

	public void setStructureStart(ChunkSectionPos pos, StructureFeature<?> feature, StructureStart<?> structureStart, StructureHolder holder) {
		holder.setStructureStart(feature.getName(), structureStart);
	}

	public void addStructureReference(ChunkSectionPos pos, StructureFeature<?> feature, long reference, StructureHolder holder) {
		holder.addStructureReference(feature.getName(), reference);
	}

	public boolean method_27834() {
		return this.field_24497.shouldGenerateStructures();
	}

	public StructureStart<?> method_28388(BlockPos blockPos, boolean bl, StructureFeature<?> structureFeature) {
		return DataFixUtils.orElse(
			this.getStructuresWithChildren(ChunkSectionPos.from(blockPos), structureFeature)
				.filter(structureStart -> structureStart.getBoundingBox().contains(blockPos))
				.filter(structureStart -> !bl || structureStart.getChildren().stream().anyMatch(structurePiece -> structurePiece.getBoundingBox().contains(blockPos)))
				.findFirst(),
			StructureStart.DEFAULT
		);
	}
}
