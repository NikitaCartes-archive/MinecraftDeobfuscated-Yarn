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

	public SwampTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function, false);
	}

	@Override
	public boolean generate(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, BlockBox blockBox) {
		int i = random.nextInt(4) + 5;
		blockPos = modifiableTestableWorld.getTopPosition(Heightmap.Type.OCEAN_FLOOR, blockPos);
		boolean bl = true;
		if (blockPos.getY() >= 1 && blockPos.getY() + i + 1 <= 256) {
			for (int j = blockPos.getY(); j <= blockPos.getY() + 1 + i; j++) {
				int k = 1;
				if (j == blockPos.getY()) {
					k = 0;
				}

				if (j >= blockPos.getY() + 1 + i - 2) {
					k = 3;
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int l = blockPos.getX() - k; l <= blockPos.getX() + k && bl; l++) {
					for (int m = blockPos.getZ() - k; m <= blockPos.getZ() + k && bl; m++) {
						if (j >= 0 && j < 256) {
							mutable.set(l, j, m);
							if (!isAirOrLeaves(modifiableTestableWorld, mutable)) {
								if (isWater(modifiableTestableWorld, mutable)) {
									if (j > blockPos.getY()) {
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
			} else if (isNaturalDirtOrGrass(modifiableTestableWorld, blockPos.method_10074()) && blockPos.getY() < 256 - i - 1) {
				this.setToDirt(modifiableTestableWorld, blockPos.method_10074());

				for (int j = blockPos.getY() - 3 + i; j <= blockPos.getY() + i; j++) {
					int kx = j - (blockPos.getY() + i);
					int n = 2 - kx / 2;

					for (int l = blockPos.getX() - n; l <= blockPos.getX() + n; l++) {
						int mx = l - blockPos.getX();

						for (int o = blockPos.getZ() - n; o <= blockPos.getZ() + n; o++) {
							int p = o - blockPos.getZ();
							if (Math.abs(mx) != n || Math.abs(p) != n || random.nextInt(2) != 0 && kx != 0) {
								BlockPos blockPos2 = new BlockPos(l, j, o);
								if (isAirOrLeaves(modifiableTestableWorld, blockPos2) || isReplaceablePlant(modifiableTestableWorld, blockPos2)) {
									this.setBlockState(set, modifiableTestableWorld, blockPos2, LEAVES, blockBox);
								}
							}
						}
					}
				}

				for (int j = 0; j < i; j++) {
					BlockPos blockPos3 = blockPos.up(j);
					if (isAirOrLeaves(modifiableTestableWorld, blockPos3) || isWater(modifiableTestableWorld, blockPos3)) {
						this.setBlockState(set, modifiableTestableWorld, blockPos3, LOG, blockBox);
					}
				}

				for (int jx = blockPos.getY() - 3 + i; jx <= blockPos.getY() + i; jx++) {
					int kx = jx - (blockPos.getY() + i);
					int n = 2 - kx / 2;
					BlockPos.Mutable mutable2 = new BlockPos.Mutable();

					for (int mx = blockPos.getX() - n; mx <= blockPos.getX() + n; mx++) {
						for (int ox = blockPos.getZ() - n; ox <= blockPos.getZ() + n; ox++) {
							mutable2.set(mx, jx, ox);
							if (isLeaves(modifiableTestableWorld, mutable2)) {
								BlockPos blockPos4 = mutable2.west();
								BlockPos blockPos2 = mutable2.east();
								BlockPos blockPos5 = mutable2.north();
								BlockPos blockPos6 = mutable2.south();
								if (random.nextInt(4) == 0 && isAir(modifiableTestableWorld, blockPos4)) {
									this.makeVines(modifiableTestableWorld, blockPos4, VineBlock.EAST);
								}

								if (random.nextInt(4) == 0 && isAir(modifiableTestableWorld, blockPos2)) {
									this.makeVines(modifiableTestableWorld, blockPos2, VineBlock.WEST);
								}

								if (random.nextInt(4) == 0 && isAir(modifiableTestableWorld, blockPos5)) {
									this.makeVines(modifiableTestableWorld, blockPos5, VineBlock.SOUTH);
								}

								if (random.nextInt(4) == 0 && isAir(modifiableTestableWorld, blockPos6)) {
									this.makeVines(modifiableTestableWorld, blockPos6, VineBlock.NORTH);
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

	private void makeVines(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, BooleanProperty booleanProperty) {
		BlockState blockState = Blocks.VINE.getDefaultState().with(booleanProperty, Boolean.valueOf(true));
		this.setBlockState(modifiableTestableWorld, blockPos, blockState);
		int i = 4;

		for (BlockPos var6 = blockPos.method_10074(); isAir(modifiableTestableWorld, var6) && i > 0; i--) {
			this.setBlockState(modifiableTestableWorld, var6, blockState);
			var6 = var6.method_10074();
		}
	}
}
