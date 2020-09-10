package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class MineshaftFeature extends StructureFeature<MineshaftFeatureConfig> {
	public MineshaftFeature(Codec<MineshaftFeatureConfig> codec) {
		super(codec);
	}

	protected boolean shouldStartAt(
		ChunkGenerator chunkGenerator,
		BiomeSource biomeSource,
		long l,
		ChunkRandom chunkRandom,
		int i,
		int j,
		Biome biome,
		ChunkPos chunkPos,
		MineshaftFeatureConfig mineshaftFeatureConfig
	) {
		chunkRandom.setCarverSeed(l, i, j);
		double d = (double)mineshaftFeatureConfig.probability;
		return chunkRandom.nextDouble() < d;
	}

	@Override
	public StructureFeature.StructureStartFactory<MineshaftFeatureConfig> getStructureStartFactory() {
		return MineshaftFeature.Start::new;
	}

	public static class Start extends StructureStart<MineshaftFeatureConfig> {
		public Start(StructureFeature<MineshaftFeatureConfig> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
			super(structureFeature, i, j, blockBox, k, l);
		}

		public void init(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			int i,
			int j,
			Biome biome,
			MineshaftFeatureConfig mineshaftFeatureConfig
		) {
			MineshaftGenerator.MineshaftRoom mineshaftRoom = new MineshaftGenerator.MineshaftRoom(
				0, this.random, (i << 4) + 2, (j << 4) + 2, mineshaftFeatureConfig.type
			);
			this.children.add(mineshaftRoom);
			mineshaftRoom.fillOpenings(mineshaftRoom, this.children, this.random);
			this.setBoundingBoxFromChildren();
			if (mineshaftFeatureConfig.type == MineshaftFeature.Type.MESA) {
				int k = -5;
				int l = chunkGenerator.getSeaLevel() - this.boundingBox.maxY + this.boundingBox.getBlockCountY() / 2 - -5;
				this.boundingBox.move(0, l, 0);

				for (StructurePiece structurePiece : this.children) {
					structurePiece.translate(0, l, 0);
				}
			} else {
				this.randomUpwardTranslation(chunkGenerator.getSeaLevel(), this.random, 10);
			}
		}
	}

	public static enum Type implements StringIdentifiable {
		NORMAL("normal"),
		MESA("mesa");

		public static final Codec<MineshaftFeature.Type> CODEC = StringIdentifiable.createCodec(MineshaftFeature.Type::values, MineshaftFeature.Type::byName);
		private static final Map<String, MineshaftFeature.Type> BY_NAME = (Map<String, MineshaftFeature.Type>)Arrays.stream(values())
			.collect(Collectors.toMap(MineshaftFeature.Type::getName, type -> type));
		private final String name;

		private Type(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}

		private static MineshaftFeature.Type byName(String name) {
			return (MineshaftFeature.Type)BY_NAME.get(name);
		}

		public static MineshaftFeature.Type byIndex(int index) {
			return index >= 0 && index < values().length ? values()[index] : NORMAL;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
