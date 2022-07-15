/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.message;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.MessageSignatureData;

/**
 * A list of messages a client has seen.
 */
public record LastSeenMessageList(List<Entry> entries) {
    public static LastSeenMessageList EMPTY = new LastSeenMessageList(List.of());
    public static final int MAX_ENTRIES = 5;

    public LastSeenMessageList(PacketByteBuf buf) {
        this(buf.readCollection(PacketByteBuf.getMaxValidator(ArrayList::new, 5), Entry::new));
    }

    public void write(PacketByteBuf buf) {
        buf.writeCollection(this.entries, (buf2, entries) -> entries.write((PacketByteBuf)buf2));
    }

    public void write(DataOutput output) throws IOException {
        for (Entry entry : this.entries) {
            UUID uUID = entry.profileId();
            MessageSignatureData messageSignatureData = entry.lastSignature();
            output.writeByte(70);
            output.writeLong(uUID.getMostSignificantBits());
            output.writeLong(uUID.getLeastSignificantBits());
            output.write(messageSignatureData.data());
        }
    }

    public record Entry(UUID profileId, MessageSignatureData lastSignature) {
        public Entry(PacketByteBuf buf) {
            this(buf.readUuid(), new MessageSignatureData(buf));
        }

        public void write(PacketByteBuf buf) {
            buf.writeUuid(this.profileId);
            this.lastSignature.write(buf);
        }
    }

    public record Acknowledgment(LastSeenMessageList lastSeen, Optional<Entry> lastReceived) {
        public Acknowledgment(PacketByteBuf buf) {
            this(new LastSeenMessageList(buf), buf.readOptional(Entry::new));
        }

        public void write(PacketByteBuf buf) {
            this.lastSeen.write(buf);
            buf.writeOptional(this.lastReceived, (buf2, lastReceived) -> lastReceived.write((PacketByteBuf)buf2));
        }
    }
}

