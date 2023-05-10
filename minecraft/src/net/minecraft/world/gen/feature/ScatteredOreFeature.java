package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ScatteredOreFeature extends Feature<OreFeatureConfig> {
	private static final int MAX_SPREAD = 7;

	ScatteredOreFeature(Codec<OreFeatureConfig> codec) {
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
			this.setPos(mutable, random, blockPos, Math.min(j, 7));
			BlockState blockState = structureWorldAccess.getBlockState(mutable);

			for (OreFeatureConfig.Target target : oreFeatureConfig.targets) {
				if (OreFeature.shouldPlace(blockState, structureWorldAccess::getBlockState, random, oreFeatureConfig, target, mutable)) {
					structureWorldAccess.setBlockState(mutable, target.state, Block.NOTIFY_LISTENERS);
					break;
				}
			}
		}

		return true;
	}

	private void setPos(BlockPos.Mutable mutable, Random random, BlockPos origin, int spread) {
		int i = this.getSpread(random, spread);
		int j = this.getSpread(random, spread);
		int k = this.getSpread(random, spread);
		mutable.set(origin, i, j, k);
	}

	private int getSpread(Random random, int spread) {
		return Math.round((random.nextFloat() - random.nextFloat()) * (float)spread);
	}
}
