package net.minecraft.world.gen;

import com.mojang.datafixers.DataFixUtils;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureHolder;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.feature.StructureFeature;

public class StructureAccessor {
	private final WorldAccess world;
	private final GeneratorOptions options;

	public StructureAccessor(WorldAccess world, GeneratorOptions options) {
		this.world = world;
		this.options = options;
	}

	public StructureAccessor forRegion(ChunkRegion region) {
		if (region.toServerWorld() != this.world) {
			throw new IllegalStateException("Using invalid feature manager (source level: " + region.toServerWorld() + ", region: " + region);
		} else {
			return new StructureAccessor(region, this.options);
		}
	}

	public Stream<? extends StructureStart<?>> getStructuresWithChildren(ChunkSectionPos pos, StructureFeature<?> feature) {
		return this.world
			.getChunk(pos.getSectionX(), pos.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES)
			.getStructureReferences(feature)
			.stream()
			.map(long_ -> ChunkSectionPos.from(new ChunkPos(long_), this.world.getMinimumSection()))
			.map(posx -> this.getStructureStart(posx, feature, this.world.getChunk(posx.getSectionX(), posx.getSectionZ(), ChunkStatus.STRUCTURE_STARTS)))
			.filter(structureStart -> structureStart != null && structureStart.hasChildren());
	}

	@Nullable
	public StructureStart<?> getStructureStart(ChunkSectionPos pos, StructureFeature<?> feature, StructureHolder holder) {
		return holder.getStructureStart(feature);
	}

	public void setStructureStart(ChunkSectionPos pos, StructureFeature<?> feature, StructureStart<?> structureStart, StructureHolder holder) {
		holder.setStructureStart(feature, structureStart);
	}

	public void addStructureReference(ChunkSectionPos pos, StructureFeature<?> feature, long reference, StructureHolder holder) {
		holder.addStructureReference(feature, reference);
	}

	public boolean shouldGenerateStructures() {
		return this.options.shouldGenerateStructures();
	}

	public StructureStart<?> getStructureAt(BlockPos pos, boolean matchChildren, StructureFeature<?> feature) {
		return DataFixUtils.orElse(
			this.getStructuresWithChildren(ChunkSectionPos.from(pos), feature)
				.filter(structureStart -> structureStart.getBoundingBox().contains(pos))
				.filter(structureStart -> !matchChildren || structureStart.getChildren().stream().anyMatch(piece -> piece.getBoundingBox().contains(pos)))
				.findFirst(),
			StructureStart.DEFAULT
		);
	}
}
