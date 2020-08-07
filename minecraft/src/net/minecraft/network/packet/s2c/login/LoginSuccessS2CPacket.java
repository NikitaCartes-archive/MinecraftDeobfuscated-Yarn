package net.minecraft.network.packet.s2c.login;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.util.dynamic.DynamicSerializableUuid;

public class LoginSuccessS2CPacket implements Packet<ClientLoginPacketListener> {
	private GameProfile profile;

	public LoginSuccessS2CPacket() {
	}

	public LoginSuccessS2CPacket(GameProfile profile) {
		this.profile = profile;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		int[] is = new int[4];

		for (int i = 0; i < is.length; i++) {
			is[i] = buf.readInt();
		}

		UUID uUID = DynamicSerializableUuid.toUuid(is);
		String string = buf.readString(16);
		this.profile = new GameProfile(uUID, string);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		for (int i : DynamicSerializableUuid.toIntArray(this.profile.getId())) {
			buf.writeInt(i);
		}

		buf.writeString(this.profile.getName());
	}

	public void method_12594(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onLoginSuccess(this);
	}

	@Environment(EnvType.CLIENT)
	public GameProfile getProfile() {
		return this.profile;
	}
}
