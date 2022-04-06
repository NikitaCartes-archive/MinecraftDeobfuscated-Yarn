package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class ScatteredOreFeature extends Feature<OreFeatureConfig> {
	private static final int field_31515 = 7;

	ScatteredOreFeature(Codec<OreFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<OreFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		AbstractRandom abstractRandom = context.getRandom();
		OreFeatureConfig oreFeatureConfig = context.getConfig();
		BlockPos blockPos = context.getOrigin();
		int i = abstractRandom.nextInt(oreFeatureConfig.size + 1);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int j = 0; j < i; j++) {
			this.setPos(mutable, abstractRandom, blockPos, Math.min(j, 7));
			BlockState blockState = structureWorldAccess.getBlockState(mutable);

			for (OreFeatureConfig.Target target : oreFeatureConfig.targets) {
				if (OreFeature.shouldPlace(blockState, structureWorldAccess::getBlockState, abstractRandom, oreFeatureConfig, target, mutable)) {
					structureWorldAccess.setBlockState(mutable, target.state, Block.NOTIFY_LISTENERS);
					break;
				}
			}
		}

		return true;
	}

	private void setPos(BlockPos.Mutable mutable, AbstractRandom abstractRandom, BlockPos origin, int spread) {
		int i = this.getSpread(abstractRandom, spread);
		int j = this.getSpread(abstractRandom, spread);
		int k = this.getSpread(abstractRandom, spread);
		mutable.set(origin, i, j, k);
	}

	private int getSpread(AbstractRandom abstractRandom, int spread) {
		return Math.round((abstractRandom.nextFloat() - abstractRandom.nextFloat()) * (float)spread);
	}
}
