package net.minecraft.network.packet.s2c.play;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;

public class ScreenHandlerSlotUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	public static final PacketCodec<RegistryByteBuf, ScreenHandlerSlotUpdateS2CPacket> CODEC = Packet.createCodec(
		ScreenHandlerSlotUpdateS2CPacket::write, ScreenHandlerSlotUpdateS2CPacket::new
	);
	public static final int UPDATE_CURSOR_SYNC_ID = -1;
	public static final int UPDATE_PLAYER_INVENTORY_SYNC_ID = -2;
	private final int syncId;
	private final int revision;
	private final int slot;
	private final ItemStack stack;

	public ScreenHandlerSlotUpdateS2CPacket(int syncId, int revision, int slot, ItemStack stack) {
		this.syncId = syncId;
		this.revision = revision;
		this.slot = slot;
		this.stack = stack.copy();
	}

	private ScreenHandlerSlotUpdateS2CPacket(RegistryByteBuf buf) {
		this.syncId = buf.readByte();
		this.revision = buf.readVarInt();
		this.slot = buf.readShort();
		this.stack = ItemStack.OPTIONAL_PACKET_CODEC.decode(buf);
	}

	private void write(RegistryByteBuf buf) {
		buf.writeByte(this.syncId);
		buf.writeVarInt(this.revision);
		buf.writeShort(this.slot);
		ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, this.stack);
	}

	@Override
	public PacketType<ScreenHandlerSlotUpdateS2CPacket> getPacketId() {
		return PlayPackets.CONTAINER_SET_SLOT;
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScreenHandlerSlotUpdate(this);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public int getSlot() {
		return this.slot;
	}

	public ItemStack getStack() {
		return this.stack;
	}

	public int getRevision() {
		return this.revision;
	}
}
