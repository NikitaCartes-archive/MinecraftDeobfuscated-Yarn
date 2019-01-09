package net.minecraft;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.GlStateManager;
import java.lang.reflect.Type;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_809 {
	public static final class_809 field_4301 = new class_809();
	public static float field_4299;
	public static float field_4298;
	public static float field_4297;
	public static float field_4296;
	public static float field_4295;
	public static float field_4312;
	public static float field_4310;
	public static float field_4309;
	public static float field_4308;
	public final class_804 field_4305;
	public final class_804 field_4307;
	public final class_804 field_4302;
	public final class_804 field_4304;
	public final class_804 field_4311;
	public final class_804 field_4300;
	public final class_804 field_4303;
	public final class_804 field_4306;

	private class_809() {
		this(
			class_804.field_4284,
			class_804.field_4284,
			class_804.field_4284,
			class_804.field_4284,
			class_804.field_4284,
			class_804.field_4284,
			class_804.field_4284,
			class_804.field_4284
		);
	}

	public class_809(class_809 arg) {
		this.field_4305 = arg.field_4305;
		this.field_4307 = arg.field_4307;
		this.field_4302 = arg.field_4302;
		this.field_4304 = arg.field_4304;
		this.field_4311 = arg.field_4311;
		this.field_4300 = arg.field_4300;
		this.field_4303 = arg.field_4303;
		this.field_4306 = arg.field_4306;
	}

	public class_809(class_804 arg, class_804 arg2, class_804 arg3, class_804 arg4, class_804 arg5, class_804 arg6, class_804 arg7, class_804 arg8) {
		this.field_4305 = arg;
		this.field_4307 = arg2;
		this.field_4302 = arg3;
		this.field_4304 = arg4;
		this.field_4311 = arg5;
		this.field_4300 = arg6;
		this.field_4303 = arg7;
		this.field_4306 = arg8;
	}

	public void method_3500(class_809.class_811 arg) {
		method_3502(this.method_3503(arg), false);
	}

	public static void method_3502(class_804 arg, boolean bl) {
		if (arg != class_804.field_4284) {
			int i = bl ? -1 : 1;
			GlStateManager.translatef(
				(float)i * (field_4299 + arg.field_4286.method_4943()), field_4298 + arg.field_4286.method_4945(), field_4297 + arg.field_4286.method_4947()
			);
			float f = field_4296 + arg.field_4287.method_4943();
			float g = field_4295 + arg.field_4287.method_4945();
			float h = field_4312 + arg.field_4287.method_4947();
			if (bl) {
				g = -g;
				h = -h;
			}

			GlStateManager.multMatrix(new class_1159(new class_1158(f, g, h, true)));
			GlStateManager.scalef(field_4310 + arg.field_4285.method_4943(), field_4309 + arg.field_4285.method_4945(), field_4308 + arg.field_4285.method_4947());
		}
	}

	public class_804 method_3503(class_809.class_811 arg) {
		switch (arg) {
			case field_4323:
				return this.field_4305;
			case field_4320:
				return this.field_4307;
			case field_4321:
				return this.field_4302;
			case field_4322:
				return this.field_4304;
			case field_4316:
				return this.field_4311;
			case field_4317:
				return this.field_4300;
			case field_4318:
				return this.field_4303;
			case field_4319:
				return this.field_4306;
			default:
				return class_804.field_4284;
		}
	}

	public boolean method_3501(class_809.class_811 arg) {
		return this.method_3503(arg) != class_804.field_4284;
	}

	@Environment(EnvType.CLIENT)
	public static class class_810 implements JsonDeserializer<class_809> {
		protected class_810() {
		}

		public class_809 method_3505(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			class_804 lv = this.method_3504(jsonDeserializationContext, jsonObject, "thirdperson_righthand");
			class_804 lv2 = this.method_3504(jsonDeserializationContext, jsonObject, "thirdperson_lefthand");
			if (lv2 == class_804.field_4284) {
				lv2 = lv;
			}

			class_804 lv3 = this.method_3504(jsonDeserializationContext, jsonObject, "firstperson_righthand");
			class_804 lv4 = this.method_3504(jsonDeserializationContext, jsonObject, "firstperson_lefthand");
			if (lv4 == class_804.field_4284) {
				lv4 = lv3;
			}

			class_804 lv5 = this.method_3504(jsonDeserializationContext, jsonObject, "head");
			class_804 lv6 = this.method_3504(jsonDeserializationContext, jsonObject, "gui");
			class_804 lv7 = this.method_3504(jsonDeserializationContext, jsonObject, "ground");
			class_804 lv8 = this.method_3504(jsonDeserializationContext, jsonObject, "fixed");
			return new class_809(lv2, lv, lv4, lv3, lv5, lv6, lv7, lv8);
		}

		private class_804 method_3504(JsonDeserializationContext jsonDeserializationContext, JsonObject jsonObject, String string) {
			return jsonObject.has(string) ? jsonDeserializationContext.deserialize(jsonObject.get(string), class_804.class) : class_804.field_4284;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_811 {
		field_4315,
		field_4323,
		field_4320,
		field_4321,
		field_4322,
		field_4316,
		field_4317,
		field_4318,
		field_4319;
	}
}
