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

	public ItemPickupAnimationS2CPacket(int entityId, int collectorId, int stackAmount) {
		this.entityId = entityId;
		this.collectorEntityId = collectorId;
		this.stackAmount = stackAmount;
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.entityId = buf.readVarInt();
		this.collectorEntityId = buf.readVarInt();
		this.stackAmount = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeVarInt(this.entityId);
		buf.writeVarInt(this.collectorEntityId);
		buf.writeVarInt(this.stackAmount);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
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
