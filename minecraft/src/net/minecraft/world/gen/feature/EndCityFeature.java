package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

public class EndCityFeature extends StructureFeature {
	public static final Codec<EndCityFeature> CODEC = createCodec(EndCityFeature::new);

	public EndCityFeature(StructureFeature.Config config) {
		super(config);
	}

	@Override
	public Optional<StructureFeature.StructurePosition> getStructurePosition(StructureFeature.Context context) {
		BlockRotation blockRotation = BlockRotation.random(context.random());
		BlockPos blockPos = this.getShiftedPos(context, blockRotation);
		return blockPos.getY() < 60
			? Optional.empty()
			: Optional.of(
				new StructureFeature.StructurePosition(blockPos, structurePiecesCollector -> this.method_39817(structurePiecesCollector, blockPos, blockRotation, context))
			);
	}

	private void method_39817(StructurePiecesCollector collector, BlockPos blockPos, BlockRotation blockRotation, StructureFeature.Context context) {
		List<StructurePiece> list = Lists.<StructurePiece>newArrayList();
		EndCityGenerator.addPieces(context.structureManager(), blockPos, blockRotation, list, context.random());
		list.forEach(collector::addPiece);
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.END_CITY;
	}
}
