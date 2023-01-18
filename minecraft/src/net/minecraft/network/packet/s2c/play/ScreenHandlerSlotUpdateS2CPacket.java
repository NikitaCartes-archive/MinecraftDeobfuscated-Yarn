package net.minecraft.network.packet.s2c.play;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;

public class ScreenHandlerSlotUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
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

	public ScreenHandlerSlotUpdateS2CPacket(PacketByteBuf buf) {
		this.syncId = buf.readByte();
		this.revision = buf.readVarInt();
		this.slot = buf.readShort();
		this.stack = buf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeByte(this.syncId);
		buf.writeVarInt(this.revision);
		buf.writeShort(this.slot);
		buf.writeItemStack(this.stack);
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

	public ItemStack getItemStack() {
		return this.stack;
	}

	public int getRevision() {
		return this.revision;
	}
}
