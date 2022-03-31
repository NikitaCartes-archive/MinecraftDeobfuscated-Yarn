package net.minecraft.world.gen.feature;

import java.util.Optional;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.random.ChunkRandom;

public abstract class BasicTempleStructureFeature extends StructureFeature {
	private final BasicTempleStructureFeature.class_7148 field_37741;
	private int field_37742;
	private int field_37743;

	protected BasicTempleStructureFeature(BasicTempleStructureFeature.class_7148 arg, int i, int j, StructureFeature.Config config) {
		super(config);
		this.field_37741 = arg;
		this.field_37742 = i;
		this.field_37743 = j;
	}

	@Override
	public Optional<StructureFeature.StructurePosition> getStructurePosition(StructureFeature.Context context) {
		return getMinCornerHeight(context, this.field_37742, this.field_37743) < context.chunkGenerator().getSeaLevel()
			? Optional.empty()
			: getStructurePosition(context, Heightmap.Type.WORLD_SURFACE_WG, structurePiecesCollector -> this.method_41606(structurePiecesCollector, context));
	}

	private void method_41606(StructurePiecesCollector structurePiecesCollector, StructureFeature.Context context) {
		ChunkPos chunkPos = context.chunkPos();
		structurePiecesCollector.addPiece(this.field_37741.construct(context.random(), chunkPos.getStartX(), chunkPos.getStartZ()));
	}

	@FunctionalInterface
	protected interface class_7148 {
		StructurePiece construct(ChunkRandom chunkRandom, int i, int j);
	}
}
