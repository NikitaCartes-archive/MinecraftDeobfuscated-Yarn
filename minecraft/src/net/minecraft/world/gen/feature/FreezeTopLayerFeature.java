package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class FreezeTopLayerFeature extends Feature<DefaultFeatureConfig> {
	public FreezeTopLayerFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function);
	}

	public boolean method_13978(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, DefaultFeatureConfig defaultFeatureConfig
	) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		BlockPos.Mutable mutable2 = new BlockPos.Mutable();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int k = blockPos.getX() + i;
				int l = blockPos.getZ() + j;
				int m = iWorld.getTop(Heightmap.Type.field_13197, k, l);
				mutable.set(k, m, l);
				mutable2.set(mutable).setOffset(Direction.field_11033, 1);
				Biome biome = iWorld.method_8310(mutable);
				if (biome.canSetSnow(iWorld, mutable2, false)) {
					iWorld.method_8652(mutable2, Blocks.field_10295.method_9564(), 2);
				}

				if (biome.canSetIce(iWorld, mutable)) {
					iWorld.method_8652(mutable, Blocks.field_10477.method_9564(), 2);
					BlockState blockState = iWorld.method_8320(mutable2);
					if (blockState.method_11570(SnowyBlock.field_11522)) {
						iWorld.method_8652(mutable2, blockState.method_11657(SnowyBlock.field_11522, Boolean.valueOf(true)), 2);
					}
				}
			}
		}

		return true;
	}
}
