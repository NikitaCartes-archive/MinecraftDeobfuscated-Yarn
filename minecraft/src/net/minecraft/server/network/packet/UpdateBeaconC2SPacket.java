package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class UpdateBeaconC2SPacket implements Packet<ServerPlayPacketListener> {
	private int primaryEffectId;
	private int secondaryEffectId;

	public UpdateBeaconC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public UpdateBeaconC2SPacket(int i, int j) {
		this.primaryEffectId = i;
		this.secondaryEffectId = j;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.primaryEffectId = packetByteBuf.readVarInt();
		this.secondaryEffectId = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.primaryEffectId);
		packetByteBuf.writeVarInt(this.secondaryEffectId);
	}

	public void method_12434(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.method_12057(this);
	}

	public int getPrimaryEffectId() {
		return this.primaryEffectId;
	}

	public int getSecondaryEffectId() {
		return this.secondaryEffectId;
	}
}
