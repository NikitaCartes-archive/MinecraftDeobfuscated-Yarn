package net.minecraft.network.packet.s2c.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.world.Vibration;

public class VibrationS2CPacket implements Packet<ClientPlayPacketListener> {
	private Vibration vibration;

	public VibrationS2CPacket() {
	}

	public VibrationS2CPacket(Vibration vibration) {
		this.vibration = vibration;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.vibration = Vibration.readFromBuf(buf);
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		Vibration.writeToBuf(buf, this.vibration);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onVibration(this);
	}

	@Environment(EnvType.CLIENT)
	public Vibration getVibration() {
		return this.vibration;
	}
}
