package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class FreezeTopLayerFeature extends Feature<DefaultFeatureConfig> {
	public FreezeTopLayerFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = context.getOrigin();
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		BlockPos.Mutable mutable2 = new BlockPos.Mutable();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int k = blockPos.getX() + i;
				int l = blockPos.getZ() + j;
				int m = structureWorldAccess.getTopY(Heightmap.Type.MOTION_BLOCKING, k, l);
				mutable.set(k, m, l);
				mutable2.set(mutable).move(Direction.DOWN, 1);
				Biome biome = structureWorldAccess.getBiome(mutable).value();
				if (biome.canSetIce(structureWorldAccess, mutable2, false)) {
					structureWorldAccess.setBlockState(mutable2, Blocks.ICE.getDefaultState(), Block.NOTIFY_LISTENERS);
				}

				if (biome.canSetSnow(structureWorldAccess, mutable)) {
					structureWorldAccess.setBlockState(mutable, Blocks.SNOW.getDefaultState(), Block.NOTIFY_LISTENERS);
					BlockState blockState = structureWorldAccess.getBlockState(mutable2);
					if (blockState.contains(SnowyBlock.SNOWY)) {
						structureWorldAccess.setBlockState(mutable2, blockState.with(SnowyBlock.SNOWY, Boolean.valueOf(true)), Block.NOTIFY_LISTENERS);
					}
				}
			}
		}

		return true;
	}
}
