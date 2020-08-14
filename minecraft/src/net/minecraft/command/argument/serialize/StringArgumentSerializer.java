package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType.StringType;
import net.minecraft.network.PacketByteBuf;

public class StringArgumentSerializer implements ArgumentSerializer<StringArgumentType> {
	public void toPacket(StringArgumentType stringArgumentType, PacketByteBuf packetByteBuf) {
		packetByteBuf.writeEnumConstant(stringArgumentType.getType());
	}

	public StringArgumentType fromPacket(PacketByteBuf packetByteBuf) {
		StringType stringType = packetByteBuf.readEnumConstant(StringType.class);
		switch (stringType) {
			case SINGLE_WORD:
				return StringArgumentType.word();
			case QUOTABLE_PHRASE:
				return StringArgumentType.string();
			case GREEDY_PHRASE:
			default:
				return StringArgumentType.greedyString();
		}
	}

	public void toJson(StringArgumentType stringArgumentType, JsonObject jsonObject) {
		switch (stringArgumentType.getType()) {
			case SINGLE_WORD:
				jsonObject.addProperty("type", "word");
				break;
			case QUOTABLE_PHRASE:
				jsonObject.addProperty("type", "phrase");
				break;
			case GREEDY_PHRASE:
			default:
				jsonObject.addProperty("type", "greedy");
		}
	}
}
