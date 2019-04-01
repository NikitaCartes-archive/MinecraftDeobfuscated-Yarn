package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;

public class class_2330 implements class_2314<IntegerArgumentType> {
	public void method_10048(IntegerArgumentType integerArgumentType, class_2540 arg) {
		boolean bl = integerArgumentType.getMinimum() != Integer.MIN_VALUE;
		boolean bl2 = integerArgumentType.getMaximum() != Integer.MAX_VALUE;
		arg.writeByte(class_2324.method_10037(bl, bl2));
		if (bl) {
			arg.writeInt(integerArgumentType.getMinimum());
		}

		if (bl2) {
			arg.writeInt(integerArgumentType.getMaximum());
		}
	}

	public IntegerArgumentType method_10050(class_2540 arg) {
		byte b = arg.readByte();
		int i = class_2324.method_10039(b) ? arg.readInt() : Integer.MIN_VALUE;
		int j = class_2324.method_10038(b) ? arg.readInt() : Integer.MAX_VALUE;
		return IntegerArgumentType.integer(i, j);
	}

	public void method_10049(IntegerArgumentType integerArgumentType, JsonObject jsonObject) {
		if (integerArgumentType.getMinimum() != Integer.MIN_VALUE) {
			jsonObject.addProperty("min", integerArgumentType.getMinimum());
		}

		if (integerArgumentType.getMaximum() != Integer.MAX_VALUE) {
			jsonObject.addProperty("max", integerArgumentType.getMaximum());
		}
	}
}
