package net.minecraft.world.gen.structure;

import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.structure.NetherFortressGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.SpawnSettings;

public class NetherFortressStructure extends Structure {
	public static final Pool<SpawnSettings.SpawnEntry> MONSTER_SPAWNS = Pool.of(
		new SpawnSettings.SpawnEntry(EntityType.BLAZE, 10, 2, 3),
		new SpawnSettings.SpawnEntry(EntityType.ZOMBIFIED_PIGLIN, 5, 4, 4),
		new SpawnSettings.SpawnEntry(EntityType.WITHER_SKELETON, 8, 5, 5),
		new SpawnSettings.SpawnEntry(EntityType.SKELETON, 2, 5, 5),
		new SpawnSettings.SpawnEntry(EntityType.MAGMA_CUBE, 3, 4, 4)
	);
	public static final MapCodec<NetherFortressStructure> CODEC = createCodec(NetherFortressStructure::new);

	public NetherFortressStructure(Structure.Config config) {
		super(config);
	}

	@Override
	public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
		ChunkPos chunkPos = context.chunkPos();
		BlockPos blockPos = new BlockPos(chunkPos.getStartX(), 64, chunkPos.getStartZ());
		return Optional.of(new Structure.StructurePosition(blockPos, collector -> addPieces(collector, context)));
	}

	private static void addPieces(StructurePiecesCollector collector, Structure.Context context) {
		NetherFortressGenerator.Start start = new NetherFortressGenerator.Start(context.random(), context.chunkPos().getOffsetX(2), context.chunkPos().getOffsetZ(2));
		collector.addPiece(start);
		start.fillOpenings(start, collector, context.random());
		List<StructurePiece> list = start.pieces;

		while (!list.isEmpty()) {
			int i = context.random().nextInt(list.size());
			StructurePiece structurePiece = (StructurePiece)list.remove(i);
			structurePiece.fillOpenings(start, collector, context.random());
		}

		collector.shiftInto(context.random(), 48, 70);
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.FORTRESS;
	}
}
