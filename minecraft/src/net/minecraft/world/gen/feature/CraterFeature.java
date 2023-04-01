package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class CraterFeature extends Feature<CraterFeatureConfig> {
	public CraterFeature(Codec<CraterFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<CraterFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = structureWorldAccess.getTopPosition(Heightmap.Type.WORLD_SURFACE, context.getOrigin()).down();
		Random random = context.getRandom();
		CraterFeatureConfig craterFeatureConfig = context.getConfig();
		int i = craterFeatureConfig.radius().get(random);
		int j = craterFeatureConfig.depth().get(random);
		if (j > i) {
			return false;
		} else {
			int k = (j * j + i * i) / (2 * j);
			BlockPos blockPos2 = blockPos.up(k - j);
			BlockPos.Mutable mutable = blockPos.mutableCopy();
			Consumer<WorldAccess> consumer = world -> {
				for (int kx = -j; kx <= k; kx++) {
					boolean bl = false;

					for (int l = -k; l <= k; l++) {
						for (int m = -k; m <= k; m++) {
							mutable.set(blockPos, l, kx, m);
							if (mutable.getSquaredDistance(blockPos2) < (double)(k * k) && !world.getBlockState(mutable).isAir()) {
								bl = true;
								world.setBlockState(mutable, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
							}
						}
					}

					if (!bl && kx > 0) {
						break;
					}
				}
			};
			if (k < 15) {
				consumer.accept(structureWorldAccess);
			} else {
				ServerWorld serverWorld = structureWorldAccess.toServerWorld();
				serverWorld.getServer().execute(() -> consumer.accept(serverWorld));
			}

			return true;
		}
	}
}
