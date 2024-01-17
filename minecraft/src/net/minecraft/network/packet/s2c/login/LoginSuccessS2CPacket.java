package net.minecraft.network.packet.s2c.login;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.LoginPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketIdentifier;

public class LoginSuccessS2CPacket implements Packet<ClientLoginPacketListener> {
	public static final PacketCodec<PacketByteBuf, LoginSuccessS2CPacket> CODEC = Packet.createCodec(LoginSuccessS2CPacket::write, LoginSuccessS2CPacket::new);
	private final GameProfile profile;

	public LoginSuccessS2CPacket(GameProfile profile) {
		this.profile = profile;
	}

	private LoginSuccessS2CPacket(PacketByteBuf buf) {
		this.profile = buf.readGameProfile();
	}

	private void write(PacketByteBuf buf) {
		buf.writeGameProfile(this.profile);
	}

	@Override
	public PacketIdentifier<LoginSuccessS2CPacket> getPacketId() {
		return LoginPackets.GAME_PROFILE;
	}

	public void apply(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onSuccess(this);
	}

	public GameProfile getProfile() {
		return this.profile;
	}

	@Override
	public boolean transitionsNetworkState() {
		return true;
	}
}
