/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.util.EnumSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class PlayerPositionLookS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final Set<Flag> flags;
    private final int teleportId;
    private final boolean shouldDismount;

    public PlayerPositionLookS2CPacket(double x, double y, double z, float yaw, float pitch, Set<Flag> flags, int teleportId, boolean shouldDismount) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.flags = flags;
        this.teleportId = teleportId;
        this.shouldDismount = shouldDismount;
    }

    public PlayerPositionLookS2CPacket(PacketByteBuf packetByteBuf) {
        this.x = packetByteBuf.readDouble();
        this.y = packetByteBuf.readDouble();
        this.z = packetByteBuf.readDouble();
        this.yaw = packetByteBuf.readFloat();
        this.pitch = packetByteBuf.readFloat();
        this.flags = Flag.getFlags(packetByteBuf.readUnsignedByte());
        this.teleportId = packetByteBuf.readVarInt();
        this.shouldDismount = packetByteBuf.readBoolean();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
        buf.writeFloat(this.yaw);
        buf.writeFloat(this.pitch);
        buf.writeByte(Flag.getBitfield(this.flags));
        buf.writeVarInt(this.teleportId);
        buf.writeBoolean(this.shouldDismount);
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onPlayerPositionLook(this);
    }

    @Environment(value=EnvType.CLIENT)
    public double getX() {
        return this.x;
    }

    @Environment(value=EnvType.CLIENT)
    public double getY() {
        return this.y;
    }

    @Environment(value=EnvType.CLIENT)
    public double getZ() {
        return this.z;
    }

    @Environment(value=EnvType.CLIENT)
    public float getYaw() {
        return this.yaw;
    }

    @Environment(value=EnvType.CLIENT)
    public float getPitch() {
        return this.pitch;
    }

    @Environment(value=EnvType.CLIENT)
    public int getTeleportId() {
        return this.teleportId;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldDismount() {
        return this.shouldDismount;
    }

    @Environment(value=EnvType.CLIENT)
    public Set<Flag> getFlags() {
        return this.flags;
    }

    public static enum Flag {
        X(0),
        Y(1),
        Z(2),
        Y_ROT(3),
        X_ROT(4);

        private final int shift;

        private Flag(int shift) {
            this.shift = shift;
        }

        private int getMask() {
            return 1 << this.shift;
        }

        private boolean isSet(int i) {
            return (i & this.getMask()) == this.getMask();
        }

        public static Set<Flag> getFlags(int i) {
            EnumSet<Flag> set = EnumSet.noneOf(Flag.class);
            for (Flag flag : Flag.values()) {
                if (!flag.isSet(i)) continue;
                set.add(flag);
            }
            return set;
        }

        public static int getBitfield(Set<Flag> set) {
            int i = 0;
            for (Flag flag : set) {
                i |= flag.getMask();
            }
            return i;
        }
    }
}

