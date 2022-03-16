package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.NetherFortressGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureType;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;

public class NetherFortressFeature extends StructureFeature {
	public static final Pool<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = Pool.of(
		new SpawnSettings.SpawnEntry(EntityType.BLAZE, 10, 2, 3),
		new SpawnSettings.SpawnEntry(EntityType.ZOMBIFIED_PIGLIN, 5, 4, 4),
		new SpawnSettings.SpawnEntry(EntityType.WITHER_SKELETON, 8, 5, 5),
		new SpawnSettings.SpawnEntry(EntityType.SKELETON, 2, 5, 5),
		new SpawnSettings.SpawnEntry(EntityType.MAGMA_CUBE, 3, 4, 4)
	);
	public static final Codec<NetherFortressFeature> CODEC = RecordCodecBuilder.create(
		instance -> method_41608(instance).apply(instance, NetherFortressFeature::new)
	);

	public NetherFortressFeature(RegistryEntryList<Biome> registryEntryList, Map<SpawnGroup, StructureSpawns> map, GenerationStep.Feature feature, boolean bl) {
		super(registryEntryList, map, feature, bl);
	}

	@Override
	public Optional<StructureFeature.class_7150> method_38676(StructureFeature.class_7149 arg) {
		ChunkPos chunkPos = arg.chunkPos();
		BlockPos blockPos = new BlockPos(chunkPos.getStartX(), 64, chunkPos.getStartZ());
		return Optional.of(new StructureFeature.class_7150(blockPos, structurePiecesCollector -> addPieces(structurePiecesCollector, arg)));
	}

	private static void addPieces(StructurePiecesCollector collector, StructureFeature.class_7149 arg) {
		NetherFortressGenerator.Start start = new NetherFortressGenerator.Start(arg.random(), arg.chunkPos().getOffsetX(2), arg.chunkPos().getOffsetZ(2));
		collector.addPiece(start);
		start.fillOpenings(start, collector, arg.random());
		List<StructurePiece> list = start.pieces;

		while (!list.isEmpty()) {
			int i = arg.random().nextInt(list.size());
			StructurePiece structurePiece = (StructurePiece)list.remove(i);
			structurePiece.fillOpenings(start, collector, arg.random());
		}

		collector.shiftInto(arg.random(), 48, 70);
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.FORTRESS;
	}
}
