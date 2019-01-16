package net.minecraft.command.arguments.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType.StringType;
import net.minecraft.util.PacketByteBuf;

public class StringArgumentSerializer implements ArgumentSerializer<StringArgumentType> {
	public void method_10053(StringArgumentType stringArgumentType, PacketByteBuf packetByteBuf) {
		packetByteBuf.writeEnumConstant(stringArgumentType.getType());
	}

	public StringArgumentType method_10052(PacketByteBuf packetByteBuf) {
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

	public void method_10051(StringArgumentType stringArgumentType, JsonObject jsonObject) {
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
