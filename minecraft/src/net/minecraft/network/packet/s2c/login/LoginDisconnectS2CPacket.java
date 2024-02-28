package net.minecraft.network.packet.s2c.login;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.LoginPackets;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;

public class LoginDisconnectS2CPacket implements Packet<ClientLoginPacketListener> {
	public static final PacketCodec<PacketByteBuf, LoginDisconnectS2CPacket> CODEC = Packet.createCodec(
		LoginDisconnectS2CPacket::write, LoginDisconnectS2CPacket::new
	);
	private final Text reason;

	public LoginDisconnectS2CPacket(Text reason) {
		this.reason = reason;
	}

	private LoginDisconnectS2CPacket(PacketByteBuf buf) {
		this.reason = Text.Serialization.fromLenientJson(buf.readString(PacketByteBuf.MAX_TEXT_LENGTH), DynamicRegistryManager.EMPTY);
	}

	private void write(PacketByteBuf buf) {
		buf.writeString(Text.Serialization.toJsonString(this.reason, DynamicRegistryManager.EMPTY));
	}

	@Override
	public PacketType<LoginDisconnectS2CPacket> getPacketId() {
		return LoginPackets.LOGIN_DISCONNECT;
	}

	public void apply(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.onDisconnect(this);
	}

	public Text getReason() {
		return this.reason;
	}
}
