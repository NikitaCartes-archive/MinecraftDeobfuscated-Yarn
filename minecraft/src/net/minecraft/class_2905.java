package net.minecraft;

import java.io.IOException;
import java.security.PublicKey;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.NetworkEncryptionUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.util.PacketByteBuf;

public class class_2905 implements Packet<ClientLoginPacketListener> {
	private String field_13209;
	private PublicKey field_13211;
	private byte[] field_13210;

	public class_2905() {
	}

	public class_2905(String string, PublicKey publicKey, byte[] bs) {
		this.field_13209 = string;
		this.field_13211 = publicKey;
		this.field_13210 = bs;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.field_13209 = packetByteBuf.readString(20);
		this.field_13211 = NetworkEncryptionUtils.method_15242(packetByteBuf.readByteArray());
		this.field_13210 = packetByteBuf.readByteArray();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeString(this.field_13209);
		packetByteBuf.writeByteArray(this.field_13211.getEncoded());
		packetByteBuf.writeByteArray(this.field_13210);
	}

	public void method_12612(ClientLoginPacketListener clientLoginPacketListener) {
		clientLoginPacketListener.method_12587(this);
	}

	@Environment(EnvType.CLIENT)
	public String method_12610() {
		return this.field_13209;
	}

	@Environment(EnvType.CLIENT)
	public PublicKey method_12611() {
		return this.field_13211;
	}

	@Environment(EnvType.CLIENT)
	public byte[] method_12613() {
		return this.field_13210;
	}
}
