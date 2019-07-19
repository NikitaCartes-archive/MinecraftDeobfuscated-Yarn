package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.TestableWorld;

public class DarkOakTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.DARK_OAK_LOG.getDefaultState();
	private static final BlockState LEAVES = Blocks.DARK_OAK_LEAVES.getDefaultState();

	public DarkOakTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory, boolean emitNeighborBlockUpdates) {
		super(configFactory, emitNeighborBlockUpdates);
	}

	@Override
	public boolean generate(Set<BlockPos> logPositions, ModifiableTestableWorld world, Random random, BlockPos pos, BlockBox blockBox) {
		int i = random.nextInt(3) + random.nextInt(2) + 6;
		int j = pos.getX();
		int k = pos.getY();
		int l = pos.getZ();
		if (k >= 1 && k + i + 1 < 256) {
			BlockPos blockPos = pos.down();
			if (!isNaturalDirtOrGrass(world, blockPos)) {
				return false;
			} else if (!this.doesTreeFit(world, pos, i)) {
				return false;
			} else {
				this.setToDirt(world, blockPos);
				this.setToDirt(world, blockPos.east());
				this.setToDirt(world, blockPos.south());
				this.setToDirt(world, blockPos.south().east());
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
					BlockPos blockPos2 = new BlockPos(o, s, p);
					if (isAirOrLeaves(world, blockPos2)) {
						this.addLog(logPositions, world, blockPos2, blockBox);
						this.addLog(logPositions, world, blockPos2.east(), blockBox);
						this.addLog(logPositions, world, blockPos2.south(), blockBox);
						this.addLog(logPositions, world, blockPos2.east().south(), blockBox);
					}
				}

				for (int r = -2; r <= 0; r++) {
					for (int s = -2; s <= 0; s++) {
						int t = -1;
						this.addLeaves(world, o + r, q + t, p + s, blockBox, logPositions);
						this.addLeaves(world, 1 + o - r, q + t, p + s, blockBox, logPositions);
						this.addLeaves(world, o + r, q + t, 1 + p - s, blockBox, logPositions);
						this.addLeaves(world, 1 + o - r, q + t, 1 + p - s, blockBox, logPositions);
						if ((r > -2 || s > -1) && (r != -1 || s != -2)) {
							int var29 = 1;
							this.addLeaves(world, o + r, q + var29, p + s, blockBox, logPositions);
							this.addLeaves(world, 1 + o - r, q + var29, p + s, blockBox, logPositions);
							this.addLeaves(world, o + r, q + var29, 1 + p - s, blockBox, logPositions);
							this.addLeaves(world, 1 + o - r, q + var29, 1 + p - s, blockBox, logPositions);
						}
					}
				}

				if (random.nextBoolean()) {
					this.addLeaves(world, o, q + 2, p, blockBox, logPositions);
					this.addLeaves(world, o + 1, q + 2, p, blockBox, logPositions);
					this.addLeaves(world, o + 1, q + 2, p + 1, blockBox, logPositions);
					this.addLeaves(world, o, q + 2, p + 1, blockBox, logPositions);
				}

				for (int r = -3; r <= 4; r++) {
					for (int sx = -3; sx <= 4; sx++) {
						if ((r != -3 || sx != -3) && (r != -3 || sx != 4) && (r != 4 || sx != -3) && (r != 4 || sx != 4) && (Math.abs(r) < 3 || Math.abs(sx) < 3)) {
							this.addLeaves(world, o + r, q, p + sx, blockBox, logPositions);
						}
					}
				}

				for (int r = -1; r <= 2; r++) {
					for (int sxx = -1; sxx <= 2; sxx++) {
						if ((r < 0 || r > 1 || sxx < 0 || sxx > 1) && random.nextInt(3) <= 0) {
							int t = random.nextInt(3) + 2;

							for (int u = 0; u < t; u++) {
								this.addLog(logPositions, world, new BlockPos(j + r, q - u - 1, l + sxx), blockBox);
							}

							for (int u = -1; u <= 1; u++) {
								for (int v = -1; v <= 1; v++) {
									this.addLeaves(world, o + r + u, q, p + sxx + v, blockBox, logPositions);
								}
							}

							for (int u = -2; u <= 2; u++) {
								for (int v = -2; v <= 2; v++) {
									if (Math.abs(u) != 2 || Math.abs(v) != 2) {
										this.addLeaves(world, o + r + u, q - 1, p + sxx + v, blockBox, logPositions);
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

	private void addLog(Set<BlockPos> logPositions, ModifiableTestableWorld world, BlockPos pos, BlockBox blockBox) {
		if (canTreeReplace(world, pos)) {
			this.setBlockState(logPositions, world, pos, LOG, blockBox);
		}
	}

	private void addLeaves(ModifiableTestableWorld modifiableTestableWorld, int x, int y, int z, BlockBox blockBox, Set<BlockPos> set) {
		BlockPos blockPos = new BlockPos(x, y, z);
		if (isAir(modifiableTestableWorld, blockPos)) {
			this.setBlockState(set, modifiableTestableWorld, blockPos, LEAVES, blockBox);
		}
	}
}
