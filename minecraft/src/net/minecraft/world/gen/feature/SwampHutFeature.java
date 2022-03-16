package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureType;
import net.minecraft.structure.SwampHutGenerator;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;

public class SwampHutFeature extends StructureFeature {
	public static final Codec<SwampHutFeature> CODEC = RecordCodecBuilder.create(instance -> method_41608(instance).apply(instance, SwampHutFeature::new));

	public SwampHutFeature(RegistryEntryList<Biome> registryEntryList, Map<SpawnGroup, StructureSpawns> map, GenerationStep.Feature feature, boolean bl) {
		super(registryEntryList, map, feature, bl);
	}

	@Override
	public Optional<StructureFeature.class_7150> method_38676(StructureFeature.class_7149 arg) {
		return method_41612(arg, Heightmap.Type.WORLD_SURFACE_WG, structurePiecesCollector -> addPieces(structurePiecesCollector, arg));
	}

	private static void addPieces(StructurePiecesCollector collector, StructureFeature.class_7149 arg) {
		collector.addPiece(new SwampHutGenerator(arg.random(), arg.chunkPos().getStartX(), arg.chunkPos().getStartZ()));
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.SWAMP_HUT;
	}
}
