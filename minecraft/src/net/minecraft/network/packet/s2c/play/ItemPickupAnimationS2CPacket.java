package net.minecraft.network.packet.s2c.play;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class ItemPickupAnimationS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<PacketByteBuf, ItemPickupAnimationS2CPacket> CODEC = Packet.createCodec(
		ItemPickupAnimationS2CPacket::write, ItemPickupAnimationS2CPacket::new
	);
	private final int entityId;
	private final int collectorEntityId;
	private final int stackAmount;

	public ItemPickupAnimationS2CPacket(int entityId, int collectorId, int stackAmount) {
		this.entityId = entityId;
		this.collectorEntityId = collectorId;
		this.stackAmount = stackAmount;
	}

	private ItemPickupAnimationS2CPacket(PacketByteBuf buf) {
		this.entityId = buf.readVarInt();
		this.collectorEntityId = buf.readVarInt();
		this.stackAmount = buf.readVarInt();
	}

	private void write(PacketByteBuf buf) {
		buf.writeVarInt(this.entityId);
		buf.writeVarInt(this.collectorEntityId);
		buf.writeVarInt(this.stackAmount);
	}

	@Override
	public PacketType<ItemPickupAnimationS2CPacket> getPacketId() {
		return PlayPackets.TAKE_ITEM_ENTITY;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onItemPickupAnimation(this);
	}

	public int getEntityId() {
		return this.entityId;
	}

	public int getCollectorEntityId() {
		return this.collectorEntityId;
	}

	public int getStackAmount() {
		return this.stackAmount;
	}
}
