/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network.packet;

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

    public CommandSuggestionsS2CPacket(int completionId, Suggestions suggestions) {
        this.completionId = completionId;
        this.suggestions = suggestions;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.completionId = buf.readVarInt();
        int i = buf.readVarInt();
        int j = buf.readVarInt();
        StringRange stringRange = StringRange.between(i, i + j);
        int k = buf.readVarInt();
        ArrayList<Suggestion> list = Lists.newArrayListWithCapacity(k);
        for (int l = 0; l < k; ++l) {
            String string = buf.readString(Short.MAX_VALUE);
            Text text = buf.readBoolean() ? buf.readText() : null;
            list.add(new Suggestion(stringRange, string, text));
        }
        this.suggestions = new Suggestions(stringRange, list);
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.completionId);
        buf.writeVarInt(this.suggestions.getRange().getStart());
        buf.writeVarInt(this.suggestions.getRange().getLength());
        buf.writeVarInt(this.suggestions.getList().size());
        for (Suggestion suggestion : this.suggestions.getList()) {
            buf.writeString(suggestion.getText());
            buf.writeBoolean(suggestion.getTooltip() != null);
            if (suggestion.getTooltip() == null) continue;
            buf.writeText(Texts.toText(suggestion.getTooltip()));
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

