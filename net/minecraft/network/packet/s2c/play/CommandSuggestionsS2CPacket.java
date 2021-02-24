/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

    public CommandSuggestionsS2CPacket(PacketByteBuf packetByteBuf2) {
        this.completionId = packetByteBuf2.readVarInt();
        int i = packetByteBuf2.readVarInt();
        int j = packetByteBuf2.readVarInt();
        StringRange stringRange = StringRange.between(i, i + j);
        List<Suggestion> list = packetByteBuf2.method_34066(packetByteBuf -> {
            String string = packetByteBuf.readString();
            Text text = packetByteBuf.readBoolean() ? packetByteBuf.readText() : null;
            return new Suggestion(stringRange, string, text);
        });
        this.suggestions = new Suggestions(stringRange, list);
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.completionId);
        buf.writeVarInt(this.suggestions.getRange().getStart());
        buf.writeVarInt(this.suggestions.getRange().getLength());
        buf.method_34062(this.suggestions.getList(), (packetByteBuf, suggestion) -> {
            packetByteBuf.writeString(suggestion.getText());
            packetByteBuf.writeBoolean(suggestion.getTooltip() != null);
            if (suggestion.getTooltip() != null) {
                packetByteBuf.writeText(Texts.toText(suggestion.getTooltip()));
            }
        });
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

