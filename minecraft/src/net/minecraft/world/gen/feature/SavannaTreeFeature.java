package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;

public class SavannaTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.field_10533.getDefaultState();
	private static final BlockState LEAVES = Blocks.field_10098.getDefaultState();

	public SavannaTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl) {
		super(function, bl);
	}

	@Override
	public boolean generate(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos) {
		int i = random.nextInt(3) + random.nextInt(3) + 5;
		boolean bl = true;
		if (blockPos.getY() >= 1 && blockPos.getY() + i + 1 <= 256) {
			for(int j = blockPos.getY(); j <= blockPos.getY() + 1 + i; ++j) {
				int k = 1;
				if (j == blockPos.getY()) {
					k = 0;
				}

				if (j >= blockPos.getY() + 1 + i - 2) {
					k = 2;
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for(int l = blockPos.getX() - k; l <= blockPos.getX() + k && bl; ++l) {
					for(int m = blockPos.getZ() - k; m <= blockPos.getZ() + k && bl; ++m) {
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
				Direction direction = Direction.class_2353.HORIZONTAL.random(random);
				int k = i - random.nextInt(4) - 1;
				int n = 3 - random.nextInt(3);
				int l = blockPos.getX();
				int m = blockPos.getZ();
				int o = 0;

				for(int p = 0; p < i; ++p) {
					int q = blockPos.getY() + p;
					if (p >= k && n > 0) {
						l += direction.getOffsetX();
						m += direction.getOffsetZ();
						--n;
					}

					BlockPos blockPos2 = new BlockPos(l, q, m);
					if (isAirOrLeaves(modifiableTestableWorld, blockPos2)) {
						this.method_13852(set, modifiableTestableWorld, blockPos2);
						o = q;
					}
				}

				BlockPos blockPos3 = new BlockPos(l, o, m);

				for(int q = -3; q <= 3; ++q) {
					for(int r = -3; r <= 3; ++r) {
						if (Math.abs(q) != 3 || Math.abs(r) != 3) {
							this.method_13853(modifiableTestableWorld, blockPos3.add(q, 0, r));
						}
					}
				}

				blockPos3 = blockPos3.up();

				for(int q = -1; q <= 1; ++q) {
					for(int r = -1; r <= 1; ++r) {
						this.method_13853(modifiableTestableWorld, blockPos3.add(q, 0, r));
					}
				}

				this.method_13853(modifiableTestableWorld, blockPos3.east(2));
				this.method_13853(modifiableTestableWorld, blockPos3.west(2));
				this.method_13853(modifiableTestableWorld, blockPos3.south(2));
				this.method_13853(modifiableTestableWorld, blockPos3.north(2));
				l = blockPos.getX();
				m = blockPos.getZ();
				Direction direction2 = Direction.class_2353.HORIZONTAL.random(random);
				if (direction2 != direction) {
					int q = k - random.nextInt(2) - 1;
					int r = 1 + random.nextInt(3);
					o = 0;

					for(int s = q; s < i && r > 0; --r) {
						if (s >= 1) {
							int t = blockPos.getY() + s;
							l += direction2.getOffsetX();
							m += direction2.getOffsetZ();
							BlockPos blockPos4 = new BlockPos(l, t, m);
							if (isAirOrLeaves(modifiableTestableWorld, blockPos4)) {
								this.method_13852(set, modifiableTestableWorld, blockPos4);
								o = t;
							}
						}

						++s;
					}

					if (o > 0) {
						BlockPos blockPos5 = new BlockPos(l, o, m);

						for(int t = -2; t <= 2; ++t) {
							for(int u = -2; u <= 2; ++u) {
								if (Math.abs(t) != 2 || Math.abs(u) != 2) {
									this.method_13853(modifiableTestableWorld, blockPos5.add(t, 0, u));
								}
							}
						}

						blockPos5 = blockPos5.up();

						for(int t = -1; t <= 1; ++t) {
							for(int u = -1; u <= 1; ++u) {
								this.method_13853(modifiableTestableWorld, blockPos5.add(t, 0, u));
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

	private void method_13852(Set<BlockPos> set, ModifiableWorld modifiableWorld, BlockPos blockPos) {
		this.setBlockState(set, modifiableWorld, blockPos, LOG);
	}

	private void method_13853(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos) {
		if (isAirOrLeaves(modifiableTestableWorld, blockPos)) {
			this.setBlockState(modifiableTestableWorld, blockPos, LEAVES);
		}
	}
}
