package net.minecraft.command.arguments.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.arguments.BrigadierArgumentTypes;
import net.minecraft.util.PacketByteBuf;

public class IntegerArgumentSerializer implements ArgumentSerializer<IntegerArgumentType> {
	public void method_10048(IntegerArgumentType integerArgumentType, PacketByteBuf packetByteBuf) {
		boolean bl = integerArgumentType.getMinimum() != Integer.MIN_VALUE;
		boolean bl2 = integerArgumentType.getMaximum() != Integer.MAX_VALUE;
		packetByteBuf.writeByte(BrigadierArgumentTypes.method_10037(bl, bl2));
		if (bl) {
			packetByteBuf.writeInt(integerArgumentType.getMinimum());
		}

		if (bl2) {
			packetByteBuf.writeInt(integerArgumentType.getMaximum());
		}
	}

	public IntegerArgumentType method_10050(PacketByteBuf packetByteBuf) {
		byte b = packetByteBuf.readByte();
		int i = BrigadierArgumentTypes.method_10039(b) ? packetByteBuf.readInt() : Integer.MIN_VALUE;
		int j = BrigadierArgumentTypes.method_10038(b) ? packetByteBuf.readInt() : Integer.MAX_VALUE;
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
