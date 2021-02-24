package net.minecraft.network.packet.c2s.play;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.screen.slot.SlotActionType;

public class ClickSlotC2SPacket implements Packet<ServerPlayPacketListener> {
	private final int syncId;
	private final int slot;
	private final int clickData;
	private final short actionId;
	private final ItemStack stack;
	private final SlotActionType actionType;

	@Environment(EnvType.CLIENT)
	public ClickSlotC2SPacket(int syncId, int slot, int clickData, SlotActionType actionType, ItemStack stack, short actionId) {
		this.syncId = syncId;
		this.slot = slot;
		this.clickData = clickData;
		this.stack = stack.copy();
		this.actionId = actionId;
		this.actionType = actionType;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onClickSlot(this);
	}

	public ClickSlotC2SPacket(PacketByteBuf packetByteBuf) {
		this.syncId = packetByteBuf.readByte();
		this.slot = packetByteBuf.readShort();
		this.clickData = packetByteBuf.readByte();
		this.actionId = packetByteBuf.readShort();
		this.actionType = packetByteBuf.readEnumConstant(SlotActionType.class);
		this.stack = packetByteBuf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeByte(this.syncId);
		buf.writeShort(this.slot);
		buf.writeByte(this.clickData);
		buf.writeShort(this.actionId);
		buf.writeEnumConstant(this.actionType);
		buf.writeItemStack(this.stack);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public int getSlot() {
		return this.slot;
	}

	public int getClickData() {
		return this.clickData;
	}

	public short getActionId() {
		return this.actionId;
	}

	public ItemStack getStack() {
		return this.stack;
	}

	public SlotActionType getActionType() {
		return this.actionType;
	}
}
