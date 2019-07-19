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

public class SpruceTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.SPRUCE_LOG.getDefaultState();
	private static final BlockState LEAVES = Blocks.SPRUCE_LEAVES.getDefaultState();

	public SpruceTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> configFactory, boolean emitNeighborBlockUpdates) {
		super(configFactory, emitNeighborBlockUpdates);
	}

	@Override
	public boolean generate(Set<BlockPos> logPositions, ModifiableTestableWorld world, Random random, BlockPos pos, BlockBox blockBox) {
		int i = random.nextInt(4) + 6;
		int j = 1 + random.nextInt(2);
		int k = i - j;
		int l = 2 + random.nextInt(2);
		boolean bl = true;
		if (pos.getY() >= 1 && pos.getY() + i + 1 <= 256) {
			for (int m = pos.getY(); m <= pos.getY() + 1 + i && bl; m++) {
				int n;
				if (m - pos.getY() < j) {
					n = 0;
				} else {
					n = l;
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int o = pos.getX() - n; o <= pos.getX() + n && bl; o++) {
					for (int p = pos.getZ() - n; p <= pos.getZ() + n && bl; p++) {
						if (m >= 0 && m < 256) {
							mutable.set(o, m, p);
							if (!isAirOrLeaves(world, mutable)) {
								bl = false;
							}
						} else {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else if (isDirtOrGrass(world, pos.down()) && pos.getY() < 256 - i - 1) {
				this.setToDirt(world, pos.down());
				int m = random.nextInt(2);
				int n = 1;
				int q = 0;

				for (int o = 0; o <= k; o++) {
					int px = pos.getY() + i - o;

					for (int r = pos.getX() - m; r <= pos.getX() + m; r++) {
						int s = r - pos.getX();

						for (int t = pos.getZ() - m; t <= pos.getZ() + m; t++) {
							int u = t - pos.getZ();
							if (Math.abs(s) != m || Math.abs(u) != m || m <= 0) {
								BlockPos blockPos = new BlockPos(r, px, t);
								if (isAirOrLeaves(world, blockPos) || isReplaceablePlant(world, blockPos)) {
									this.setBlockState(logPositions, world, blockPos, LEAVES, blockBox);
								}
							}
						}
					}

					if (m >= n) {
						m = q;
						q = 1;
						if (++n > l) {
							n = l;
						}
					} else {
						m++;
					}
				}

				int o = random.nextInt(3);

				for (int px = 0; px < i - o; px++) {
					if (isAirOrLeaves(world, pos.up(px))) {
						this.setBlockState(logPositions, world, pos.up(px), LOG, blockBox);
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
