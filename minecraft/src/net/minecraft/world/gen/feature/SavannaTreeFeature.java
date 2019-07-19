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
import net.minecraft.world.ModifiableWorld;

public class SavannaTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.ACACIA_LOG.getDefaultState();
	private static final BlockState LEAVES = Blocks.ACACIA_LEAVES.getDefaultState();

	public SavannaTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory, boolean emitNeighborBlockUpdates) {
		super(configFactory, emitNeighborBlockUpdates);
	}

	@Override
	public boolean generate(Set<BlockPos> logPositions, ModifiableTestableWorld world, Random random, BlockPos pos, BlockBox blockBox) {
		int i = random.nextInt(3) + random.nextInt(3) + 5;
		boolean bl = true;
		if (pos.getY() >= 1 && pos.getY() + i + 1 <= 256) {
			for (int j = pos.getY(); j <= pos.getY() + 1 + i; j++) {
				int k = 1;
				if (j == pos.getY()) {
					k = 0;
				}

				if (j >= pos.getY() + 1 + i - 2) {
					k = 2;
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int l = pos.getX() - k; l <= pos.getX() + k && bl; l++) {
					for (int m = pos.getZ() - k; m <= pos.getZ() + k && bl; m++) {
						if (j < 0 || j >= 256) {
							bl = false;
						} else if (!canTreeReplace(world, mutable.set(l, j, m))) {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else if (isNaturalDirtOrGrass(world, pos.down()) && pos.getY() < 256 - i - 1) {
				this.setToDirt(world, pos.down());
				Direction direction = Direction.Type.HORIZONTAL.random(random);
				int kx = i - random.nextInt(4) - 1;
				int n = 3 - random.nextInt(3);
				int l = pos.getX();
				int mx = pos.getZ();
				int o = 0;

				for (int p = 0; p < i; p++) {
					int q = pos.getY() + p;
					if (p >= kx && n > 0) {
						l += direction.getOffsetX();
						mx += direction.getOffsetZ();
						n--;
					}

					BlockPos blockPos = new BlockPos(l, q, mx);
					if (isAirOrLeaves(world, blockPos)) {
						this.addLog(logPositions, world, blockPos, blockBox);
						o = q;
					}
				}

				BlockPos blockPos2 = new BlockPos(l, o, mx);

				for (int qx = -3; qx <= 3; qx++) {
					for (int r = -3; r <= 3; r++) {
						if (Math.abs(qx) != 3 || Math.abs(r) != 3) {
							this.addLeaves(logPositions, world, blockPos2.add(qx, 0, r), blockBox);
						}
					}
				}

				blockPos2 = blockPos2.up();

				for (int qx = -1; qx <= 1; qx++) {
					for (int rx = -1; rx <= 1; rx++) {
						this.addLeaves(logPositions, world, blockPos2.add(qx, 0, rx), blockBox);
					}
				}

				this.addLeaves(logPositions, world, blockPos2.east(2), blockBox);
				this.addLeaves(logPositions, world, blockPos2.west(2), blockBox);
				this.addLeaves(logPositions, world, blockPos2.south(2), blockBox);
				this.addLeaves(logPositions, world, blockPos2.north(2), blockBox);
				l = pos.getX();
				mx = pos.getZ();
				Direction direction2 = Direction.Type.HORIZONTAL.random(random);
				if (direction2 != direction) {
					int qx = kx - random.nextInt(2) - 1;
					int rx = 1 + random.nextInt(3);
					o = 0;

					for (int s = qx; s < i && rx > 0; rx--) {
						if (s >= 1) {
							int t = pos.getY() + s;
							l += direction2.getOffsetX();
							mx += direction2.getOffsetZ();
							BlockPos blockPos3 = new BlockPos(l, t, mx);
							if (isAirOrLeaves(world, blockPos3)) {
								this.addLog(logPositions, world, blockPos3, blockBox);
								o = t;
							}
						}

						s++;
					}

					if (o > 0) {
						BlockPos blockPos4 = new BlockPos(l, o, mx);

						for (int t = -2; t <= 2; t++) {
							for (int u = -2; u <= 2; u++) {
								if (Math.abs(t) != 2 || Math.abs(u) != 2) {
									this.addLeaves(logPositions, world, blockPos4.add(t, 0, u), blockBox);
								}
							}
						}

						blockPos4 = blockPos4.up();

						for (int t = -1; t <= 1; t++) {
							for (int ux = -1; ux <= 1; ux++) {
								this.addLeaves(logPositions, world, blockPos4.add(t, 0, ux), blockBox);
							}
						}
					}
				}

				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private void addLog(Set<BlockPos> logPositions, ModifiableWorld world, BlockPos pos, BlockBox blockBox) {
		this.setBlockState(logPositions, world, pos, LOG, blockBox);
	}

	private void addLeaves(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, BlockBox blockBox) {
		if (isAirOrLeaves(modifiableTestableWorld, blockPos)) {
			this.setBlockState(set, modifiableTestableWorld, blockPos, LEAVES, blockBox);
		}
	}
}
