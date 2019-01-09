package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.FloatArgumentType;

public class class_2327 implements class_2314<FloatArgumentType> {
	public void method_10044(FloatArgumentType floatArgumentType, class_2540 arg) {
		boolean bl = floatArgumentType.getMinimum() != -Float.MAX_VALUE;
		boolean bl2 = floatArgumentType.getMaximum() != Float.MAX_VALUE;
		arg.writeByte(class_2324.method_10037(bl, bl2));
		if (bl) {
			arg.writeFloat(floatArgumentType.getMinimum());
		}

		if (bl2) {
			arg.writeFloat(floatArgumentType.getMaximum());
		}
	}

	public FloatArgumentType method_10045(class_2540 arg) {
		byte b = arg.readByte();
		float f = class_2324.method_10039(b) ? arg.readFloat() : -Float.MAX_VALUE;
		float g = class_2324.method_10038(b) ? arg.readFloat() : Float.MAX_VALUE;
		return FloatArgumentType.floatArg(f, g);
	}

	public void method_10046(FloatArgumentType floatArgumentType, JsonObject jsonObject) {
		if (floatArgumentType.getMinimum() != -Float.MAX_VALUE) {
			jsonObject.addProperty("min", floatArgumentType.getMinimum());
		}

		if (floatArgumentType.getMaximum() != Float.MAX_VALUE) {
			jsonObject.addProperty("max", floatArgumentType.getMaximum());
		}
	}
}
