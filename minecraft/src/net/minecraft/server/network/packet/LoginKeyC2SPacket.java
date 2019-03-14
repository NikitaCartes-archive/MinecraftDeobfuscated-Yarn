package net.minecraft.server.network.packet;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerLoginPacketListener;
import net.minecraft.util.PacketByteBuf;

public class LoginKeyC2SPacket implements Packet<ServerLoginPacketListener> {
	private byte[] field_13274 = new byte[0];
	private byte[] field_13273 = new byte[0];

	public LoginKeyC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public LoginKeyC2SPacket(SecretKey secretKey, PublicKey publicKey, byte[] bs) {
		this.field_13274 = NetworkEncryptionUtils.encrypt(publicKey, secretKey.getEncoded());
		this.field_13273 = NetworkEncryptionUtils.encrypt(publicKey, bs);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13274 = packetByteBuf.readByteArray();
		this.field_13273 = packetByteBuf.readByteArray();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByteArray(this.field_13274);
		packetByteBuf.writeByteArray(this.field_13273);
	}

	public void method_12653(ServerLoginPacketListener serverLoginPacketListener) {
		serverLoginPacketListener.onKey(this);
	}

	public SecretKey method_12654(PrivateKey privateKey) {
		return NetworkEncryptionUtils.method_15234(privateKey, this.field_13274);
	}

	public byte[] method_12655(PrivateKey privateKey) {
		return privateKey == null ? this.field_13273 : NetworkEncryptionUtils.decrypt(privateKey, this.field_13273);
	}
}
