package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public record Transformation(Transformation.Target target, Keyframe... keyframes) {
	@Environment(EnvType.CLIENT)
	public interface Interpolation {
		Vector3f apply(Vector3f dest, float delta, Keyframe[] keyframes, int start, int end, float scale);
	}

	@Environment(EnvType.CLIENT)
	public static class Interpolations {
		public static final Transformation.Interpolation LINEAR = (dest, delta, keyframes, start, end, scale) -> {
			Vector3f vector3f = keyframes[start].target();
			Vector3f vector3f2 = keyframes[end].target();
			return vector3f.lerp(vector3f2, delta, dest).mul(scale);
		};
		public static final Transformation.Interpolation CUBIC = (dest, delta, keyframes, start, end, scale) -> {
			Vector3f vector3f = keyframes[Math.max(0, start - 1)].target();
			Vector3f vector3f2 = keyframes[start].target();
			Vector3f vector3f3 = keyframes[end].target();
			Vector3f vector3f4 = keyframes[Math.min(keyframes.length - 1, end + 1)].target();
			dest.set(
				MathHelper.catmullRom(delta, vector3f.x(), vector3f2.x(), vector3f3.x(), vector3f4.x()) * scale,
				MathHelper.catmullRom(delta, vector3f.y(), vector3f2.y(), vector3f3.y(), vector3f4.y()) * scale,
				MathHelper.catmullRom(delta, vector3f.z(), vector3f2.z(), vector3f3.z(), vector3f4.z()) * scale
			);
			return dest;
		};
	}

	@Environment(EnvType.CLIENT)
	public interface Target {
		void apply(ModelPart modelPart, Vector3f vec);
	}

	@Environment(EnvType.CLIENT)
	public static class Targets {
		public static final Transformation.Target TRANSLATE = ModelPart::translate;
		public static final Transformation.Target ROTATE = ModelPart::rotate;
		public static final Transformation.Target SCALE = ModelPart::scale;
	}
}
