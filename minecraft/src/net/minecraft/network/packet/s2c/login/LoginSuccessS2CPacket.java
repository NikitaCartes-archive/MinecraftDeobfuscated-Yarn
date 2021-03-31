package net.minecraft.network.packet.s2c.login;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.util.dynamic.DynamicSerializableUuid;

public class LoginSuccessS2CPacket implements Packet<ClientLoginPacketListener> {
	private final GameProfile profile;

	public LoginSuccessS2CPacket(GameProfile profile) {
		this.profile = profile;
	}

	public LoginSuccessS2CPacket(PacketByteBuf buf) {
		int[] is = new int[4];

		for (int i = 0; i < is.length; i++) {
			is[i] = buf.readInt();
		}

		UUID uUID = DynamicSerializableUuid.toUuid(is);
		String string = buf.readString(16);
		this.profile = new GameProfile(uUID, string);
	}

	@Override
	public void write(PacketByteBuf buf) {
		for (int i : DynamicSerializableUuid.toIntArray(this.profile.getId())) {
			buf.writeInt(i);
		}

		buf.writeString(this.profile.getName());
	}

	public void apply(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onLoginSuccess(this);
	}

	public GameProfile getProfile() {
		return this.profile;
	}
}
