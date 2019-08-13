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

public class PineTreeFeature extends AbstractTreeFeature<DefaultFeatureConfig> {
	private static final BlockState LOG = Blocks.field_10037.getDefaultState();
	private static final BlockState LEAVES = Blocks.field_9988.getDefaultState();

	public PineTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
		super(function, false);
	}

	@Override
	public boolean generate(
		Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, MutableIntBoundingBox mutableIntBoundingBox
	) {
		int i = random.nextInt(5) + 7;
		int j = i - random.nextInt(2) - 3;
		int k = i - j;
		int l = 1 + random.nextInt(k + 1);
		if (blockPos.getY() >= 1 && blockPos.getY() + i + 1 <= 256) {
			boolean bl = true;

			for (int m = blockPos.getY(); m <= blockPos.getY() + 1 + i && bl; m++) {
				int n = 1;
				if (m - blockPos.getY() < j) {
					n = 0;
				} else {
					n = l;
				}

				BlockPos.Mutable mutable = new BlockPos.Mutable();

				for (int o = blockPos.getX() - n; o <= blockPos.getX() + n && bl; o++) {
					for (int p = blockPos.getZ() - n; p <= blockPos.getZ() + n && bl; p++) {
						if (m < 0 || m >= 256) {
							bl = false;
						} else if (!canTreeReplace(modifiableTestableWorld, mutable.set(o, m, p))) {
							bl = false;
						}
					}
				}
			}

			if (!bl) {
				return false;
			} else if (isNaturalDirtOrGrass(modifiableTestableWorld, blockPos.down()) && blockPos.getY() < 256 - i - 1) {
				this.setToDirt(modifiableTestableWorld, blockPos.down());
				int m = 0;

				for (int n = blockPos.getY() + i; n >= blockPos.getY() + j; n--) {
					for (int q = blockPos.getX() - m; q <= blockPos.getX() + m; q++) {
						int o = q - blockPos.getX();

						for (int px = blockPos.getZ() - m; px <= blockPos.getZ() + m; px++) {
							int r = px - blockPos.getZ();
							if (Math.abs(o) != m || Math.abs(r) != m || m <= 0) {
								BlockPos blockPos2 = new BlockPos(q, n, px);
								if (isAirOrLeaves(modifiableTestableWorld, blockPos2)) {
									this.setBlockState(set, modifiableTestableWorld, blockPos2, LEAVES, mutableIntBoundingBox);
								}
							}
						}
					}

					if (m >= 1 && n == blockPos.getY() + j + 1) {
						m--;
					} else if (m < l) {
						m++;
					}
				}

				for (int n = 0; n < i - 1; n++) {
					if (isAirOrLeaves(modifiableTestableWorld, blockPos.up(n))) {
						this.setBlockState(set, modifiableTestableWorld, blockPos.up(n), LOG, mutableIntBoundingBox);
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
