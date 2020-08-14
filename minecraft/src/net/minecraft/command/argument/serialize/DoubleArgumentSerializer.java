package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.command.argument.BrigadierArgumentTypes;
import net.minecraft.network.PacketByteBuf;

public class DoubleArgumentSerializer implements ArgumentSerializer<DoubleArgumentType> {
	public void toPacket(DoubleArgumentType doubleArgumentType, PacketByteBuf packetByteBuf) {
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

	public DoubleArgumentType fromPacket(PacketByteBuf packetByteBuf) {
		byte b = packetByteBuf.readByte();
		double d = BrigadierArgumentTypes.hasMin(b) ? packetByteBuf.readDouble() : -Double.MAX_VALUE;
		double e = BrigadierArgumentTypes.hasMax(b) ? packetByteBuf.readDouble() : Double.MAX_VALUE;
		return DoubleArgumentType.doubleArg(d, e);
	}

	public void toJson(DoubleArgumentType doubleArgumentType, JsonObject jsonObject) {
		if (doubleArgumentType.getMinimum() != -Double.MAX_VALUE) {
			jsonObject.addProperty("min", doubleArgumentType.getMinimum());
		}

		if (doubleArgumentType.getMaximum() != Double.MAX_VALUE) {
			jsonObject.addProperty("max", doubleArgumentType.getMaximum());
		}
	}
}
