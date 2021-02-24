/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

    public OpenScreenS2CPacket(PacketByteBuf packetByteBuf) {
        this.syncId = packetByteBuf.readVarInt();
        this.screenHandlerId = packetByteBuf.readVarInt();
        this.name = packetByteBuf.readText();
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

    @Environment(value=EnvType.CLIENT)
    public int getSyncId() {
        return this.syncId;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public ScreenHandlerType<?> getScreenHandlerType() {
        return (ScreenHandlerType)Registry.SCREEN_HANDLER.get(this.screenHandlerId);
    }

    @Environment(value=EnvType.CLIENT)
    public Text getName() {
        return this.name;
    }
}

