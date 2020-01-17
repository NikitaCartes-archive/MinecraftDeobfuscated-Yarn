/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.network.packet.c2s.login;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.util.PacketByteBuf;

public class LoginHelloC2SPacket
implements Packet<ServerLoginPacketListener> {
    private GameProfile profile;

    public LoginHelloC2SPacket() {
    }

    public LoginHelloC2SPacket(GameProfile gameProfile) {
        this.profile = gameProfile;
    }

    @Override
    public void read(PacketByteBuf buf) throws IOException {
        this.profile = new GameProfile(null, buf.readString(16));
    }

    @Override
    public void write(PacketByteBuf buf) throws IOException {
        buf.writeString(this.profile.getName());
    }

    @Override
    public void apply(ServerLoginPacketListener serverLoginPacketListener) {
        serverLoginPacketListener.onHello(this);
    }

    public GameProfile getProfile() {
        return this.profile;
    }
}

