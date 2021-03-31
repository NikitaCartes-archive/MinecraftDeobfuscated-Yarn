/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class OpenScreenS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final int syncId;
    private final int screenHandlerId;
    private final Text name;

    public OpenScreenS2CPacket(int syncId, ScreenHandlerType<?> type, Text name) {
        this.syncId = syncId;
        this.screenHandlerId = Registry.SCREEN_HANDLER.getRawId(type);
        this.name = name;
    }

    public OpenScreenS2CPacket(PacketByteBuf buf) {
        this.syncId = buf.readVarInt();
        this.screenHandlerId = buf.readVarInt();
        this.name = buf.readText();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeVarInt(this.syncId);
        buf.writeVarInt(this.screenHandlerId);
        buf.writeText(this.name);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onOpenScreen(this);
    }

    public int getSyncId() {
        return this.syncId;
    }

    @Nullable
    public ScreenHandlerType<?> getScreenHandlerType() {
        return (ScreenHandlerType)Registry.SCREEN_HANDLER.get(this.screenHandlerId);
    }

    public Text getName() {
        return this.name;
    }
}

