package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_770 {
	public static final class_770.class_771 field_4154 = new class_770.class_771(
		"banner_", new class_2960("textures/entity/banner_base.png"), "textures/entity/banner/"
	);
	public static final class_770.class_771 field_4155 = new class_770.class_771(
		"shield_", new class_2960("textures/entity/shield_base.png"), "textures/entity/shield/"
	);
	public static final class_2960 field_4152 = new class_2960("textures/entity/shield_base_nopattern.png");
	public static final class_2960 field_4153 = new class_2960("textures/entity/banner/base.png");

	@Environment(EnvType.CLIENT)
	public static class class_771 {
		private final Map<String, class_770.class_772> field_4159 = Maps.<String, class_770.class_772>newLinkedHashMap();
		private final class_2960 field_4157;
		private final String field_4156;
		private final String field_4158;

		public class_771(String string, class_2960 arg, String string2) {
			this.field_4158 = string;
			this.field_4157 = arg;
			this.field_4156 = string2;
		}

		@Nullable
		public class_2960 method_3331(String string, List<class_2582> list, List<class_1767> list2) {
			if (string.isEmpty()) {
				return null;
			} else if (!list.isEmpty() && !list2.isEmpty()) {
				string = this.field_4158 + string;
				class_770.class_772 lv = (class_770.class_772)this.field_4159.get(string);
				if (lv == null) {
					if (this.field_4159.size() >= 256 && !this.method_3332()) {
						return class_770.field_4153;
					}

					List<String> list3 = Lists.<String>newArrayList();

					for (class_2582 lv2 : list) {
						list3.add(this.field_4156 + lv2.method_10947() + ".png");
					}

					lv = new class_770.class_772();
					lv.field_4160 = new class_2960(string);
					class_310.method_1551().method_1531().method_4616(lv.field_4160, new class_1045(this.field_4157, list3, list2));
					this.field_4159.put(string, lv);
				}

				lv.field_4161 = class_156.method_658();
				return lv.field_4160;
			} else {
				return class_1047.method_4539();
			}
		}

		private boolean method_3332() {
			long l = class_156.method_658();
			Iterator<String> iterator = this.field_4159.keySet().iterator();

			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				class_770.class_772 lv = (class_770.class_772)this.field_4159.get(string);
				if (l - lv.field_4161 > 5000L) {
					class_310.method_1551().method_1531().method_4615(lv.field_4160);
					iterator.remove();
					return true;
				}
			}

			return this.field_4159.size() < 256;
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_772 {
		public long field_4161;
		public class_2960 field_4160;

		private class_772() {
		}
	}
}
