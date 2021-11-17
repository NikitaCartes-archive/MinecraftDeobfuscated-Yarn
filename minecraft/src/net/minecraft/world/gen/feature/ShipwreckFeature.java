package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_6834;
import net.minecraft.structure.ShipwreckGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public class ShipwreckFeature extends StructureFeature<ShipwreckFeatureConfig> {
	public ShipwreckFeature(Codec<ShipwreckFeatureConfig> configCodec) {
		super(configCodec, class_6834.simple(ShipwreckFeature::method_39820, ShipwreckFeature::addPieces));
	}

	private static boolean method_39820(class_6834.class_6835<ShipwreckFeatureConfig> arg) {
		Heightmap.Type type = ((ShipwreckFeatureConfig)arg.config()).isBeached ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG;
		return arg.method_39848(type);
	}

	private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<ShipwreckFeatureConfig> context) {
		BlockRotation blockRotation = BlockRotation.random(context.random());
		BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 90, context.chunkPos().getStartZ());
		ShipwreckGenerator.addParts(context.structureManager(), blockPos, blockRotation, collector, context.random(), (ShipwreckFeatureConfig)context.config());
	}
}
