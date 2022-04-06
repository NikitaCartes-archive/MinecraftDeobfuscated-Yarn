package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class EndIslandFeature extends Feature<DefaultFeatureConfig> {
	public EndIslandFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		AbstractRandom abstractRandom = context.getRandom();
		BlockPos blockPos = context.getOrigin();
		float f = (float)abstractRandom.nextInt(3) + 4.0F;

		for (int i = 0; f > 0.5F; i--) {
			for (int j = MathHelper.floor(-f); j <= MathHelper.ceil(f); j++) {
				for (int k = MathHelper.floor(-f); k <= MathHelper.ceil(f); k++) {
					if ((float)(j * j + k * k) <= (f + 1.0F) * (f + 1.0F)) {
						this.setBlockState(structureWorldAccess, blockPos.add(j, i, k), Blocks.END_STONE.getDefaultState());
					}
				}
			}

			f -= (float)abstractRandom.nextInt(2) + 0.5F;
		}

		return true;
	}
}
