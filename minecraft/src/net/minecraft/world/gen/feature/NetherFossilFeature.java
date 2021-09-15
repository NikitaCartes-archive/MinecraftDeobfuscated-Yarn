package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.MarginedStructureStart;
import net.minecraft.structure.NetherFossilGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;

public class NetherFossilFeature extends StructureFeature<RangeDecoratorConfig> {
	public NetherFossilFeature(Codec<RangeDecoratorConfig> codec) {
		super(codec);
	}

	@Override
	public StructureFeature.StructureStartFactory<RangeDecoratorConfig> getStructureStartFactory() {
		return NetherFossilFeature.Start::new;
	}

	public static class Start extends MarginedStructureStart<RangeDecoratorConfig> {
		public Start(StructureFeature<RangeDecoratorConfig> structureFeature, ChunkPos chunkPos, int i, long l) {
			super(structureFeature, chunkPos, i, l);
		}

		public void init(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			ChunkPos chunkPos,
			RangeDecoratorConfig rangeDecoratorConfig,
			HeightLimitView heightLimitView,
			Predicate<Biome> predicate
		) {
			int i = chunkPos.getStartX() + this.random.nextInt(16);
			int j = chunkPos.getStartZ() + this.random.nextInt(16);
			int k = chunkGenerator.getSeaLevel();
			HeightContext heightContext = new HeightContext(chunkGenerator, heightLimitView);
			int l = rangeDecoratorConfig.heightProvider.get(this.random, heightContext);
			VerticalBlockSample verticalBlockSample = chunkGenerator.getColumnSample(i, j, heightLimitView);
			BlockPos.Mutable mutable = new BlockPos.Mutable(i, l, j);

			while (l > k) {
				BlockState blockState = verticalBlockSample.getState(l);
				BlockState blockState2 = verticalBlockSample.getState(--l);
				if (blockState.isAir() && (blockState2.isOf(Blocks.SOUL_SAND) || blockState2.isSideSolidFullSquare(EmptyBlockView.INSTANCE, mutable.setY(l), Direction.UP))
					)
				 {
					break;
				}
			}

			if (l > k) {
				if (predicate.test(chunkGenerator.getBiomeForNoiseGen(BiomeCoords.fromBlock(i), BiomeCoords.fromBlock(l), BiomeCoords.fromBlock(j)))) {
					NetherFossilGenerator.addPieces(structureManager, this, this.random, new BlockPos(i, l, j));
				}
			}
		}
	}
}
