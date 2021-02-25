package net.minecraft.network.packet.c2s.login;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerLoginPacketListener;

public class LoginHelloC2SPacket implements Packet<ServerLoginPacketListener> {
	private final GameProfile profile;

	public LoginHelloC2SPacket(GameProfile profile) {
		this.profile = profile;
	}

	public LoginHelloC2SPacket(PacketByteBuf buf) {
		this.profile = new GameProfile(null, buf.readString(16));
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeString(this.profile.getName());
	}

	public void apply(ServerLoginPacketListener serverLoginPacketListener) {
		serverLoginPacketListener.onHello(this);
	}

	public GameProfile getProfile() {
		return this.profile;
	}
}
