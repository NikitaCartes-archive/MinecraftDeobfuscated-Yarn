/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public class AdvancementTabC2SPacket
implements Packet<ServerPlayPacketListener> {
    private Action action;
    private Identifier tabToOpen;

    public AdvancementTabC2SPacket() {
    }

    @Environment(value=EnvType.CLIENT)
    public AdvancementTabC2SPacket(Action action, @Nullable Identifier identifier) {
        this.action = action;
        this.tabToOpen = identifier;
    }

    @Environment(value=EnvType.CLIENT)
    public static AdvancementTabC2SPacket open(Advancement advancement) {
        return new AdvancementTabC2SPacket(Action.OPENED_TAB, advancement.getId());
    }

    @Environment(value=EnvType.CLIENT)
    public static AdvancementTabC2SPacket close() {
        return new AdvancementTabC2SPacket(Action.CLOSED_SCREEN, null);
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.action = packetByteBuf.readEnumConstant(Action.class);
        if (this.action == Action.OPENED_TAB) {
            this.tabToOpen = packetByteBuf.readIdentifier();
        }
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeEnumConstant(this.action);
        if (this.action == Action.OPENED_TAB) {
            packetByteBuf.writeIdentifier(this.tabToOpen);
        }
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onAdvancementTab(this);
    }

    public Action getAction() {
        return this.action;
    }

    public Identifier getTabToOpen() {
        return this.tabToOpen;
    }

    public static enum Action {
        OPENED_TAB,
        CLOSED_SCREEN;

    }
}

