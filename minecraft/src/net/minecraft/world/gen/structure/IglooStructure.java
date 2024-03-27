package net.minecraft.world.gen.structure;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.structure.IglooGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.Heightmap;

public class IglooStructure extends Structure {
	public static final MapCodec<IglooStructure> CODEC = createCodec(IglooStructure::new);

	public IglooStructure(Structure.Config config) {
		super(config);
	}

	@Override
	public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
		return getStructurePosition(context, Heightmap.Type.WORLD_SURFACE_WG, collector -> this.addPieces(collector, context));
	}

	private void addPieces(StructurePiecesCollector collector, Structure.Context context) {
		ChunkPos chunkPos = context.chunkPos();
		ChunkRandom chunkRandom = context.random();
		BlockPos blockPos = new BlockPos(chunkPos.getStartX(), 90, chunkPos.getStartZ());
		BlockRotation blockRotation = BlockRotation.random(chunkRandom);
		IglooGenerator.addPieces(context.structureTemplateManager(), blockPos, blockRotation, collector, chunkRandom);
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.IGLOO;
	}
}
