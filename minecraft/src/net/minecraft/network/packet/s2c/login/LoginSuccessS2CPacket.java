package net.minecraft.network.packet.s2c.login;

import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.LoginPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;

public record LoginSuccessS2CPacket(GameProfile profile, @Deprecated(forRemoval = true) boolean strictErrorHandling)
	implements Packet<ClientLoginPacketListener> {
	public static final PacketCodec<ByteBuf, LoginSuccessS2CPacket> CODEC = PacketCodec.tuple(
		PacketCodecs.GAME_PROFILE, LoginSuccessS2CPacket::profile, PacketCodecs.BOOL, LoginSuccessS2CPacket::strictErrorHandling, LoginSuccessS2CPacket::new
	);

	@Override
	public PacketType<LoginSuccessS2CPacket> getPacketId() {
		return LoginPackets.GAME_PROFILE;
	}

	public void apply(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onSuccess(this);
	}

	@Override
	public boolean transitionsNetworkState() {
		return true;
	}
}
