package net.minecraft;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1085 implements class_3270<class_1084> {
	public class_1084 method_4698(JsonObject jsonObject) {
		boolean bl = class_3518.method_15258(jsonObject, "blur", false);
		boolean bl2 = class_3518.method_15258(jsonObject, "clamp", false);
		return new class_1084(bl, bl2);
	}

	@Override
	public String method_14420() {
		return "texture";
	}
}
