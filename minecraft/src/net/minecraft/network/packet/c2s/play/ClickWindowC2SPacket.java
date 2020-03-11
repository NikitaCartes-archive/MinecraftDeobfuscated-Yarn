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
	private int button;
	private short transactionId;
	private ItemStack stack = ItemStack.EMPTY;
	private SlotActionType actionType;

	public ClickWindowC2SPacket() {
	}

	@Environment(EnvType.CLIENT)
	public ClickWindowC2SPacket(int i, int j, int k, SlotActionType slotActionType, ItemStack stack, short s) {
		this.syncId = i;
		this.slot = j;
		this.button = k;
		this.stack = stack.copy();
		this.transactionId = s;
		this.actionType = slotActionType;
	}

	public void apply(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onClickWindow(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.syncId = buf.readByte();
		this.slot = buf.readShort();
		this.button = buf.readByte();
		this.transactionId = buf.readShort();
		this.actionType = buf.readEnumConstant(SlotActionType.class);
		this.stack = buf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.syncId);
		buf.writeShort(this.slot);
		buf.writeByte(this.button);
		buf.writeShort(this.transactionId);
		buf.writeEnumConstant(this.actionType);
		buf.writeItemStack(this.stack);
	}

	public int getSyncId() {
		return this.syncId;
	}

	public int getSlot() {
		return this.slot;
	}

	public int getButton() {
		return this.button;
	}

	public short getTransactionId() {
		return this.transactionId;
	}

	public ItemStack getStack() {
		return this.stack;
	}

	public SlotActionType getActionType() {
		return this.actionType;
	}
}
