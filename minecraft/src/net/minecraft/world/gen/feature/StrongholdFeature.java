package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureType;

public class StrongholdFeature extends StructureFeature {
	public static final Codec<StrongholdFeature> CODEC = createCodec(StrongholdFeature::new);

	public StrongholdFeature(StructureFeature.Config config) {
		super(config);
	}

	@Override
	public Optional<StructureFeature.StructurePosition> getStructurePosition(StructureFeature.Context context) {
		return Optional.of(
			new StructureFeature.StructurePosition(context.chunkPos().getStartPos(), structurePiecesCollector -> addPieces(structurePiecesCollector, context))
		);
	}

	private static void addPieces(StructurePiecesCollector structurePiecesCollector, StructureFeature.Context context) {
		int i = 0;

		StrongholdGenerator.Start start;
		do {
			structurePiecesCollector.clear();
			context.random().setCarverSeed(context.seed() + (long)(i++), context.chunkPos().x, context.chunkPos().z);
			StrongholdGenerator.init();
			start = new StrongholdGenerator.Start(context.random(), context.chunkPos().getOffsetX(2), context.chunkPos().getOffsetZ(2));
			structurePiecesCollector.addPiece(start);
			start.fillOpenings(start, structurePiecesCollector, context.random());
			List<StructurePiece> list = start.pieces;

			while (!list.isEmpty()) {
				int j = context.random().nextInt(list.size());
				StructurePiece structurePiece = (StructurePiece)list.remove(j);
				structurePiece.fillOpenings(start, structurePiecesCollector, context.random());
			}

			structurePiecesCollector.shiftInto(context.chunkGenerator().getSeaLevel(), context.chunkGenerator().getMinimumY(), context.random(), 10);
		} while (structurePiecesCollector.isEmpty() || start.portalRoom == null);
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.STRONGHOLD;
	}
}
