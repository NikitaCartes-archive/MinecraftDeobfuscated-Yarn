package net.minecraft.network.packet.s2c.play;

import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public record ChatSuggestionsS2CPacket(ChatSuggestionsS2CPacket.Action action, List<String> entries) implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, ChatSuggestionsS2CPacket> CODEC = Packet.createCodec(
		ChatSuggestionsS2CPacket::write, ChatSuggestionsS2CPacket::new
	);

	private ChatSuggestionsS2CPacket(PacketByteBuf buf) {
		this(buf.readEnumConstant(ChatSuggestionsS2CPacket.Action.class), buf.readList(PacketByteBuf::readString));
	}

	private void write(PacketByteBuf buf) {
		buf.writeEnumConstant(this.action);
		buf.writeCollection(this.entries, PacketByteBuf::writeString);
	}

	@Override
	public PacketType<ChatSuggestionsS2CPacket> getPacketId() {
		return PlayPackets.CUSTOM_CHAT_COMPLETIONS;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onChatSuggestions(this);
	}

	public static enum Action {
		ADD,
		REMOVE,
		SET;
	}
}
