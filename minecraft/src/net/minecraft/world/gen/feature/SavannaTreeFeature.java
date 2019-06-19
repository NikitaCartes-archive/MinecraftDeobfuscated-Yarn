package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;

public class SavannaTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.field_10533.getDefaultState();
	private static final BlockState LEAVES = Blocks.field_10098.getDefaultState();

	public SavannaTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl) {
		super(function, bl);
	}

	@Override
	public boolean generate(
		Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox
	) {
		int i = random.nextInt(3) + random.nextInt(3) + 5;
		boolean bl = true;
		if (blockPos.getY() >= 1 && blockPos.getY() + i + 1 <= 256) {
			for (int j = blockPos.getY(); j <= blockPos.getY() + 1 + i; j++) {
				int k = 1;
				if (j == blockPos.getY()) {
					k = 0;
				}

				if (j >= blockPos.getY() + 1 + i - 2) {
					k = 2;
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int l = blockPos.getX() - k; l <= blockPos.getX() + k && bl; l++) {
					for (int m = blockPos.getZ() - k; m <= blockPos.getZ() + k && bl; m++) {
						if (j < 0 || j >= 256) {
							bl = false;
						} else if (!canTreeReplace(modifiableTestableWorld, mutable.set(l, j, m))) {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else if (isNaturalDirtOrGrass(modifiableTestableWorld, blockPos.down()) && blockPos.getY() < 256 - i - 1) {
				this.setToDirt(modifiableTestableWorld, blockPos.down());
				Direction direction = Direction.Type.field_11062.random(random);
				int kx = i - random.nextInt(4) - 1;
				int n = 3 - random.nextInt(3);
				int l = blockPos.getX();
				int mx = blockPos.getZ();
				int o = 0;

				for (int p = 0; p < i; p++) {
					int q = blockPos.getY() + p;
					if (p >= kx && n > 0) {
						l += direction.getOffsetX();
						mx += direction.getOffsetZ();
						n--;
					}

					BlockPos blockPos2 = new BlockPos(l, q, mx);
					if (isAirOrLeaves(modifiableTestableWorld, blockPos2)) {
						this.addLog(set, modifiableTestableWorld, blockPos2, mutableIntBoundingBox);
						o = q;
					}
				}

				BlockPos blockPos3 = new BlockPos(l, o, mx);

				for (int qx = -3; qx <= 3; qx++) {
					for (int r = -3; r <= 3; r++) {
						if (Math.abs(qx) != 3 || Math.abs(r) != 3) {
							this.addLeaves(set, modifiableTestableWorld, blockPos3.add(qx, 0, r), mutableIntBoundingBox);
						}
					}
				}

				blockPos3 = blockPos3.up();

				for (int qx = -1; qx <= 1; qx++) {
					for (int rx = -1; rx <= 1; rx++) {
						this.addLeaves(set, modifiableTestableWorld, blockPos3.add(qx, 0, rx), mutableIntBoundingBox);
					}
				}

				this.addLeaves(set, modifiableTestableWorld, blockPos3.east(2), mutableIntBoundingBox);
				this.addLeaves(set, modifiableTestableWorld, blockPos3.west(2), mutableIntBoundingBox);
				this.addLeaves(set, modifiableTestableWorld, blockPos3.south(2), mutableIntBoundingBox);
				this.addLeaves(set, modifiableTestableWorld, blockPos3.north(2), mutableIntBoundingBox);
				l = blockPos.getX();
				mx = blockPos.getZ();
				Direction direction2 = Direction.Type.field_11062.random(random);
				if (direction2 != direction) {
					int qx = kx - random.nextInt(2) - 1;
					int rx = 1 + random.nextInt(3);
					o = 0;

					for (int s = qx; s < i && rx > 0; rx--) {
						if (s >= 1) {
							int t = blockPos.getY() + s;
							l += direction2.getOffsetX();
							mx += direction2.getOffsetZ();
							BlockPos blockPos4 = new BlockPos(l, t, mx);
							if (isAirOrLeaves(modifiableTestableWorld, blockPos4)) {
								this.addLog(set, modifiableTestableWorld, blockPos4, mutableIntBoundingBox);
								o = t;
							}
						}

						s++;
					}

					if (o > 0) {
						BlockPos blockPos5 = new BlockPos(l, o, mx);

						for (int t = -2; t <= 2; t++) {
							for (int u = -2; u <= 2; u++) {
								if (Math.abs(t) != 2 || Math.abs(u) != 2) {
									this.addLeaves(set, modifiableTestableWorld, blockPos5.add(t, 0, u), mutableIntBoundingBox);
								}
							}
						}

						blockPos5 = blockPos5.up();

						for (int t = -1; t <= 1; t++) {
							for (int ux = -1; ux <= 1; ux++) {
								this.addLeaves(set, modifiableTestableWorld, blockPos5.add(t, 0, ux), mutableIntBoundingBox);
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

	private void addLog(Set<BlockPos> set, ModifiableWorld modifiableWorld, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox) {
		this.setBlockState(set, modifiableWorld, blockPos, LOG, mutableIntBoundingBox);
	}

	private void addLeaves(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox) {
		if (isAirOrLeaves(modifiableTestableWorld, blockPos)) {
			this.setBlockState(set, modifiableTestableWorld, blockPos, LEAVES, mutableIntBoundingBox);
		}
	}
}
