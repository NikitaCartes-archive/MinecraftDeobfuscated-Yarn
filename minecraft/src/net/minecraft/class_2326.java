package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.DoubleArgumentType;

public class class_2326 implements class_2314<DoubleArgumentType> {
	public void method_10041(DoubleArgumentType doubleArgumentType, class_2540 arg) {
		boolean bl = doubleArgumentType.getMinimum() != -Double.MAX_VALUE;
		boolean bl2 = doubleArgumentType.getMaximum() != Double.MAX_VALUE;
		arg.writeByte(class_2324.method_10037(bl, bl2));
		if (bl) {
			arg.writeDouble(doubleArgumentType.getMinimum());
		}

		if (bl2) {
			arg.writeDouble(doubleArgumentType.getMaximum());
		}
	}

	public DoubleArgumentType method_10042(class_2540 arg) {
		byte b = arg.readByte();
		double d = class_2324.method_10039(b) ? arg.readDouble() : -Double.MAX_VALUE;
		double e = class_2324.method_10038(b) ? arg.readDouble() : Double.MAX_VALUE;
		return DoubleArgumentType.doubleArg(d, e);
	}

	public void method_10043(DoubleArgumentType doubleArgumentType, JsonObject jsonObject) {
		if (doubleArgumentType.getMinimum() != -Double.MAX_VALUE) {
			jsonObject.addProperty("min", doubleArgumentType.getMinimum());
		}

		if (doubleArgumentType.getMaximum() != Double.MAX_VALUE) {
			jsonObject.addProperty("max", doubleArgumentType.getMaximum());
		}
	}
}
