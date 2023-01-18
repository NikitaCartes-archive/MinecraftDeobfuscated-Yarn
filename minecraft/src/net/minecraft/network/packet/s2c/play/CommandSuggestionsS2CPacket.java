package net.minecraft.network.packet.s2c.play;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.List;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class CommandSuggestionsS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int completionId;
	private final Suggestions suggestions;

	public CommandSuggestionsS2CPacket(int completionId, Suggestions suggestions) {
		this.completionId = completionId;
		this.suggestions = suggestions;
	}

	public CommandSuggestionsS2CPacket(PacketByteBuf buf) {
		this.completionId = buf.readVarInt();
		int i = buf.readVarInt();
		int j = buf.readVarInt();
		StringRange stringRange = StringRange.between(i, i + j);
		List<Suggestion> list = buf.readList(buf2 -> {
			String string = buf2.readString();
			Text text = buf2.readNullable(PacketByteBuf::readText);
			return new Suggestion(stringRange, string, text);
		});
		this.suggestions = new Suggestions(stringRange, list);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeVarInt(this.completionId);
		buf.writeVarInt(this.suggestions.getRange().getStart());
		buf.writeVarInt(this.suggestions.getRange().getLength());
		buf.writeCollection(this.suggestions.getList(), (buf2, suggestion) -> {
			buf2.writeString(suggestion.getText());
			buf2.writeNullable(suggestion.getTooltip(), (buf3, tooltip) -> buf3.writeText(Texts.toText(tooltip)));
		});
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCommandSuggestions(this);
	}

	public int getCompletionId() {
		return this.completionId;
	}

	public Suggestions getSuggestions() {
		return this.suggestions;
	}
}
