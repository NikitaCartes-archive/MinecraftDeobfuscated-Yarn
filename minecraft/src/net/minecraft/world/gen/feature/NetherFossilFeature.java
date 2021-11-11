package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.NetherFossilGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;

public class NetherFossilFeature extends MarginedStructureFeature<RangeDecoratorConfig> {
	public NetherFossilFeature(Codec<RangeDecoratorConfig> configCodec) {
		super(configCodec, NetherFossilFeature::addPieces);
	}

	private static void addPieces(StructurePiecesCollector collector, RangeDecoratorConfig config, StructurePiecesGenerator.Context context) {
		int i = context.chunkPos().getStartX() + context.random().nextInt(16);
		int j = context.chunkPos().getStartZ() + context.random().nextInt(16);
		int k = context.chunkGenerator().getSeaLevel();
		HeightContext heightContext = new HeightContext(context.chunkGenerator(), context.world());
		int l = config.heightProvider.get(context.random(), heightContext);
		VerticalBlockSample verticalBlockSample = context.chunkGenerator().getColumnSample(i, j, context.world());
		BlockPos.Mutable mutable = new BlockPos.Mutable(i, l, j);

		while (l > k) {
			BlockState blockState = verticalBlockSample.getState(l);
			BlockState blockState2 = verticalBlockSample.getState(--l);
			if (blockState.isAir() && (blockState2.isOf(Blocks.SOUL_SAND) || blockState2.isSideSolidFullSquare(EmptyBlockView.INSTANCE, mutable.setY(l), Direction.UP))) {
				break;
			}
		}

		if (l > k) {
			if (context.biomeLimit().test(context.chunkGenerator().getBiomeForNoiseGen(BiomeCoords.fromBlock(i), BiomeCoords.fromBlock(l), BiomeCoords.fromBlock(j)))) {
				NetherFossilGenerator.addPieces(context.structureManager(), collector, context.random(), new BlockPos(i, l, j));
			}
		}
	}
}
