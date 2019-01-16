package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class UpdateBeaconServerPacket implements Packet<ServerPlayPacketListener> {
	private int primaryEffectId;
	private int secondaryEffectId;

	public UpdateBeaconServerPacket() {
	}

	@Environment(EnvType.CLIENT)
	public UpdateBeaconServerPacket(int i, int j) {
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

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onUpdateBeacon(this);
	}

	public int getPrimaryEffectId() {
		return this.primaryEffectId;
	}

	public int getSecondaryEffectId() {
		return this.secondaryEffectId;
	}
}
