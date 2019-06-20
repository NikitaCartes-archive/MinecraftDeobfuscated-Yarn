package net.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2878 extends class_2869 {
	public class_2878(class_1937 arg, class_2874 arg2) {
		super(arg, arg2);
	}

	@Override
	public class_2874 method_12460() {
		return class_2874.field_13072;
	}

	@Override
	public class_2794<? extends class_2888> method_12443() {
		class_1942 lv = this.field_13058.method_8401().method_153();
		class_2798<class_3232, class_2897> lv2 = class_2798.field_12766;
		class_2798<class_2892, class_2891> lv3 = class_2798.field_12768;
		class_2798<class_2900, class_2908> lv4 = class_2798.field_12765;
		class_2798<class_2916, class_2914> lv5 = class_2798.field_12770;
		class_2798<class_2906, class_2912> lv6 = class_2798.field_12769;
		class_1969<class_1991, class_1992> lv7 = class_1969.field_9401;
		class_1969<class_2084, class_2088> lv8 = class_1969.field_9402;
		class_1969<class_1976, class_1973> lv9 = class_1969.field_9398;
		if (lv == class_1942.field_9277) {
			class_3232 lv10 = class_3232.method_14323(new Dynamic<>(class_2509.field_11560, this.field_13058.method_8401().method_169()));
			class_1991 lv11 = lv7.method_8774().method_8782(lv10.method_14326());
			return lv2.create(this.field_13058, lv7.method_8772(lv11), lv10);
		} else if (lv == class_1942.field_9266) {
			class_1991 lv12 = lv7.method_8774().method_8782(class_1972.field_9451);
			return lv3.create(this.field_13058, lv7.method_8772(lv12), lv3.method_12117());
		} else if (lv != class_1942.field_9275) {
			class_2906 lv24 = lv6.method_12117();
			class_2084 lv25 = lv8.method_8774().method_9002(this.field_13058.method_8401()).method_9004(lv24);
			return lv6.create(this.field_13058, lv8.method_8772(lv25), lv24);
		} else {
			class_1966 lv13 = null;
			JsonElement jsonElement = Dynamic.convert(class_2509.field_11560, JsonOps.INSTANCE, this.field_13058.method_8401().method_169());
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			if (jsonObject.has("biome_source") && jsonObject.getAsJsonObject("biome_source").has("type") && jsonObject.getAsJsonObject("biome_source").has("options")) {
				class_1969<?, ?> lv14 = class_2378.field_11151
					.method_10223(new class_2960(jsonObject.getAsJsonObject("biome_source").getAsJsonPrimitive("type").getAsString()));
				JsonObject jsonObject2 = jsonObject.getAsJsonObject("biome_source").getAsJsonObject("options");
				class_1959[] lvs = new class_1959[]{class_1972.field_9423};
				if (jsonObject2.has("biomes")) {
					JsonArray jsonArray = jsonObject2.getAsJsonArray("biomes");
					lvs = jsonArray.size() > 0 ? new class_1959[jsonArray.size()] : new class_1959[]{class_1972.field_9423};

					for (int i = 0; i < jsonArray.size(); i++) {
						lvs[i] = (class_1959)class_2378.field_11153.method_17966(new class_2960(jsonArray.get(i).getAsString())).orElse(class_1972.field_9423);
					}
				}

				if (class_1969.field_9401 == lv14) {
					class_1991 lv15 = lv7.method_8774().method_8782(lvs[0]);
					lv13 = lv7.method_8772(lv15);
				}

				if (class_1969.field_9398 == lv14) {
					int j = jsonObject2.has("size") ? jsonObject2.getAsJsonPrimitive("size").getAsInt() : 2;
					class_1976 lv16 = lv9.method_8774().method_8777(lvs).method_8780(j);
					lv13 = lv9.method_8772(lv16);
				}

				if (class_1969.field_9402 == lv14) {
					class_2084 lv17 = lv8.method_8774().method_9004(new class_2906()).method_9002(this.field_13058.method_8401());
					lv13 = lv8.method_8772(lv17);
				}
			}

			if (lv13 == null) {
				lv13 = lv7.method_8772(lv7.method_8774().method_8782(class_1972.field_9423));
			}

			class_2680 lv18 = class_2246.field_10340.method_9564();
			class_2680 lv19 = class_2246.field_10382.method_9564();
			if (jsonObject.has("chunk_generator") && jsonObject.getAsJsonObject("chunk_generator").has("options")) {
				if (jsonObject.getAsJsonObject("chunk_generator").getAsJsonObject("options").has("default_block")) {
					String string = jsonObject.getAsJsonObject("chunk_generator").getAsJsonObject("options").getAsJsonPrimitive("default_block").getAsString();
					lv18 = class_2378.field_11146.method_10223(new class_2960(string)).method_9564();
				}

				if (jsonObject.getAsJsonObject("chunk_generator").getAsJsonObject("options").has("default_fluid")) {
					String string = jsonObject.getAsJsonObject("chunk_generator").getAsJsonObject("options").getAsJsonPrimitive("default_fluid").getAsString();
					lv19 = class_2378.field_11146.method_10223(new class_2960(string)).method_9564();
				}
			}

			if (jsonObject.has("chunk_generator") && jsonObject.getAsJsonObject("chunk_generator").has("type")) {
				class_2798<?, ?> lv20 = class_2378.field_11149
					.method_10223(new class_2960(jsonObject.getAsJsonObject("chunk_generator").getAsJsonPrimitive("type").getAsString()));
				if (class_2798.field_12765 == lv20) {
					class_2900 lv21 = lv4.method_12117();
					lv21.method_12571(lv18);
					lv21.method_12572(lv19);
					return lv4.create(this.field_13058, lv13, lv21);
				}

				if (class_2798.field_12770 == lv20) {
					class_2916 lv22 = lv5.method_12117();
					lv22.method_12651(new class_2338(0, 64, 0));
					lv22.method_12571(lv18);
					lv22.method_12572(lv19);
					return lv5.create(this.field_13058, lv13, lv22);
				}
			}

			class_2906 lv23 = lv6.method_12117();
			lv23.method_12571(lv18);
			lv23.method_12572(lv19);
			return lv6.create(this.field_13058, lv13, lv23);
		}
	}

	@Nullable
	@Override
	public class_2338 method_12452(class_1923 arg, boolean bl) {
		for (int i = arg.method_8326(); i <= arg.method_8327(); i++) {
			for (int j = arg.method_8328(); j <= arg.method_8329(); j++) {
				class_2338 lv = this.method_12444(i, j, bl);
				if (lv != null) {
					return lv;
				}
			}
		}

		return null;
	}

	@Nullable
	@Override
	public class_2338 method_12444(int i, int j, boolean bl) {
		class_2338.class_2339 lv = new class_2338.class_2339(i, 0, j);
		class_1959 lv2 = this.field_13058.method_8310(lv);
		class_2680 lv3 = lv2.method_8722().method_15337();
		if (bl && !lv3.method_11614().method_9525(class_3481.field_15478)) {
			return null;
		} else {
			class_2818 lv4 = this.field_13058.method_8497(i >> 4, j >> 4);
			int k = lv4.method_12005(class_2902.class_2903.field_13197, i & 15, j & 15);
			if (k < 0) {
				return null;
			} else if (lv4.method_12005(class_2902.class_2903.field_13202, i & 15, j & 15) > lv4.method_12005(class_2902.class_2903.field_13200, i & 15, j & 15)) {
				return null;
			} else {
				for (int l = k + 1; l >= 0; l--) {
					lv.method_10103(i, l, j);
					class_2680 lv5 = this.field_13058.method_8320(lv);
					if (!lv5.method_11618().method_15769()) {
						break;
					}

					if (lv5.equals(lv3)) {
						return lv.method_10084().method_10062();
					}
				}

				return null;
			}
		}
	}

	@Override
	public float method_12464(long l, float f) {
		double d = class_3532.method_15385((double)l / 24000.0 - 0.25);
		double e = 0.5 - Math.cos(d * Math.PI) / 2.0;
		return (float)(d * 2.0 + e) / 3.0F;
	}

	@Override
	public boolean method_12462() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_243 method_12445(float f, float g) {
		float h = class_3532.method_15362(f * (float) (Math.PI * 2)) * 2.0F + 0.5F;
		h = class_3532.method_15363(h, 0.0F, 1.0F);
		float i = 0.7529412F;
		float j = 0.84705883F;
		float k = 1.0F;
		i *= h * 0.94F + 0.06F;
		j *= h * 0.94F + 0.06F;
		k *= h * 0.91F + 0.09F;
		return new class_243((double)i, (double)j, (double)k);
	}

	@Override
	public boolean method_12448() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_12453(int i, int j) {
		return false;
	}
}
