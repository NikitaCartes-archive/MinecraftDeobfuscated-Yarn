package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.StrongholdGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureType;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;

public class StrongholdFeature extends StructureFeature {
	public static final Codec<StrongholdFeature> CODEC = RecordCodecBuilder.create(instance -> method_41608(instance).apply(instance, StrongholdFeature::new));

	public StrongholdFeature(RegistryEntryList<Biome> registryEntryList, Map<SpawnGroup, StructureSpawns> map, GenerationStep.Feature feature, boolean bl) {
		super(registryEntryList, map, feature, bl);
	}

	@Override
	public Optional<StructureFeature.class_7150> method_38676(StructureFeature.class_7149 arg) {
		return Optional.of(new StructureFeature.class_7150(arg.chunkPos().getStartPos(), structurePiecesCollector -> method_41691(structurePiecesCollector, arg)));
	}

	private static void method_41691(StructurePiecesCollector structurePiecesCollector, StructureFeature.class_7149 arg) {
		int i = 0;

		StrongholdGenerator.Start start;
		do {
			structurePiecesCollector.clear();
			arg.random().setCarverSeed(arg.seed() + (long)(i++), arg.chunkPos().x, arg.chunkPos().z);
			StrongholdGenerator.init();
			start = new StrongholdGenerator.Start(arg.random(), arg.chunkPos().getOffsetX(2), arg.chunkPos().getOffsetZ(2));
			structurePiecesCollector.addPiece(start);
			start.fillOpenings(start, structurePiecesCollector, arg.random());
			List<StructurePiece> list = start.pieces;

			while (!list.isEmpty()) {
				int j = arg.random().nextInt(list.size());
				StructurePiece structurePiece = (StructurePiece)list.remove(j);
				structurePiece.fillOpenings(start, structurePiecesCollector, arg.random());
			}

			structurePiecesCollector.shiftInto(arg.chunkGenerator().getSeaLevel(), arg.chunkGenerator().getMinimumY(), arg.random(), 10);
		} while (structurePiecesCollector.isEmpty() || start.portalRoom == null);
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.STRONGHOLD;
	}
}
