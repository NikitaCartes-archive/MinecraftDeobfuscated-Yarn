package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
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
		ChunkPos chunkPos,
		Biome biome,
		ChunkPos chunkPos2,
		MineshaftFeatureConfig mineshaftFeatureConfig,
		HeightLimitView heightLimitView
	) {
		chunkRandom.setCarverSeed(l, chunkPos.x, chunkPos.z);
		double d = (double)mineshaftFeatureConfig.probability;
		return chunkRandom.nextDouble() < d;
	}

	@Override
	public StructureFeature.StructureStartFactory<MineshaftFeatureConfig> getStructureStartFactory() {
		return MineshaftFeature.Start::new;
	}

	public static class Start extends StructureStart<MineshaftFeatureConfig> {
		public Start(StructureFeature<MineshaftFeatureConfig> structureFeature, ChunkPos chunkPos, int i, long l) {
			super(structureFeature, chunkPos, i, l);
		}

		public void init(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			ChunkPos chunkPos,
			Biome biome,
			MineshaftFeatureConfig mineshaftFeatureConfig,
			HeightLimitView heightLimitView
		) {
			MineshaftGenerator.MineshaftRoom mineshaftRoom = new MineshaftGenerator.MineshaftRoom(
				0, this.random, chunkPos.getOffsetX(2), chunkPos.getOffsetZ(2), mineshaftFeatureConfig.type
			);
			this.addPiece(mineshaftRoom);
			mineshaftRoom.fillOpenings(mineshaftRoom, this, this.random);
			if (mineshaftFeatureConfig.type == MineshaftFeature.Type.MESA) {
				int i = -5;
				BlockBox blockBox = this.setBoundingBoxFromChildren();
				int j = chunkGenerator.getSeaLevel() - blockBox.getMaxY() + blockBox.getBlockCountY() / 2 - -5;
				this.translateUpward(j);
			} else {
				this.randomUpwardTranslation(chunkGenerator.getSeaLevel(), chunkGenerator.getMinimumY(), this.random, 10);
			}
		}
	}

	public static enum Type implements StringIdentifiable {
		NORMAL("normal", Blocks.OAK_LOG, Blocks.OAK_PLANKS, Blocks.OAK_FENCE),
		MESA("mesa", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_FENCE);

		public static final Codec<MineshaftFeature.Type> CODEC = StringIdentifiable.createCodec(MineshaftFeature.Type::values, MineshaftFeature.Type::byName);
		private static final Map<String, MineshaftFeature.Type> BY_NAME = (Map<String, MineshaftFeature.Type>)Arrays.stream(values())
			.collect(Collectors.toMap(MineshaftFeature.Type::getName, type -> type));
		private final String name;
		private final BlockState log;
		private final BlockState planks;
		private final BlockState fence;

		private Type(String name, Block log, Block planks, Block fence) {
			this.name = name;
			this.log = log.getDefaultState();
			this.planks = planks.getDefaultState();
			this.fence = fence.getDefaultState();
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

		public BlockState getLog() {
			return this.log;
		}

		public BlockState getPlanks() {
			return this.planks;
		}

		public BlockState getFence() {
			return this.fence;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
