package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class class_5533 {
	@Nullable
	public static Vec3d method_31524(PathAwareEntity pathAwareEntity, int i, int j, double d, double e, float f, int k, int l) {
		boolean bl = class_5493.method_31517(pathAwareEntity, i);
		return class_5535.method_31538(
			pathAwareEntity,
			() -> {
				BlockPos blockPos = class_5535.method_31542(pathAwareEntity.getRandom(), i, j, 0, d, e, (double)f);
				if (blockPos == null) {
					return null;
				} else {
					BlockPos blockPos2 = class_5534.method_31532(pathAwareEntity, i, bl, blockPos);
					if (blockPos2 == null) {
						return null;
					} else {
						blockPos2 = class_5535.method_31539(
							blockPos2,
							pathAwareEntity.getRandom().nextInt(k - l + 1) + l,
							pathAwareEntity.world.getTopHeightLimit(),
							blockPosx -> class_5493.method_31523(pathAwareEntity, blockPosx)
						);
						return !class_5493.method_31518(pathAwareEntity, blockPos2) && !class_5493.method_31522(pathAwareEntity, blockPos2) ? blockPos2 : null;
					}
				}
			}
		);
	}
}
