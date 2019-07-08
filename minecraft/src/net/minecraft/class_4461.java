package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.command.arguments.BrigadierArgumentTypes;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import net.minecraft.util.PacketByteBuf;

public class class_4461 implements ArgumentSerializer<LongArgumentType> {
	public void method_21690(LongArgumentType longArgumentType, PacketByteBuf packetByteBuf) {
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

	public LongArgumentType method_21691(PacketByteBuf packetByteBuf) {
		byte b = packetByteBuf.readByte();
		long l = BrigadierArgumentTypes.hasMin(b) ? packetByteBuf.readLong() : Long.MIN_VALUE;
		long m = BrigadierArgumentTypes.hasMax(b) ? packetByteBuf.readLong() : Long.MAX_VALUE;
		return LongArgumentType.longArg(l, m);
	}

	public void method_21689(LongArgumentType longArgumentType, JsonObject jsonObject) {
		if (longArgumentType.getMinimum() != Long.MIN_VALUE) {
			jsonObject.addProperty("min", longArgumentType.getMinimum());
		}

		if (longArgumentType.getMaximum() != Long.MAX_VALUE) {
			jsonObject.addProperty("max", longArgumentType.getMaximum());
		}
	}
}
