package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.class_6622;
import net.minecraft.class_6626;
import net.minecraft.structure.OceanRuinGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public class OceanRuinFeature extends StructureFeature<OceanRuinFeatureConfig> {
	public OceanRuinFeature(Codec<OceanRuinFeatureConfig> codec) {
		super(codec, OceanRuinFeature::method_38700);
	}

	private static void method_38700(class_6626 arg, OceanRuinFeatureConfig oceanRuinFeatureConfig, class_6622.class_6623 arg2) {
		if (arg2.method_38707(Heightmap.Type.OCEAN_FLOOR_WG)) {
			BlockPos blockPos = new BlockPos(arg2.chunkPos().getStartX(), 90, arg2.chunkPos().getStartZ());
			BlockRotation blockRotation = BlockRotation.random(arg2.random());
			OceanRuinGenerator.addPieces(arg2.structureManager(), blockPos, blockRotation, arg, arg2.random(), oceanRuinFeatureConfig);
		}
	}

	public static enum BiomeType implements StringIdentifiable {
		WARM("warm"),
		COLD("cold");

		public static final Codec<OceanRuinFeature.BiomeType> CODEC = StringIdentifiable.createCodec(
			OceanRuinFeature.BiomeType::values, OceanRuinFeature.BiomeType::byName
		);
		private static final Map<String, OceanRuinFeature.BiomeType> BY_NAME = (Map<String, OceanRuinFeature.BiomeType>)Arrays.stream(values())
			.collect(Collectors.toMap(OceanRuinFeature.BiomeType::getName, biomeType -> biomeType));
		private final String name;

		private BiomeType(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		@Nullable
		public static OceanRuinFeature.BiomeType byName(String name) {
			return (OceanRuinFeature.BiomeType)BY_NAME.get(name);
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
