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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Components;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class CommandSuggestionsS2CPacket implements Packet<ClientPlayPacketListener> {
	private int completionId;
	private Suggestions suggestions;

	public CommandSuggestionsS2CPacket() {
	}

	public CommandSuggestionsS2CPacket(int i, Suggestions suggestions) {
		this.completionId = i;
		this.suggestions = suggestions;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.completionId = packetByteBuf.readVarInt();
		int i = packetByteBuf.readVarInt();
		int j = packetByteBuf.readVarInt();
		StringRange stringRange = StringRange.between(i, i + j);
		int k = packetByteBuf.readVarInt();
		List<Suggestion> list = Lists.<Suggestion>newArrayListWithCapacity(k);

		for (int l = 0; l < k; l++) {
			String string = packetByteBuf.readString(32767);
			Component component = packetByteBuf.readBoolean() ? packetByteBuf.readTextComponent() : null;
			list.add(new Suggestion(stringRange, string, component));
		}

		this.suggestions = new Suggestions(stringRange, list);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.completionId);
		packetByteBuf.writeVarInt(this.suggestions.getRange().getStart());
		packetByteBuf.writeVarInt(this.suggestions.getRange().getLength());
		packetByteBuf.writeVarInt(this.suggestions.getList().size());

		for (Suggestion suggestion : this.suggestions.getList()) {
			packetByteBuf.writeString(suggestion.getText());
			packetByteBuf.writeBoolean(suggestion.getTooltip() != null);
			if (suggestion.getTooltip() != null) {
				packetByteBuf.writeTextComponent(Components.message(suggestion.getTooltip()));
			}
		}
	}

	public void method_11398(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onCommandSuggestions(this);
	}

	@Environment(EnvType.CLIENT)
	public int getCompletionId() {
		return this.completionId;
	}

	@Environment(EnvType.CLIENT)
	public Suggestions getSuggestions() {
		return this.suggestions;
	}
}
