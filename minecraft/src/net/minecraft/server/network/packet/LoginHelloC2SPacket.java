package net.minecraft.server.network.packet;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.util.PacketByteBuf;

public class LoginHelloC2SPacket implements Packet<ServerLoginPacketListener> {
	private GameProfile profile;

	public LoginHelloC2SPacket() {
	}

	public LoginHelloC2SPacket(GameProfile gameProfile) {
		this.profile = gameProfile;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.profile = new GameProfile(null, packetByteBuf.readString(16));
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeString(this.profile.getName());
	}

	public void method_12649(ServerLoginPacketListener serverLoginPacketListener) {
		serverLoginPacketListener.onHello(this);
	}

	public GameProfile getProfile() {
		return this.profile;
	}
}
