package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.OceanMonumentGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesList;
import net.minecraft.structure.StructureType;
import net.minecraft.tag.BiomeTags;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import net.minecraft.world.gen.random.RandomSeed;

public class OceanMonumentFeature extends StructureFeature {
	public static final Codec<OceanMonumentFeature> CODEC = RecordCodecBuilder.create(
		instance -> method_41608(instance).apply(instance, OceanMonumentFeature::new)
	);

	public OceanMonumentFeature(RegistryEntryList<Biome> registryEntryList, Map<SpawnGroup, StructureSpawns> map, GenerationStep.Feature feature, boolean bl) {
		super(registryEntryList, map, feature, bl);
	}

	@Override
	public Optional<StructureFeature.class_7150> method_38676(StructureFeature.class_7149 arg) {
		int i = arg.chunkPos().getOffsetX(9);
		int j = arg.chunkPos().getOffsetZ(9);

		for (RegistryEntry<Biome> registryEntry : arg.biomeSource().getBiomesInArea(i, arg.chunkGenerator().getSeaLevel(), j, 29, arg.randomState().sampler())) {
			if (!registryEntry.isIn(BiomeTags.REQUIRED_OCEAN_MONUMENT_SURROUNDING)) {
				return Optional.empty();
			}
		}

		return method_41612(arg, Heightmap.Type.OCEAN_FLOOR_WG, structurePiecesCollector -> addPieces(structurePiecesCollector, arg));
	}

	private static StructurePiece createBasePiece(ChunkPos pos, ChunkRandom random) {
		int i = pos.getStartX() - 29;
		int j = pos.getStartZ() - 29;
		Direction direction = Direction.Type.HORIZONTAL.random(random);
		return new OceanMonumentGenerator.Base(random, i, j, direction);
	}

	private static void addPieces(StructurePiecesCollector collector, StructureFeature.class_7149 arg) {
		collector.addPiece(createBasePiece(arg.chunkPos(), arg.random()));
	}

	public static StructurePiecesList modifyPiecesOnRead(ChunkPos pos, long worldSeed, StructurePiecesList pieces) {
		if (pieces.isEmpty()) {
			return pieces;
		} else {
			ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(RandomSeed.getSeed()));
			chunkRandom.setCarverSeed(worldSeed, pos.x, pos.z);
			StructurePiece structurePiece = (StructurePiece)pieces.pieces().get(0);
			BlockBox blockBox = structurePiece.getBoundingBox();
			int i = blockBox.getMinX();
			int j = blockBox.getMinZ();
			Direction direction = Direction.Type.HORIZONTAL.random(chunkRandom);
			Direction direction2 = (Direction)Objects.requireNonNullElse(structurePiece.getFacing(), direction);
			StructurePiece structurePiece2 = new OceanMonumentGenerator.Base(chunkRandom, i, j, direction2);
			StructurePiecesCollector structurePiecesCollector = new StructurePiecesCollector();
			structurePiecesCollector.addPiece(structurePiece2);
			return structurePiecesCollector.toList();
		}
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.OCEAN_MONUMENT;
	}
}
