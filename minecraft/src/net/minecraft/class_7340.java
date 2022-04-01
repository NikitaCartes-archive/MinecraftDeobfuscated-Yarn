package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public record class_7340(class_7340.class_7343 target, class_7347... keyframes) {
	@Environment(EnvType.CLIENT)
	public interface class_7341 {
		Vec3f apply(Vec3f vec3f, float f, class_7347[] args, int i, int j, float g);
	}

	@Environment(EnvType.CLIENT)
	public static class class_7342 {
		public static final class_7340.class_7341 field_38619 = (vec3f, f, args, i, j, g) -> {
			Vec3f vec3f2 = args[i].target();
			Vec3f vec3f3 = args[j].target();
			vec3f.set(
				MathHelper.lerp(f, vec3f2.getX(), vec3f3.getX()) * g,
				MathHelper.lerp(f, vec3f2.getY(), vec3f3.getY()) * g,
				MathHelper.lerp(f, vec3f2.getZ(), vec3f3.getZ()) * g
			);
			return vec3f;
		};
		public static final class_7340.class_7341 field_38620 = (vec3f, f, args, i, j, g) -> {
			Vec3f vec3f2 = args[Math.max(0, i - 1)].target();
			Vec3f vec3f3 = args[i].target();
			Vec3f vec3f4 = args[j].target();
			Vec3f vec3f5 = args[Math.min(args.length - 1, j + 1)].target();
			vec3f.set(
				MathHelper.method_42772(f, vec3f2.getX(), vec3f3.getX(), vec3f4.getX(), vec3f5.getX()) * g,
				MathHelper.method_42772(f, vec3f2.getY(), vec3f3.getY(), vec3f4.getY(), vec3f5.getY()) * g,
				MathHelper.method_42772(f, vec3f2.getZ(), vec3f3.getZ(), vec3f4.getZ(), vec3f5.getZ()) * g
			);
			return vec3f;
		};
	}

	@Environment(EnvType.CLIENT)
	public interface class_7343 {
		void apply(ModelPart modelPart, Vec3f vec3f);
	}

	@Environment(EnvType.CLIENT)
	public static class class_7344 {
		public static final class_7340.class_7343 field_38621 = ModelPart::method_42989;
		public static final class_7340.class_7343 field_38622 = ModelPart::method_42991;
		public static final class_7340.class_7343 field_38623 = ModelPart::method_42993;
	}
}
