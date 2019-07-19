/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.text.Text;
import net.minecraft.util.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public class TitleS2CPacket
implements Packet<ClientPlayPacketListener> {
    private Action action;
    private Text text;
    private int fadeInTicks;
    private int stayTicks;
    private int fadeOutTicks;

    public TitleS2CPacket() {
    }

    public TitleS2CPacket(Action action, Text text) {
        this(action, text, -1, -1, -1);
    }

    public TitleS2CPacket(int i, int j, int k) {
        this(Action.TIMES, null, i, j, k);
    }

    public TitleS2CPacket(Action action, @Nullable Text text, int i, int j, int k) {
        this.action = action;
        this.text = text;
        this.fadeInTicks = i;
        this.stayTicks = j;
        this.fadeOutTicks = k;
    }

    @Override
    public void read(PacketByteBuf packetByteBuf) throws IOException {
        this.action = packetByteBuf.readEnumConstant(Action.class);
        if (this.action == Action.TITLE || this.action == Action.SUBTITLE || this.action == Action.ACTIONBAR) {
            this.text = packetByteBuf.readText();
        }
        if (this.action == Action.TIMES) {
            this.fadeInTicks = packetByteBuf.readInt();
            this.stayTicks = packetByteBuf.readInt();
            this.fadeOutTicks = packetByteBuf.readInt();
        }
    }

    @Override
    public void write(PacketByteBuf packetByteBuf) throws IOException {
        packetByteBuf.writeEnumConstant(this.action);
        if (this.action == Action.TITLE || this.action == Action.SUBTITLE || this.action == Action.ACTIONBAR) {
            packetByteBuf.writeText(this.text);
        }
        if (this.action == Action.TIMES) {
            packetByteBuf.writeInt(this.fadeInTicks);
            packetByteBuf.writeInt(this.stayTicks);
            packetByteBuf.writeInt(this.fadeOutTicks);
        }
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onTitle(this);
    }

    @Environment(value=EnvType.CLIENT)
    public Action getAction() {
        return this.action;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getText() {
        return this.text;
    }

    @Environment(value=EnvType.CLIENT)
    public int getFadeInTicks() {
        return this.fadeInTicks;
    }

    @Environment(value=EnvType.CLIENT)
    public int getStayTicks() {
        return this.stayTicks;
    }

    @Environment(value=EnvType.CLIENT)
    public int getFadeOutTicks() {
        return this.fadeOutTicks;
    }

    public static enum Action {
        TITLE,
        SUBTITLE,
        ACTIONBAR,
        TIMES,
        CLEAR,
        RESET;

    }
}

