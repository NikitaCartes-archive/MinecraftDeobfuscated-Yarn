/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class PlaySoundS2CPacket
implements Packet<ClientPlayPacketListener> {
    public static final float COORDINATE_SCALE = 8.0f;
    private final RegistryEntry<SoundEvent> sound;
    private final SoundCategory category;
    private final int fixedX;
    private final int fixedY;
    private final int fixedZ;
    private final float volume;
    private final float pitch;
    private final long seed;

    public PlaySoundS2CPacket(RegistryEntry<SoundEvent> sound, SoundCategory category, double x, double y, double z, float volume, float pitch, long seed) {
        this.sound = sound;
        this.category = category;
        this.fixedX = (int)(x * 8.0);
        this.fixedY = (int)(y * 8.0);
        this.fixedZ = (int)(z * 8.0);
        this.volume = volume;
        this.pitch = pitch;
        this.seed = seed;
    }

    public PlaySoundS2CPacket(PacketByteBuf buf) {
        this.sound = buf.readRegistryEntry(Registries.SOUND_EVENT.getIndexedEntries(), SoundEvent::fromBuf);
        this.category = buf.readEnumConstant(SoundCategory.class);
        this.fixedX = buf.readInt();
        this.fixedY = buf.readInt();
        this.fixedZ = buf.readInt();
        this.volume = buf.readFloat();
        this.pitch = buf.readFloat();
        this.seed = buf.readLong();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeRegistryEntry(Registries.SOUND_EVENT.getIndexedEntries(), this.sound, (packetByteBuf, soundEvent) -> soundEvent.writeBuf((PacketByteBuf)packetByteBuf));
        buf.writeEnumConstant(this.category);
        buf.writeInt(this.fixedX);
        buf.writeInt(this.fixedY);
        buf.writeInt(this.fixedZ);
        buf.writeFloat(this.volume);
        buf.writeFloat(this.pitch);
        buf.writeLong(this.seed);
    }

    public RegistryEntry<SoundEvent> getSound() {
        return this.sound;
    }

    public SoundCategory getCategory() {
        return this.category;
    }

    public double getX() {
        return (float)this.fixedX / 8.0f;
    }

    public double getY() {
        return (float)this.fixedY / 8.0f;
    }

    public double getZ() {
        return (float)this.fixedZ / 8.0f;
    }

    public float getVolume() {
        return this.volume;
    }

    public float getPitch() {
        return this.pitch;
    }

    public long getSeed() {
        return this.seed;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onPlaySound(this);
    }
}

