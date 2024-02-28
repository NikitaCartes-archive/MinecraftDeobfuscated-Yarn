package net.minecraft.network.packet.s2c.play;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.Texts;

public record CommandSuggestionsS2CPacket(int id, int start, int length, List<CommandSuggestionsS2CPacket.Suggestion> suggestions)
	implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, CommandSuggestionsS2CPacket> CODEC = PacketCodec.tuple(
		PacketCodecs.VAR_INT,
		CommandSuggestionsS2CPacket::id,
		PacketCodecs.VAR_INT,
		CommandSuggestionsS2CPacket::start,
		PacketCodecs.VAR_INT,
		CommandSuggestionsS2CPacket::length,
		CommandSuggestionsS2CPacket.Suggestion.CODEC.collect(PacketCodecs.toList()),
		CommandSuggestionsS2CPacket::suggestions,
		CommandSuggestionsS2CPacket::new
	);

	public CommandSuggestionsS2CPacket(int completionId, Suggestions suggestions) {
		this(
			completionId,
			suggestions.getRange().getStart(),
			suggestions.getRange().getLength(),
			suggestions.getList()
				.stream()
				.map(suggestion -> new CommandSuggestionsS2CPacket.Suggestion(suggestion.getText(), Optional.ofNullable(suggestion.getTooltip()).map(Texts::toText)))
				.toList()
		);
	}

	@Override
	public PacketType<CommandSuggestionsS2CPacket> getPacketId() {
		return PlayPackets.COMMAND_SUGGESTIONS;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCommandSuggestions(this);
	}

	public Suggestions getSuggestions() {
		StringRange stringRange = StringRange.between(this.start, this.start + this.length);
		return new Suggestions(
			stringRange,
			this.suggestions
				.stream()
				.map(suggestion -> new com.mojang.brigadier.suggestion.Suggestion(stringRange, suggestion.text(), (Message)suggestion.tooltip().orElse(null)))
				.toList()
		);
	}

	public static record Suggestion(String text, Optional<Text> tooltip) {
		public static final PacketCodec<RegistryByteBuf, CommandSuggestionsS2CPacket.Suggestion> CODEC = PacketCodec.tuple(
			PacketCodecs.STRING,
			CommandSuggestionsS2CPacket.Suggestion::text,
			TextCodecs.OPTIONAL_UNLIMITED_REGISTRY_PACKET_CODEC,
			CommandSuggestionsS2CPacket.Suggestion::tooltip,
			CommandSuggestionsS2CPacket.Suggestion::new
		);
	}
}
