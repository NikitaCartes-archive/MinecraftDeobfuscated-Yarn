package net.minecraft.server.packet;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.util.PacketByteBuf;

public class LoginHelloServerPacket implements Packet<ServerLoginPacketListener> {
	private GameProfile field_13271;

	public LoginHelloServerPacket() {
	}

	public LoginHelloServerPacket(GameProfile gameProfile) {
		this.field_13271 = gameProfile;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13271 = new GameProfile(null, packetByteBuf.readString(16));
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeString(this.field_13271.getName());
	}

	public void apply(ServerLoginPacketListener serverLoginPacketListener) {
		serverLoginPacketListener.onHello(this);
	}

	public GameProfile getProfile() {
		return this.field_13271;
	}
}
