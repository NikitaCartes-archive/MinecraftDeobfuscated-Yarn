package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_801 {
	public static final List<String> field_4270 = Lists.<String>newArrayList("layer0", "layer1", "layer2", "layer3", "layer4");

	public class_793 method_3479(Function<class_2960, class_1058> function, class_793 arg) {
		Map<String, String> map = Maps.<String, String>newHashMap();
		List<class_785> list = Lists.<class_785>newArrayList();

		for (int i = 0; i < field_4270.size(); i++) {
			String string = (String)field_4270.get(i);
			if (!arg.method_3432(string)) {
				break;
			}

			String string2 = arg.method_3436(string);
			map.put(string, string2);
			class_1058 lv = (class_1058)function.apply(new class_2960(string2));
			list.addAll(this.method_3480(i, string, lv));
		}

		map.put("particle", arg.method_3432("particle") ? arg.method_3436("particle") : (String)map.get("layer0"));
		class_793 lv2 = new class_793(null, list, map, false, false, arg.method_3443(), arg.method_3434());
		lv2.field_4252 = arg.field_4252;
		return lv2;
	}

	private List<class_785> method_3480(int i, String string, class_1058 arg) {
		Map<class_2350, class_783> map = Maps.<class_2350, class_783>newHashMap();
		map.put(class_2350.field_11035, new class_783(null, i, string, new class_787(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)));
		map.put(class_2350.field_11043, new class_783(null, i, string, new class_787(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
		List<class_785> list = Lists.<class_785>newArrayList();
		list.add(new class_785(new class_1160(0.0F, 0.0F, 7.5F), new class_1160(16.0F, 16.0F, 8.5F), map, null, true));
		list.addAll(this.method_3481(arg, string, i));
		return list;
	}

	private List<class_785> method_3481(class_1058 arg, String string, int i) {
		float f = (float)arg.method_4578();
		float g = (float)arg.method_4595();
		List<class_785> list = Lists.<class_785>newArrayList();

		for (class_801.class_802 lv : this.method_3478(arg)) {
			float h = 0.0F;
			float j = 0.0F;
			float k = 0.0F;
			float l = 0.0F;
			float m = 0.0F;
			float n = 0.0F;
			float o = 0.0F;
			float p = 0.0F;
			float q = 16.0F / f;
			float r = 16.0F / g;
			float s = (float)lv.method_3487();
			float t = (float)lv.method_3485();
			float u = (float)lv.method_3486();
			class_801.class_803 lv2 = lv.method_3484();
			switch (lv2) {
				case field_4281:
					m = s;
					h = s;
					k = n = t + 1.0F;
					o = u;
					j = u;
					l = u;
					p = u + 1.0F;
					break;
				case field_4277:
					o = u;
					p = u + 1.0F;
					m = s;
					h = s;
					k = n = t + 1.0F;
					j = u + 1.0F;
					l = u + 1.0F;
					break;
				case field_4278:
					m = u;
					h = u;
					k = u;
					n = u + 1.0F;
					p = s;
					j = s;
					l = o = t + 1.0F;
					break;
				case field_4283:
					m = u;
					n = u + 1.0F;
					h = u + 1.0F;
					k = u + 1.0F;
					p = s;
					j = s;
					l = o = t + 1.0F;
			}

			h *= q;
			k *= q;
			j *= r;
			l *= r;
			j = 16.0F - j;
			l = 16.0F - l;
			m *= q;
			n *= q;
			o *= r;
			p *= r;
			Map<class_2350, class_783> map = Maps.<class_2350, class_783>newHashMap();
			map.put(lv2.method_3488(), new class_783(null, i, string, new class_787(new float[]{m, o, n, p}, 0)));
			switch (lv2) {
				case field_4281:
					list.add(new class_785(new class_1160(h, j, 7.5F), new class_1160(k, j, 8.5F), map, null, true));
					break;
				case field_4277:
					list.add(new class_785(new class_1160(h, l, 7.5F), new class_1160(k, l, 8.5F), map, null, true));
					break;
				case field_4278:
					list.add(new class_785(new class_1160(h, j, 7.5F), new class_1160(h, l, 8.5F), map, null, true));
					break;
				case field_4283:
					list.add(new class_785(new class_1160(k, j, 7.5F), new class_1160(k, l, 8.5F), map, null, true));
			}
		}

		return list;
	}

	private List<class_801.class_802> method_3478(class_1058 arg) {
		int i = arg.method_4578();
		int j = arg.method_4595();
		List<class_801.class_802> list = Lists.<class_801.class_802>newArrayList();

		for (int k = 0; k < arg.method_4592(); k++) {
			for (int l = 0; l < j; l++) {
				for (int m = 0; m < i; m++) {
					boolean bl = !this.method_3477(arg, k, m, l, i, j);
					this.method_3476(class_801.class_803.field_4281, list, arg, k, m, l, i, j, bl);
					this.method_3476(class_801.class_803.field_4277, list, arg, k, m, l, i, j, bl);
					this.method_3476(class_801.class_803.field_4278, list, arg, k, m, l, i, j, bl);
					this.method_3476(class_801.class_803.field_4283, list, arg, k, m, l, i, j, bl);
				}
			}
		}

		return list;
	}

	private void method_3476(class_801.class_803 arg, List<class_801.class_802> list, class_1058 arg2, int i, int j, int k, int l, int m, boolean bl) {
		boolean bl2 = this.method_3477(arg2, i, j + arg.method_3490(), k + arg.method_3489(), l, m) && bl;
		if (bl2) {
			this.method_3482(list, arg, j, k);
		}
	}

	private void method_3482(List<class_801.class_802> list, class_801.class_803 arg, int i, int j) {
		class_801.class_802 lv = null;

		for (class_801.class_802 lv2 : list) {
			if (lv2.method_3484() == arg) {
				int k = arg.method_3491() ? j : i;
				if (lv2.method_3486() == k) {
					lv = lv2;
					break;
				}
			}
		}

		int l = arg.method_3491() ? j : i;
		int m = arg.method_3491() ? i : j;
		if (lv == null) {
			list.add(new class_801.class_802(arg, m, l));
		} else {
			lv.method_3483(m);
		}
	}

	private boolean method_3477(class_1058 arg, int i, int j, int k, int l, int m) {
		return j >= 0 && k >= 0 && j < l && k < m ? arg.method_4583(i, j, k) : true;
	}

	@Environment(EnvType.CLIENT)
	static class class_802 {
		private final class_801.class_803 field_4271;
		private int field_4274;
		private int field_4273;
		private final int field_4272;

		public class_802(class_801.class_803 arg, int i, int j) {
			this.field_4271 = arg;
			this.field_4274 = i;
			this.field_4273 = i;
			this.field_4272 = j;
		}

		public void method_3483(int i) {
			if (i < this.field_4274) {
				this.field_4274 = i;
			} else if (i > this.field_4273) {
				this.field_4273 = i;
			}
		}

		public class_801.class_803 method_3484() {
			return this.field_4271;
		}

		public int method_3487() {
			return this.field_4274;
		}

		public int method_3485() {
			return this.field_4273;
		}

		public int method_3486() {
			return this.field_4272;
		}
	}

	@Environment(EnvType.CLIENT)
	static enum class_803 {
		field_4281(class_2350.field_11036, 0, -1),
		field_4277(class_2350.field_11033, 0, 1),
		field_4278(class_2350.field_11034, -1, 0),
		field_4283(class_2350.field_11039, 1, 0);

		private final class_2350 field_4276;
		private final int field_4280;
		private final int field_4279;

		private class_803(class_2350 arg, int j, int k) {
			this.field_4276 = arg;
			this.field_4280 = j;
			this.field_4279 = k;
		}

		public class_2350 method_3488() {
			return this.field_4276;
		}

		public int method_3490() {
			return this.field_4280;
		}

		public int method_3489() {
			return this.field_4279;
		}

		private boolean method_3491() {
			return this == field_4277 || this == field_4281;
		}
	}
}
