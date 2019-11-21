package net.minecraft.command.arguments.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.command.arguments.BrigadierArgumentTypes;
import net.minecraft.util.PacketByteBuf;

public class LongArgumentSerializer implements ArgumentSerializer<LongArgumentType> {
	public void toPacket(LongArgumentType longArgumentType, PacketByteBuf packetByteBuf) {
		boolean bl = longArgumentType.getMinimum() != Long.MIN_VALUE;
		boolean bl2 = longArgumentType.getMaximum() != Long.MAX_VALUE;
		packetByteBuf.writeByte(BrigadierArgumentTypes.createFlag(bl, bl2));
		if (bl) {
			packetByteBuf.writeLong(longArgumentType.getMinimum());
		}

		if (bl2) {
			packetByteBuf.writeLong(longArgumentType.getMaximum());
		}
	}

	public LongArgumentType fromPacket(PacketByteBuf packetByteBuf) {
		byte b = packetByteBuf.readByte();
		long l = BrigadierArgumentTypes.hasMin(b) ? packetByteBuf.readLong() : Long.MIN_VALUE;
		long m = BrigadierArgumentTypes.hasMax(b) ? packetByteBuf.readLong() : Long.MAX_VALUE;
		return LongArgumentType.longArg(l, m);
	}

	public void toJson(LongArgumentType longArgumentType, JsonObject jsonObject) {
		if (longArgumentType.getMinimum() != Long.MIN_VALUE) {
			jsonObject.addProperty("min", longArgumentType.getMinimum());
		}

		if (longArgumentType.getMaximum() != Long.MAX_VALUE) {
			jsonObject.addProperty("max", longArgumentType.getMaximum());
		}
	}
}
