package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;

public class OakTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.field_10431.method_9564();
	private static final BlockState LEAVES = Blocks.field_10503.method_9564();
	protected final int height;
	private final boolean hasVinesAndCocoa;
	private final BlockState log;
	private final BlockState leaves;

	public OakTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl) {
		this(function, bl, 4, LOG, LEAVES, false);
	}

	public OakTreeFeature(
		Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl, int i, BlockState blockState, BlockState blockState2, boolean bl2
	) {
		super(function, bl);
		this.height = i;
		this.log = blockState;
		this.leaves = blockState2;
		this.hasVinesAndCocoa = bl2;
	}

	@Override
	public boolean method_12775(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos) {
		int i = this.getTreeHeight(random);
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
						} else if (!method_16432(modifiableTestableWorld, mutable.set(l, j, m))) {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else if (method_16433(modifiableTestableWorld, blockPos.down()) && blockPos.getY() < 256 - i - 1) {
				this.method_16427(modifiableTestableWorld, blockPos.down());
				int j = 3;
				int kx = 0;

				for (int n = blockPos.getY() - 3 + i; n <= blockPos.getY() + i; n++) {
					int l = n - (blockPos.getY() + i);
					int mx = 1 - l / 2;

					for (int o = blockPos.getX() - mx; o <= blockPos.getX() + mx; o++) {
						int p = o - blockPos.getX();

						for (int q = blockPos.getZ() - mx; q <= blockPos.getZ() + mx; q++) {
							int r = q - blockPos.getZ();
							if (Math.abs(p) != mx || Math.abs(r) != mx || random.nextInt(2) != 0 && l != 0) {
								BlockPos blockPos2 = new BlockPos(o, n, q);
								if (method_16420(modifiableTestableWorld, blockPos2) || method_16425(modifiableTestableWorld, blockPos2)) {
									this.method_13153(modifiableTestableWorld, blockPos2, this.leaves);
								}
							}
						}
					}
				}

				for (int n = 0; n < i; n++) {
					if (method_16420(modifiableTestableWorld, blockPos.up(n)) || method_16425(modifiableTestableWorld, blockPos.up(n))) {
						this.method_12773(set, modifiableTestableWorld, blockPos.up(n), this.log);
						if (this.hasVinesAndCocoa && n > 0) {
							if (random.nextInt(3) > 0 && method_16424(modifiableTestableWorld, blockPos.add(-1, n, 0))) {
								this.method_14065(modifiableTestableWorld, blockPos.add(-1, n, 0), VineBlock.field_11702);
							}

							if (random.nextInt(3) > 0 && method_16424(modifiableTestableWorld, blockPos.add(1, n, 0))) {
								this.method_14065(modifiableTestableWorld, blockPos.add(1, n, 0), VineBlock.field_11696);
							}

							if (random.nextInt(3) > 0 && method_16424(modifiableTestableWorld, blockPos.add(0, n, -1))) {
								this.method_14065(modifiableTestableWorld, blockPos.add(0, n, -1), VineBlock.field_11699);
							}

							if (random.nextInt(3) > 0 && method_16424(modifiableTestableWorld, blockPos.add(0, n, 1))) {
								this.method_14065(modifiableTestableWorld, blockPos.add(0, n, 1), VineBlock.field_11706);
							}
						}
					}
				}

				if (this.hasVinesAndCocoa) {
					for (int nx = blockPos.getY() - 3 + i; nx <= blockPos.getY() + i; nx++) {
						int l = nx - (blockPos.getY() + i);
						int mx = 2 - l / 2;
						BlockPos.Mutable mutable2 = new BlockPos.Mutable();

						for (int p = blockPos.getX() - mx; p <= blockPos.getX() + mx; p++) {
							for (int qx = blockPos.getZ() - mx; qx <= blockPos.getZ() + mx; qx++) {
								mutable2.set(p, nx, qx);
								if (method_16416(modifiableTestableWorld, mutable2)) {
									BlockPos blockPos3 = mutable2.west();
									BlockPos blockPos2 = mutable2.east();
									BlockPos blockPos4 = mutable2.north();
									BlockPos blockPos5 = mutable2.south();
									if (random.nextInt(4) == 0 && method_16424(modifiableTestableWorld, blockPos3)) {
										this.method_14064(modifiableTestableWorld, blockPos3, VineBlock.field_11702);
									}

									if (random.nextInt(4) == 0 && method_16424(modifiableTestableWorld, blockPos2)) {
										this.method_14064(modifiableTestableWorld, blockPos2, VineBlock.field_11696);
									}

									if (random.nextInt(4) == 0 && method_16424(modifiableTestableWorld, blockPos4)) {
										this.method_14064(modifiableTestableWorld, blockPos4, VineBlock.field_11699);
									}

									if (random.nextInt(4) == 0 && method_16424(modifiableTestableWorld, blockPos5)) {
										this.method_14064(modifiableTestableWorld, blockPos5, VineBlock.field_11706);
									}
								}
							}
						}
					}

					if (random.nextInt(5) == 0 && i > 5) {
						for (int nx = 0; nx < 2; nx++) {
							for (Direction direction : Direction.Type.HORIZONTAL) {
								if (random.nextInt(4 - nx) == 0) {
									Direction direction2 = direction.getOpposite();
									this.method_14063(modifiableTestableWorld, random.nextInt(3), blockPos.add(direction2.getOffsetX(), i - 5 + nx, direction2.getOffsetZ()), direction);
								}
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

	protected int getTreeHeight(Random random) {
		return this.height + random.nextInt(3);
	}

	private void method_14063(ModifiableWorld modifiableWorld, int i, BlockPos blockPos, Direction direction) {
		this.method_13153(
			modifiableWorld,
			blockPos,
			Blocks.field_10302.method_9564().method_11657(CocoaBlock.field_10779, Integer.valueOf(i)).method_11657(CocoaBlock.field_11177, direction)
		);
	}

	private void method_14065(ModifiableWorld modifiableWorld, BlockPos blockPos, BooleanProperty booleanProperty) {
		this.method_13153(modifiableWorld, blockPos, Blocks.field_10597.method_9564().method_11657(booleanProperty, Boolean.valueOf(true)));
	}

	private void method_14064(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, BooleanProperty booleanProperty) {
		this.method_14065(modifiableTestableWorld, blockPos, booleanProperty);
		int i = 4;

		for (BlockPos var5 = blockPos.down(); method_16424(modifiableTestableWorld, var5) && i > 0; i--) {
			this.method_14065(modifiableTestableWorld, var5, booleanProperty);
			var5 = var5.down();
		}
	}
}
