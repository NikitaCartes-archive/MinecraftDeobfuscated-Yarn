package net.minecraft.network.packet.s2c.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;

public class ScreenHandlerSlotUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private final int syncId;
	private final int slot;
	private final ItemStack stack;

	public ScreenHandlerSlotUpdateS2CPacket(int syncId, int slot, ItemStack stack) {
		this.syncId = syncId;
		this.slot = slot;
		this.stack = stack.copy();
	}

	public ScreenHandlerSlotUpdateS2CPacket(PacketByteBuf buf) {
		this.syncId = buf.readByte();
		this.slot = buf.readShort();
		this.stack = buf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeByte(this.syncId);
		buf.writeShort(this.slot);
		buf.writeItemStack(this.stack);
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onScreenHandlerSlotUpdate(this);
	}

	@Environment(EnvType.CLIENT)
	public int getSyncId() {
		return this.syncId;
	}

	@Environment(EnvType.CLIENT)
	public int getSlot() {
		return this.slot;
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getItemStack() {
		return this.stack;
	}
}
