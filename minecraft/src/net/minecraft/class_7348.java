package net.minecraft;

import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class class_7348 {
	public static void method_42976(SinglePartEntityModel<?> singlePartEntityModel, class_7345 arg, long l, float f, Vec3f vec3f) {
		float g = method_42975(arg, l);

		for (Entry<String, List<class_7340>> entry : arg.boneAnimations().entrySet()) {
			Optional<ModelPart> optional = singlePartEntityModel.method_42984((String)entry.getKey());
			List<class_7340> list = (List<class_7340>)entry.getValue();
			optional.ifPresent(modelPart -> list.forEach(argx -> {
					class_7347[] lvs = argx.keyframes();
					int i = Math.max(0, MathHelper.binarySearch(0, lvs.length, ix -> g <= lvs[ix].timestamp()) - 1);
					int j = Math.min(lvs.length - 1, i + 1);
					class_7347 lv = lvs[i];
					class_7347 lv2 = lvs[j];
					float h = g - lv.timestamp();
					float k = MathHelper.clamp(h / (lv2.timestamp() - lv.timestamp()), 0.0F, 1.0F);
					lv2.interpolation().apply(vec3f, k, lvs, i, j, f);
					argx.target().apply(modelPart, vec3f);
				}));
		}
	}

	private static float method_42975(class_7345 arg, long l) {
		float f = (float)l / 1000.0F;
		return arg.looping() ? f % arg.lengthInSeconds() : f;
	}

	public static Vec3f method_42972(float f, float g, float h) {
		return new Vec3f(f, -g, h);
	}

	public static Vec3f method_42978(float f, float g, float h) {
		return new Vec3f(f * (float) (Math.PI / 180.0), g * (float) (Math.PI / 180.0), h * (float) (Math.PI / 180.0));
	}

	public static Vec3f method_42971(double d, double e, double f) {
		return new Vec3f((float)(d - 1.0), (float)(e - 1.0), (float)(f - 1.0));
	}
}
