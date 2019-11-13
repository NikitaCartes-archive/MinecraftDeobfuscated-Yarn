package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ModifiableTestableWorld;

public abstract class BranchedTreeFeature<T extends BranchedTreeFeatureConfig> extends AbstractTreeFeature<T> {
	public BranchedTreeFeature(Function<Dynamic<?>, ? extends T> function) {
		super(function);
	}

	protected void method_23379(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		int i,
		BlockPos blockPos,
		int j,
		Set<BlockPos> set,
		BlockBox blockBox,
		BranchedTreeFeatureConfig branchedTreeFeatureConfig
	) {
		for (int k = 0; k < i - j; k++) {
			this.method_23382(modifiableTestableWorld, random, blockPos.up(k), set, blockBox, branchedTreeFeatureConfig);
		}
	}

	public Optional<BlockPos> method_23378(
		ModifiableTestableWorld modifiableTestableWorld, int i, int j, int k, BlockPos blockPos, BranchedTreeFeatureConfig branchedTreeFeatureConfig
	) {
		BlockPos blockPos2;
		if (!branchedTreeFeatureConfig.field_21593) {
			int l = modifiableTestableWorld.getTopPosition(Heightmap.Type.OCEAN_FLOOR, blockPos).getY();
			int m = modifiableTestableWorld.getTopPosition(Heightmap.Type.WORLD_SURFACE, blockPos).getY();
			blockPos2 = new BlockPos(blockPos.getX(), l, blockPos.getZ());
			if (m - l > branchedTreeFeatureConfig.field_21268) {
				return Optional.empty();
			}
		} else {
			blockPos2 = blockPos;
		}

		if (blockPos2.getY() >= 1 && blockPos2.getY() + i + 1 <= 256) {
			for (int l = 0; l <= i + 1; l++) {
				int m = branchedTreeFeatureConfig.foliagePlacer.method_23447(j, i, k, l);
				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int n = -m; n <= m; n++) {
					int o = -m;

					while (o <= m) {
						if (l + blockPos2.getY() >= 0 && l + blockPos2.getY() < 256) {
							mutable.set(n + blockPos2.getX(), l + blockPos2.getY(), o + blockPos2.getZ());
							if (canTreeReplace(modifiableTestableWorld, mutable) && (branchedTreeFeatureConfig.field_21269 || !isLeaves(modifiableTestableWorld, mutable))) {
								o++;
								continue;
							}

							return Optional.empty();
						}

						return Optional.empty();
					}
				}
			}

			return isDirtOrGrass(modifiableTestableWorld, blockPos2.method_10074()) && blockPos2.getY() < 256 - i - 1 ? Optional.of(blockPos2) : Optional.empty();
		} else {
			return Optional.empty();
		}
	}
}
