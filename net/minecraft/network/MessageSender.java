/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network;

import java.util.UUID;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

/**
 * The sender, or the source, of a message.
 * 
 * <p>An instance can be obtained via {@link net.minecraft.entity.Entity#asMessageSender}.
 */
public record MessageSender(UUID uuid, Text name, @Nullable Text teamName) {
    public MessageSender(UUID uuid, Text name) {
        this(uuid, name, null);
    }

    public MessageSender(PacketByteBuf buf) {
        this(buf.readUuid(), buf.readText(), (Text)buf.readNullable(PacketByteBuf::readText));
    }

    public static MessageSender of(Text name) {
        return new MessageSender(Util.NIL_UUID, name);
    }

    public void write(PacketByteBuf buf) {
        buf.writeUuid(this.uuid);
        buf.writeText(this.name);
        buf.writeNullable(this.teamName, PacketByteBuf::writeText);
    }

    public MessageSender withTeamName(Text teamName) {
        return new MessageSender(this.uuid, this.name, teamName);
    }

    @Nullable
    public Text teamName() {
        return this.teamName;
    }
}

