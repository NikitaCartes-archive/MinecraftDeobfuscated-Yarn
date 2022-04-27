/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.s2c.login;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientLoginPacketListener;

public class LoginSuccessS2CPacket
implements Packet<ClientLoginPacketListener> {
    private final GameProfile profile;

    public LoginSuccessS2CPacket(GameProfile profile) {
        this.profile = profile;
    }

    public LoginSuccessS2CPacket(PacketByteBuf buf) {
        this.profile = buf.readGameProfile();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeGameProfile(this.profile);
    }

    @Override
    public void apply(ClientLoginPacketListener clientLoginPacketListener) {
        clientLoginPacketListener.onSuccess(this);
    }

    public GameProfile getProfile() {
        return this.profile;
    }
}

