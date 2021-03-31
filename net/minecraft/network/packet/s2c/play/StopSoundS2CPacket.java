/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class StopSoundS2CPacket
implements Packet<ClientPlayPacketListener> {
    private static final int CATEGORY_MASK = 1;
    private static final int SOUND_ID_MASK = 2;
    @Nullable
    private final Identifier soundId;
    @Nullable
    private final SoundCategory category;

    public StopSoundS2CPacket(@Nullable Identifier soundId, @Nullable SoundCategory category) {
        this.soundId = soundId;
        this.category = category;
    }

    public StopSoundS2CPacket(PacketByteBuf buf) {
        byte i = buf.readByte();
        this.category = (i & 1) > 0 ? buf.readEnumConstant(SoundCategory.class) : null;
        this.soundId = (i & 2) > 0 ? buf.readIdentifier() : null;
    }

    @Override
    public void write(PacketByteBuf buf) {
        if (this.category != null) {
            if (this.soundId != null) {
                buf.writeByte(3);
                buf.writeEnumConstant(this.category);
                buf.writeIdentifier(this.soundId);
            } else {
                buf.writeByte(1);
                buf.writeEnumConstant(this.category);
            }
        } else if (this.soundId != null) {
            buf.writeByte(2);
            buf.writeIdentifier(this.soundId);
        } else {
            buf.writeByte(0);
        }
    }

    @Nullable
    public Identifier getSoundId() {
        return this.soundId;
    }

    @Nullable
    public SoundCategory getCategory() {
        return this.category;
    }

    @Override
    public void apply(ClientPlayPacketListener clientPlayPacketListener) {
        clientPlayPacketListener.onStopSound(this);
    }
}

