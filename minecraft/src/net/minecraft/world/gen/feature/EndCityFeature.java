package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;

public class EndCityFeature extends StructureFeature {
	public static final Codec<EndCityFeature> CODEC = RecordCodecBuilder.create(instance -> method_41608(instance).apply(instance, EndCityFeature::new));

	public EndCityFeature(RegistryEntryList<Biome> registryEntryList, Map<SpawnGroup, StructureSpawns> map, GenerationStep.Feature feature, boolean bl) {
		super(registryEntryList, map, feature, bl);
	}

	@Override
	public Optional<StructureFeature.class_7150> method_38676(StructureFeature.class_7149 arg) {
		BlockRotation blockRotation = BlockRotation.random(arg.random());
		BlockPos blockPos = this.method_42382(arg, blockRotation);
		return blockPos.getY() < 60
			? Optional.empty()
			: Optional.of(
				new StructureFeature.class_7150(blockPos, structurePiecesCollector -> this.method_39817(structurePiecesCollector, blockPos, blockRotation, arg))
			);
	}

	private void method_39817(StructurePiecesCollector collector, BlockPos blockPos, BlockRotation blockRotation, StructureFeature.class_7149 arg) {
		List<StructurePiece> list = Lists.<StructurePiece>newArrayList();
		EndCityGenerator.addPieces(arg.structureTemplateManager(), blockPos, blockRotation, list, arg.random());
		list.forEach(collector::addPiece);
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.END_CITY;
	}
}
