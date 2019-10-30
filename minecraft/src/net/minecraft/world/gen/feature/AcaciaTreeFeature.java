package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ModifiableTestableWorld;

public class AcaciaTreeFeature extends BranchedTreeFeature<BranchedTreeFeatureConfig> {
	public AcaciaTreeFeature(Function<Dynamic<?>, ? extends BranchedTreeFeatureConfig> function) {
		super(function);
	}

	public boolean method_23386(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		BlockPos blockPos,
		Set<BlockPos> set,
		Set<BlockPos> set2,
		BlockBox blockBox,
		BranchedTreeFeatureConfig branchedTreeFeatureConfig
	) {
		int i = branchedTreeFeatureConfig.baseHeight
			+ random.nextInt(branchedTreeFeatureConfig.heightRandA + 1)
			+ random.nextInt(branchedTreeFeatureConfig.heightRandB + 1);
		int j = branchedTreeFeatureConfig.trunkHeight >= 0
			? branchedTreeFeatureConfig.trunkHeight + random.nextInt(branchedTreeFeatureConfig.trunkHeightRandom + 1)
			: i - (branchedTreeFeatureConfig.field_21266 + random.nextInt(branchedTreeFeatureConfig.field_21267 + 1));
		int k = branchedTreeFeatureConfig.foliagePlacer.method_23452(random, j, i, branchedTreeFeatureConfig);
		Optional<BlockPos> optional = this.method_23378(modifiableTestableWorld, i, j, k, blockPos, branchedTreeFeatureConfig);
		if (!optional.isPresent()) {
			return false;
		} else {
			BlockPos blockPos2 = (BlockPos)optional.get();
			this.setToDirt(modifiableTestableWorld, blockPos2.method_10074());
			Direction direction = Direction.Type.HORIZONTAL.random(random);
			int l = i - random.nextInt(4) - 1;
			int m = 3 - random.nextInt(3);
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			int n = blockPos2.getX();
			int o = blockPos2.getZ();
			int p = 0;

			for (int q = 0; q < i; q++) {
				int r = blockPos2.getY() + q;
				if (q >= l && m > 0) {
					n += direction.getOffsetX();
					o += direction.getOffsetZ();
					m--;
				}

				if (this.method_23382(modifiableTestableWorld, random, mutable.set(n, r, o), set, blockBox, branchedTreeFeatureConfig)) {
					p = r;
				}
			}

			BlockPos blockPos3 = new BlockPos(n, p, o);
			branchedTreeFeatureConfig.foliagePlacer.method_23448(modifiableTestableWorld, random, branchedTreeFeatureConfig, i, j, k + 1, blockPos3, set2);
			n = blockPos2.getX();
			o = blockPos2.getZ();
			Direction direction2 = Direction.Type.HORIZONTAL.random(random);
			if (direction2 != direction) {
				int rx = l - random.nextInt(2) - 1;
				int s = 1 + random.nextInt(3);
				p = 0;

				for (int t = rx; t < i && s > 0; s--) {
					if (t >= 1) {
						int u = blockPos2.getY() + t;
						n += direction2.getOffsetX();
						o += direction2.getOffsetZ();
						if (this.method_23382(modifiableTestableWorld, random, mutable.set(n, u, o), set, blockBox, branchedTreeFeatureConfig)) {
							p = u;
						}
					}

					t++;
				}

				if (p > 0) {
					BlockPos blockPos4 = new BlockPos(n, p, o);
					branchedTreeFeatureConfig.foliagePlacer.method_23448(modifiableTestableWorld, random, branchedTreeFeatureConfig, i, j, k, blockPos4, set2);
				}
			}

			return true;
		}
	}
}
