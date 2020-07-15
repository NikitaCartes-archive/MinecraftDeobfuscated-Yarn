package net.minecraft.command.argument;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.command.argument.serialize.DoubleArgumentSerializer;
import net.minecraft.command.argument.serialize.FloatArgumentSerializer;
import net.minecraft.command.argument.serialize.IntegerArgumentSerializer;
import net.minecraft.command.argument.serialize.LongArgumentSerializer;
import net.minecraft.command.argument.serialize.StringArgumentSerializer;

public class BrigadierArgumentTypes {
	public static void register() {
		ArgumentTypes.register("brigadier:bool", BoolArgumentType.class, new ConstantArgumentSerializer(BoolArgumentType::bool));
		ArgumentTypes.register("brigadier:float", FloatArgumentType.class, new FloatArgumentSerializer());
		ArgumentTypes.register("brigadier:double", DoubleArgumentType.class, new DoubleArgumentSerializer());
		ArgumentTypes.register("brigadier:integer", IntegerArgumentType.class, new IntegerArgumentSerializer());
		ArgumentTypes.register("brigadier:long", LongArgumentType.class, new LongArgumentSerializer());
		ArgumentTypes.register("brigadier:string", StringArgumentType.class, new StringArgumentSerializer());
	}

	public static byte createFlag(boolean hasMin, boolean hasMax) {
		byte b = 0;
		if (hasMin) {
			b = (byte)(b | 1);
		}

		if (hasMax) {
			b = (byte)(b | 2);
		}

		return b;
	}

	public static boolean hasMin(byte rangeFlag) {
		return (rangeFlag & 1) != 0;
	}

	public static boolean hasMax(byte rangeFlag) {
		return (rangeFlag & 2) != 0;
	}
}
