package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;

public class SpruceTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.field_10037.method_9564();
	private static final BlockState LEAVES = Blocks.field_9988.method_9564();

	public SpruceTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl) {
		super(function, bl);
	}

	@Override
	public boolean method_12775(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos) {
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
							if (!method_16420(modifiableTestableWorld, mutable)) {
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
			} else if (method_16433(modifiableTestableWorld, blockPos.down()) && blockPos.getY() < 256 - i - 1) {
				this.method_16427(modifiableTestableWorld, blockPos.down());
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
								if (method_16420(modifiableTestableWorld, blockPos2) || method_16425(modifiableTestableWorld, blockPos2)) {
									this.method_13153(modifiableTestableWorld, blockPos2, LEAVES);
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
					if (method_16420(modifiableTestableWorld, blockPos.up(px))) {
						this.method_12773(set, modifiableTestableWorld, blockPos.up(px), LOG);
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
