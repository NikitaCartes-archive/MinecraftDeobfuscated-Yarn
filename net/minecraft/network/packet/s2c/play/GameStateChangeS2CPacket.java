/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class GameStateChangeS2CPacket
implements Packet<ClientPlayPacketListener> {
    public static final Reason NO_RESPAWN_BLOCK = new Reason(0);
    public static final Reason RAIN_STARTED = new Reason(1);
    public static final Reason RAIN_STOPPED = new Reason(2);
    public static final Reason GAME_MODE_CHANGED = new Reason(3);
    public static final Reason GAME_WON = new Reason(4);
    public static final Reason DEMO_MESSAGE_SHOWN = new Reason(5);
    public static final Reason PROJECTILE_HIT_PLAYER = new Reason(6);
    public static final Reason RAIN_GRADIENT_CHANGED = new Reason(7);
    public static final Reason THUNDER_GRADIENT_CHANGED = new Reason(8);
    public static final Reason PUFFERFISH_STING = new Reason(9);
    public static final Reason ELDER_GUARDIAN_EFFECT = new Reason(10);
    public static final Reason IMMEDIATE_RESPAWN = new Reason(11);
    private final Reason reason;
    private final float value;

    public GameStateChangeS2CPacket(Reason reason, float value) {
        this.reason = reason;
        this.value = value;
    }

    public GameStateChangeS2CPacket(PacketByteBuf packetByteBuf) {
        this.reason = (Reason)Reason.REASONS.get(packetByteBuf.readUnsignedByte());
        this.value = packetByteBuf.readFloat();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeByte(this.reason.id);
        buf.writeFloat(this.value);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onGameStateChange(this);
    }

    @Environment(value=EnvType.CLIENT)
    public Reason getReason() {
        return this.reason;
    }

    @Environment(value=EnvType.CLIENT)
    public float getValue() {
        return this.value;
    }

    public static class Reason {
        private static final Int2ObjectMap<Reason> REASONS = new Int2ObjectOpenHashMap<Reason>();
        private final int id;

        public Reason(int id) {
            this.id = id;
            REASONS.put(id, this);
        }
    }
}

