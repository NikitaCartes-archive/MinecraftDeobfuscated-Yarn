package net.minecraft;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

public class class_2324 {
	public static void method_10040() {
		class_2316.method_10017("brigadier:bool", BoolArgumentType.class, new class_2319(BoolArgumentType::bool));
		class_2316.method_10017("brigadier:float", FloatArgumentType.class, new class_2327());
		class_2316.method_10017("brigadier:double", DoubleArgumentType.class, new class_2326());
		class_2316.method_10017("brigadier:integer", IntegerArgumentType.class, new class_2330());
		class_2316.method_10017("brigadier:string", StringArgumentType.class, new class_2332());
	}

	public static byte method_10037(boolean bl, boolean bl2) {
		byte b = 0;
		if (bl) {
			b = (byte)(b | 1);
		}

		if (bl2) {
			b = (byte)(b | 2);
		}

		return b;
	}

	public static boolean method_10039(byte b) {
		return (b & 1) != 0;
	}

	public static boolean method_10038(byte b) {
		return (b & 2) != 0;
	}
}
