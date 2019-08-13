package net.minecraft.command.arguments.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.command.arguments.BrigadierArgumentTypes;
import net.minecraft.util.PacketByteBuf;

public class DoubleArgumentSerializer implements ArgumentSerializer<DoubleArgumentType> {
	public void method_10041(DoubleArgumentType doubleArgumentType, PacketByteBuf packetByteBuf) {
		boolean bl = doubleArgumentType.getMinimum() != -Double.MAX_VALUE;
		boolean bl2 = doubleArgumentType.getMaximum() != Double.MAX_VALUE;
		packetByteBuf.writeByte(BrigadierArgumentTypes.createFlag(bl, bl2));
		if (bl) {
			packetByteBuf.writeDouble(doubleArgumentType.getMinimum());
		}

		if (bl2) {
			packetByteBuf.writeDouble(doubleArgumentType.getMaximum());
		}
	}

	public DoubleArgumentType method_10042(PacketByteBuf packetByteBuf) {
		byte b = packetByteBuf.readByte();
		double d = BrigadierArgumentTypes.hasMin(b) ? packetByteBuf.readDouble() : -Double.MAX_VALUE;
		double e = BrigadierArgumentTypes.hasMax(b) ? packetByteBuf.readDouble() : Double.MAX_VALUE;
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
