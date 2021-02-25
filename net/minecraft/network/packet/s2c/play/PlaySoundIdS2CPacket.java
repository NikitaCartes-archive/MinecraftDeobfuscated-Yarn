/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class PlaySoundIdS2CPacket
implements Packet<ClientPlayPacketListener> {
    private final Identifier id;
    private final SoundCategory category;
    private final int fixedX;
    private final int fixedY;
    private final int fixedZ;
    private final float volume;
    private final float pitch;

    public PlaySoundIdS2CPacket(Identifier sound, SoundCategory category, Vec3d pos, float volume, float pitch) {
        this.id = sound;
        this.category = category;
        this.fixedX = (int)(pos.x * 8.0);
        this.fixedY = (int)(pos.y * 8.0);
        this.fixedZ = (int)(pos.z * 8.0);
        this.volume = volume;
        this.pitch = pitch;
    }

    public PlaySoundIdS2CPacket(PacketByteBuf buf) {
        this.id = buf.readIdentifier();
        this.category = buf.readEnumConstant(SoundCategory.class);
        this.fixedX = buf.readInt();
        this.fixedY = buf.readInt();
        this.fixedZ = buf.readInt();
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(this.id);
        buf.writeEnumConstant(this.category);
        buf.writeInt(this.fixedX);
        buf.writeInt(this.fixedY);
        buf.writeInt(this.fixedZ);
        buf.writeFloat(this.volume);
        buf.writeFloat(this.pitch);
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier getSoundId() {
        return this.id;
    }

    @Environment(value=EnvType.CLIENT)
    public SoundCategory getCategory() {
        return this.category;
    }

    @Environment(value=EnvType.CLIENT)
    public double getX() {
        return (float)this.fixedX / 8.0f;
    }

    @Environment(value=EnvType.CLIENT)
    public double getY() {
        return (float)this.fixedY / 8.0f;
    }

    @Environment(value=EnvType.CLIENT)
    public double getZ() {
        return (float)this.fixedZ / 8.0f;
    }

    @Environment(value=EnvType.CLIENT)
    public float getVolume() {
        return this.volume;
    }

    @Environment(value=EnvType.CLIENT)
    public float getPitch() {
        return this.pitch;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onPlaySoundId(this);
    }
}

