package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ModifiableTestableWorld;

public class SwampTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.OAK_LOG.getDefaultState();
	private static final BlockState LEAVES = Blocks.OAK_LEAVES.getDefaultState();

	public SwampTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
		super(configFactory, false);
	}

	@Override
	public boolean generate(Set<BlockPos> logPositions, ModifiableTestableWorld world, Random random, BlockPos pos, BlockBox blockBox) {
		int i = random.nextInt(4) + 5;
		pos = world.getTopPosition(Heightmap.Type.OCEAN_FLOOR, pos);
		boolean bl = true;
		if (pos.getY() >= 1 && pos.getY() + i + 1 <= 256) {
			for (int j = pos.getY(); j <= pos.getY() + 1 + i; j++) {
				int k = 1;
				if (j == pos.getY()) {
					k = 0;
				}

				if (j >= pos.getY() + 1 + i - 2) {
					k = 3;
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int l = pos.getX() - k; l <= pos.getX() + k && bl; l++) {
					for (int m = pos.getZ() - k; m <= pos.getZ() + k && bl; m++) {
						if (j >= 0 && j < 256) {
							mutable.set(l, j, m);
							if (!isAirOrLeaves(world, mutable)) {
								if (isWater(world, mutable)) {
									if (j > pos.getY()) {
										bl = false;
									}
								} else {
									bl = false;
								}
							}
						} else {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else if (isNaturalDirtOrGrass(world, pos.down()) && pos.getY() < 256 - i - 1) {
				this.setToDirt(world, pos.down());

				for (int j = pos.getY() - 3 + i; j <= pos.getY() + i; j++) {
					int kx = j - (pos.getY() + i);
					int n = 2 - kx / 2;

					for (int l = pos.getX() - n; l <= pos.getX() + n; l++) {
						int mx = l - pos.getX();

						for (int o = pos.getZ() - n; o <= pos.getZ() + n; o++) {
							int p = o - pos.getZ();
							if (Math.abs(mx) != n || Math.abs(p) != n || random.nextInt(2) != 0 && kx != 0) {
								BlockPos blockPos = new BlockPos(l, j, o);
								if (isAirOrLeaves(world, blockPos) || isReplaceablePlant(world, blockPos)) {
									this.setBlockState(logPositions, world, blockPos, LEAVES, blockBox);
								}
							}
						}
					}
				}

				for (int j = 0; j < i; j++) {
					BlockPos blockPos2 = pos.up(j);
					if (isAirOrLeaves(world, blockPos2) || isWater(world, blockPos2)) {
						this.setBlockState(logPositions, world, blockPos2, LOG, blockBox);
					}
				}

				for (int jx = pos.getY() - 3 + i; jx <= pos.getY() + i; jx++) {
					int kx = jx - (pos.getY() + i);
					int n = 2 - kx / 2;
					BlockPos.Mutable mutable2 = new BlockPos.Mutable();

					for (int mx = pos.getX() - n; mx <= pos.getX() + n; mx++) {
						for (int ox = pos.getZ() - n; ox <= pos.getZ() + n; ox++) {
							mutable2.set(mx, jx, ox);
							if (isLeaves(world, mutable2)) {
								BlockPos blockPos3 = mutable2.west();
								BlockPos blockPos = mutable2.east();
								BlockPos blockPos4 = mutable2.north();
								BlockPos blockPos5 = mutable2.south();
								if (random.nextInt(4) == 0 && isAir(world, blockPos3)) {
									this.makeVines(world, blockPos3, VineBlock.EAST);
								}

								if (random.nextInt(4) == 0 && isAir(world, blockPos)) {
									this.makeVines(world, blockPos, VineBlock.WEST);
								}

								if (random.nextInt(4) == 0 && isAir(world, blockPos4)) {
									this.makeVines(world, blockPos4, VineBlock.SOUTH);
								}

								if (random.nextInt(4) == 0 && isAir(world, blockPos5)) {
									this.makeVines(world, blockPos5, VineBlock.NORTH);
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

	private void makeVines(ModifiableTestableWorld world, BlockPos pos, BooleanProperty directionProperty) {
		BlockState blockState = Blocks.VINE.getDefaultState().with(directionProperty, Boolean.valueOf(true));
		this.setBlockState(world, pos, blockState);
		int i = 4;

		for (BlockPos var6 = pos.down(); isAir(world, var6) && i > 0; i--) {
			this.setBlockState(world, var6, blockState);
			var6 = var6.down();
		}
	}
}
