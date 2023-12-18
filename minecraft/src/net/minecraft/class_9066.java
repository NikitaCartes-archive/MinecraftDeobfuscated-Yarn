package net.minecraft;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class class_9066 {
	public static final class_9066 field_47751 = new class_9066(Map.of());
	private final Map<class_9064, List<Vec3d>> field_47752;

	class_9066(Map<class_9064, List<Vec3d>> map) {
		this.field_47752 = map;
	}

	public static class_9066.class_9067 method_55673() {
		return new class_9066.class_9067();
	}

	public class_9066 method_55674(float f, float g, float h) {
		Map<class_9064, List<Vec3d>> map = new EnumMap(class_9064.class);

		for (Entry<class_9064, List<Vec3d>> entry : this.field_47752.entrySet()) {
			map.put((class_9064)entry.getKey(), method_55677((List<Vec3d>)entry.getValue(), f, g, h));
		}

		return new class_9066(map);
	}

	private static List<Vec3d> method_55677(List<Vec3d> list, float f, float g, float h) {
		List<Vec3d> list2 = new ArrayList(list.size());

		for (Vec3d vec3d : list) {
			list2.add(vec3d.multiply((double)f, (double)g, (double)h));
		}

		return list2;
	}

	@Nullable
	public Vec3d method_55675(class_9064 arg, int i, float f) {
		List<Vec3d> list = (List<Vec3d>)this.field_47752.get(arg);
		return i >= 0 && i < list.size() ? method_55676((Vec3d)list.get(i), f) : null;
	}

	public Vec3d method_55678(class_9064 arg, int i, float f) {
		Vec3d vec3d = this.method_55675(arg, i, f);
		if (vec3d == null) {
			throw new IllegalStateException("Had no attachment point of type: " + arg + " for index: " + i);
		} else {
			return vec3d;
		}
	}

	public Vec3d method_55679(class_9064 arg, int i, float f) {
		List<Vec3d> list = (List<Vec3d>)this.field_47752.get(arg);
		if (list.isEmpty()) {
			throw new IllegalStateException("Had no attachment points of type: " + arg);
		} else {
			Vec3d vec3d = (Vec3d)list.get(MathHelper.clamp(i, 0, list.size() - 1));
			return method_55676(vec3d, f);
		}
	}

	private static Vec3d method_55676(Vec3d vec3d, float f) {
		return vec3d.rotateY(-f * (float) (Math.PI / 180.0));
	}

	public static class class_9067 {
		private final Map<class_9064, List<Vec3d>> field_47753 = new EnumMap(class_9064.class);

		class_9067() {
		}

		public class_9066.class_9067 method_55682(class_9064 arg, float f, float g, float h) {
			return this.method_55683(arg, new Vec3d((double)f, (double)g, (double)h));
		}

		public class_9066.class_9067 method_55683(class_9064 arg, Vec3d vec3d) {
			((List)this.field_47753.computeIfAbsent(arg, argx -> new ArrayList(1))).add(vec3d);
			return this;
		}

		public class_9066 method_55680(float f, float g) {
			Map<class_9064, List<Vec3d>> map = new EnumMap(class_9064.class);

			for (class_9064 lv : class_9064.values()) {
				List<Vec3d> list = (List<Vec3d>)this.field_47753.get(lv);
				map.put(lv, list != null ? List.copyOf(list) : lv.method_55670(f, g));
			}

			return new class_9066(map);
		}
	}
}
