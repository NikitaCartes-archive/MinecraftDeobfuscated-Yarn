package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_6622;
import net.minecraft.class_6626;
import net.minecraft.structure.ShipwreckGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public class ShipwreckFeature extends StructureFeature<ShipwreckFeatureConfig> {
	public ShipwreckFeature(Codec<ShipwreckFeatureConfig> codec) {
		super(codec, ShipwreckFeature::method_38685);
	}

	private static void method_38685(class_6626 arg, ShipwreckFeatureConfig shipwreckFeatureConfig, class_6622.class_6623 arg2) {
		Heightmap.Type type = shipwreckFeatureConfig.isBeached ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG;
		if (arg2.method_38707(type)) {
			BlockRotation blockRotation = BlockRotation.random(arg2.random());
			BlockPos blockPos = new BlockPos(arg2.chunkPos().getStartX(), 90, arg2.chunkPos().getStartZ());
			ShipwreckGenerator.addParts(arg2.structureManager(), blockPos, blockRotation, arg, arg2.random(), shipwreckFeatureConfig);
		}
	}
}
