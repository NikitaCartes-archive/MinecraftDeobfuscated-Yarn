package net.minecraft;

import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class class_7019 {
	public static void method_40907(SinglePartEntityModel<?> singlePartEntityModel, Animation animation, long l, float f, Vec3f vec3f) {
		float g = method_40906(animation, l);

		for (Entry<String, List<Transformation>> entry : animation.boneAnimations().entrySet()) {
			Optional<ModelPart> optional = singlePartEntityModel.method_40912((String)entry.getKey());
			List<Transformation> list = (List<Transformation>)entry.getValue();
			optional.ifPresent(modelPart -> list.forEach(transformation -> {
					Keyframe[] keyframes = transformation.keyframes();
					int i = Math.max(0, MathHelper.binarySearch(0, keyframes.length, ix -> g <= keyframes[ix].timestamp()) - 1);
					int j = Math.min(keyframes.length - 1, i + 1);
					Keyframe keyframe = keyframes[i];
					Keyframe keyframe2 = keyframes[j];
					float h = g - keyframe.timestamp();
					float k = MathHelper.clamp(h / (keyframe2.timestamp() - keyframe.timestamp()), 0.0F, 1.0F);
					keyframe2.interpolation().apply(vec3f, k, keyframes, i, j, f);
					transformation.target().apply(modelPart, vec3f);
				}));
		}
	}

	private static float method_40906(Animation animation, long l) {
		float f = (float)l / 1000.0F;
		return animation.looping() ? f % animation.lengthInSeconds() : f;
	}

	public static Vec3f method_40903(float f, float g, float h) {
		return new Vec3f(f, -g, h);
	}

	public static Vec3f method_40909(float f, float g, float h) {
		return new Vec3f(f * (float) (Math.PI / 180.0), g * (float) (Math.PI / 180.0), h * (float) (Math.PI / 180.0));
	}

	public static Vec3f method_40902(double d, double e, double f) {
		return new Vec3f((float)(d - 1.0), (float)(e - 1.0), (float)(f - 1.0));
	}
}
