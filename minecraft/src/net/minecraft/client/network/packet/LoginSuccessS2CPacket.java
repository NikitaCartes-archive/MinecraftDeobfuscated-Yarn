package net.minecraft.client.network.packet;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.util.PacketByteBuf;

public class LoginSuccessS2CPacket implements Packet<ClientLoginPacketListener> {
	private GameProfile profile;

	public LoginSuccessS2CPacket() {
	}

	public LoginSuccessS2CPacket(GameProfile gameProfile) {
		this.profile = gameProfile;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		String string = packetByteBuf.readString(36);
		String string2 = packetByteBuf.readString(16);
		UUID uUID = UUID.fromString(string);
		this.profile = new GameProfile(uUID, string2);
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		UUID uUID = this.profile.getId();
		packetByteBuf.writeString(uUID == null ? "" : uUID.toString());
		packetByteBuf.writeString(this.profile.getName());
	}

	public void method_12594(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onLoginSuccess(this);
	}

	@Environment(EnvType.CLIENT)
	public GameProfile getProfile() {
		return this.profile;
	}
}
