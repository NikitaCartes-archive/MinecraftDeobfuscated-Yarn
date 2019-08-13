package net.minecraft.server.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.SlotActionType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

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
	public ClickWindowC2SPacket(int i, int j, int k, SlotActionType slotActionType, ItemStack itemStack, short s) {
		this.syncId = i;
		this.slot = j;
		this.button = k;
		this.stack = itemStack.copy();
		this.transactionId = s;
		this.actionType = slotActionType;
	}

	public void method_12191(ServerPlayPacketListener serverPlayPacketListener) {
		serverPlayPacketListener.onClickWindow(this);
	}

	@Override
	public void read(PacketByteBuf packetByteBuf) throws IOException {
		this.syncId = packetByteBuf.readByte();
		this.slot = packetByteBuf.readShort();
		this.button = packetByteBuf.readByte();
		this.transactionId = packetByteBuf.readShort();
		this.actionType = packetByteBuf.readEnumConstant(SlotActionType.class);
		this.stack = packetByteBuf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf packetByteBuf) throws IOException {
		packetByteBuf.writeByte(this.syncId);
		packetByteBuf.writeShort(this.slot);
		packetByteBuf.writeByte(this.button);
		packetByteBuf.writeShort(this.transactionId);
		packetByteBuf.writeEnumConstant(this.actionType);
		packetByteBuf.writeItemStack(this.stack);
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
