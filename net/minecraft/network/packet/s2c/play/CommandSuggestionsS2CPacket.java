/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class CommandSuggestionsS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final int completionId;
    private final Suggestions suggestions;

    public CommandSuggestionsS2CPacket(int completionId, Suggestions suggestions) {
        this.completionId = completionId;
        this.suggestions = suggestions;
    }

    public CommandSuggestionsS2CPacket(PacketByteBuf buf2) {
        this.completionId = buf2.readVarInt();
        int i = buf2.readVarInt();
        int j = buf2.readVarInt();
        StringRange stringRange = StringRange.between(i, i + j);
        List<Suggestion> list = buf2.readList(buf -> {
            String string = buf.readString();
            Text text = buf.readBoolean() ? buf.readText() : null;
            return new Suggestion(stringRange, string, text);
        });
        this.suggestions = new Suggestions(stringRange, list);
    }

    @Override
    public void write(PacketByteBuf buf2) {
        buf2.writeVarInt(this.completionId);
        buf2.writeVarInt(this.suggestions.getRange().getStart());
        buf2.writeVarInt(this.suggestions.getRange().getLength());
        buf2.writeCollection(this.suggestions.getList(), (buf, suggestion) -> {
            buf.writeString(suggestion.getText());
            buf.writeBoolean(suggestion.getTooltip() != null);
            if (suggestion.getTooltip() != null) {
                buf.writeText(Texts.toText(suggestion.getTooltip()));
            }
        });
    }

    @Override
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

