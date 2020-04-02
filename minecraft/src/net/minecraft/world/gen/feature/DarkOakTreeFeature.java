package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.TestableWorld;

public class DarkOakTreeFeature extends AbstractTreeFeature<MegaTreeFeatureConfig> {
	public DarkOakTreeFeature(Function<Dynamic<?>, ? extends MegaTreeFeatureConfig> function) {
		super(function);
	}

	public boolean generate(
		ModifiableTestableWorld modifiableTestableWorld,
		Random random,
		BlockPos blockPos,
		Set<BlockPos> set,
		Set<BlockPos> set2,
		BlockBox blockBox,
		MegaTreeFeatureConfig megaTreeFeatureConfig
	) {
		int i = random.nextInt(3) + random.nextInt(2) + megaTreeFeatureConfig.baseHeight;
		int j = blockPos.getX();
		int k = blockPos.getY();
		int l = blockPos.getZ();
		if (k >= 1 && k + i + 1 < 256) {
			BlockPos blockPos2 = blockPos.down();
			if (!isNaturalDirtOrGrass(modifiableTestableWorld, blockPos2)) {
				return false;
			} else if (!this.doesTreeFit(modifiableTestableWorld, blockPos, i)) {
				return false;
			} else {
				this.setToDirt(modifiableTestableWorld, blockPos2);
				this.setToDirt(modifiableTestableWorld, blockPos2.east());
				this.setToDirt(modifiableTestableWorld, blockPos2.south());
				this.setToDirt(modifiableTestableWorld, blockPos2.south().east());
				Direction direction = Direction.Type.HORIZONTAL.random(random);
				int m = i - random.nextInt(4);
				int n = 2 - random.nextInt(3);
				int o = j;
				int p = l;
				int q = k + i - 1;

				for (int r = 0; r < i; r++) {
					if (r >= m && n > 0) {
						o += direction.getOffsetX();
						p += direction.getOffsetZ();
						n--;
					}

					int s = k + r;
					BlockPos blockPos3 = new BlockPos(o, s, p);
					if (isAirOrLeaves(modifiableTestableWorld, blockPos3)) {
						setLogBlockState(modifiableTestableWorld, random, blockPos3, set, blockBox, megaTreeFeatureConfig);
						setLogBlockState(modifiableTestableWorld, random, blockPos3.east(), set, blockBox, megaTreeFeatureConfig);
						setLogBlockState(modifiableTestableWorld, random, blockPos3.south(), set, blockBox, megaTreeFeatureConfig);
						setLogBlockState(modifiableTestableWorld, random, blockPos3.east().south(), set, blockBox, megaTreeFeatureConfig);
					}
				}

				for (int r = -2; r <= 0; r++) {
					for (int s = -2; s <= 0; s++) {
						int t = -1;
						this.setLeavesBlockState(modifiableTestableWorld, random, new BlockPos(o + r, q + t, p + s), set2, blockBox, megaTreeFeatureConfig);
						this.setLeavesBlockState(modifiableTestableWorld, random, new BlockPos(1 + o - r, q + t, p + s), set2, blockBox, megaTreeFeatureConfig);
						this.setLeavesBlockState(modifiableTestableWorld, random, new BlockPos(o + r, q + t, 1 + p - s), set2, blockBox, megaTreeFeatureConfig);
						this.setLeavesBlockState(modifiableTestableWorld, random, new BlockPos(1 + o - r, q + t, 1 + p - s), set2, blockBox, megaTreeFeatureConfig);
						if ((r > -2 || s > -1) && (r != -1 || s != -2)) {
							int var31 = 1;
							this.setLeavesBlockState(modifiableTestableWorld, random, new BlockPos(o + r, q + var31, p + s), set2, blockBox, megaTreeFeatureConfig);
							this.setLeavesBlockState(modifiableTestableWorld, random, new BlockPos(1 + o - r, q + var31, p + s), set2, blockBox, megaTreeFeatureConfig);
							this.setLeavesBlockState(modifiableTestableWorld, random, new BlockPos(o + r, q + var31, 1 + p - s), set2, blockBox, megaTreeFeatureConfig);
							this.setLeavesBlockState(modifiableTestableWorld, random, new BlockPos(1 + o - r, q + var31, 1 + p - s), set2, blockBox, megaTreeFeatureConfig);
						}
					}
				}

				if (random.nextBoolean()) {
					this.setLeavesBlockState(modifiableTestableWorld, random, new BlockPos(o, q + 2, p), set2, blockBox, megaTreeFeatureConfig);
					this.setLeavesBlockState(modifiableTestableWorld, random, new BlockPos(o + 1, q + 2, p), set2, blockBox, megaTreeFeatureConfig);
					this.setLeavesBlockState(modifiableTestableWorld, random, new BlockPos(o + 1, q + 2, p + 1), set2, blockBox, megaTreeFeatureConfig);
					this.setLeavesBlockState(modifiableTestableWorld, random, new BlockPos(o, q + 2, p + 1), set2, blockBox, megaTreeFeatureConfig);
				}

				for (int r = -3; r <= 4; r++) {
					for (int sx = -3; sx <= 4; sx++) {
						if ((r != -3 || sx != -3) && (r != -3 || sx != 4) && (r != 4 || sx != -3) && (r != 4 || sx != 4) && (Math.abs(r) < 3 || Math.abs(sx) < 3)) {
							this.setLeavesBlockState(modifiableTestableWorld, random, new BlockPos(o + r, q, p + sx), set2, blockBox, megaTreeFeatureConfig);
						}
					}
				}

				for (int r = -1; r <= 2; r++) {
					for (int sxx = -1; sxx <= 2; sxx++) {
						if ((r < 0 || r > 1 || sxx < 0 || sxx > 1) && random.nextInt(3) <= 0) {
							int t = random.nextInt(3) + 2;

							for (int u = 0; u < t; u++) {
								setLogBlockState(modifiableTestableWorld, random, new BlockPos(j + r, q - u - 1, l + sxx), set, blockBox, megaTreeFeatureConfig);
							}

							for (int u = -1; u <= 1; u++) {
								for (int v = -1; v <= 1; v++) {
									this.setLeavesBlockState(modifiableTestableWorld, random, new BlockPos(o + r + u, q, p + sxx + v), set2, blockBox, megaTreeFeatureConfig);
								}
							}

							for (int u = -2; u <= 2; u++) {
								for (int v = -2; v <= 2; v++) {
									if (Math.abs(u) != 2 || Math.abs(v) != 2) {
										this.setLeavesBlockState(modifiableTestableWorld, random, new BlockPos(o + r + u, q - 1, p + sxx + v), set2, blockBox, megaTreeFeatureConfig);
									}
								}
							}
						}
					}
				}

				return true;
			}
		} else {
			return false;
		}
	}

	private boolean doesTreeFit(TestableWorld world, BlockPos pos, int treeHeight) {
		int i = pos.getX();
		int j = pos.getY();
		int k = pos.getZ();
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int l = 0; l <= treeHeight + 1; l++) {
			int m = 1;
			if (l == 0) {
				m = 0;
			}

			if (l >= treeHeight - 1) {
				m = 2;
			}

			for (int n = -m; n <= m; n++) {
				for (int o = -m; o <= m; o++) {
					if (!canTreeReplace(world, mutable.set(i + n, j + l, k + o))) {
						return false;
					}
				}
			}
		}

		return true;
	}
}
