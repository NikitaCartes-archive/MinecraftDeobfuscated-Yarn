package net.minecraft;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import net.minecraft.world.chunk.ChunkGenerating;
import net.minecraft.world.chunk.ChunkStatus;

public record class_9768(ImmutableList<class_9770> steps) {
	public static final class_9768 field_51900 = new class_9768.class_9769()
		.method_60544(ChunkStatus.EMPTY, arg -> arg)
		.method_60544(ChunkStatus.STRUCTURE_STARTS, arg -> arg.method_60565(ChunkGenerating::generateStructures))
		.method_60544(
			ChunkStatus.STRUCTURE_REFERENCES, arg -> arg.method_60564(ChunkStatus.STRUCTURE_STARTS, 8).method_60565(ChunkGenerating::generateStructureReferences)
		)
		.method_60544(ChunkStatus.BIOMES, arg -> arg.method_60564(ChunkStatus.STRUCTURE_STARTS, 8).method_60565(ChunkGenerating::populateBiomes))
		.method_60544(
			ChunkStatus.NOISE,
			arg -> arg.method_60564(ChunkStatus.STRUCTURE_STARTS, 8).method_60564(ChunkStatus.BIOMES, 1).method_60562(0).method_60565(ChunkGenerating::populateNoise)
		)
		.method_60544(
			ChunkStatus.SURFACE,
			arg -> arg.method_60564(ChunkStatus.STRUCTURE_STARTS, 8).method_60564(ChunkStatus.BIOMES, 1).method_60562(0).method_60565(ChunkGenerating::buildSurface)
		)
		.method_60544(ChunkStatus.CARVERS, arg -> arg.method_60564(ChunkStatus.STRUCTURE_STARTS, 8).method_60562(0).method_60565(ChunkGenerating::carve))
		.method_60544(
			ChunkStatus.FEATURES,
			arg -> arg.method_60564(ChunkStatus.STRUCTURE_STARTS, 8)
					.method_60564(ChunkStatus.CARVERS, 1)
					.method_60562(1)
					.method_60565(ChunkGenerating::generateFeatures)
		)
		.method_60544(ChunkStatus.INITIALIZE_LIGHT, arg -> arg.method_60565(ChunkGenerating::initializeLight))
		.method_60544(ChunkStatus.LIGHT, arg -> arg.method_60564(ChunkStatus.INITIALIZE_LIGHT, 1).method_60565(ChunkGenerating::method_60555))
		.method_60544(ChunkStatus.SPAWN, arg -> arg.method_60564(ChunkStatus.BIOMES, 1).method_60565(ChunkGenerating::generateEntities))
		.method_60544(ChunkStatus.FULL, arg -> arg.method_60565(ChunkGenerating::method_60556))
		.method_60543();
	public static final class_9768 field_51901 = new class_9768.class_9769()
		.method_60544(ChunkStatus.EMPTY, arg -> arg)
		.method_60544(ChunkStatus.STRUCTURE_STARTS, arg -> arg.method_60565(ChunkGenerating::loadStructures))
		.method_60544(ChunkStatus.STRUCTURE_REFERENCES, arg -> arg)
		.method_60544(ChunkStatus.BIOMES, arg -> arg)
		.method_60544(ChunkStatus.NOISE, arg -> arg)
		.method_60544(ChunkStatus.SURFACE, arg -> arg)
		.method_60544(ChunkStatus.CARVERS, arg -> arg)
		.method_60544(ChunkStatus.FEATURES, arg -> arg)
		.method_60544(ChunkStatus.INITIALIZE_LIGHT, arg -> arg.method_60565(ChunkGenerating::initializeLight))
		.method_60544(ChunkStatus.LIGHT, arg -> arg.method_60564(ChunkStatus.INITIALIZE_LIGHT, 1).method_60565(ChunkGenerating::method_60555))
		.method_60544(ChunkStatus.SPAWN, arg -> arg)
		.method_60544(ChunkStatus.FULL, arg -> arg.method_60565(ChunkGenerating::method_60556))
		.method_60543();

	public class_9770 method_60518(ChunkStatus chunkStatus) {
		return (class_9770)this.steps.get(chunkStatus.getIndex());
	}

	public static class class_9769 {
		private final List<class_9770> field_51902 = new ArrayList();

		public class_9768 method_60543() {
			return new class_9768(ImmutableList.copyOf(this.field_51902));
		}

		public class_9768.class_9769 method_60544(ChunkStatus chunkStatus, UnaryOperator<class_9770.class_9771> unaryOperator) {
			class_9770.class_9771 lv;
			if (this.field_51902.isEmpty()) {
				lv = new class_9770.class_9771(chunkStatus);
			} else {
				lv = new class_9770.class_9771(chunkStatus, (class_9770)this.field_51902.getLast());
			}

			this.field_51902.add(((class_9770.class_9771)unaryOperator.apply(lv)).method_60561());
			return this;
		}
	}
}
