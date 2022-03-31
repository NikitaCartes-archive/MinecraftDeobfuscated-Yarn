package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.structure.BuriedTreasureGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public class BuriedTreasureFeature extends StructureFeature {
	public static final Codec<BuriedTreasureFeature> CODEC = createCodec(BuriedTreasureFeature::new);

	public BuriedTreasureFeature(StructureFeature.Config config) {
		super(config);
	}

	@Override
	public Optional<StructureFeature.StructurePosition> getStructurePosition(StructureFeature.Context context) {
		return getStructurePosition(context, Heightmap.Type.OCEAN_FLOOR_WG, structurePiecesCollector -> addPieces(structurePiecesCollector, context));
	}

	private static void addPieces(StructurePiecesCollector collector, StructureFeature.Context context) {
		BlockPos blockPos = new BlockPos(context.chunkPos().getOffsetX(9), 90, context.chunkPos().getOffsetZ(9));
		collector.addPiece(new BuriedTreasureGenerator.Piece(blockPos));
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.BURIED_TREASURE;
	}
}
