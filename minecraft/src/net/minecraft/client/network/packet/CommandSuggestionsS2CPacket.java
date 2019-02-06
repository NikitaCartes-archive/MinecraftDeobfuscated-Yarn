package net.minecraft.client.network.packet;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.io.IOException;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormatter;
import net.minecraft.util.PacketByteBuf;

public class CommandSuggestionsS2CPacket implements Packet<ClientPlayPacketListener> {
	private int field_12122;
	private Suggestions suggestions;

	public CommandSuggestionsS2CPacket() {
	}

	public CommandSuggestionsS2CPacket(int i, Suggestions suggestions) {
		this.field_12122 = i;
		this.suggestions = suggestions;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_12122 = packetByteBuf.readVarInt();
		int i = packetByteBuf.readVarInt();
		int j = packetByteBuf.readVarInt();
		StringRange stringRange = StringRange.between(i, i + j);
		int k = packetByteBuf.readVarInt();
		List<Suggestion> list = Lists.<Suggestion>newArrayListWithCapacity(k);

		for (int l = 0; l < k; l++) {
			String string = packetByteBuf.readString(32767);
			TextComponent textComponent = packetByteBuf.readBoolean() ? packetByteBuf.readTextComponent() : null;
			list.add(new Suggestion(stringRange, string, textComponent));
		}

		this.suggestions = new Suggestions(stringRange, list);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.field_12122);
		packetByteBuf.writeVarInt(this.suggestions.getRange().getStart());
		packetByteBuf.writeVarInt(this.suggestions.getRange().getLength());
		packetByteBuf.writeVarInt(this.suggestions.getList().size());

		for (Suggestion suggestion : this.suggestions.getList()) {
			packetByteBuf.writeString(suggestion.getText());
			packetByteBuf.writeBoolean(suggestion.getTooltip() != null);
			if (suggestion.getTooltip() != null) {
				packetByteBuf.writeTextComponent(TextFormatter.message(suggestion.getTooltip()));
			}
		}
	}

	public void method_11398(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.method_11081(this);
	}

	@Environment(EnvType.CLIENT)
	public int method_11399() {
		return this.field_12122;
	}

	@Environment(EnvType.CLIENT)
	public Suggestions getSuggestions() {
		return this.suggestions;
	}
}
