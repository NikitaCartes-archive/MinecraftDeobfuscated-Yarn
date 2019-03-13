package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ModifiableTestableWorld;

public class BirchTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.field_10511.method_9564();
	private static final BlockState LEAVES = Blocks.field_10539.method_9564();
	private final boolean alwaysTall;

	public BirchTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl, boolean bl2) {
		super(function, bl);
		this.alwaysTall = bl2;
	}

	@Override
	public boolean method_12775(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos) {
		int i = random.nextInt(3) + 5;
		if (this.alwaysTall) {
			i += random.nextInt(7);
		}

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

				for (int j = blockPos.getY() - 3 + i; j <= blockPos.getY() + i; j++) {
					int kx = j - (blockPos.getY() + i);
					int n = 1 - kx / 2;

					for (int l = blockPos.getX() - n; l <= blockPos.getX() + n; l++) {
						int mx = l - blockPos.getX();

						for (int o = blockPos.getZ() - n; o <= blockPos.getZ() + n; o++) {
							int p = o - blockPos.getZ();
							if (Math.abs(mx) != n || Math.abs(p) != n || random.nextInt(2) != 0 && kx != 0) {
								BlockPos blockPos2 = new BlockPos(l, j, o);
								if (method_16420(modifiableTestableWorld, blockPos2)) {
									this.method_13153(modifiableTestableWorld, blockPos2, LEAVES);
								}
							}
						}
					}
				}

				for (int j = 0; j < i; j++) {
					if (method_16420(modifiableTestableWorld, blockPos.up(j))) {
						this.method_12773(set, modifiableTestableWorld, blockPos.up(j), LOG);
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
