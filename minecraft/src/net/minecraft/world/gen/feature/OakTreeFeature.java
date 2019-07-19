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
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.ModifiableWorld;

public class OakTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.OAK_LOG.getDefaultState();
	private static final BlockState LEAVES = Blocks.OAK_LEAVES.getDefaultState();
	protected final int height;
	private final boolean hasVinesAndCocoa;
	private final BlockState log;
	private final BlockState leaves;

	public OakTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory, boolean emitNeighborBlockUpdates) {
		this(configFactory, emitNeighborBlockUpdates, 4, LOG, LEAVES, false);
	}

	public OakTreeFeature(
		Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl, int height, BlockState log, BlockState leaves, boolean hasVinesAndCocoa
	) {
		super(function, bl);
		this.height = height;
		this.log = log;
		this.leaves = leaves;
		this.hasVinesAndCocoa = hasVinesAndCocoa;
	}

	@Override
	public boolean generate(Set<BlockPos> logPositions, ModifiableTestableWorld world, Random random, BlockPos pos, BlockBox blockBox) {
		int i = this.getTreeHeight(random);
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
			} else if (isDirtOrGrass(world, pos.down()) && pos.getY() < 256 - i - 1) {
				this.setToDirt(world, pos.down());
				int j = 3;
				int kx = 0;

				for (int n = pos.getY() - 3 + i; n <= pos.getY() + i; n++) {
					int l = n - (pos.getY() + i);
					int mx = 1 - l / 2;

					for (int o = pos.getX() - mx; o <= pos.getX() + mx; o++) {
						int p = o - pos.getX();

						for (int q = pos.getZ() - mx; q <= pos.getZ() + mx; q++) {
							int r = q - pos.getZ();
							if (Math.abs(p) != mx || Math.abs(r) != mx || random.nextInt(2) != 0 && l != 0) {
								BlockPos blockPos = new BlockPos(o, n, q);
								if (isAirOrLeaves(world, blockPos) || isReplaceablePlant(world, blockPos)) {
									this.setBlockState(logPositions, world, blockPos, this.leaves, blockBox);
								}
							}
						}
					}
				}

				for (int n = 0; n < i; n++) {
					if (isAirOrLeaves(world, pos.up(n)) || isReplaceablePlant(world, pos.up(n))) {
						this.setBlockState(logPositions, world, pos.up(n), this.log, blockBox);
						if (this.hasVinesAndCocoa && n > 0) {
							if (random.nextInt(3) > 0 && isAir(world, pos.add(-1, n, 0))) {
								this.makeVine(world, pos.add(-1, n, 0), VineBlock.EAST);
							}

							if (random.nextInt(3) > 0 && isAir(world, pos.add(1, n, 0))) {
								this.makeVine(world, pos.add(1, n, 0), VineBlock.WEST);
							}

							if (random.nextInt(3) > 0 && isAir(world, pos.add(0, n, -1))) {
								this.makeVine(world, pos.add(0, n, -1), VineBlock.SOUTH);
							}

							if (random.nextInt(3) > 0 && isAir(world, pos.add(0, n, 1))) {
								this.makeVine(world, pos.add(0, n, 1), VineBlock.NORTH);
							}
						}
					}
				}

				if (this.hasVinesAndCocoa) {
					for (int nx = pos.getY() - 3 + i; nx <= pos.getY() + i; nx++) {
						int l = nx - (pos.getY() + i);
						int mx = 2 - l / 2;
						BlockPos.Mutable mutable2 = new BlockPos.Mutable();

						for (int p = pos.getX() - mx; p <= pos.getX() + mx; p++) {
							for (int qx = pos.getZ() - mx; qx <= pos.getZ() + mx; qx++) {
								mutable2.set(p, nx, qx);
								if (isLeaves(world, mutable2)) {
									BlockPos blockPos2 = mutable2.west();
									BlockPos blockPos = mutable2.east();
									BlockPos blockPos3 = mutable2.north();
									BlockPos blockPos4 = mutable2.south();
									if (random.nextInt(4) == 0 && isAir(world, blockPos2)) {
										this.makeVineColumn(world, blockPos2, VineBlock.EAST);
									}

									if (random.nextInt(4) == 0 && isAir(world, blockPos)) {
										this.makeVineColumn(world, blockPos, VineBlock.WEST);
									}

									if (random.nextInt(4) == 0 && isAir(world, blockPos3)) {
										this.makeVineColumn(world, blockPos3, VineBlock.SOUTH);
									}

									if (random.nextInt(4) == 0 && isAir(world, blockPos4)) {
										this.makeVineColumn(world, blockPos4, VineBlock.NORTH);
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
									this.makeCocoa(world, random.nextInt(3), pos.add(direction2.getOffsetX(), i - 5 + nx, direction2.getOffsetZ()), direction);
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

	private void makeCocoa(ModifiableWorld worlf, int age, BlockPos pos, Direction direction) {
		this.setBlockState(worlf, pos, Blocks.COCOA.getDefaultState().with(CocoaBlock.AGE, Integer.valueOf(age)).with(CocoaBlock.FACING, direction));
	}

	private void makeVine(ModifiableWorld world, BlockPos pos, BooleanProperty directionProperty) {
		this.setBlockState(world, pos, Blocks.VINE.getDefaultState().with(directionProperty, Boolean.valueOf(true)));
	}

	private void makeVineColumn(ModifiableTestableWorld world, BlockPos pos, BooleanProperty directionProperty) {
		this.makeVine(world, pos, directionProperty);
		int i = 4;

		for (BlockPos var5 = pos.down(); isAir(world, var5) && i > 0; i--) {
			this.makeVine(world, var5, directionProperty);
			var5 = var5.down();
		}
	}
}
