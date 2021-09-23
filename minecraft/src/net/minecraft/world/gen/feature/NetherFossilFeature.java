package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.class_6622;
import net.minecraft.class_6626;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.MarginedStructureStart;
import net.minecraft.structure.NetherFossilGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;

public class NetherFossilFeature extends MarginedStructureStart<RangeDecoratorConfig> {
	public NetherFossilFeature(Codec<RangeDecoratorConfig> codec) {
		super(codec, NetherFossilFeature::method_38699);
	}

	private static void method_38699(class_6626 arg, RangeDecoratorConfig rangeDecoratorConfig, class_6622.class_6623 arg2) {
		int i = arg2.chunkPos().getStartX() + arg2.random().nextInt(16);
		int j = arg2.chunkPos().getStartZ() + arg2.random().nextInt(16);
		int k = arg2.chunkGenerator().getSeaLevel();
		HeightContext heightContext = new HeightContext(arg2.chunkGenerator(), arg2.heightAccessor());
		int l = rangeDecoratorConfig.heightProvider.get(arg2.random(), heightContext);
		VerticalBlockSample verticalBlockSample = arg2.chunkGenerator().getColumnSample(i, j, arg2.heightAccessor());
		BlockPos.Mutable mutable = new BlockPos.Mutable(i, l, j);

		while (l > k) {
			BlockState blockState = verticalBlockSample.getState(l);
			BlockState blockState2 = verticalBlockSample.getState(--l);
			if (blockState.isAir() && (blockState2.isOf(Blocks.SOUL_SAND) || blockState2.isSideSolidFullSquare(EmptyBlockView.INSTANCE, mutable.setY(l), Direction.UP))) {
				break;
			}
		}

		if (l > k) {
			if (arg2.validBiome().test(arg2.chunkGenerator().getBiomeForNoiseGen(BiomeCoords.fromBlock(i), BiomeCoords.fromBlock(l), BiomeCoords.fromBlock(j)))) {
				NetherFossilGenerator.addPieces(arg2.structureManager(), arg, arg2.random(), new BlockPos(i, l, j));
			}
		}
	}
}
