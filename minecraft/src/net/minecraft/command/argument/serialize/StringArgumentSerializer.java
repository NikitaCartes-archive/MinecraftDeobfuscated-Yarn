package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType.StringType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.PacketByteBuf;

public class StringArgumentSerializer implements ArgumentSerializer<StringArgumentType, StringArgumentSerializer.Properties> {
	public void writePacket(StringArgumentSerializer.Properties properties, PacketByteBuf packetByteBuf) {
		packetByteBuf.writeEnumConstant(properties.type);
	}

	public StringArgumentSerializer.Properties fromPacket(PacketByteBuf packetByteBuf) {
		StringType stringType = packetByteBuf.readEnumConstant(StringType.class);
		return new StringArgumentSerializer.Properties(stringType);
	}

	public void writeJson(StringArgumentSerializer.Properties properties, JsonObject jsonObject) {
		jsonObject.addProperty("type", switch (properties.type) {
			case SINGLE_WORD -> "word";
			case QUOTABLE_PHRASE -> "phrase";
			case GREEDY_PHRASE -> "greedy";
		});
	}

	public StringArgumentSerializer.Properties getArgumentTypeProperties(StringArgumentType stringArgumentType) {
		return new StringArgumentSerializer.Properties(stringArgumentType.getType());
	}

	public final class Properties implements ArgumentSerializer.ArgumentTypeProperties<StringArgumentType> {
		final StringType type;

		public Properties(StringType type) {
			this.type = type;
		}

		public StringArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
			return switch (this.type) {
				case SINGLE_WORD -> StringArgumentType.word();
				case QUOTABLE_PHRASE -> StringArgumentType.string();
				case GREEDY_PHRASE -> StringArgumentType.greedyString();
			};
		}

		@Override
		public ArgumentSerializer<StringArgumentType, ?> getSerializer() {
			return StringArgumentSerializer.this;
		}
	}
}
