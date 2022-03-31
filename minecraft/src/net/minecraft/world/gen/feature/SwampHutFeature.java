package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureType;
import net.minecraft.structure.SwampHutGenerator;
import net.minecraft.world.Heightmap;

public class SwampHutFeature extends StructureFeature {
	public static final Codec<SwampHutFeature> CODEC = createCodec(SwampHutFeature::new);

	public SwampHutFeature(StructureFeature.Config config) {
		super(config);
	}

	@Override
	public Optional<StructureFeature.StructurePosition> getStructurePosition(StructureFeature.Context context) {
		return getStructurePosition(context, Heightmap.Type.WORLD_SURFACE_WG, structurePiecesCollector -> addPieces(structurePiecesCollector, context));
	}

	private static void addPieces(StructurePiecesCollector collector, StructureFeature.Context context) {
		collector.addPiece(new SwampHutGenerator(context.random(), context.chunkPos().getStartX(), context.chunkPos().getStartZ()));
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.SWAMP_HUT;
	}
}
