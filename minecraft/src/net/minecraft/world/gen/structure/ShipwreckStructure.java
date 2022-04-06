package net.minecraft.world.gen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.structure.ShipwreckGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public class ShipwreckStructure extends StructureType {
	public static final Codec<ShipwreckStructure> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(configCodecBuilder(instance), Codec.BOOL.fieldOf("is_beached").forGetter(shipwreckStructure -> shipwreckStructure.beached))
				.apply(instance, ShipwreckStructure::new)
	);
	public final boolean beached;

	public ShipwreckStructure(StructureType.Config config, boolean beached) {
		super(config);
		this.beached = beached;
	}

	@Override
	public Optional<StructureType.StructurePosition> getStructurePosition(StructureType.Context context) {
		Heightmap.Type type = this.beached ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG;
		return getStructurePosition(context, type, collector -> this.addPieces(collector, context));
	}

	private void addPieces(StructurePiecesCollector collector, StructureType.Context context) {
		BlockRotation blockRotation = BlockRotation.random(context.random());
		BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 90, context.chunkPos().getStartZ());
		ShipwreckGenerator.addParts(context.structureManager(), blockPos, blockRotation, collector, context.random(), this.beached);
	}

	@Override
	public net.minecraft.structure.StructureType<?> getType() {
		return net.minecraft.structure.StructureType.SHIPWRECK;
	}
}
