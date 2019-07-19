/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Lists;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.io.IOException;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.PacketByteBuf;

public class CommandSuggestionsS2CPacket
implements Packet<ClientPlayPacketListener> {
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
        ArrayList<Suggestion> list = Lists.newArrayListWithCapacity(k);
        for (int l = 0; l < k; ++l) {
            String string = packetByteBuf.readString(Short.MAX_VALUE);
            Text text = packetByteBuf.readBoolean() ? packetByteBuf.readText() : null;
            list.add(new Suggestion(stringRange, string, text));
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
            if (suggestion.getTooltip() == null) continue;
            packetByteBuf.writeText(Texts.toText(suggestion.getTooltip()));
        }
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onCommandSuggestions(this);
    }

    @Environment(value=EnvType.CLIENT)
    public int getCompletionId() {
        return this.completionId;
    }

    @Environment(value=EnvType.CLIENT)
    public Suggestions getSuggestions() {
        return this.suggestions;
    }
}

