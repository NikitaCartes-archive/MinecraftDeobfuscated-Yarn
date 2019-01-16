package net.minecraft.command.arguments;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.arguments.serialize.ConstantArgumentSerializer;
import net.minecraft.command.arguments.serialize.DoubleArgumentSerializer;
import net.minecraft.command.arguments.serialize.FloatArgumentSerializer;
import net.minecraft.command.arguments.serialize.IntegerArgumentSerializer;
import net.minecraft.command.arguments.serialize.StringArgumentSerializer;

public class BrigadierArgumentTypes {
	public static void register() {
		ArgumentTypes.register("brigadier:bool", BoolArgumentType.class, new ConstantArgumentSerializer(BoolArgumentType::bool));
		ArgumentTypes.register("brigadier:float", FloatArgumentType.class, new FloatArgumentSerializer());
		ArgumentTypes.register("brigadier:double", DoubleArgumentType.class, new DoubleArgumentSerializer());
		ArgumentTypes.register("brigadier:integer", IntegerArgumentType.class, new IntegerArgumentSerializer());
		ArgumentTypes.register("brigadier:string", StringArgumentType.class, new StringArgumentSerializer());
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
