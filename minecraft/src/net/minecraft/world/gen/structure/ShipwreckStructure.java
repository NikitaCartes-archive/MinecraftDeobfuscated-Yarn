package net.minecraft.world.gen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.structure.ShipwreckGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public class ShipwreckStructure extends Structure {
	public static final MapCodec<ShipwreckStructure> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(configCodecBuilder(instance), Codec.BOOL.fieldOf("is_beached").forGetter(shipwreckStructure -> shipwreckStructure.beached))
				.apply(instance, ShipwreckStructure::new)
	);
	public final boolean beached;

	public ShipwreckStructure(Structure.Config config, boolean beached) {
		super(config);
		this.beached = beached;
	}

	@Override
	public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
		Heightmap.Type type = this.beached ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG;
		return getStructurePosition(context, type, collector -> this.addPieces(collector, context));
	}

	private void addPieces(StructurePiecesCollector collector, Structure.Context context) {
		BlockRotation blockRotation = BlockRotation.random(context.random());
		BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 90, context.chunkPos().getStartZ());
		ShipwreckGenerator.Piece piece = ShipwreckGenerator.addParts(
			context.structureTemplateManager(), blockPos, blockRotation, collector, context.random(), this.beached
		);
		if (piece.isTooLargeForNormalGeneration()) {
			BlockBox blockBox = piece.getBoundingBox();
			int j;
			if (this.beached) {
				int i = Structure.getMinCornerHeight(context, blockBox.getMinX(), blockBox.getBlockCountX(), blockBox.getMinZ(), blockBox.getBlockCountZ());
				j = piece.findGroundedY(i, context.random());
			} else {
				j = Structure.getAverageCornerHeights(context, blockBox.getMinX(), blockBox.getBlockCountX(), blockBox.getMinZ(), blockBox.getBlockCountZ());
			}

			piece.setY(j);
		}
	}

	@Override
	public StructureType<?> getType() {
		return StructureType.SHIPWRECK;
	}
}
