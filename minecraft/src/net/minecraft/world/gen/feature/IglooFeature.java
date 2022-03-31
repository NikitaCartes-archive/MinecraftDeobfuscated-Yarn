package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.structure.IglooGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.random.ChunkRandom;

public class IglooFeature extends StructureFeature {
	public static final Codec<IglooFeature> CODEC = createCodec(IglooFeature::new);

	public IglooFeature(StructureFeature.Config config) {
		super(config);
	}

	@Override
	public Optional<StructureFeature.StructurePosition> getStructurePosition(StructureFeature.Context context) {
		return getStructurePosition(context, Heightmap.Type.WORLD_SURFACE_WG, structurePiecesCollector -> this.addPieces(structurePiecesCollector, context));
	}

	private void addPieces(StructurePiecesCollector structurePiecesCollector, StructureFeature.Context context) {
		ChunkPos chunkPos = context.chunkPos();
		ChunkRandom chunkRandom = context.random();
		BlockPos blockPos = new BlockPos(chunkPos.getStartX(), 90, chunkPos.getStartZ());
		BlockRotation blockRotation = BlockRotation.random(chunkRandom);
		IglooGenerator.addPieces(context.structureManager(), blockPos, blockRotation, structurePiecesCollector, chunkRandom);
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.IGLOO;
	}
}
