package net.minecraft.entity.ai;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;

public class BlockSenses {
	public static Optional<BlockPos> findBlock(BlockPos center, int horizontalRadius, int verticalRadius, Predicate<BlockPos> predicate) {
		if (predicate.test(center)) {
			return Optional.of(center);
		} else {
			int i = Math.max(horizontalRadius, verticalRadius);
			BlockPos.Mutable mutable = new BlockPos.Mutable(center);

			for (int j = 1; j <= i; j++) {
				for (int k = -j; k <= j; k++) {
					if (k <= horizontalRadius && k >= -horizontalRadius) {
						boolean bl = k == -j || k == j;

						for (int l = -j; l <= j; l++) {
							if (l <= verticalRadius && l >= -verticalRadius) {
								boolean bl2 = l == -j || l == j;

								for (int m = -j; m <= j; m++) {
									if (m <= horizontalRadius && m >= -horizontalRadius) {
										boolean bl3 = m == -j || m == j;
										if ((bl || bl2 || bl3) && predicate.test(mutable.set(center).setOffset(k, l, m))) {
											return Optional.of(center.add(k, l, m));
										}
									}
								}
							}
						}
					}
				}
			}

			return Optional.empty();
		}
	}
}
