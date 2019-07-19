package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;

public class BirchTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.BIRCH_LOG.getDefaultState();
	private static final BlockState LEAVES = Blocks.BIRCH_LEAVES.getDefaultState();
	private final boolean alwaysTall;

	public BirchTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory, boolean emitNeighborBlockUpdates, boolean alwaysTall) {
		super(configFactory, emitNeighborBlockUpdates);
		this.alwaysTall = alwaysTall;
	}

	@Override
	public boolean generate(Set<BlockPos> logPositions, ModifiableTestableWorld world, Random random, BlockPos pos, BlockBox blockBox) {
		int i = random.nextInt(3) + 5;
		if (this.alwaysTall) {
			i += random.nextInt(7);
		}

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

				for (int j = pos.getY() - 3 + i; j <= pos.getY() + i; j++) {
					int kx = j - (pos.getY() + i);
					int n = 1 - kx / 2;

					for (int l = pos.getX() - n; l <= pos.getX() + n; l++) {
						int mx = l - pos.getX();

						for (int o = pos.getZ() - n; o <= pos.getZ() + n; o++) {
							int p = o - pos.getZ();
							if (Math.abs(mx) != n || Math.abs(p) != n || random.nextInt(2) != 0 && kx != 0) {
								BlockPos blockPos = new BlockPos(l, j, o);
								if (isAirOrLeaves(world, blockPos)) {
									this.setBlockState(logPositions, world, blockPos, LEAVES, blockBox);
								}
							}
						}
					}
				}

				for (int j = 0; j < i; j++) {
					if (isAirOrLeaves(world, pos.up(j))) {
						this.setBlockState(logPositions, world, pos.up(j), LOG, blockBox);
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
}
