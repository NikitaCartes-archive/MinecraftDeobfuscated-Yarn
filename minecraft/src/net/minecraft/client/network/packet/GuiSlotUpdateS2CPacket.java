package net.minecraft.client.network.packet;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.PacketByteBuf;

public class GuiSlotUpdateS2CPacket implements Packet<ClientPlayPacketListener> {
	private int id;
	private int slot;
	private ItemStack stack = ItemStack.EMPTY;

	public GuiSlotUpdateS2CPacket() {
	}

	public GuiSlotUpdateS2CPacket(int id, int slot, ItemStack stack) {
		this.id = id;
		this.slot = slot;
		this.stack = stack.copy();
	}

	public void apply(ClientPlayPacketListener clientPlayPacketListener) {
		clientPlayPacketListener.onGuiSlotUpdate(this);
	}

	@Override
	public void read(PacketByteBuf buf) throws IOException {
		this.id = buf.readByte();
		this.slot = buf.readShort();
		this.stack = buf.readItemStack();
	}

	@Override
	public void write(PacketByteBuf buf) throws IOException {
		buf.writeByte(this.id);
		buf.writeShort(this.slot);
		buf.writeItemStack(this.stack);
	}

	@Environment(EnvType.CLIENT)
	public int getId() {
		return this.id;
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
