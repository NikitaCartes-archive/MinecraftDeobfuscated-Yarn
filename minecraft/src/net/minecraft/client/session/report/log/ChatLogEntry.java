package net.minecraft.client.session.report.log;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;

/**
 * An entry logged to {@link ChatLog}.
 */
@Environment(EnvType.CLIENT)
public interface ChatLogEntry {
	Codec<ChatLogEntry> CODEC = StringIdentifiable.createCodec(ChatLogEntry.Type::values).dispatch(ChatLogEntry::getType, ChatLogEntry.Type::getCodec);

	ChatLogEntry.Type getType();

	@Environment(EnvType.CLIENT)
	public static enum Type implements StringIdentifiable {
		PLAYER("player", () -> ReceivedMessage.ChatMessage.CHAT_MESSAGE_CODEC),
		SYSTEM("system", () -> ReceivedMessage.GameMessage.GAME_MESSAGE_CODEC);

		private final String id;
		private final Supplier<MapCodec<? extends ChatLogEntry>> codecSupplier;

		private Type(final String id, final Supplier<MapCodec<? extends ChatLogEntry>> codecSupplier) {
			this.id = id;
			this.codecSupplier = codecSupplier;
		}

		private MapCodec<? extends ChatLogEntry> getCodec() {
			return (MapCodec<? extends ChatLogEntry>)this.codecSupplier.get();
		}

		@Override
		public String asString() {
			return this.id;
		}
	}
}
