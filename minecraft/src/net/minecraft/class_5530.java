package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class class_5530 {
	@Nullable
	public static Vec3d method_31504(PathAwareEntity pathAwareEntity, int i, int j, int k, double d, double e, double f) {
		boolean bl = class_5493.method_31517(pathAwareEntity, i);
		return class_5535.method_31538(pathAwareEntity, () -> method_31505(pathAwareEntity, i, j, k, d, e, f, bl));
	}

	@Nullable
	public static BlockPos method_31505(PathAwareEntity pathAwareEntity, int i, int j, int k, double d, double e, double f, boolean bl) {
		BlockPos blockPos = class_5535.method_31542(pathAwareEntity.getRandom(), i, j, k, d, e, f);
		if (blockPos == null) {
			return null;
		} else {
			BlockPos blockPos2 = class_5535.method_31537(pathAwareEntity, i, pathAwareEntity.getRandom(), blockPos);
			if (!class_5493.method_31520(blockPos2, pathAwareEntity) && !class_5493.method_31521(bl, pathAwareEntity, blockPos2)) {
				blockPos2 = class_5535.method_31540(blockPos2, pathAwareEntity.world.getTopY(), blockPosx -> class_5493.method_31523(pathAwareEntity, blockPosx));
				return class_5493.method_31522(pathAwareEntity, blockPos2) ? null : blockPos2;
			} else {
				return null;
			}
		}
	}
}
