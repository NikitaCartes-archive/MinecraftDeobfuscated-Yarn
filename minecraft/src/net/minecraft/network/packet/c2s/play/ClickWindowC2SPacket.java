package net.minecraft.network.packet.c2s.play;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.screen.slot.SlotActionType;

public class ClickWindowC2SPacket implements Packet<ServerPlayPacketListener> {
	private int syncId;
	private int slot;
	private int clickData;
	private short actionId;
	private ItemStack stack = ItemStack.EMPTY;
	private SlotActionType actionType;

	public ClickWindowC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public ClickWindowC2SPacket(int syncId, int slot, int clickData, SlotActionType actionType, ItemStack stack, short actionId) {
		this.syncId = syncId;
		this.slot = slot;
		this.clickData = clickData;
		this.stack = stack.copy();
		this.actionId = actionId;
		this.actionType = actionType;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onClickWindow(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.syncId = buf.readByte();
		this.slot = buf.readShort();
		this.clickData = buf.readByte();
		this.actionId = buf.readShort();
		this.actionType = buf.readEnumConstant(SlotActionType.class);
		this.stack = buf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
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
