package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.authlib.GameProfile;
import java.util.Set;

public class class_3668 extends class_120 {
	private final class_47.class_50 field_16227;

	public class_3668(class_209[] args, class_47.class_50 arg) {
		super(args);
		this.field_16227 = arg;
	}

	@Override
	public Set<class_169<?>> method_293() {
		return ImmutableSet.of(this.field_16227.method_315());
	}

	@Override
	public class_1799 method_522(class_1799 arg, class_47 arg2) {
		if (arg.method_7909() == class_1802.field_8575) {
			class_1297 lv = arg2.method_296(this.field_16227.method_315());
			if (lv instanceof class_1657) {
				GameProfile gameProfile = ((class_1657)lv).method_7334();
				arg.method_7948().method_10566("SkullOwner", class_2512.method_10684(new class_2487(), gameProfile));
			}
		}

		return arg;
	}

	public static class class_3669 extends class_120.class_123<class_3668> {
		public class_3669() {
			super(new class_2960("fill_player_head"), class_3668.class);
		}

		public void method_15957(JsonObject jsonObject, class_3668 arg, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, arg, jsonSerializationContext);
			jsonObject.add("entity", jsonSerializationContext.serialize(arg.field_16227));
		}

		public class_3668 method_15958(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_209[] args) {
			class_47.class_50 lv = class_3518.method_15272(jsonObject, "entity", jsonDeserializationContext, class_47.class_50.class);
			return new class_3668(args, lv);
		}
	}
}
