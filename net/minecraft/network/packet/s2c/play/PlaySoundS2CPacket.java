/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.Validate;

public class PlaySoundS2CPacket
implements Packet<ClientPlayPacketListener> {
    private SoundEvent sound;
    private SoundCategory category;
    private int fixedX;
    private int fixedY;
    private int fixedZ;
    private float volume;
    private float pitch;

    public PlaySoundS2CPacket() {
    }

    public PlaySoundS2CPacket(SoundEvent sound, SoundCategory category, double x, double y, double z, float volume, float pitch) {
        Validate.notNull(sound, "sound", new Object[0]);
        this.sound = sound;
        this.category = category;
        this.fixedX = (int)(x * 8.0);
        this.fixedY = (int)(y * 8.0);
        this.fixedZ = (int)(z * 8.0);
        this.volume = volume;
        this.pitch = pitch;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.sound = (SoundEvent)Registry.SOUND_EVENT.get(buf.readVarInt());
        this.category = buf.readEnumConstant(SoundCategory.class);
        this.fixedX = buf.readInt();
        this.fixedY = buf.readInt();
        this.fixedZ = buf.readInt();
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeVarInt(Registry.SOUND_EVENT.getRawId(this.sound));
        buf.writeEnumConstant(this.category);
        buf.writeInt(this.fixedX);
        buf.writeInt(this.fixedY);
        buf.writeInt(this.fixedZ);
        buf.writeFloat(this.volume);
        buf.writeFloat(this.pitch);
    }

    @Environment(value=EnvType.CLIENT)
    public SoundEvent getSound() {
        return this.sound;
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
        clientPlayPacketListener.onPlaySound(this);
    }
}

