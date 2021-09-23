package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_6622;
import net.minecraft.class_6626;
import net.minecraft.structure.IglooGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public class IglooFeature extends StructureFeature<DefaultFeatureConfig> {
	public IglooFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec, IglooFeature::method_38675);
	}

	private static void method_38675(class_6626 arg, DefaultFeatureConfig defaultFeatureConfig, class_6622.class_6623 arg2) {
		if (arg2.method_38707(Heightmap.Type.WORLD_SURFACE_WG)) {
			BlockPos blockPos = new BlockPos(arg2.chunkPos().getStartX(), 90, arg2.chunkPos().getStartZ());
			BlockRotation blockRotation = BlockRotation.random(arg2.random());
			IglooGenerator.addPieces(arg2.structureManager(), blockPos, blockRotation, arg, arg2.random());
		}
	}
}
