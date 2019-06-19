package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.ModifiableTestableWorld;

public class SpruceTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.field_10037.getDefaultState();
	private static final BlockState LEAVES = Blocks.field_9988.getDefaultState();

	public SpruceTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl) {
		super(function, bl);
	}

	@Override
	public boolean generate(
		Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox
	) {
		int i = random.nextInt(4) + 6;
		int j = 1 + random.nextInt(2);
		int k = i - j;
		int l = 2 + random.nextInt(2);
		boolean bl = true;
		if (blockPos.getY() >= 1 && blockPos.getY() + i + 1 <= 256) {
			for (int m = blockPos.getY(); m <= blockPos.getY() + 1 + i && bl; m++) {
				int n;
				if (m - blockPos.getY() < j) {
					n = 0;
				} else {
					n = l;
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int o = blockPos.getX() - n; o <= blockPos.getX() + n && bl; o++) {
					for (int p = blockPos.getZ() - n; p <= blockPos.getZ() + n && bl; p++) {
						if (m >= 0 && m < 256) {
							mutable.set(o, m, p);
							if (!isAirOrLeaves(modifiableTestableWorld, mutable)) {
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
			} else if (isDirtOrGrass(modifiableTestableWorld, blockPos.down()) && blockPos.getY() < 256 - i - 1) {
				this.setToDirt(modifiableTestableWorld, blockPos.down());
				int m = random.nextInt(2);
				int n = 1;
				int q = 0;

				for (int o = 0; o <= k; o++) {
					int px = blockPos.getY() + i - o;

					for (int r = blockPos.getX() - m; r <= blockPos.getX() + m; r++) {
						int s = r - blockPos.getX();

						for (int t = blockPos.getZ() - m; t <= blockPos.getZ() + m; t++) {
							int u = t - blockPos.getZ();
							if (Math.abs(s) != m || Math.abs(u) != m || m <= 0) {
								BlockPos blockPos2 = new BlockPos(r, px, t);
								if (isAirOrLeaves(modifiableTestableWorld, blockPos2) || isReplaceablePlant(modifiableTestableWorld, blockPos2)) {
									this.setBlockState(set, modifiableTestableWorld, blockPos2, LEAVES, mutableIntBoundingBox);
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
					if (isAirOrLeaves(modifiableTestableWorld, blockPos.up(px))) {
						this.setBlockState(set, modifiableTestableWorld, blockPos.up(px), LOG, mutableIntBoundingBox);
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
