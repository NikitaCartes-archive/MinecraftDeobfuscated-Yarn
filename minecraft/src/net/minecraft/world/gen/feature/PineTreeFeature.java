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

public class PineTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.SPRUCE_LOG.getDefaultState();
	private static final BlockState LEAVES = Blocks.SPRUCE_LEAVES.getDefaultState();

	public PineTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory) {
		super(configFactory, false);
	}

	@Override
	public boolean generate(Set<BlockPos> logPositions, ModifiableTestableWorld world, Random random, BlockPos pos, BlockBox blockBox) {
		int i = random.nextInt(5) + 7;
		int j = i - random.nextInt(2) - 3;
		int k = i - j;
		int l = 1 + random.nextInt(k + 1);
		if (pos.getY() >= 1 && pos.getY() + i + 1 <= 256) {
			boolean bl = true;

			for (int m = pos.getY(); m <= pos.getY() + 1 + i && bl; m++) {
				int n = 1;
				if (m - pos.getY() < j) {
					n = 0;
				} else {
					n = l;
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int o = pos.getX() - n; o <= pos.getX() + n && bl; o++) {
					for (int p = pos.getZ() - n; p <= pos.getZ() + n && bl; p++) {
						if (m < 0 || m >= 256) {
							bl = false;
						} else if (!canTreeReplace(world, mutable.set(o, m, p))) {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else if (isNaturalDirtOrGrass(world, pos.down()) && pos.getY() < 256 - i - 1) {
				this.setToDirt(world, pos.down());
				int m = 0;

				for (int n = pos.getY() + i; n >= pos.getY() + j; n--) {
					for (int q = pos.getX() - m; q <= pos.getX() + m; q++) {
						int o = q - pos.getX();

						for (int px = pos.getZ() - m; px <= pos.getZ() + m; px++) {
							int r = px - pos.getZ();
							if (Math.abs(o) != m || Math.abs(r) != m || m <= 0) {
								BlockPos blockPos = new BlockPos(q, n, px);
								if (isAirOrLeaves(world, blockPos)) {
									this.setBlockState(logPositions, world, blockPos, LEAVES, blockBox);
								}
							}
						}
					}

					if (m >= 1 && n == pos.getY() + j + 1) {
						m--;
					} else if (m < l) {
						m++;
					}
				}

				for (int n = 0; n < i - 1; n++) {
					if (isAirOrLeaves(world, pos.up(n))) {
						this.setBlockState(logPositions, world, pos.up(n), LOG, blockBox);
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
