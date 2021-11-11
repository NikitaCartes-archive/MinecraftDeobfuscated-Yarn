package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.structure.OceanRuinGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

public class OceanRuinFeature extends StructureFeature<OceanRuinFeatureConfig> {
	public OceanRuinFeature(Codec<OceanRuinFeatureConfig> configCodec) {
		super(configCodec, OceanRuinFeature::addPieces);
	}

	private static void addPieces(StructurePiecesCollector collector, OceanRuinFeatureConfig config, StructurePiecesGenerator.Context context) {
		if (context.isBiomeValid(Heightmap.Type.OCEAN_FLOOR_WG)) {
			BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 90, context.chunkPos().getStartZ());
			BlockRotation blockRotation = BlockRotation.random(context.random());
			OceanRuinGenerator.addPieces(context.structureManager(), blockPos, blockRotation, collector, context.random(), config);
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
