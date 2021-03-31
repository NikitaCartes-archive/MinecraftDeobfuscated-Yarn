package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.structure.OceanRuinGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class OceanRuinFeature extends StructureFeature<OceanRuinFeatureConfig> {
	public OceanRuinFeature(Codec<OceanRuinFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public StructureFeature.StructureStartFactory<OceanRuinFeatureConfig> getStructureStartFactory() {
		return OceanRuinFeature.Start::new;
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

	public static class Start extends StructureStart<OceanRuinFeatureConfig> {
		public Start(StructureFeature<OceanRuinFeatureConfig> structureFeature, ChunkPos chunkPos, int i, long l) {
			super(structureFeature, chunkPos, i, l);
		}

		public void init(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			ChunkPos chunkPos,
			Biome biome,
			OceanRuinFeatureConfig oceanRuinFeatureConfig,
			HeightLimitView heightLimitView
		) {
			BlockPos blockPos = new BlockPos(chunkPos.getStartX(), 90, chunkPos.getStartZ());
			BlockRotation blockRotation = BlockRotation.random(this.random);
			OceanRuinGenerator.addPieces(structureManager, blockPos, blockRotation, this, this.random, oceanRuinFeatureConfig);
		}
	}
}
