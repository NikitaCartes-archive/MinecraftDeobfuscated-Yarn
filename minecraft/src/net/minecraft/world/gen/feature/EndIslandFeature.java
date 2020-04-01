package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public class EndIslandFeature extends Feature<SingleStateFeatureConfig> {
	public EndIslandFeature(Function<Dynamic<?>, ? extends SingleStateFeatureConfig> function, Function<Random, ? extends SingleStateFeatureConfig> function2) {
		super(function, function2);
	}

	public boolean generate(
		IWorld iWorld,
		ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator,
		Random random,
		BlockPos blockPos,
		SingleStateFeatureConfig singleStateFeatureConfig
	) {
		float f = (float)(random.nextInt(3) + 4);

		for (int i = 0; f > 0.5F; i--) {
			for (int j = MathHelper.floor(-f); j <= MathHelper.ceil(f); j++) {
				for (int k = MathHelper.floor(-f); k <= MathHelper.ceil(f); k++) {
					if ((float)(j * j + k * k) <= (f + 1.0F) * (f + 1.0F)) {
						this.setBlockState(iWorld, blockPos.add(j, i, k), singleStateFeatureConfig.state);
					}
				}
			}

			f = (float)((double)f - ((double)random.nextInt(2) + 0.5));
		}

		return true;
	}
}
