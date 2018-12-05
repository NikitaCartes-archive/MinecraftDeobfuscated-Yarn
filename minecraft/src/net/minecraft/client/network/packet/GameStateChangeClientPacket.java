package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class GameStateChangeClientPacket implements Packet<ClientPlayPacketListener> {
	public static final String[] REASON_MESSAGES = new String[]{"block.minecraft.bed.not_valid"};
	private int reason;
	private float value;

	public GameStateChangeClientPacket() {
	}

	public GameStateChangeClientPacket(int i, float f) {
		this.reason = i;
		this.value = f;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.reason = packetByteBuf.readUnsignedByte();
		this.value = packetByteBuf.readFloat();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.reason);
		packetByteBuf.writeFloat(this.value);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGameStateChange(this);
	}

	@Environment(EnvType.CLIENT)
	public int getReason() {
		return this.reason;
	}

	@Environment(EnvType.CLIENT)
	public float getValue() {
		return this.value;
	}
}
