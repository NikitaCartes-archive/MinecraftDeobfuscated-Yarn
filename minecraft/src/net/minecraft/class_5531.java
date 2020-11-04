package net.minecraft;

import javax.annotation.Nullable;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class class_5531 {
	@Nullable
	public static Vec3d method_31508(PathAwareEntity pathAwareEntity, int i, int j, int k, Vec3d vec3d, double d) {
		Vec3d vec3d2 = vec3d.subtract(pathAwareEntity.getX(), pathAwareEntity.getY(), pathAwareEntity.getZ());
		boolean bl = class_5493.method_31517(pathAwareEntity, i);
		return class_5535.method_31538(pathAwareEntity, () -> {
			BlockPos blockPos = class_5530.method_31505(pathAwareEntity, i, j, k, vec3d2.x, vec3d2.z, d, bl);
			return blockPos != null && !class_5493.method_31518(pathAwareEntity, blockPos) ? blockPos : null;
		});
	}
}
