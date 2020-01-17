package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class GameStateChangeS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final String[] REASON_MESSAGES = new String[]{"block.minecraft.bed.not_valid"};
	private int reason;
	private float value;

	public GameStateChangeS2CPacket() {
	}

	public GameStateChangeS2CPacket(int reason, float value) {
		this.reason = reason;
		this.value = value;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.reason = buf.readUnsignedByte();
		this.value = buf.readFloat();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.reason);
		buf.writeFloat(this.value);
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
