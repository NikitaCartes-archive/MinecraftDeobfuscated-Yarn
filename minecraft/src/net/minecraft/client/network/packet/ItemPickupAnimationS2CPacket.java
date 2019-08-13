package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class ItemPickupAnimationS2CPacket implements Packet<ClientPlayPacketListener> {
	private int entityId;
	private int collectorEntityId;
	private int stackAmount;

	public ItemPickupAnimationS2CPacket() {
	}

	public ItemPickupAnimationS2CPacket(int i, int j, int k) {
		this.entityId = i;
		this.collectorEntityId = j;
		this.stackAmount = k;
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.entityId = packetByteBuf.readVarInt();
		this.collectorEntityId = packetByteBuf.readVarInt();
		this.stackAmount = packetByteBuf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeVarInt(this.entityId);
		packetByteBuf.writeVarInt(this.collectorEntityId);
		packetByteBuf.writeVarInt(this.stackAmount);
	}

	public void method_11914(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onItemPickupAnimation(this);
	}

	@Environment(EnvType.CLIENT)
	public int getEntityId() {
		return this.entityId;
	}

	@Environment(EnvType.CLIENT)
	public int getCollectorEntityId() {
		return this.collectorEntityId;
	}

	@Environment(EnvType.CLIENT)
	public int getStackAmount() {
		return this.stackAmount;
	}
}
