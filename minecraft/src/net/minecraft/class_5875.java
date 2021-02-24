package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class class_5875 extends Feature<OreFeatureConfig> {
	public class_5875(Codec<OreFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<OreFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		Random random = context.getRandom();
		OreFeatureConfig oreFeatureConfig = context.getConfig();
		BlockPos blockPos = context.getOrigin();
		int i = random.nextInt(oreFeatureConfig.size + 1);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int j = 0; j < i; j++) {
			this.method_33985(mutable, random, blockPos, Math.min(j, 7));
			BlockState blockState = structureWorldAccess.getBlockState(mutable);

			for (OreFeatureConfig.class_5876 lv : oreFeatureConfig.field_29063) {
				if (OreFeature.method_33983(blockState, structureWorldAccess::getBlockState, random, oreFeatureConfig, lv, mutable)) {
					structureWorldAccess.setBlockState(mutable, lv.field_29069, 2);
					break;
				}
			}
		}

		return true;
	}

	private void method_33985(BlockPos.Mutable mutable, Random random, BlockPos blockPos, int i) {
		int j = this.method_33986(random, i);
		int k = this.method_33986(random, i);
		int l = this.method_33986(random, i);
		mutable.set(blockPos, j, k, l);
	}

	private int method_33986(Random random, int i) {
		return Math.round((random.nextFloat() - random.nextFloat()) * (float)i);
	}
}
