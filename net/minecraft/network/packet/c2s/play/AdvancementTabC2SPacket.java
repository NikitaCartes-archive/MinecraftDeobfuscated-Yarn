/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class AdvancementTabC2SPacket
implements Packet<ServerPlayPacketListener> {
    private final Action action;
    @Nullable
    private final Identifier tabToOpen;

    @Environment(value=EnvType.CLIENT)
    public AdvancementTabC2SPacket(Action action, @Nullable Identifier tab) {
        this.action = action;
        this.tabToOpen = tab;
    }

    @Environment(value=EnvType.CLIENT)
    public static AdvancementTabC2SPacket open(Advancement advancement) {
        return new AdvancementTabC2SPacket(Action.OPENED_TAB, advancement.getId());
    }

    @Environment(value=EnvType.CLIENT)
    public static AdvancementTabC2SPacket close() {
        return new AdvancementTabC2SPacket(Action.CLOSED_SCREEN, null);
    }

    public AdvancementTabC2SPacket(PacketByteBuf packetByteBuf) {
        this.action = packetByteBuf.readEnumConstant(Action.class);
        this.tabToOpen = this.action == Action.OPENED_TAB ? packetByteBuf.readIdentifier() : null;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(this.action);
        if (this.action == Action.OPENED_TAB) {
            buf.writeIdentifier(this.tabToOpen);
        }
    }

    @Override
    public void apply(ServerPlayPacketListener serverPlayPacketListener) {
        serverPlayPacketListener.onAdvancementTab(this);
    }

    public Action getAction() {
        return this.action;
    }

    @Nullable
    public Identifier getTabToOpen() {
        return this.tabToOpen;
    }

    public static enum Action {
        OPENED_TAB,
        CLOSED_SCREEN;

    }
}

