package net.minecraft;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3890 implements class_3270<class_3888> {
	public class_3888 method_17171(JsonObject jsonObject) {
		return new class_3888(class_3888.class_3889.method_17170(class_3518.method_15253(jsonObject, "hat", "none")));
	}

	@Override
	public String method_14420() {
		return "villager";
	}
}
