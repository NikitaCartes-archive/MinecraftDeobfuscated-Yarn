package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class ItemPickupAnimationS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int entityId;
	private final int collectorEntityId;
	private final int stackAmount;

	public ItemPickupAnimationS2CPacket(int entityId, int collectorId, int stackAmount) {
		this.entityId = entityId;
		this.collectorEntityId = collectorId;
		this.stackAmount = stackAmount;
	}

	public ItemPickupAnimationS2CPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
		this.collectorEntityId = buf.readVarInt();
		this.stackAmount = buf.readVarInt();
	}

	@Override
	public void write(PacketByteBuf buf) {
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
