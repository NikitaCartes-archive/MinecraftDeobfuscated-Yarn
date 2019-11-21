package net.minecraft.command.arguments.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.arguments.BrigadierArgumentTypes;
import net.minecraft.util.PacketByteBuf;

public class FloatArgumentSerializer implements ArgumentSerializer<FloatArgumentType> {
	public void toPacket(FloatArgumentType floatArgumentType, PacketByteBuf packetByteBuf) {
		boolean bl = floatArgumentType.getMinimum() != -Float.MAX_VALUE;
		boolean bl2 = floatArgumentType.getMaximum() != Float.MAX_VALUE;
		packetByteBuf.writeByte(BrigadierArgumentTypes.createFlag(bl, bl2));
		if (bl) {
			packetByteBuf.writeFloat(floatArgumentType.getMinimum());
		}

		if (bl2) {
			packetByteBuf.writeFloat(floatArgumentType.getMaximum());
		}
	}

	public FloatArgumentType fromPacket(PacketByteBuf packetByteBuf) {
		byte b = packetByteBuf.readByte();
		float f = BrigadierArgumentTypes.hasMin(b) ? packetByteBuf.readFloat() : -Float.MAX_VALUE;
		float g = BrigadierArgumentTypes.hasMax(b) ? packetByteBuf.readFloat() : Float.MAX_VALUE;
		return FloatArgumentType.floatArg(f, g);
	}

	public void toJson(FloatArgumentType floatArgumentType, JsonObject jsonObject) {
		if (floatArgumentType.getMinimum() != -Float.MAX_VALUE) {
			jsonObject.addProperty("min", floatArgumentType.getMinimum());
		}

		if (floatArgumentType.getMaximum() != Float.MAX_VALUE) {
			jsonObject.addProperty("max", floatArgumentType.getMaximum());
		}
	}
}
