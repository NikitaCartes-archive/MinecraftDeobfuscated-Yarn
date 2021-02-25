package net.minecraft.network.packet.c2s.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.Identifier;

public class CustomPayloadC2SPacket implements Packet<ServerPlayPacketListener> {
	public static final Identifier BRAND = new Identifier("brand");
	private final Identifier channel;
	private final PacketByteBuf data;

	@Environment(EnvType.CLIENT)
	public CustomPayloadC2SPacket(Identifier channel, PacketByteBuf data) {
		this.channel = channel;
		this.data = data;
	}

	public CustomPayloadC2SPacket(PacketByteBuf buf) {
		this.channel = buf.readIdentifier();
		int i = buf.readableBytes();
		if (i >= 0 && i <= 32767) {
			this.data = new PacketByteBuf(buf.readBytes(i));
		} else {
			throw new IllegalArgumentException("Payload may not be larger than 32767 bytes");
		}
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeIdentifier(this.channel);
		buf.writeBytes(this.data);
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onCustomPayload(this);
		this.data.release();
	}
}
