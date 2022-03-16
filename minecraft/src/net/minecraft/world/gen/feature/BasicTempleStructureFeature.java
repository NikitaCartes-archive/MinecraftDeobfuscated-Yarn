package net.minecraft.world.gen.feature;

import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.random.ChunkRandom;

public abstract class BasicTempleStructureFeature extends StructureFeature {
	private final BasicTempleStructureFeature.class_7148 field_37741;
	private int field_37742;
	private int field_37743;

	protected BasicTempleStructureFeature(
		BasicTempleStructureFeature.class_7148 arg,
		int i,
		int j,
		RegistryEntryList<Biome> validBiomes,
		Map<SpawnGroup, StructureSpawns> structureSpawns,
		GenerationStep.Feature featureGenerationStep,
		boolean bl
	) {
		super(validBiomes, structureSpawns, featureGenerationStep, bl);
		this.field_37741 = arg;
		this.field_37742 = i;
		this.field_37743 = j;
	}

	@Override
	public Optional<StructureFeature.class_7150> method_38676(StructureFeature.class_7149 arg) {
		return method_41610(arg, this.field_37742, this.field_37743) < arg.chunkGenerator().getSeaLevel()
			? Optional.empty()
			: method_41612(arg, Heightmap.Type.WORLD_SURFACE_WG, structurePiecesCollector -> this.method_41606(structurePiecesCollector, arg));
	}

	private void method_41606(StructurePiecesCollector structurePiecesCollector, StructureFeature.class_7149 arg) {
		ChunkPos chunkPos = arg.chunkPos();
		structurePiecesCollector.addPiece(this.field_37741.construct(arg.random(), chunkPos.getStartX(), chunkPos.getStartZ()));
	}

	@FunctionalInterface
	protected interface class_7148 {
		StructurePiece construct(ChunkRandom chunkRandom, int i, int j);
	}
}
